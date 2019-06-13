package assseq.gui.pane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import assseq.AminoAcid;
import assseq.Assseq;
import assseq.Base;
import assseq.NucleotideUtilities;
import assseq.ScrollViewChangeListener;
import assseq.alignment.Alignment;
import assseq.alignment.AlignmentEvent;
import assseq.alignment.AlignmentListener;
import assseq.color.ColorScheme;
import assseq.color.ColorSchemeFactory;
import assseq.color.ColorUtils;
import assseq.gui.ScrollBarModelSyncChangeListener;
import assseq.messenges.Messenger;
import assseq.sequencelist.AlignmentDataListener;
import assseq.sequencelist.AlignmentSelectionEvent;
import assseq.sequencelist.AlignmentSelectionListener;
import assseq.sequencelist.SequenceJList;
import assseq.sequences.AminoAcidAndPosition;
import assseq.sequences.Sequence;
import assseq.settings.Settings;
import assseq.utils.ArrayUtilities;
import utils.OSNativeUtils;
import utils.nexus.CharSet;
import utils.nexus.CharSets;


// HAS to be JPanel - JComponent is not enough for only partial cliprect when in jscrollpane when painting
// When JComponent only then I had to paint it all (maybe because of layoutmanager?)
public class AlignmentPane extends JPanel implements AlignmentSelectionListener, ViewListener{
	private static final long serialVersionUID = 601195400946835871L;
	private static final Logger logger = Logger.getLogger(AlignmentPane.class);
	private static final double MIN_CHAR_SIZE = 1;
	private static final int MAX_CHAR_SIZE = 26;
	private static final double CHAR_HEIGHT_RATIO = 2.5; //2.5;
	private static final double CHAR_ZOOM_CHANGE_RATIO = 0.12;
	public static final int MAX_CHARSIZE_TO_DRAW = 6;
	//private static final Color ALPHACOLOR = new Color(255, 255,255, 128 );
	double charWidth = 10; //10
	double charHeight = charWidth * CHAR_HEIGHT_RATIO; //12
	Font baseFont = new Font(OSNativeUtils.getMonospacedFontName(), Font.PLAIN, (int)charWidth);
	Font highDPIFont = new Font(OSNativeUtils.getMonospacedFontName(), Font.PLAIN, (int)charWidth);
	int highDPIScaleFactor = 1;

	private Alignment alignment;

	private ColorScheme colorSchemeAminoAcid = Settings.getColorSchemeAminoAcid();
	private ColorScheme colorSchemeNucleotide = Settings.getColorSchemeNucleotide();
	//	private Rectangle tempSelectionRect;

	// TODO This should instead be tracing a sequence instead of a position?
	int differenceTraceSequencePosition = 0;
	boolean showTranslation = false;
	boolean showTranslationAndNuc = false;
	//	private boolean showTranslationOnePos = false;
	AlignmentRuler alignmentRuler;
	CharsetRuler charsetRuler;
	ConsensusRuler consensusRuler;
	boolean drawAminoAcidCode; 
	boolean drawCodonPosOnRuler;
	Rectangle lastClip = new Rectangle();
	boolean rulerIsDirty;
	boolean highlightDiffTrace;
	boolean highlightNonCons; // Default value is set by button press in AssseqWindow initWindow() method
	boolean highlightCons;
	boolean ignoreGapInTranslation;
	long endTime; // performance measure
	int drawCounter = 0; // performance measure
	int DRAWCOUNT_LOF_INTERVAL = 1; // performance measure
	int fontCase = Settings.getFontCase().getIntValue();

	// These are the pixel containers needed for an alignment
	// they are all created on every font size or colorscheme change
	CharPixelsContainer charPixDefaultNuc;
	CharPixelsContainer charPixSelectedNuc;
	CharPixelsContainer charPixConsensusNuc;
	CharPixelsContainer charPixNonConsensusNuc;
	CharPixelsContainerNucQuality charPixQualityNuc;
	CharPixelsContainer charPixQualClipNuc;

	CharPixelsContainerAA charPixDefaultAA;
	CharPixelsContainerAA charPixSelectedAA;
	CharPixelsContainerAA charPixConsensusAA;

	CharPixelsContainerTranslation charPixTranslationDefault;
	CharPixelsContainerTranslation charPixTranslationSelected;
	CharPixelsContainerTranslation charPixTranslationLetter;
	CharPixelsContainerTranslation charPixTranslationSelectedLetter;
	CharPixelsContainerTranslation charPixTranslationAndNucDefault;
	CharPixelsContainerTranslation charPixTranslationAndNucSelected;
	CharPixelsContainerTranslation charPixTranslationAndNucDefaultNoAALetter;
	CharPixelsContainerTranslation charPixTranslationAndNucSelectedNoAALetter;
	CharPixelsContainerTranslation charPixTranslationAndNucDominantNuc;
	CharPixelsContainerTranslation charPixTranslationAndNucDominantNucNoAALetter;
	CharPixelsContainerTranslation charPixTranslationAndNucDominantNucSelected;
	CharPixelsContainerTranslation charPixTranslationAndNucDominantNucNoAALetterSelected;
	CharPixelsContainerTranslation charPixTranslationQualClip;


	private double smallCharsSizeNumber = 0;
	private int CHARSET_LINE_HEIGHT = 5;
	ViewModel viewModel;


	public AlignmentPane(ViewModel viewModel) {
		this.viewModel = viewModel;
		highDPIScaleFactor = (int)OSNativeUtils.getHighDPIScaleFactor();
		createAdjustedDerivedBaseFont();
		createAdjustedDerivedHighDPIFont();
		createCharPixelsContainers();
		//	highDPIScaleFactor = 1;
		logger.info("highDPIScaleFactor" + highDPIScaleFactor);
		this.setOpaque(true);
		//this.setDoubleBuffered(false);
		//this.setBackground(Color.white);
		//this.infoLabel = infoLabel;
		alignmentRuler = new AlignmentRuler(this);
		charsetRuler = new CharsetRuler(this);
		consensusRuler = new ConsensusRuler(this);
		// Now we can update model wiith char sizes
		viewModel.setNewView(this, charWidth, charHeight, null);
		logger.info("Done init AliPane");
	}

	public long getEndTime(){
		return endTime;
	}

	public boolean isOnlyDrawDiff() {
		return highlightDiffTrace;
	}

	public void setHighlightDiffTrace(boolean highlightDiff) {
		this.highlightDiffTrace = highlightDiff;
	}

	public void setHighlightNonCons(boolean b) {
		this.highlightNonCons = b;
	}

	public boolean isHighlightNonCons() {
		return highlightNonCons;
	}

	public void setHighlightCons(boolean b) {
		this.highlightCons = b;
	}

	public boolean isHighlightCons() {
		return highlightCons;
	}

	public void setDrawCodonPosOnRuler(boolean drawCodonPosOnRuler) {
		this.drawCodonPosOnRuler = drawCodonPosOnRuler;
	}

	public boolean getDrawCodonPosOnRuler() {
		return this.drawCodonPosOnRuler;
	}

	public void setShowCharsetRuler(boolean selected) {
		charsetRuler.setVisible(selected);
	}

	public ColorScheme getColorSchemeNucleotide() {
		return colorSchemeNucleotide;
	}

	public boolean decCharSize(){

		// stop when everything is in view (or char is 1 for smaller alignments)
		//		Dimension prefSize = getPreferredSize();	

		// go on decreasing while everything is not in view or while font size >=1
		boolean didDecrease = false;
		if(this.getSize().width > this.getVisibleRect().width || this.getSize().height > this.getVisibleRect().height || charWidth >=1){

			double preferredWidth = charWidth;
			double preferredHeight = charHeight;

			if(charWidth > 1){
				// a little bit faster above char 18
				if(charWidth >= 18){
					preferredWidth = (int) (charWidth - CHAR_ZOOM_CHANGE_RATIO*charWidth); // +1
				}else{
					preferredWidth = charWidth - 1;
				}


				preferredHeight = (int)(preferredWidth*getCharHeightRatio());// 1.2 * charWidth;
			}
			else{

				if(charWidth == 1){
					smallCharsSizeNumber = 1;
				}else{
					smallCharsSizeNumber ++;
				}

				preferredWidth = Math.pow(0.85, smallCharsSizeNumber);
				preferredHeight = preferredWidth;
			}

			if(preferredWidth >= MIN_CHAR_SIZE){
				charWidth = preferredWidth;
				charHeight = preferredHeight;
			}
			//baseFont = new Font(baseFont.getName(), baseFont.getStyle(), (int)charWidth);

			createAdjustedDerivedBaseFont();
			createAdjustedDerivedHighDPIFont();
			createCharPixelsContainers();
			//	logFontMetrics();
			this.validateSize();	
			didDecrease = true;
		}



		return didDecrease;

	}

	double getCharHeightRatio() {
		return CHAR_HEIGHT_RATIO;
	}

	public void setCharWidth(double newCharWidth){
		charWidth = newCharWidth;

		if(charWidth >= 1){
			charHeight = (int)(charWidth*getCharHeightRatio());
		}else{
			smallCharsSizeNumber --;
			if(smallCharsSizeNumber <= 0){
				charWidth = 1;
			}
			else{
				charWidth = Math.pow(0.85, smallCharsSizeNumber);
			}
			charHeight = charWidth; // +1	
		}

		if(charWidth > MAX_CHAR_SIZE){
			charWidth = MAX_CHAR_SIZE;
			charHeight = (int)(charWidth*getCharHeightRatio());
		}

		logger.debug("charWidth" + charWidth);
		logger.debug("charHeight" + charHeight);

		createAdjustedDerivedBaseFont();
		createAdjustedDerivedHighDPIFont();
		createCharPixelsContainers();
		//		logFontMetrics();
		//this.validateSize();

	}

	public void incCharSize(){
		if(charWidth >= 1){
			// a little bit faster above char 16
			if(charWidth >= 16){
				charWidth = (int) (charWidth + CHAR_ZOOM_CHANGE_RATIO*charWidth); // +1
			}
			else{
				charWidth = (int) charWidth + 1; // +1
			}
			charHeight = (int)(charWidth*getCharHeightRatio());
		}else{
			smallCharsSizeNumber --;
			if(smallCharsSizeNumber <= 0){
				charWidth = 1;
			}
			else{
				charWidth = Math.pow(0.85, smallCharsSizeNumber);
			}

			charHeight = charWidth; // +1	
		}
		if(charWidth > MAX_CHAR_SIZE){
			charWidth = MAX_CHAR_SIZE;
			charHeight = (int)(charWidth*getCharHeightRatio());
		}

		logger.debug("charWidth" + charWidth);
		logger.debug("charHeight" + charHeight);

		createAdjustedDerivedBaseFont();
		createAdjustedDerivedHighDPIFont();
		createCharPixelsContainers();
		//		logFontMetrics();
		this.validateSize();
	}


	private void createCharPixelsContainers(){

		long startTime = System.currentTimeMillis();

		Font charFont = highDPIFont;
		// no less than 1
		int charPixWidth = Math.max(1, (int)(getCharWidth()));
		charPixWidth = charPixWidth * highDPIScaleFactor;
		// no less than 1
		int charPixHeight = Math.max(1, (int)(getCharHeight()));
		charPixHeight = charPixHeight * highDPIScaleFactor;

		int charMaxSizeToDraw = (int)MAX_CHARSIZE_TO_DRAW * highDPIScaleFactor;

		logger.info("charFont" + charFont.getSize());
		logger.info("charPixWidth" + charPixWidth);


		// Nucleotides

		charPixDefaultNuc = CharPixelsContainer.createDefaultNucleotideContainer(charFont, charMaxSizeToDraw, charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());
		charPixSelectedNuc = CharPixelsContainer.createSelectedNucleotideContainer(charFont, charMaxSizeToDraw, charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());
		charPixConsensusNuc = CharPixelsContainer.createConsensusNucleotideContainer(charFont, charMaxSizeToDraw, charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());
		charPixNonConsensusNuc = CharPixelsContainer.createNonConsensusNucleotideContainer(charFont, charMaxSizeToDraw, charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());
		charPixQualityNuc = CharPixelsContainerNucQuality.createQualityNucleotideContainer(charFont, charMaxSizeToDraw, charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());
		charPixQualClipNuc = CharPixelsContainer.createQualityClippedNucleotideContainer(charFont, charMaxSizeToDraw, charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		// Translated

		charPixTranslationDefault = CharPixelsContainerTranslation.createDefaultTranslationPixelsContainer(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationSelected = CharPixelsContainerTranslation.createSelectedTranslationPixelsContainer(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationLetter = CharPixelsContainerTranslation.createLetterTranslationPixelsContainer(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationSelectedLetter = CharPixelsContainerTranslation.createSelectedLetterTranslationPixelsContainer(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());



		// Translated and nuc at same time

		charPixTranslationAndNucDefault = CharPixelsContainerTranslation.createDefaultTranslationAndNucPixelsContainer(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationAndNucDefaultNoAALetter = CharPixelsContainerTranslation.createDefaultTranslationAndNucPixelsContainerNoAALetter(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationAndNucSelected = CharPixelsContainerTranslation.createSelectedTranslationAndNucPixelsContainer(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationAndNucSelectedNoAALetter = CharPixelsContainerTranslation.createSelectedTranslationAndNucPixelsContainerNoAALetter(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationAndNucDominantNuc = CharPixelsContainerTranslation.createDominantNucTranslationAndNucPixelsContainer(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationAndNucDominantNucNoAALetter = CharPixelsContainerTranslation.createDominantNucTranslationAndNucPixelsContainerNoAALetter(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationAndNucDominantNucSelected = CharPixelsContainerTranslation.createSelectedDominantNucTranslationAndNucPixelsContainer(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationAndNucDominantNucNoAALetterSelected = CharPixelsContainerTranslation.createSelectedDominantNucTranslationAndNucPixelsContainerNoAALetter(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());

		charPixTranslationQualClip = CharPixelsContainerTranslation.createTranslationQualClip(charFont, charMaxSizeToDraw,
				charPixWidth, charPixHeight, colorSchemeNucleotide, getFontCase());


		// AminoAcid
		charPixDefaultAA = CharPixelsContainerAA.createDefaultAAContainer(charFont, charMaxSizeToDraw, charPixWidth, charPixHeight, colorSchemeAminoAcid, getFontCase());

		charPixSelectedAA = CharPixelsContainerAA.createSelectedAAContainer(charFont, charMaxSizeToDraw, charPixWidth, charPixHeight, colorSchemeAminoAcid, getFontCase());

		charPixConsensusAA = CharPixelsContainerAA.createConsensusAAContainer(charFont, charMaxSizeToDraw, charPixWidth, charPixHeight, colorSchemeAminoAcid, getFontCase());

		endTime = System.currentTimeMillis();
		logger.info("Creating charPixContainers took " + (endTime - startTime) + " milliseconds");

	}

	private int getFontCase() {
		return fontCase;
	}

	@Override
	public Font getFont() {
		return baseFont;
	}

	private void createAdjustedDerivedBaseFont() {
		Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();

		// create a font without Tracking to see the diff in font actual size and specified font size
		double fontWidth = charWidth;
		attributes.put(TextAttribute.TRACKING, 0);
		attributes.put(TextAttribute.SIZE, (int)fontWidth);
		Font calcFont = baseFont.deriveFont(attributes);
		FontMetrics metrics = getFontMetrics(calcFont);
		int fontActualWidth = metrics.stringWidth("X");

		double sizeDiff = fontWidth - fontActualWidth;
		// Calculate tracking for font size
		double tracking = (double)sizeDiff/fontWidth;
		logger.info("tracking" + tracking);

		// Create a font with correct tracking so characters are exactly spaced as pixels on pane
		attributes.put(TextAttribute.TRACKING, tracking); // 8
		attributes.put(TextAttribute.SIZE, (int)fontWidth);
		Font spacedFont = baseFont.deriveFont(attributes);

		baseFont = spacedFont;

	}

	private void createAdjustedDerivedHighDPIFont() {
		Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();

		// create a font without Tracking to see the diff in font actual size and specified font size
		attributes.put(TextAttribute.TRACKING, 0);
		attributes.put(TextAttribute.SIZE, (int)charWidth*highDPIScaleFactor);
		Font calcFont = baseFont.deriveFont(attributes);
		FontMetrics metrics = getFontMetrics(calcFont);
		int fontActualWidth = metrics.stringWidth("X");

		double sizeDiff = charWidth*highDPIScaleFactor - fontActualWidth;
		// Calculate tracking for font size
		double tracking = (double)sizeDiff/charWidth*highDPIScaleFactor;
		logger.info("tracking" + tracking);

		// Create a font with correct tracking so characters are exactly spaced as pixels on pane
		attributes.put(TextAttribute.TRACKING, tracking); // 8
		attributes.put(TextAttribute.SIZE, (int)charWidth*highDPIScaleFactor);
		Font spacedFont = baseFont.deriveFont(attributes);

		highDPIFont = spacedFont;

	}

	private void logFontMetrics(Font font){
		FontMetrics metrics = this.getGraphics().getFontMetrics(font);

		logger.info("font.getSize()" + font.getSize());
		logger.info("font.getSize2D()" + font.getSize2D());

		// get the height of a line of text in this
		// font and render context	logger.info("baseFont.getSize()" + baseFont.getSize());
		logger.info("font.getSize2D()" + font.getSize2D());

		int hgt = metrics.getHeight();
		logger.info("metrics.getHeight()" + metrics.getHeight());
		logger.info("metrics.getMaxAdvance()" + metrics.getMaxAdvance());	// get the advance of my text in this font
		logger.info("metrics.getLeading()" + metrics.getLeading());

		int adv = metrics.stringWidth("A");


		logger.info("metrics.stringWidth(\"A\")" + metrics.stringWidth("AAAAAAAAAA"));
		logger.info("metrics.stringWidth(\"T\")" + metrics.stringWidth("T"));
		logger.info("metrics.stringWidth(\"c\")" + metrics.stringWidth("c"));
		logger.info("font.getAttributes().get(WIDTH_REGULAR)" + font.getAttributes().get(TextAttribute.WIDTH_REGULAR));

	}

	// should throw no valid base error
	public Point getBasePosition(Base base){
		if(base == null){
			return null;
		}
		int x = (int) (base.getPosition() * charWidth);
		int y = (int) (alignment.getSequenceIndex(base.getSequence()) * charHeight);

		Point pos = new Point(x,y);

		return pos;
	}

	public Base selectBaseAt(Point pos) throws InvalidAlignmentPositionException{

		Base base = null;

		base = getBaseAt(pos);
		if(base != null){

			base.getPosition();
			base.getSequence();
			alignment.getSequenceIndex(base.getSequence());
			alignment.setSelectionAt(base.getPosition(), alignment.getSequenceIndex(base.getSequence()),true);
		}

		return base;
	}

	public int getUngapedPositionInSequenceAt(Point pos) throws InvalidAlignmentPositionException{
		int ungapedPos = 0;

		Base base = getBaseAt(pos);
		if(base != null){
			ungapedPos = base.getUngapedPosition();
		}
		else{

		}

		return ungapedPos;
	}

	public int getPositionInSequenceAt(Point pos) throws InvalidAlignmentPositionException{

		int xPos = 0;
		Base base = getBaseAt(pos);
		if(base != null){
			xPos = base.getPosition();
		}

		return xPos;
	}



	public void selectColumnAt(Point pos) {
		int columnIndex = getColumnAt(pos);
		getAlignment().selectColumn(columnIndex);
	}


	public Base getBaseAt(Point pos) throws InvalidAlignmentPositionException{

		Point matrixPoint = paneCoordToMatrixCoord(pos);

		Base base = null;
		if(alignment.isPositionValid(matrixPoint.x,matrixPoint.y)){
			Sequence seq = (Sequence) alignment.getSequences().get(matrixPoint.y);
			base = new Base(seq, matrixPoint.x);
		}
		else{
			base = null;
		}
		return base;
	}




	public Base getClosestBaseAt(Point pos){

		Point matrixPoint = paneCoordToMatrixCoord(pos);

		Base base = null;
		if(alignment.isPositionValid(matrixPoint.x,matrixPoint.y)){
			Sequence seq = (Sequence) alignment.getSequences().get(matrixPoint.y);
			base = new Base(seq, matrixPoint.x);
		}
		else{
			// get last sequence
			Sequence seq = (Sequence) alignment.getSequences().get(alignment.getSequences().getSize()-1);
			base = new Base(seq, matrixPoint.x);
		}

		return base;
	}


	public int getColumnAt(Point pos){

		Point matrixPoint = paneCoordToMatrixCoord(pos);

		return matrixPoint.x;

	}


	public void setAlignment(Alignment alignment){
		this.alignment = alignment;
		//		this.infoLabel.setAlignment(alignment);
		this.validateSize();
	}


	public void repaintAndForceRuler(){
		rulerIsDirty = true;
		repaint();
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		paintAlignment(g);
	}


	public void paintAlignment(Graphics g){
		drawCounter ++;
		long startTime = System.currentTimeMillis();	
		if(Assseq.isDebugMode() && drawCounter % DRAWCOUNT_LOF_INTERVAL == 0){
			logger.info("Inside paintAlignment: Time from last endTim " + (startTime - endTime) + " milliseconds");
			System.out.println("Inside paintAlignment: Time from last endTim " + (startTime - endTime) + " milliseconds");
		}

		Graphics2D g2d = (Graphics2D) g;

		// What part of alignment matrix is in view (what part of matrix is in graphical view)
		Rectangle clip = g2d.getClipBounds();


		Rectangle matrixClip = paneCoordToMatrixCoord(clip);

		//		 logger.info(matrixClip);

		int xMin = matrixClip.x - 1;
		int yMin = matrixClip.y - 1;
		int xMax = (int) matrixClip.getMaxX() + 1;
		int yMax = (int) matrixClip.getMaxY() + 1;
		//
		//				logger.info("yMin" + yMin);
		//				logger.info("yMin" + yMax);
		//				logger.info("xMin" + xMin);
		//				logger.info("xMax" + xMax);
		//
		// add one extra position when drawing translated
		// otherwise there could be some white borders when scrolling
		if(showTranslation){
			xMin --;
			xMax ++;
		}

		// adjust for part of matrix that exists
		xMin = Math.min(alignment.getMaxX(), xMin);
		xMin = Math.max(0, xMin);

		yMin = Math.min(alignment.getMaxY(), yMin);
		yMin = Math.max(0, yMin);

		xMax = Math.min(alignment.getMaxX(), xMax);
		yMax = Math.min(alignment.getMaxY(), yMax);

		//				logger.info("yMin" + yMin);
		//				logger.info("yMax" + yMax);
		//				logger.info("xMin" + xMin);
		//				logger.info("xMax" + xMax);

		// Extra because pixelCopyDraw
		int height = (yMax - yMin) * (int)charHeight;
		int width = (xMax - xMin) * (int)charWidth;
		//
		//				logger.info("width" + width);
		//				logger.info("height" + height);
		//
		// Small chars
		if(charWidth < 1){
			height = clip.height;
			width = clip.width;
		}
		//
		//				logger.info("yMax" + yMax);
		//				logger.info("yMin" + yMin);
		//				logger.info("width" + width);
		//				logger.info("clipHeight" + clip.height);
		//				logger.info("width" + width);
		//				logger.info("height" + height);


		// TODO adjust for retina

		int[] pixArray = new int[width* highDPIScaleFactor * height * highDPIScaleFactor];
		//	logger.info(pixArray.length);
		RGBArray clipRGB = new RGBArray(pixArray, width*highDPIScaleFactor, height*highDPIScaleFactor);

		// HERE FILL RGB-ARRAY DRAW...
		//		fillRGBArrayAndPaint(xMin, xMax, yMin, yMax, clipRGB, clip, g2d);
		fillRGBArrayAndPaintMultithreaded(xMin, xMax, yMin, yMax, clipRGB, clip, g2d);

		if(drawCounter % DRAWCOUNT_LOF_INTERVAL == 0){
			endTime = System.currentTimeMillis();
			logger.info("Alignment pane PaintComponent took " + (endTime - startTime) + " milliseconds");
		}

		// repaint ruler also if needed
		if(clip.x != lastClip.x || clip.width != lastClip.width || rulerIsDirty){
			alignmentRuler.repaint();
			charsetRuler.repaint();
			consensusRuler.repaint();
			rulerIsDirty = false;
		}

		lastClip = clip;

	}

	private void fillRGBArrayAndPaintMultithreaded(int xMin, int xMax, int yMin, int yMax, RGBArray clipRGB, Rectangle clip, Graphics2D g2d){
		// these vals are not going to change so get it only once
		boolean isNucleotideAlignment = alignment.isNucleotideAlignment();
		double seqPerPixX = 1/(double)charWidth;
		double seqPerPixY = 1/(double)charWidth;

		logger.info("Runtime.getRuntime().availableProcessors()" + Runtime.getRuntime().availableProcessors());
		int nThreads = 1;

		// Only one thread if filesequences - more threads make reading file slower
		if(alignment.isFileSequences()){
			nThreads = 1;
		}else{
			if(Runtime.getRuntime().availableProcessors() > 2){
				nThreads = 2;
			}
			if(Runtime.getRuntime().availableProcessors() > 3){
				nThreads = 3;
			}
		}


		//
		//
		// Small chars have their own loop here
		//
		//
		if(charWidth < 1){
			ExecutorService executor = Executors.newFixedThreadPool(nThreads);

			int clipYPos = 0;
			for(int y = clip.y; y < clip.getMaxY(); y ++){

				int ySeq = (int)((double)(y) * seqPerPixY);

				if(ySeq <= yMax && ySeq >= 0){

					int seqYPos = ySeq;
					Sequence seq = alignment.getSequences().get(seqYPos);
					int xPosStart =  clip.x;
					int xPosEnd =  (int) clip.getMaxX();	

					if(isNucleotideAlignment){
						if(isShowTranslationOnePos()){							
							SequencePainter seqPainter = new SequencePainterAminoAcid(seq, seqYPos, clipYPos, xPosStart, xPosEnd, seqPerPixX, 1, 1, highDPIScaleFactor, clipRGB, this, alignment);	
							executor.execute(seqPainter);

						}else if(showTranslation && !isShowTranslationOnePos() && ignoreGapInTranslation){
							SequencePainter seqPainter = new SequencePainterAminoAcidTranslatedIgnoreGap(seq, seqYPos, clipYPos, xPosStart, xPosEnd, seqPerPixX, 1, 1, highDPIScaleFactor, clipRGB, this, alignment);	
							executor.execute(seqPainter);

						}else if(showTranslation){
							if(showTranslationAndNuc){
								SequencePainter seqPainter = new SequencePainterNucleotideTranslatedShowNucAndAcid(seq, seqYPos, clipYPos, xPosStart, xPosEnd, seqPerPixX, 1, 1, highDPIScaleFactor, clipRGB, this, alignment);	
								executor.execute(seqPainter);

							}else{
								SequencePainter seqPainter = new SequencePainterAminoAcidTranslated(seq, seqYPos, clipYPos, xPosStart, xPosEnd, seqPerPixX, 1, 1, highDPIScaleFactor, clipRGB, this, alignment);	
								executor.execute(seqPainter);
							}

						}else{
							SequencePainter seqPainter = new SequencePainterNucleotide(seq, seqYPos, clipYPos, xPosStart, xPosEnd, seqPerPixX, 1, 1, highDPIScaleFactor, clipRGB, this, alignment);	
							executor.execute(seqPainter);
						}
					}

					// Else draw as AminoAcids
					//
					else{
						SequencePainter seqPainter = new SequencePainterAminoAcid(seq, seqYPos, clipYPos, xPosStart, xPosEnd, seqPerPixX, 1, 1, highDPIScaleFactor, clipRGB, this, alignment);	
						executor.execute(seqPainter);
					}


				}
				else{
					logger.info("outside");
				}

				clipYPos ++;
			}


			executor.shutdown();
			try {
				executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}	


		/////////////////////////
		//
		// Normal char width
		//
		/////////////////////////
		else{


			ExecutorService executor = Executors.newFixedThreadPool(nThreads);

			int clipYPos = 0;
			// Loop rows (The sequence painter is painting a row in its own thread)
			for(int y = yMin; y < yMax; y = y + 1){

				int seqYPos = y;
				Sequence seq = alignment.getSequences().get(seqYPos);
				int normalCharSeqPerPix = 1;
				int xPosStart = xMin;
				int xPosEnd = xMax;


				if(isNucleotideAlignment){
					if(isShowTranslationOnePos()){							
						SequencePainter seqPainter = new SequencePainterAminoAcid(seq, seqYPos, clipYPos, xPosStart, xPosEnd, normalCharSeqPerPix, charWidth, charHeight, highDPIScaleFactor, clipRGB, this, alignment);	
						executor.execute(seqPainter);

					}else if(showTranslation && !isShowTranslationOnePos() && ignoreGapInTranslation){
						SequencePainter seqPainter = new SequencePainterAminoAcidTranslatedIgnoreGap(seq, seqYPos, clipYPos, xPosStart, xPosEnd, normalCharSeqPerPix, charWidth, charHeight, highDPIScaleFactor, clipRGB, this, alignment);	
						executor.execute(seqPainter);

					}else if(showTranslation){
						if(showTranslationAndNuc){
							SequencePainter seqPainter = new SequencePainterNucleotideTranslatedShowNucAndAcid(seq, seqYPos, clipYPos, xPosStart, xPosEnd, normalCharSeqPerPix, charWidth, charHeight, highDPIScaleFactor, clipRGB, this, alignment);	
							executor.execute(seqPainter);

						}else{
							SequencePainter seqPainter = new SequencePainterAminoAcidTranslated(seq, seqYPos, clipYPos, xPosStart, xPosEnd, normalCharSeqPerPix, charWidth, charHeight, highDPIScaleFactor, clipRGB, this, alignment);	
							executor.execute(seqPainter);
						}

						// Normal nucleotide
					}else{
						SequencePainter seqPainter = new SequencePainterNucleotide(seq, seqYPos, clipYPos, xPosStart, xPosEnd, normalCharSeqPerPix, charWidth, charHeight, highDPIScaleFactor, clipRGB, this, alignment);	
						executor.execute(seqPainter);
					}
				}
				// Draw as AminoAcids
				else{
					SequencePainter seqPainter = new SequencePainterAminoAcid(seq, seqYPos, clipYPos, xPosStart, xPosEnd, normalCharSeqPerPix, charWidth, charHeight, highDPIScaleFactor, clipRGB, this, alignment);	
					executor.execute(seqPainter);
				}

				clipYPos ++;
			}

			executor.shutdown();
			try {
				executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//
		//		// Draw Excludes by manipulating pixelColor			
		//		if(! isShowTranslationOnePos()){
		//			// Two versions depending on if it is small chars or not
		//			if(charWidth < 1){
		//				for(int x = clip.x; x < clip.getMaxX() ; x++){
		//					int xPos =(int)((double)x * (1/(double)charWidth));
		//					if(alignment.isExcluded(xPos) == true){	
		//						logger.info("is excl");
		//						ImageUtils.darkerRGBArrayColumn(clipRGB, x);
		//					}
		//				}
		//			}else{
		//				for(int x = xMin; x < xMax ; x++){
		//					if(alignment.isExcluded(x) == true){
		//
		//						for(int col = x; col < charWidth; col++){
		//							logger.info("is excl");
		//							ImageUtils.darkerRGBArrayColumn(clipRGB, col);
		//						}
		//
		//					}
		//				}
		//			}
		//		}


		// Now draw the pixels onto the image
		Image img = createImage(new MemoryImageSource(clipRGB.getScanWidth(), clipRGB.getHeight(), clipRGB.getBackend(), 0, clipRGB.getScanWidth()));
		// First fill background
		g2d.setColor(this.getBackground());
		g2d.fill(clip);



		int clipRGBXPos = clip.x;
		int clipRGBYPos = clip.y;
		// Adjust because we start always on exact char upp to one pos before
		if(charWidth > 1){
			clipRGBXPos = (int)(xMin * charWidth);
			clipRGBYPos = (int)(yMin * charHeight);
		}

		if (img != null){	
			// Mac retina screen
			if(highDPIScaleFactor > 1){
				int dx1 = clipRGBXPos;
				int dx2 = dx1 + clipRGB.getScanWidth() / highDPIScaleFactor;
				int dy1 = clipRGBYPos;
				int dy2 = dy1 + clipRGB.getHeight() / highDPIScaleFactor;

				int sx1 = 0;
				int sx2 = sx1 + clipRGB.getScanWidth();
				int sy1 = 0;
				int sy2 = sy1 + clipRGB.getHeight();
				g2d.drawImage(img,dx1,dy1,dx2,dy2,sx1,sy1,sx2,sy2, null);
			}else{
				g2d.drawImage(img, clipRGBXPos, clipRGBYPos, null);
			}
		}


		// Draw excludes	
		if(isShowTranslationOnePos()){

			// calculate height for excludes (this is to avoid drawing below alignment if alignment is not filling panel)
			int drawExcludesHeight = (int) Math.min(this.getVisibleRect().getHeight(), alignment.getSize()  * charHeight);

			// Two versions depending on if it is small chars or not
			if(charWidth < 1){
				for(int x = clip.x; x < clip.getMaxX() ; x++){
					int xPos =(int)((double)x * (1/(double)charWidth));
					if(alignment.isExcluded(xPos) == true){
						g2d.setColor(ColorScheme.GREY_TRANSPARENT);
						g2d.fillRect(x, this.getVisibleRect().y, 1, drawExcludesHeight);
						//				logger.info("drawExclude");
					}
				}
			}else{
				for(int x = xMin; x < xMax ; x++){
					if(alignment.isExcluded(x) == true){
						g2d.setColor(ColorScheme.GREY_TRANSPARENT);
						g2d.fillRect((int)(x * charWidth), this.getVisibleRect().y, (int)charWidth, drawExcludesHeight);				
					}
				}
			}
		}
		else{
			// calculate height for excludes (this is to avoid drawing below alignment if alignment is not filling panel)
			int drawExcludesHeight = (int) Math.min(this.getVisibleRect().getHeight(), alignment.getSize()  * charHeight);

			// Two versions depending on if it is small chars or not
			if(charWidth < 1){
				for(int x = clip.x; x < clip.getMaxX() ; x++){
					int xPos =(int)((double)x * (1/(double)charWidth));


					if(alignment.isExcluded(xPos) == true){
						g2d.setColor(ColorScheme.GREY_TRANSPARENT);
						g2d.fillRect(x, this.getVisibleRect().y, 1, drawExcludesHeight);
						//				logger.info("drawExclude");
					}
				}
			}else{
				for(int x = xMin; x < xMax ; x++){
					if(alignment.isExcluded(x) == true){
						g2d.setColor(ColorScheme.GREY_TRANSPARENT);
						g2d.fillRect((int)(x * charWidth), this.getVisibleRect().y, (int)charWidth, drawExcludesHeight);				
					}
				}
			}


		}

		logger.info("done");


	}


	public Alignment getAlignment() {
		return alignment;
	}

	public void validateSequenceOrder(){
		// verify that tracing sequence not is out of index
		if(differenceTraceSequencePosition >= alignment.getSize()){
			differenceTraceSequencePosition = 0;
		}
	}

	public int selectWithin(Rectangle rect){
		// First clear
		int selectionSize = addSelectionWithin(rect);
		return selectionSize;
	}

	public int selectColumnsWithin(Rectangle rect) {
		// First clear
		int selectionSize = addColumnSelectionWithin(rect);
		return selectionSize;
	}


	public int addColumnSelectionWithin(Rectangle rect){
		int nSelection = 0;

		// grow so all sequences are included
		Rectangle columns = new Rectangle(rect.x, 0, rect.width, this.getHeight());
		return addSelectionWithin(columns);
	}

	public int addSelectionWithin(Rectangle rect){
		int nSelection = 0;
		// calculate what part of alignment matrix is in view (what part of matrix is in graphical view)
		Rectangle bounds = paneCoordToMatrixCoord(rect);

		alignment.setSelectionWithin(bounds);

		return nSelection;
	}

	/*
	private Rectangle getTempSelection() {
		return alignment.getTempSelection();
	}
	 */

	/*
	public void clearTempSelection() {
		this.tempSelectionRect = null;
	}

	 */

	public Rectangle paneCoordToMatrixCoord(Rectangle rect){

		// TODO maybe problem when calculating a 0-width rect - then it will give eg. xmin=34 xmax=35

		//				logger.info("rect.getMinX()" + rect.getMinX());
		//				logger.info("rect.getMaxX()" + rect.getMaxX());
		//				logger.info("rect.getMinX()/charWidth" + rect.getMinX()/charWidth);

		int matrixMinX = (int) Math.floor(rect.getMinX()/charWidth); // always round down
		int matrixMaxX = (int) Math.floor(rect.getMaxX()/charWidth); // always round up
		int matrixMinY = (int) Math.floor(rect.getMinY()/charHeight); // always round down
		int matrixMaxY = (int) Math.floor(rect.getMaxY()/charHeight); // always round down

		// also set min to 0
		matrixMinX = Math.max(0, matrixMinX);
		matrixMaxX = Math.max(0, matrixMaxX);
		matrixMinY = Math.max(0, matrixMinY);
		matrixMaxY = Math.max(0, matrixMaxY);
		//
		//				logger.info("matrixMinX" + matrixMinX);
		//				logger.info("matrixMaxX" + matrixMaxX);
		//	     	logger.info(getMatrixTopOffset());


		Rectangle converted = new Rectangle(matrixMinX, matrixMinY, matrixMaxX - matrixMinX, matrixMaxY - matrixMinY); 
		//		logger.info("converted" + converted);
		return converted;
	}

	// todo this should be listening to changes in alignmnet instead
	public void updateStatisticsLabel(){
		//		logger.info("unimplemented should be done by changelistener");
	}

	public void validateSize() {
		// Set component preferred size
		Dimension current = getSize();
		Dimension prefSize = getCalculatedPreferredSize();
		//	Rectangle prefRect = this.getVisibleRect();

		if(current.width != prefSize.width || current.height != prefSize.height){
			this.setPreferredSize(prefSize);
			//this.updateStatisticsLabel();
			this.rulerIsDirty = true;
			this.revalidate();
		}
	}

	@Override
	public void setSize(Dimension d) {
		super.setSize(d);
	}

	@Override
	public Dimension getPreferredSize() {
		return getCalculatedPreferredSize();
	}

	private Dimension getCalculatedPreferredSize(){
		Dimension newDim;

		//		logger.info("charWidth" + charWidth);
		//		logger.info("charHeight" + charHeight);

		//		if(showTranslationOnePos){
		//			newDim = new Dimension((int) (charWidth * alignment.getAlignentMeta().getCodonPositions().getLengthOfTranslatedPos()), (int)(charHeight * alignment.getSize()));
		//		}else{
		newDim = new Dimension((int) (charWidth * alignment.getMaximumSequenceLength()), (int)(charHeight * alignment.getSize()));
		//		}		
		//		logger.info("newDim" + newDim);

		if(newDim.width == Integer.MAX_VALUE || newDim.height == Integer.MAX_VALUE){
			Messenger.showMaxJPanelSizeMessageOnceThisSession();
			//			logger.info("Hit max jpanel length");
		}
		return newDim;

	}

	public Point paneCoordToMatrixCoord(Point pos){

		int matrixX = (int) Math.floor(pos.getX() / charWidth);
		int matrixY = (int) Math.floor(pos.getY() /  charHeight);
		Point converted = new Point(matrixX, matrixY);	
		return converted;
	}

	public Point matrixCoordToPaneCoord(Point pos){
		int paneX = (int) (pos.getX() * charWidth);
		int paneY = (int) (pos.getY() * charHeight);
		Point converted = new Point(paneX, paneY);
		return converted;
	}


	public Rectangle matrixCoordToPaneCoord(Rectangle rect){
		Point min = new Point((int)rect.getMinX(), (int)rect.getMinY());
		//		logger.info("min" + min);
		Point max = new Point((int)rect.getMaxX(), (int)rect.getMaxY());
		//		logger.info("max" + max);
		Rectangle converted = new Rectangle(matrixCoordToPaneCoord(min));
		converted.add(matrixCoordToPaneCoord(max));
		//		logger.info("converted" + converted);
		return converted;
	}

	public boolean isPointWithinMatrix(Point pos) {
		Point matrixPoint = paneCoordToMatrixCoord(pos);
		return alignment.isPositionValid(matrixPoint.x, matrixPoint.y);
	}


	public double getCharHeight() {
		return this.charHeight;
	}

	public double getCharWidth() {
		return this.charWidth;
	}


	public void setDifferenceTraceSequence(Point pos) throws InvalidAlignmentPositionException {
		Point matrixPoint = paneCoordToMatrixCoord(pos);
		Sequence seq = null;
		if(alignment.isPositionValid(matrixPoint.x,matrixPoint.y)){
			// todo this should be changed because problem when removed or moved
			this.differenceTraceSequencePosition = matrixPoint.y;
		}
		else{
			throw new InvalidAlignmentPositionException("Position is out of range" + pos);
		}
	}

	public void setDifferenceTraceSequence(int nIndex){
		this.differenceTraceSequencePosition = nIndex;
	}

	public Sequence getSequenceAt(Point pos) throws InvalidAlignmentPositionException {
		Point matrixPoint = paneCoordToMatrixCoord(pos);
		Sequence seq = null;
		if(alignment.isPositionValid(matrixPoint.x,matrixPoint.y)){
			seq = (Sequence) alignment.getSequences().get(matrixPoint.y);
		}
		else{
			throw new InvalidAlignmentPositionException("Position is out of range" + pos);
		}
		return seq;
	}

	public boolean isWithinExistingSelection(Point point) {
		boolean isSelected = false;

		try {
			Base base = getBaseAt(point);
			if(base != null){
				isSelected = base.isSelected();
			}
		} catch (InvalidAlignmentPositionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return isSelected;
	}

	public void setShowTranslation(boolean showTranslation){
		this.showTranslation = showTranslation;
	}

	public boolean isShowTranslation() {
		return showTranslation;
	}

	public boolean isShowTranslationOnePos() {
		return alignment.isTranslatedOnePos();
	}

	public JComponent getRulerComponent(){
		return this.alignmentRuler;
	}

	public JComponent getConsensusRulerComponent(){
		return this.consensusRuler;
	}

	public JComponent getCharsetRulerComponent(){
		return this.charsetRuler;
	}

	public void setDrawAminoAcidCode(boolean drawCode){
		this.drawAminoAcidCode = drawCode;
	}

	public boolean isDrawAminoAcidCode(){
		return this.drawAminoAcidCode;
	}

	public void setColorSchemeAminoAcid(ColorScheme aScheme){
		this.colorSchemeAminoAcid = aScheme;
		createCharPixelsContainers();

	}

	public void setColorSchemeNucleotide(ColorScheme aScheme) {
		this.colorSchemeNucleotide = aScheme;
		createCharPixelsContainers();
	}

	public Point getVisibleUpperLeftMatrixPos() {
		Rectangle rect = this.getVisibleRect();
		Point ulPanePos = rect.getLocation();
		Point ulMatrixPos = paneCoordToMatrixCoord(ulPanePos);
		return ulMatrixPos;
	}

	public Point getVisibleCenterMatrixPos() {
		Rectangle rect = this.getVisibleRect();
		Point centerPanePos = new Point((int)rect.getCenterX(),(int)rect.getCenterY());
		Point centerMatrixPos = paneCoordToMatrixCoord(centerPanePos);
		return centerMatrixPos;
	}

	public void scrollToVisibleUpperLeftMatrixPos(Point ulPos) {
		Point ulPanePos = matrixCoordToPaneCoord(ulPos);
		Rectangle rect = new Rectangle(ulPanePos, this.getVisibleRect().getSize());
		rect.grow(-10, -10);
		logger.info("ulPanePos" + ulPanePos);
		logger.info("currentVisibleRect " + this.getVisibleRect());
		logger.info("Scroll to rect" + rect);

		// TODO Maye make this better working
		// As a workaround first setLocation(0,0)
		// then scrollRectToVisible is working OK
		this.setLocation(0,0);
		this.scrollRectToVisible(rect);
		logger.info("after this.getVisibleRect()" + this.getVisibleRect());
	}

	public void scrollMatrixX(int offset) {
		int offsetPane = (int)(offset * charWidth);
		this.setLocation( getLocation().x + offsetPane, getLocation().y );
	}


	public void scrollRectToSelection() {
		Rectangle selectRect = alignment.getSelectionAsMinRect();
		if(selectRect != null){
			Rectangle grown1xtra = new Rectangle(selectRect.x - 1, selectRect.y - 1, selectRect.width + 3, selectRect.height + 3);
			Rectangle paneCoord = matrixCoordToPaneCoord(grown1xtra);
			if(! getVisibleRect().contains(selectRect)){
				logger.info("not visible");
				scrollRectToVisible(paneCoord);
			}
		}
	}

	public void scrollRectToSelectionCenter() {
		Rectangle selectRect = alignment.getSelectionAsMinRect();
		if(selectRect != null){
			Rectangle grown1xtra = new Rectangle(selectRect.x - 1, selectRect.y - 1, selectRect.width + 3, selectRect.height + 3);
			Rectangle paneCoord = matrixCoordToPaneCoord(grown1xtra);
			if(! getVisibleRect().contains(selectRect)){
				logger.info("not visible");
				scrollRectToVisible(paneCoord);
			}
		}
	}

	public void scrollToPos(Point matrixPos) {

		logger.info("newMatrixPos" + matrixPos);

		Point paneCoord = matrixCoordToPaneCoord(matrixPos);
		Rectangle newVisible = new Rectangle(paneCoord);
		// We need to grow to half the width and height to center it
		newVisible.grow(getVisibleRect().width/2,getVisibleRect().height/2);
		scrollRectToVisible(newVisible);
	}

	public boolean getIgnoreGapInTranslation(){	
		return ignoreGapInTranslation;
	}

	public void setIgnoreGapInTranslation(boolean ignoreGapInTranslation) {
		this.ignoreGapInTranslation = ignoreGapInTranslation;
	}

	public void setFontCase(int fontCase){
		this.fontCase = fontCase;
		createCharPixelsContainers();
	}

	public boolean getShowTranslationAndNuc() {
		return showTranslationAndNuc;
	}

	public void setShowTranslationAndNuc(boolean b) {
		showTranslationAndNuc = b;
	}

	public int getDifferenceTraceSequencePosition() {
		return differenceTraceSequencePosition;
	}

	public boolean isHighlightDiffTrace() {
		return highlightDiffTrace;
	}

	//
	// AlignmentSelectionListener
	//
	public void selectionChanged(AlignmentSelectionEvent e) {
		logger.info("selectionChanged");
		requestRepaintRect(e.getBounds());
	}

	public void requestRepaintRect(Rectangle rect) {

		int dx =(int) (this.getCharWidth() * 3);
		int dy =(int) (this.getCharHeight() * 1);
		// if small chars, redraw at least a few pix
		if(dx < 3 || dy < 1){
			dx = 6;
			dy = 2;
		}

		Rectangle paneBounds = this.matrixCoordToPaneCoord(rect);
		Rectangle grown = new Rectangle(paneBounds.x - dx, paneBounds.y - dy, paneBounds.getBounds().width + 2*dx, paneBounds.getBounds().height + 2*dy);

		this.validateSize();
		this.validateSequenceOrder();

		//alignmentPane.paintImmediately(paneBounds);
		//aliList.paintImmediately(aliList.getVisibleRect());
		//alignmentPane.scrollRectToVisible(paneBounds);
		//logger.info("paneBounds" + paneBounds);
		this.repaint(grown);

		//alignmentPane.repaint();
		//aliList.repaint();

	}


	class AlignmentRuler extends JPanel{

		private AlignmentPane alignmentPane;

		public AlignmentRuler(AlignmentPane alignmentPane) {
			this.alignmentPane = alignmentPane;
		}


		public void paintComponent(Graphics g){
			super.paintComponent(g);
			paintRuler(g);
		}

		public void paintRuler(Graphics g){

			long startTime = System.currentTimeMillis();

			//super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
					RenderingHints.VALUE_ANTIALIAS_OFF); 
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_SPEED);
			//			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
			//					RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
					RenderingHints.VALUE_DITHER_DISABLE);		

			//			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
			//					RenderingHints.VALUE_RENDER_QUALITY);
			//			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			//					RenderingHints.VALUE_ANTIALIAS_ON);
			//			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			//								RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			//			//g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
			//					RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			//g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
			//					RenderingHints.VALUE_COLOR_RENDER_SPEED);
			//g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
			//					RenderingHints.VALUE_DITHER_DISABLE);


			//		g2d.setFont(baseFont);

			// What part of alignment matrix is in view (what part of matrix is in graphical view)
			Rectangle paneClip = alignmentPane.getVisibleRect();
			Rectangle matrixClip = paneCoordToMatrixCoord(paneClip);

			// todo calculate from font metrics
			double charCenterXOffset = 0.9997;


			// NUMBERS
			int rulerCharWidth = 11;
			//int rulerCharHeight = 11;
			Font rulerFont = new Font(alignmentPane.getFont().getName(), alignmentPane.getFont().getStyle(), (int)rulerCharWidth);
			g2d.setFont(rulerFont);

			//
			// Draw ruler background
			//
			Rectangle rulerRect = new Rectangle(this.getVisibleRect());
			g2d.setColor(getBackground());
			g2d.fill(rulerRect);

			int offsetDueToScrollPanePosition = 0;

			// Normal char-with smaller 
			if(charWidth >= 1){

				offsetDueToScrollPanePosition = paneClip.x % (int)charWidth;
				offsetDueToScrollPanePosition = offsetDueToScrollPanePosition -1;

				// Tickmarks
				int posTick = 0;
				int count = 0;

				int maxY = alignment.getMaxY();
				int maxX = alignment.getMaxX();
				//				if(showTranslationOnePos){
				//					maxX = alignment.getAlignentMeta().getCodonPositions().getTranslatedAminAcidLength();
				//				}

				for(int x = matrixClip.x ; x < matrixClip.getMaxX() + 1; x++){

					// Only draw part of matrix that exists 
					if(maxY > 0 && x >= 0 && x < maxX){

						// draw codon-pos background on ruler depending on codonpos
						if(drawCodonPosOnRuler && ! isShowTranslationOnePos()){
							int codonPos = alignment.getCodonPosAt(x);
							//logger.info(codonPos);
							Color codonPosColor = Color.GREEN;
							if(codonPos == 0){
								codonPosColor = Color.LIGHT_GRAY;
							}else if(codonPos == 1){
								codonPosColor = Color.GREEN;
							}else if(codonPos == 2){
								codonPosColor = Color.orange;
							}else if(codonPos == 3){
								codonPosColor = Color.red;
							}

							g2d.setColor(codonPosColor);

							int boxHeight = 5;
							// we are drawing not on a large scrollable ruler, but a window sized fixed pane we have to adjust with offsetDueToScrollPanePosition
							g2d.fillRect((int)(posTick * charCenterXOffset * charWidth - offsetDueToScrollPanePosition), (int) (rulerRect.getMaxY() - boxHeight), (int)charWidth, boxHeight);
						}



						// draw tickmarks
						g2d.setColor(Color.DARK_GRAY);
						// make every 5 tickmarks a bit bigger
						if(x % 5 == 4 && charWidth > 0.6){ // it has to be 4 and not 0 due to the fact that 1:st base har position 0 in matrix
							// we are drawing not on a large scrollable ruler, but a window sized fixed pane we have to adjust with offsetDueToScrollPanePosition
							// since it is hidden in scrollpane 
							g2d.drawLine((int)(posTick * charCenterXOffset * charWidth + charWidth/2 - offsetDueToScrollPanePosition), (int) (rulerRect.getMaxY() - 2), (int)(posTick * charCenterXOffset * charWidth +  charWidth/2 - offsetDueToScrollPanePosition), (int)rulerRect.getMaxY() - 5);
						}
						// dont draw smallest tick if to small
						else if(charWidth > 4){
							// we are drawing not on a large scrollable ruler, but a window sized fixed pane we have to adjust with offsetDueToScrollPanePosition
							// since it is hidden in scrollpane 
							g2d.drawLine((int)(posTick * charCenterXOffset * charWidth + charWidth/2 - offsetDueToScrollPanePosition), (int) (rulerRect.getMaxY() - 2), (int)(posTick * charCenterXOffset * charWidth +  charWidth/2 - offsetDueToScrollPanePosition), (int)rulerRect.getMaxY() - 3);
						}


						// and numbers


						posTick ++;
					}
					count ++;
				}

				// NUMBERS

				// Only draw every xx pos
				int drawEveryNpos = 10;

				if(charWidth < 4){
					drawEveryNpos = 50;
				}else if(charWidth < 5){
					drawEveryNpos = 20;
				}

				// position numbers
				int lastTextEndPos = 0;
				int pos = 0;
				for(int x = matrixClip.x ; x < matrixClip.getMaxX() + 1; x++){

					if(x % drawEveryNpos == 0){
						String posText = Integer.toString(x);
						int stringSizeOffset = g2d.getFontMetrics().stringWidth(posText) / 2;
						//int stringSizeOffset = (int)((posText.length()*0.8 * rulerCharWidth) / 2) + 5;
						//int stringSizeOffset = ( posText.length()*(rulerFont.getSize()) ) / 2;
						//	int stringSizeOffset = (int)((posText.length()*0.8 * rulerCharWidth) / 2) + 5;
						int textPosX = (int)((pos -1) * charCenterXOffset * charWidth + charWidth/2 - offsetDueToScrollPanePosition) - stringSizeOffset;
						// dont draw on top of last (if number is very long)
						if(lastTextEndPos < textPosX){
							g2d.drawString(posText, textPosX, 10);
							lastTextEndPos = textPosX + stringSizeOffset + 40; // add 40 extra space between numbers
						}
					}
					pos ++;
				}	
			}
			// Less than one pix char size 
			else{



				double seqOffsetVisiblePanePos = matrixClip.getMinX() -1; //(double)paneClip.x / charWidth;


				// pos per pixel
				//	double posPerPix = 1/charWidth;

				double posPerPix = matrixClip.getWidth() / paneClip.getWidth();

				int xStep = 10;

				if(posPerPix < 2.5){
					xStep = 10;
				}
				else{	
					// This loop is the same as all the commented (else if) below
					// first set something if something in loop goes wrong...
					xStep = 100000000;
					for(int posPixRange = 5; posPixRange < Integer.MAX_VALUE; posPixRange = (int)(posPixRange * 2)){		
						if(posPerPix < posPixRange){
							xStep = posPixRange * 5;

							break;
						}
					}
				}


				/*
				else if(posPerPix < 5){
					xStep = 25;
				}
				else if(posPerPix < 10){
					xStep = 50;
				}
				else if(posPerPix < 20){
					xStep = 100;
				}
				else if(posPerPix < 40){
					xStep = 200;
				}
				else if(posPerPix < 80){
					xStep = 400;
				}
				else if(posPerPix < 160){
					xStep = 800;
				}
				else if(posPerPix < 320){
					xStep = 1600;
				}
				else if(posPerPix < 640){
					xStep = 3200;
				}
				else if(posPerPix < 1000){
					xStep = 5000;
				}
				else if(posPerPix < 2000){
					xStep = 10000;
				}
				else{
					xStep = 80000;
				}

				 */


				double startPosSeq = roundToClosestUpper((int)seqOffsetVisiblePanePos,xStep);
				int startPosPane = (int) (charWidth * startPosSeq);

				//				logger.info("ruler startPosSeq" + startPosSeq);
				//				logger.info("ruler startPosPane" + startPosPane);		
				//				logger.info("posPerPix" + posPerPix);


				int maxY = alignment.getMaxY();
				int maxX = alignment.getMaxX();
				//				if(showTranslationOnePos){
				//					maxX = alignment.getAlignentMeta().getCodonPositions().getTranslatedAminAcidLength();
				//				}

				int maxVisibleSeq = (int)matrixClip.getMaxX();
				logger.info("maxVisibleSeq" + maxVisibleSeq + 200);

				int lastTextEndPos = 0;

				// Tickmarks
				int countTicks = 0;

				// Same color for everything
				g2d.setColor(Color.DARK_GRAY);

				// X Loop Start
				for(int xSeq = (int)startPosSeq; xSeq < maxVisibleSeq; xSeq = xSeq + xStep){

					// get closest pane pos
					int xPane = (int)  ( (double) xSeq / posPerPix ); 
					//					
					//					logger.info("maxX" + maxX);
					//					logger.info("xPane" + xPane);

					// Only draw part of matrix that exists 
					if(maxY > 0 && xSeq >= 0 && xSeq < maxX){

						// no no codon-pos-ruler


						// we are drawing not on a large scrollable ruler, but a window sized fixed pane we have to adjust with pane.x
						// since it is hidden in scrollpane
						int tickPosX = (xPane - paneClip.x);

						// larger and text every 10-interval
						int tickSize;	
						int largerInterval = xStep * 10;

						if(xSeq % largerInterval == 0){																				
							String posText = Integer.toString(xSeq);

							int stringSizeOffset = g2d.getFontMetrics().stringWidth(posText) / 2;

							//				int stringSizeOffset = ( posText.length()*(rulerFont.getSize() -1) ) / 2;
							//int stringSizeOffset = (int)((posText.length() * (rulerCharWidth)) / 2) ;
							int textPosX = (int)(tickPosX - stringSizeOffset);
							// dont draw text outside
							if(textPosX >=0){
								// dont draw on top of last (if number is very long)
								if(lastTextEndPos < textPosX){
									g2d.drawString(posText, textPosX, 10);
									lastTextEndPos = textPosX + stringSizeOffset + 40; // add 40 extra space between numbers
								}
							}
							// larger tick size
							tickSize = 3;	
						}else{
							// smaller tick size
							tickSize = 1;
						}		

						// draw tick
						g2d.drawLine(tickPosX, (int) (rulerRect.getMaxY() - 2),tickPosX, (int)rulerRect.getMaxY() - 2 - tickSize);

						countTicks ++;
					}
				}

			} // end draw small char

			long endTime = System.currentTimeMillis();
			logger.info("Ruler PaintComponent took " + (endTime - startTime) + " milliseconds");


		}


		private int roundToClosestUpper(int inval, int roundTo) {
			// int rounded = ((num + 99) / 100 ) * 100;
			int rounded = ((inval + roundTo -1) / roundTo ) * roundTo;
			return rounded;
		}

	} // end Ruler class


	private class CharsetRuler extends JPanel{

		private AlignmentPane alignmentPane;
		private Color[] charsetColors = new Color[]{new Color(107,215,204), new Color(239,189,93), new Color(215,127,163),new Color(210,213,102), new Color(127,107,215),new Color(203,241,136)};

		public CharsetRuler(AlignmentPane alignmentPane) {
			this.alignmentPane = alignmentPane;
			// Add this component to ToolTipManager
			ToolTipManager.sharedInstance().registerComponent(this);
		}

		public void paintComponent(Graphics g){
			super.paintComponent(g);
			if(isVisible() && !isShowTranslationOnePos()){
				paintCharsetRuler(g);
			}
		}

		@Override
		public Dimension getPreferredSize(){
			logger.info("get pref size");
			if(!isVisible()){
				return new Dimension(0,0);
			}else{
				Dimension superSize = super.getPreferredSize();
				int preferredHeight = calculatePreferredHeight();
				return new Dimension(superSize.width, preferredHeight);
			}	
		}

		private int calculatePreferredHeight(){
			// loop through all charsets and see how many overlaps

			int maxCharsetOverlapCount = alignment.getAlignmentMeta().getCharsets().getMaxOverlapCount();
			int preferredHeight = CHARSET_LINE_HEIGHT * (maxCharsetOverlapCount + 1); // 1 overlap means t

			return preferredHeight;	
		}


		public void paintCharsetRuler(Graphics g){

			long startTime = System.currentTimeMillis();

			Graphics2D g2d = (Graphics2D) g;

			// What part of alignment matrix is in view (what part of matrix is in graphical view)
			Rectangle paneClip = alignmentPane.getVisibleRect();
			Rectangle matrixClip = paneCoordToMatrixCoord(paneClip);

			// Draw ruler background
			Rectangle rulerRect = new Rectangle(this.getVisibleRect());
			g2d.setColor(colorSchemeNucleotide.getBaseBackgroundColor(NucleotideUtilities.GAP));
			g2d.fill(rulerRect);

			int offsetDueToScrollPanePosition = paneClip.x;

			CharSets charsets = alignment.getAlignmentMeta().getCharsets();

			int maxCharsetOverlapCount = charsets.getMaxOverlapCount();
			logger.info("maxCharsetOverlapCount" + maxCharsetOverlapCount); 

			int maxX = Math.min(alignment.getMaxX(), (int) matrixClip.getMaxX());
			int minX = (int) matrixClip.getMinX();

			int colorIndex = 0;
			int charsetIndex = 0;
			for(CharSet charSet: charsets){
				if(charSet.intersects(minX,maxX)){
					logger.info("intersects" + charSet.getName());

					int lineHeight = CHARSET_LINE_HEIGHT;
					int charsetLineYPos = (charsetIndex % (maxCharsetOverlapCount + 1)) * lineHeight;
					logger.info("charsetLineYPos" + charsetLineYPos);

					int charSetMinX = charSet.getMinimumStartPos();
					int charSetMaxX = charSet.getMaximumEndPos();

					Point charSetMinXPanePos = alignmentPane.matrixCoordToPaneCoord(new Point(charSetMinX, 0));
					Point charSetMaxXPanePos = alignmentPane.matrixCoordToPaneCoord(new Point(charSetMaxX, 0));

					int width = charSetMaxXPanePos.x - charSetMinXPanePos.x + (int)(1*charWidth); // + 1 because if start and end is same should be one pixel

					// we are drawing not on a large scrollable ruler, but a window sized fixed pane we have to adjust with offsetDueToScrollPanePosition
					Rectangle charsetRect = new Rectangle(charSetMinXPanePos.x - offsetDueToScrollPanePosition, charsetLineYPos, width, lineHeight);	

					Color charsetColor = charsetColors[colorIndex % charsetColors.length];
					g2d.setColor(charsetColor);

					g2d.fill(charsetRect);
				}
				charsetIndex ++;
				colorIndex ++;
			}

			long endTime = System.currentTimeMillis();
			logger.info("CharsetRuler PaintComponent took " + (endTime - startTime) + " milliseconds");
		}


		private int roundToClosestUpper(int inval, int roundTo) {
			// int rounded = ((num + 99) / 100 ) * 100;
			int rounded = ((inval + roundTo -1) / roundTo ) * roundTo;
			return rounded;
		}

		@Override
		public String getToolTipText(MouseEvent event) {
			logger.info("ToolTipLoc:" + event.getPoint());

			Rectangle paneClip = alignmentPane.getVisibleRect();
			int offsetDueToScrollPanePosition = paneClip.x;
			int xPosPane = offsetDueToScrollPanePosition + event.getPoint().x;
			Point posMatrix = paneCoordToMatrixCoord(new Point(xPosPane,0));

			String toolTip = "<html>";	
			CharSets charsets = alignment.getAlignmentMeta().getCharsets();
			for(CharSet charSet: charsets){
				if(charSet.contains(posMatrix.x)){
					toolTip += charSet.getName() + "<br>";
				}
			}			
			toolTip += "</html>";

			return toolTip;		
		}


	} // end CodonPosRuler class

	private class ConsensusRuler extends JPanel{

		private AlignmentPane alignmentPane;

		public ConsensusRuler(AlignmentPane alignmentPane) {
			this.alignmentPane = alignmentPane;
		}


		public void paintComponent(Graphics g){
			super.paintComponent(g);
			paintRuler(g);
		}

		public void paintRuler(Graphics g){

			long startTime = System.currentTimeMillis();

			//super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
					RenderingHints.VALUE_ANTIALIAS_OFF); 
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_SPEED);
			//			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
			//					RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
					RenderingHints.VALUE_DITHER_DISABLE);		

			//			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
			//					RenderingHints.VALUE_RENDER_QUALITY);
			//			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			//					RenderingHints.VALUE_ANTIALIAS_ON);
			//			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			//								RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			//			//g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
			//					RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			//g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
			//					RenderingHints.VALUE_COLOR_RENDER_SPEED);
			//g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
			//					RenderingHints.VALUE_DITHER_DISABLE);


			//		g2d.setFont(baseFont);

			// What part of alignment matrix is in view (what part of matrix is in graphical view)
			Rectangle paneClip = alignmentPane.getVisibleRect();
			Rectangle matrixClip = paneCoordToMatrixCoord(paneClip);

			// todo calculate from font metrics
			double charCenterXOffset = 0.9997;


			// NUMBERS
			int rulerCharWidth = 11;
			Font rulerFont = new Font(alignmentPane.getFont().getName(), alignmentPane.getFont().getStyle(), (int)rulerCharWidth);
			g2d.setFont(rulerFont);
			
			int textPosY = (int)((double)rulerCharWidth * 1.4);
			
			//
			// Draw ruler background
			//
			Rectangle rulerRect = new Rectangle(this.getVisibleRect());
			g2d.setColor(getBackground());
			g2d.fill(rulerRect);
			
			int offsetDueToScrollPanePosition = 0;

			// Normal char-with smaller 
			if(charWidth >= 1){

				offsetDueToScrollPanePosition = paneClip.x % (int)charWidth;

				// NUMBERS

				// Only draw every xx pos
				int drawEveryNpos = 1;

				if(charWidth < 4){
					drawEveryNpos = 50;
				}else if(charWidth < 5){
					drawEveryNpos = 20;
				}

				// position numbers
				int lastTextEndPos = 0;
				int pos = 0;
				for(int x = matrixClip.x ; x < matrixClip.getMaxX() + 1; x++){
				
					if(alignment != null && alignment.getFixedNucleotideConsensus() != null && alignment.getFixedNucleotideConsensus().getLength() > x) {
						//char consensusResidue = alignment.getNucleotideConsensusAt(x);
						char consensusResidue = alignment.getFixedNucleotideConsensusAt(x);
						int qualVal = alignment.getFixedNucleotideConsensusQualityAt(x);
						String stringToDraw = String.valueOf(consensusResidue);
						
						
						int posX = (int)((pos) * charWidth - offsetDueToScrollPanePosition -1); // -1 just beause it looks good since it is exact lining up with alignment
	
						
						double textXOffset = (double)charWidth * 0.2;
						
						// Draq quality background color // new Color((int)(Math.random() * 0x1000000));
						Color qualValColor = colorSchemeNucleotide.getBaseQualityBackgroundColor(consensusResidue, CharPixelsContainerNucQuality.getQualClassFromQualVal(qualVal));
						g2d.setColor(qualValColor);
						g2d.fillRect(posX, 0, (int)charWidth, paneClip.height);
						
						// Draw base
						g2d.setColor(Color.black);
						g2d.drawString(stringToDraw, posX + (int)textXOffset, textPosY);
	
						pos ++;
					}
				}	
			}


			long endTime = System.currentTimeMillis();
			logger.info("Ruler PaintComponent took " + (endTime - startTime) + " milliseconds");


		}


		private int roundToClosestUpper(int inval, int roundTo) {
			// int rounded = ((num + 99) / 100 ) * 100;
			int rounded = ((inval + roundTo -1) / roundTo ) * roundTo;
			return rounded;
		}

	} // end Ruler class



	private JScrollPane getParentScrollPane(){
		JScrollPane parentScrollPane = null;
		Container c = getParent();
		while(c != null) {
			if( c instanceof JScrollPane ) {
				parentScrollPane = (JScrollPane) c;
				break;
			}
			c = c.getParent();
		}
		return parentScrollPane;
	}

	public void zoomIn(){
		JScrollPane scrollPane = getParentScrollPane();
		// if pointer is on pane
		Point zoomPoint = this.getMousePosition();
		if(zoomPoint == null){
			//			// else get center position of view
			//			Point viewPoint = scrollPane.getViewport().getViewPosition();
			//			Dimension dimension = scrollPane.getViewport().getExtentSize();
			//			Point centerPos = new Point(viewPoint.x + dimension.width / 2, viewPoint.y + dimension.height / 2);
			//			zoomPoint = centerPos;
			// else get top position of current view and centerX
			Point viewPoint = scrollPane.getViewport().getViewPosition();
			Dimension dimension = scrollPane.getViewport().getExtentSize();
			Point centerPos = new Point(viewPoint.x + dimension.width / 2, viewPoint.y);
			zoomPoint = centerPos;
		}
		this.zoomInAt(zoomPoint);

	}

	public void zoomOut(){
		JScrollPane scrollPane = getParentScrollPane();
		// if pointer is on pane
		Point zoomPoint = this.getMousePosition();
		if(zoomPoint == null){
			// else get center position of view
			//			Point viewPoint = scrollPane.getViewport().getViewPosition();
			//			Dimension dimension = scrollPane.getViewport().getExtentSize();
			//			Point centerPos = new Point(viewPoint.x + dimension.width / 2, viewPoint.y + dimension.height / 2);
			//			zoomPoint = centerPos;
			// else get top position of current view and centerX
			Point viewPoint = scrollPane.getViewport().getViewPosition();
			Dimension dimension = scrollPane.getViewport().getExtentSize();
			Point centerPos = new Point(viewPoint.x + dimension.width / 2, viewPoint.y);
			zoomPoint = centerPos;
		}
		this.zoomOutAt(zoomPoint);
	}


	public void zoomOutAt(Point zoomPoint){

		// Get alignmentPane size before resize since it will change afeter resize
		// we need to now relative size different between new and old size because
		// we want to zoom in on same position (same nucleotide) where mouse was in
		// old pane
		Dimension oldSize = this.getPreferredSize();
		// TODO what if panel is not in a scrollPane???!!!
		final JScrollPane scrollPane = getParentScrollPane();
		Point viewPoint = scrollPane.getViewport().getViewPosition();
		Point mousePosInScrollPaneCoord = new Point(zoomPoint.x - viewPoint.x, zoomPoint.y - viewPoint.y);

		//	Point mouseInMatrixCoord = this.paneCoordToMatrixCoord(mousePosInScrollPaneCoord);

		logger.info("oldSize" + oldSize);

		boolean didChangeSize = decCharSize();

		if(didChangeSize){

			final Dimension newSize = this.getPreferredSize();
			this.setSize(newSize);
			logger.info("newSize" + newSize);

			// Now when alignmentPanel coordinates have changed due to resize, lets focus on the 
			// relative position where mouse pointer were earlier (same nucleotide)		
			double paneRelSizeX = newSize.getWidth()/oldSize.getWidth();
			double paneRelSizeY = newSize.getHeight()/oldSize.getHeight();

			int mousePosXOnResizedPane = (int) (zoomPoint.getX() * paneRelSizeX);
			int mousePosYOnResizedPane = (int) (viewPoint.getY() * paneRelSizeY);
			int viewPosXOnResizedPane = (int) (viewPoint.getX() * paneRelSizeX);
			int viewPosYOnResizedPane = (int) (viewPoint.getY() * paneRelSizeY);

			Point mousePosOnResizedPane = new Point(mousePosXOnResizedPane, mousePosYOnResizedPane);
			Point viewPosOnResizedPane = new Point(viewPosXOnResizedPane, viewPosYOnResizedPane);


			// calculate new vew location	
			// This is changed to viewPoint and not mouseY to get zoomIn zoomOut keep top sequence fixed on zoomIn zoomOut
			int newX = mousePosOnResizedPane.x - mousePosInScrollPaneCoord.x;
			//int newY = mousePosOnResizedPane.y - mousePosInScrollPaneCoord.y;
			int newY = viewPosOnResizedPane.y;
			final Point newViewPoint = new Point(newX, newY);

			// Old viewport has to be replaced
			ChangeListener[] changeListeners = scrollPane.getViewport().getChangeListeners();
			scrollPane.setViewport(null);
			scrollPane.setViewportView(this);
			// Add listeners back
			for(ChangeListener cl: changeListeners) {
				scrollPane.getViewport().addChangeListener(cl);
			}
			// Set new pos
			scrollPane.getViewport().setViewPosition(newViewPoint);

			viewModel.setNewView(this, getCharWidth(), getCharHeight(), newViewPoint);

			Point afterViewPoint = scrollPane.getViewport().getViewPosition();
			logger.info("afterViewPoint" + viewPoint);

		}
	}

	public void zoomInAt(Point mousePos){

		// TODO Problem is that a scrollpane need to be resized before setViewPosiiton()
		// and all should be done at once before repaint!!!

		// Get alignmentPane size before resize since it will change afeter resize
		// we need to now relative size different between new and old size because
		// we want to zoom in on same position (same nucleotide) where mouse was in
		// old pane
		Dimension oldSize = this.getPreferredSize();
		final JScrollPane scrollPane = getParentScrollPane();
		logger.info("scrollPane" + scrollPane);
		Point viewPoint = scrollPane.getViewport().getViewPosition();
		Point mousePosInScrollPaneCoord = new Point(mousePos.x - viewPoint.x, mousePos.y - viewPoint.y);

		logger.info("oldSize" + oldSize);
		logger.info("getCharWidth" + getCharWidth());

		logger.info("mousePosInScrollPaneCoord" + mousePosInScrollPaneCoord);

		logger.info("mousePosOnPane" + mousePos);

		incCharSize();

		logger.info("new getCharWidth" + getCharWidth());

		Dimension newSize = this.getPreferredSize();
		this.setSize(newSize);

		logger.info("pane-newSize" + newSize);

		// Now when alignmentPanel coordinates have changed due to resize, lets focus on the 
		// relative position where mouse pointer were earlier (same nucleotide)		
		double paneRelSizeX = newSize.getWidth()/oldSize.getWidth();
		double paneRelSizeY = newSize.getHeight()/oldSize.getHeight();

		int mousePosXOnResizedPane = (int) (mousePos.getX() * paneRelSizeX);
		int mousePosYOnResizedPane = (int) (mousePos.getY() * paneRelSizeY);
		int viewPosXOnResizedPane = (int) (viewPoint.getX() * paneRelSizeX);
		int viewPosYOnResizedPane = (int) (viewPoint.getY() * paneRelSizeY);

		Point mousePosOnResizedPane = new Point(mousePosXOnResizedPane, mousePosYOnResizedPane);
		Point viewPosOnResizedPane = new Point(viewPosXOnResizedPane, viewPosYOnResizedPane);

		// calculate new view location	
		int newX = mousePosOnResizedPane.x - mousePosInScrollPaneCoord.x;
		// This is changed to viewPoint and not mouseY to get zoomIn zoomOut keep top sequence fixed on zoomIn zoomOut
		//int newY = mousePosOnResizedPane.y - mousePosInScrollPaneCoord.y;
		int newY = viewPosOnResizedPane.y;

		logger.info("newX" + newX);
		logger.info("newY" + newY);

		final Point newViewPoint = new Point(newX, newY);

		viewPoint = scrollPane.getViewport().getViewPosition();
		logger.info("beforeViewPoint" + viewPoint);
		logger.info("newViewPoint" + newViewPoint);

		// Old viewport has to be replaced - otherwise problem drawing graphics
		// Not needed actually on zoomIn - only on zoomOut
		//		alignmentScrollPane.setViewport(null);
		//		alignmentScrollPane.setViewportView(alignmentPane);

		// Set new position
		scrollPane.getViewport().setViewPosition(newViewPoint);

		viewModel.setNewView(this, getCharWidth(), getCharHeight(), newViewPoint);

		Point afterViewPoint = scrollPane.getViewport().getViewPosition();
		logger.info("afterViewPoint" + viewPoint);


	}

	public boolean isReverseHorizontalRotation() {
		return Settings.getReverseHorizontalMouseWheel().getBooleanValue();
	}

	public boolean isReverseVerticalRotation() {
		return Settings.getReverseVerticalMouseWheel().getBooleanValue();
	}


	public void viewChanged(ViewEvent event) {	

		logger.info("inside viewChanged");

		ViewModel model = (ViewModel)event.getSource();
		double charWidth = model.getCharWidth();
		Point modelPoint = model.getViewPoint();

		if(this.getCharWidth() != charWidth) {
			setCharWidth(charWidth);
		}

		final JScrollPane scrollPane = getParentScrollPane();
		if(modelPoint != scrollPane.getViewport().getViewPosition()) {

			//			// Old viewport has to be replaced
			//			ChangeListener[] changeListeners = scrollPane.getViewport().getChangeListeners();
			//			scrollPane.setViewport(null);
			//			scrollPane.setViewportView(this);
			//			// Add listeners back
			//			for(ChangeListener cl: changeListeners) {
			//				scrollPane.getViewport().addChangeListener(cl);
			//			}

			// Set new pos
			scrollPane.getViewport().setViewPosition(modelPoint);
		}

	}


}

