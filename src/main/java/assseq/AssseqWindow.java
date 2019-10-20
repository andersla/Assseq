package assseq;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import utils.DialogUtils;
import utils.FileUtilities;
import utils.OSNativeUtils;
import utils.nexus.CharSet;
import utils.nexus.CodonPos;
import utils.nexus.NexusUtilities;
import assseq.aligner.Aligner;
import assseq.aligner.AlignerADDItemsFrame;
import assseq.aligner.AlignerALLItemsFrame;
import assseq.alignment.AliCursor;
import assseq.alignment.Alignment;
import assseq.alignment.AlignmentEvent;
import assseq.alignment.AlignmentFile;
import assseq.alignment.AlignmentListener;
import assseq.alignment.AlignmentMeta;
import assseq.assembler.AlignedQ;
import assseq.assembler.Assembler;
import assseq.assembler.MSAQ;
import assseq.assembler.SequenceQ;
import assseq.color.ColorScheme;
import assseq.exporter.ImageExporter;
import assseq.externalcommands.CommandItem;
import assseq.externalcommands.ExternalCommandExecutor;
import assseq.gui.AssseqToolBar;
import assseq.gui.AssseqJMenuBar;
import assseq.gui.AssseqJMenuBarFactory;
import assseq.gui.AlignmentPopupMenu;
import assseq.gui.AppIcons;
import assseq.gui.GlassPaneKeyListener;
import assseq.gui.GlassPaneMouseListener;
import assseq.gui.ListTopOffsetJPanel;
import assseq.gui.MessageLogFrame;
import assseq.gui.PanelAndListSynchronizer;
import assseq.gui.ScrollBarModelSyncChangeListener;
import assseq.gui.SearchPanel;
import assseq.gui.SplitSyncher;
import assseq.gui.StatusPanel;
import assseq.gui.TextEditDialog;
import assseq.gui.TextEditFrame;
import assseq.gui.TextEditPanel;
import assseq.gui.TextEditPanelCharsets;
import assseq.gui.TranslationToolPanel;
import assseq.gui.pane.AlignmentPane;
import assseq.gui.pane.AlignmentPaneMouseListener;
import assseq.gui.pane.CharPixels;
import assseq.gui.pane.InvalidAlignmentPositionException;
import assseq.gui.pane.NotUsed_AlignmentPane_Orig;
import assseq.gui.pane.TracePanel;
import assseq.gui.pane.ViewModel;
import assseq.importer.AlignmentFactory;
import assseq.importer.AlignmentImportException;
import assseq.importer.FileFormat;
import assseq.importer.FileImportUtils;
import assseq.importer.SequencesFactory;
import assseq.messenges.Messenger;
import assseq.phenotype2genotype.Phenotype2Genootype;
import assseq.primer.Primer;
import assseq.primer.PrimerResultsFrame;
import assseq.sequencelist.AlignmentDataEvent;
import assseq.sequencelist.AlignmentDataListener;
import assseq.sequencelist.AlignmentListModel;
import assseq.sequencelist.AlignmentSelectionEvent;
import assseq.sequencelist.AlignmentSelectionListener;
import assseq.sequencelist.FilePage;
import assseq.sequencelist.FileSequenceAlignmentListModel;
import assseq.sequencelist.FindObject;
import assseq.sequencelist.SequenceJList;
import assseq.sequencelist.SequenceListMouseListener;
import assseq.sequencelist.SequenceListMouseWheelListener;
import assseq.sequencelist.SequenceListSelectionModel;
import assseq.sequences.BasicQualCalledSequence;
import assseq.sequences.QualCalledSequence;
import assseq.sequences.Sequence;
import assseq.sequences.SequenceUtils;
import assseq.settings.PrimerSettingsPanel;
import assseq.settings.Settings;
import assseq.settings.SettingsFrame;
import assseq.subprocesses.SubProcessWindow;
import assseq.test.RubberBandingListener;
import assseq.undo.UndoSavedState;
import assseq.undo.UndoSavedStateEditedSequences;
import assseq.undo.UndoSavedStateEverything;
import assseq.undo.UndoSavedStateMetaOnly;
import assseq.undo.UndoSavedStateSequenceOrder;
import assseq.utils.FileDrop;
import assseq.utils.Utils;
import assseq.utils.FileDrop.Listener;

public class AssseqWindow extends JFrame implements UndoControler, AlignmentListener, AlignmentSelectionListener, AlignmentDataListener{

	private static final String LF = System.getProperty("line.separator");
	private static final Logger logger = Logger.getLogger(AssseqWindow.class);
	private static final SequencesFactory seqFactory = new SequencesFactory();
	private AssseqWindow aliViewWindow;
	private Preferences prefs = Preferences.userNodeForPackage(AssseqWindow.class);
	private static final Rectangle DEFAULT_WIN_GEOMETRY = new Rectangle(20,20,600,400);
	private static final int DEFAULT_WIN_EXTENDED_STATE = Frame.NORMAL;
	protected JViewport viewport;
	protected AlignmentPane alignmentPane;
	protected TracePanel tracePanel;
	// MyScrollPane 
	JScrollPane alignmentScrollPane;
	//JPanel alignmentAndRulerPanel;
	private SequenceJList sequenceJList;
	private SequenceJList traceSequenceJList;
	private Alignment alignment;
	private SearchPanel searchPanel;
	private StatusPanel statusPanel;
	private JTextField primer1txtField;
	private JTextField primer2txtField;
	private PrimerResultsFrame primerResultsFrame;
	private int nextNameFindSequenceNumber;
	private int pastedSeqCounter = 1;
	//	private LimitedStack<UndoSavedState> undoStack = new LimitedStack<UndoSavedState>(30);
	//	private LimitedStack<UndoSavedState> redoStack = new LimitedStack<UndoSavedState>(30);	
	//	private Stack<UndoSavedState> undoStack = new Stack<UndoSavedState>();
	//	private Stack<UndoSavedState> redoStack = new Stack<UndoSavedState>();

	UndoList undoList = new UndoList();

	private boolean hasUnsavedUndoableEdits;
	private static Component glassPane;
	private AssseqJMenuBarFactory menuBarFactory;
	private JMenu mnFilePages;
	private UndoControler undoControler;
	private FindObject findObj;
	private JScrollPane listScrollPane;
	private ButtonModel editModeModel;
	private AssseqJMenuBar aliViewMenuBar;
	//	private AlignmentDataAndSelectionListener aliListener;
	private TranslationToolPanel translationPanel;
	private AssseqToolBar aliToolbar;
	private ListTopOffsetJPanel listTopOffset;
	private boolean hasNotifiedUserAboutLimitedUndo;
	/*
	public AliViewWindow(AliViewJMenuBarFactory menuBarFactory) {
		this(null, menuBarFactory);
	}
	 */
	private JPanel alignmentAndRulerPanel;
	private JPanel listAndTopOffset;
	private JPanel rulerPanel;


	public long getLastPaneEndTime(){
		return alignmentPane.getEndTime();
	}


	public AssseqWindow(File alignmentFile,AssseqJMenuBarFactory menuBarFactory) {
		this.aliViewWindow = this;
		this.menuBarFactory = menuBarFactory;

		// prepare glassPane
		glassPane = this.getGlassPane();
		glassPane.addMouseListener(new GlassPaneMouseListener());
		glassPane.addKeyListener(new GlassPaneKeyListener());


		// try to Load alignment
		Alignment newAlignment = null;
		if(alignmentFile != null){
			if(alignmentFile.exists()){
				newAlignment = AlignmentFactory.createNewAlignment(alignmentFile);
				Settings.putLoadAlignmentDirectory(alignmentFile.getAbsoluteFile().getParent());
				Settings.putLoadAlignmentDirectory(alignmentFile.getAbsoluteFile().getParent());
				Settings.addRecentFile(alignmentFile);
			}else{
				Messenger.showOKOnlyMessage(Messenger.FILE_OPEN_NOT_EXISTS,
						"Filename: " + alignmentFile,
						aliViewWindow);	
			}		
		}else{
			logger.info("no file");
		}

		// create empty alignment if it is still null after loading
		if(newAlignment == null){
			logger.info("alignment was null creating new");
			newAlignment = AlignmentFactory.createNewEmptyAlignment();
		}


		// Create File drop handler
		new  FileDrop( this.getRootPane(), true, new FileDrop.Listener(){		
			public void  filesDropped( java.io.File[] files, DropTargetDropEvent evt ){   

				aliViewWindow.filesDropped(files, evt.getDropAction());	

			}   // end filesDropped
		}); // end FileDrop.Listener

		// remember and restore geometry
		this.restoreWindowGeometry();

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				saveWindowGeometry();
			}
		});


		// Set close window accelerator
		Action closeWinAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				Assseq.closeWindow(aliViewWindow);
			}
		};
		this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(OSNativeUtils.getCloseWinKeyAccelerator(), "CloseWin");
		this.getRootPane().getActionMap().put("CloseWin", closeWinAction);

		// Set focus next window accelerator
		//	    Action focusNextWinAction = new AbstractAction() {
		//	         public void actionPerformed(ActionEvent e) {
		//	        	 logger.info("focusNext");
		//	            AliView.focusNextWin();
		//	         }
		//	    };
		//	    this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(OSNativeUtils.getFocusNextWinKeyAccelerator(), "FocusNextWin");
		//	    this.getRootPane().getActionMap().put("FocusNextWin", focusNextWinAction);

		// TODO maybe there are other keys in Mac to remove....
		// remove F2 as edit key in JTable etc.
		Action doNothing = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				logger.info("doing nothing");
			}
		};
		this.getRootPane().getInputMap().put(KeyStroke.getKeyStroke("F2"),"doNothing");
		this.getRootPane().getActionMap().put("doNothing", doNothing);



		// Icon
		this.setIconImage(AppIcons.getProgramIconImage());

		// Init dialog utilities with this frame
		DialogUtils.init(this);

		initWindow(newAlignment);

	}

	public void fileDropped(List<File> fileList, int dropAction) {
		// Convert list to array
		File[] filesArray = new File[ fileList.size() ];
		fileList.toArray( filesArray );
		filesDropped(filesArray, dropAction);
	}


	public void filesDropped(File[] files, int dropAction) {
		logger.info("DnDConstants.ACTION_COPY" + DnDConstants.ACTION_COPY);
		// Shift modifier = DnDConstants.ACTION_MOVE
		logger.info("DnDConstants.ACTION_MOVE" + DnDConstants.ACTION_MOVE);
		logger.info("dropAction" + dropAction);


		// On linux it is opposite 
		if(OSNativeUtils.isLinuxOrUnix()){
			if(dropAction == DnDConstants.ACTION_MOVE){
				dropAction = DnDConstants.ACTION_COPY;
			}else if(dropAction == DnDConstants.ACTION_COPY){
				dropAction = DnDConstants.ACTION_MOVE;
			}
		}


		// Action_MOVE = shift-drop = add file as fasta instead 
		if(dropAction  == DnDConstants.ACTION_MOVE){
			for(File droppedFile: files){
				logger.info("file dropped");
				addSequencesFromFile(droppedFile,0);
				//// TODO only open one for now
				//break;
			}
		}
		// Action_COPY = open new window
		else{
			if(files.length > 10){
				Messenger.showOKCancelMessage(Messenger.MULTI_FILE_DROP_WARNING,
						aliViewWindow);
				int choise = Messenger.getLastSelectedOption();
				if(choise == JOptionPane.CANCEL_OPTION){
					return;
				}
			}
			for(File droppedFile: files){
				logger.info("file dropped");
				Assseq.openAlignmentFile(droppedFile);
			}
		}

	}


	public UndoControler getUndoControler(){
		return undoControler;
	}

	public static Component getAliViewWindowGlassPane(){
		return glassPane;
	}

	/*
	public void logScrollPane() {
		logger.info(alignmentScrollPane.getViewport().getSize());
	}
	 */


	private void initWindow(Alignment newAlignment) {
		logger.info("inside init()");

		//Color COLORSCHEME_BACKGROUND = Settings.getColorSchemeNucleotide().getBaseBackgroundColor(NucleotideUtilities.GAP);
		//Color COLORSCHEME_BACKGROUND = Settings.getColorSchemeNucleotide().getBaseBackgroundColor(NucleotideUtilities.GAP);
		//Color COLORSCHEME_BACKGROUND = Color.white;//Settings.getColorSchemeNucleotide().getBaseBackgroundColor(NucleotideUtilities.GAP);

		alignment = newAlignment;

		// When alignment is loaded
		this.updateWindowTitle();

		// add listener
		alignment.addAlignmentListener(this);
		alignment.addAlignmentDataListener(this);
		alignment.addAlignmentSelectionListener(this);

		// UNDO
		if(alignment.isUndoable()){
			this.undoControler = aliViewWindow;
		}else{
			this.undoControler = new EmptyUndoControler();
		}

		logger.info("here");

		// Create the main panel where alignment is drawn

		ViewModel viewModel = new ViewModel();
		alignmentPane = new AlignmentPane(viewModel);
		alignmentPane.setAlignment(alignment);


		// Always horizontal scrollbar so list and pane not have varied height - then list and alignment could get out of synch	
		//	alignmentScrollPane = new MyScrollPane(alignmentPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JScrollPane alignmentScrollPane = new JScrollPane(alignmentPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		alignmentScrollPane.setAutoscrolls(true);
		alignmentScrollPane.setMinimumSize(new Dimension(150, 150));
		alignmentScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		alignmentScrollPane.getHorizontalScrollBar().setUnitIncrement(160);
		alignmentScrollPane.setBorder(BorderFactory.createEmptyBorder());
		viewport = alignmentScrollPane.getViewport();
		viewport.setAutoscrolls(true);

		boolean paneDoubleBuff = true;
		alignmentScrollPane.setDoubleBuffered(paneDoubleBuff);
		alignmentPane.setDoubleBuffered(paneDoubleBuff);

		// BACKINGSTORE_SCROLL_MODE is not working on Mac Retina-screen
		//alignmentScrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);	

		alignment.addAlignmentSelectionListener(alignmentPane);

		sequenceJList = new SequenceJList(alignment.getSequences(), alignmentPane.getCharHeight(), this);
		alignment.addAlignmentSelectionListener(sequenceJList);

		//
		// To be able to consume mouse events before they gets to the
		// JList default listeners we first remove all built in listeners
		// and then instead add our ones at top, and finally add the old ones back
		//
		MouseListener[] oldOnes = sequenceJList.getMouseListeners();
		for(MouseListener oldMl: oldOnes){
			sequenceJList.removeMouseListener(oldMl);
		}
		MouseMotionListener[] oldMMOnes = sequenceJList.getMouseMotionListeners();
		for(MouseMotionListener oldMl: oldMMOnes){
			sequenceJList.removeMouseMotionListener(oldMl);
		}

		// Add our listeners
		SequenceListMouseListener ourSeqListML = new SequenceListMouseListener(this);
		sequenceJList.addMouseListener(ourSeqListML);
		sequenceJList.addMouseMotionListener(ourSeqListML);
		SequenceListMouseWheelListener ourSeqListMWL = new SequenceListMouseWheelListener(alignmentPane);
		sequenceJList.addMouseWheelListener(ourSeqListMWL);

		// And return the default listeners below our ones
		for(MouseListener oldMl: oldOnes){
			sequenceJList.addMouseListener(oldMl);
		}
		for(MouseMotionListener oldMl: oldMMOnes){
			sequenceJList.addMouseMotionListener(oldMl);
		}



		//	sequenceJList.setBackground(COLORSCHEME_BACKGROUND);
		//		aliListener = new AlignmentDataAndSelectionListener(alignmentPane, this, sequenceJList);
		//sequenceJList.addListSelectionListener(aliListener);

		//		alignment.getSequences().addAlignmentDataListener(aliListener);
		//		alignment.getSequences().addAlignmentSelectionListener(aliListener);

		// Always horizontal scrollbar so list and pane not have varied height - then list and alignment could get out of synch
		listScrollPane = new JScrollPane(sequenceJList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		listScrollPane.setBorder(new EmptyBorder(0,0,0,0));
		//listScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER);


		// Synchronize vertical scroll between two panes (alignmentPane and sequenceList)
		//	ScrollBarModelSyncChangeListener seqListScrollBarListener = new ScrollBarModelSyncChangeListener(listScrollPane.getVerticalScrollBar().getModel());
		//	ScrollBarModelSyncChangeListener aliPaneScrollBarListener = new ScrollBarModelSyncChangeListener(alignmentScrollPane.getVerticalScrollBar().getModel());
		//	alignmentScrollPane.getVerticalScrollBar().getModel().addChangeListener( seqListScrollBarListener );
		//	listScrollPane.getVerticalScrollBar().getModel().addChangeListener( aliPaneScrollBarListener );

		// Ruler
		JComponent alignmentRuler = alignmentPane.getRulerComponent();
		Dimension alignmentRulerDimension = new Dimension(1000,20);
		alignmentRuler.setPreferredSize(alignmentRulerDimension);

		// RulerPanel	
		rulerPanel = new JPanel();
		rulerPanel.setLayout(new BoxLayout(rulerPanel, BoxLayout.Y_AXIS));
		rulerPanel.add(alignmentRuler);

		// Consensus Ruler
		JComponent consensusRuler = alignmentPane.getConsensusRulerComponent();
		consensusRuler.setPreferredSize(new Dimension(1000,20));

		// Alignment And RulerPanel together
		alignmentAndRulerPanel = new JPanel(new BorderLayout());
		alignmentAndRulerPanel.add(alignmentScrollPane, BorderLayout.CENTER);
		alignmentAndRulerPanel.add(rulerPanel, BorderLayout.NORTH);		
		alignmentAndRulerPanel.add(consensusRuler, BorderLayout.SOUTH);

		// consensus label
		JPanel consensusLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		consensusLabelPanel.add(new JLabel("Consensus:"));
		consensusLabelPanel.setPreferredSize(new Dimension(100, consensusRuler.getPreferredSize().height));

		// topoffset listpanel to match rulers height and also consensus label
		listTopOffset = new ListTopOffsetJPanel(rulerPanel);
		listTopOffset.setPreferredSize(new Dimension(100, rulerPanel.getPreferredSize().height));
		listAndTopOffset = new JPanel(new BorderLayout());
		listAndTopOffset.add(listScrollPane, BorderLayout.CENTER);
		listAndTopOffset.add(listTopOffset, BorderLayout.NORTH);
		listAndTopOffset.add(consensusLabelPanel, BorderLayout.SOUTH);

		// Splitpane between list and alignmentview
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listAndTopOffset, alignmentAndRulerPanel);
		splitPane.setDividerSize(6);
		splitPane.setDividerLocation(200);
		//listTopOffset.setBackground(splitPane.getBackground());

		//this.getContentPane().add(splitPane, BorderLayout.CENTER);


		//
		//
		// TRACE
		//
		//


		// Create traceJList and trace panel
		// Create the Trace panel where trace is drawn
		tracePanel = new TracePanel(viewModel);
		tracePanel.setAlignment(alignment);
		tracePanel.setDoubleBuffered(paneDoubleBuff);
		alignment.addAlignmentSelectionListener(tracePanel);

		JScrollPane traceScrollPane = new JScrollPane(tracePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		traceScrollPane.setAutoscrolls(true);
		traceScrollPane.setMinimumSize(new Dimension(150, 150));
		traceScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		traceScrollPane.getHorizontalScrollBar().setUnitIncrement(160);
		traceScrollPane.setBorder(BorderFactory.createEmptyBorder());
		JViewport viewport = traceScrollPane.getViewport();
		viewport.setAutoscrolls(true);

		traceScrollPane.setDoubleBuffered(paneDoubleBuff);
		tracePanel.setDoubleBuffered(paneDoubleBuff);


		traceSequenceJList = new SequenceJList(alignment.getSequences(), alignmentPane.getCharHeight(), this);
		alignment.addAlignmentSelectionListener(traceSequenceJList);

		JScrollPane traceListScrollPane = new JScrollPane(traceSequenceJList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		traceListScrollPane.setBorder(new EmptyBorder(0,0,0,0));


		//
		// To be able to consume mouse events before they gets to the
		// JList default listeners we first remove all built in listeners
		// and then instead add our ones at top, and finally add the old ones back
		//
		MouseListener[] oldTraceOnes = traceSequenceJList.getMouseListeners();
		for(MouseListener oldMl: oldTraceOnes){
			traceSequenceJList.removeMouseListener(oldMl);
		}
		MouseMotionListener[] oldTraceMMOnes = traceSequenceJList.getMouseMotionListeners();
		for(MouseMotionListener oldMl: oldTraceMMOnes){
			sequenceJList.removeMouseMotionListener(oldMl);
		}

		// Add our listeners
		SequenceListMouseListener ourTraceSeqListML = new SequenceListMouseListener(this);
		traceSequenceJList.addMouseListener(ourTraceSeqListML);
		traceSequenceJList.addMouseMotionListener(ourTraceSeqListML);
		SequenceListMouseWheelListener ourTraceSeqListMWL = new SequenceListMouseWheelListener(tracePanel);
		traceSequenceJList.addMouseWheelListener(ourTraceSeqListMWL);

		// And return the default listeners below our ones
		for(MouseListener oldMl: oldTraceOnes){
			sequenceJList.addMouseListener(oldMl);
		}
		for(MouseMotionListener oldMl: oldTraceMMOnes){
			sequenceJList.addMouseMotionListener(oldMl);
		}



		// Add traceJList and tracePanel to splitPane
		JSplitPane listAndTraceSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, traceListScrollPane, traceScrollPane);
		listAndTraceSplitPane.setDividerSize(6);
		listAndTraceSplitPane.setDividerLocation(300);

		//Split between alignment and trace
		JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPane, listAndTraceSplitPane);
		verticalSplitPane.setDividerSize(6);
		logger.debug(verticalSplitPane.getSize());
		verticalSplitPane.setResizeWeight(0.51);

		this.getContentPane().add(verticalSplitPane, BorderLayout.CENTER);

		SplitSyncher splitSynch = new SplitSyncher(splitPane, listAndTraceSplitPane);

		// Synchronize vertical scroll between two panes (tracePanel and traceList)
		//		ScrollBarModelSyncChangeListener traceListScrollBarListener = new ScrollBarModelSyncChangeListener(traceListScrollPane.getVerticalScrollBar().getModel());
		//		ScrollBarModelSyncChangeListener tracePanelScrollBarListener = new ScrollBarModelSyncChangeListener(traceScrollPane.getVerticalScrollBar().getModel());
		//		traceScrollPane.getVerticalScrollBar().getModel().addChangeListener( traceListScrollBarListener );
		//		traceListScrollPane.getVerticalScrollBar().getModel().addChangeListener( tracePanelScrollBarListener );


		//		// Trace Ruler
		//		JComponent traceRuler = tracePanel.getRulerComponent();
		//		Dimension traceRulerDimension = new Dimension(1000,20);
		//		traceRuler.setPreferredSize(traceRulerDimension);
		//		
		//		JPanel traceRulerPanel;
		//		JPanel traceAndRulerPanel;
		//		ListTopOffsetJPanel traceListTopOffset;
		//		JPanel traceListAndTopOffset;
		//		
		//		// RulerPanel	
		//		traceRulerPanel = new JPanel();
		//		traceRulerPanel.setLayout(new BoxLayout(traceRulerPanel, BoxLayout.Y_AXIS));
		//		traceRulerPanel.add(traceRuler);
		//
		//		// Trace And RulerPanel together
		//		traceAndRulerPanel = new JPanel(new BorderLayout());
		//		traceAndRulerPanel.add(traceScrollPane, BorderLayout.CENTER);
		//		traceAndRulerPanel.add(traceRulerPanel, BorderLayout.NORTH);
		//
		//		// topoffset listpanel to match rulers height
		//		traceListTopOffset = new ListTopOffsetJPanel(traceRulerPanel);
		//		traceListTopOffset.setPreferredSize(new Dimension(100, traceRulerPanel.getPreferredSize().height));
		//		traceListAndTopOffset = new JPanel(new BorderLayout());
		//		traceListAndTopOffset.add(traceListScrollPane, BorderLayout.CENTER);
		//		traceListAndTopOffset.add(traceListTopOffset, BorderLayout.NORTH);

		//
		//
		// End TRACE
		//
		//


		//		// Synchronize horizontal scroll between two panes (alignmentPane and sequenceList)
		//		ScrollBarModelSyncChangeListener traceScrollBarListener = new ScrollBarModelSyncChangeListener(traceScrollPane.getHorizontalScrollBar().getModel());
		//		ScrollBarModelSyncChangeListener aliPaneHorizontalScrollBarListener = new ScrollBarModelSyncChangeListener(alignmentScrollPane.getHorizontalScrollBar().getModel());
		//		alignmentScrollPane.getHorizontalScrollBar().getModel().addChangeListener( traceScrollBarListener );
		//		traceScrollPane.getHorizontalScrollBar().getModel().addChangeListener( aliPaneHorizontalScrollBarListener );

		//		// Synchronize vertical scroll between two panes (alignmentPane and tracePanel)
		//		ScrollBarModelSyncChangeListener aliListScrollBarListener = new ScrollBarModelSyncChangeListener(listScrollPane.getVerticalScrollBar().getModel());
		//		ScrollBarModelSyncChangeListener traceListVertScrollBarListener = new ScrollBarModelSyncChangeListener(traceListScrollPane.getVerticalScrollBar().getModel());
		//		listScrollPane.getVerticalScrollBar().getModel().addChangeListener( aliListScrollBarListener );
		//		traceListScrollPane.getVerticalScrollBar().getModel().addChangeListener( traceListVertScrollBarListener );

		//		ScrollBarModelSyncChangeListener aliScrollBarListener = new ScrollBarModelSyncChangeListener(alignmentScrollPane.getHorizontalScrollBar().getModel());
		//		ScrollBarModelSyncChangeListener traceScrollBarListener = new ScrollBarModelSyncChangeListener(traceScrollPane.getHorizontalScrollBar().getModel());
		//		alignmentScrollPane.getHorizontalScrollBar().getModel().addChangeListener( traceScrollBarListener );
		//		traceScrollPane.getHorizontalScrollBar().getModel().addChangeListener( aliScrollBarListener );


		//	alignmentScrollPane.getHorizontalScrollBar().setModel(traceScrollPane.getHorizontalScrollBar().getModel());

		// Synchronize two alignment-scrollpane and trace scrollpane
		//	traceScrollPane.getViewport().addChangeListener(new ScrollViewChangeListener(alignmentScrollPane));
		//	alignmentScrollPane.getViewport().addChangeListener(new ScrollViewChangeListener(traceScrollPane));

		traceScrollPane.getViewport().addChangeListener(viewModel);
		alignmentScrollPane.getViewport().addChangeListener(viewModel);

		//	alignmentScrollPane.getHorizontalScrollBar().getModel().addChangeListener(viewModel);
		//	traceListScrollPane.getHorizontalScrollBar().getModel().addChangeListener(viewModel);

		viewModel.addViewListener(alignmentPane);
		viewModel.addViewListener(tracePanel);

		viewModel.addViewListener(sequenceJList);
		viewModel.addViewListener(traceSequenceJList);

		// Panel with all small status message labels such as xpos ypos 
		statusPanel = new StatusPanel(alignmentPane, alignment);
		alignment.addAlignmentListener(statusPanel);
		alignment.addAlignmentDataListener(statusPanel);
		alignment.addAlignmentSelectionListener(statusPanel);

		// bottomPanel with  status and in the future maybe more....
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		//bottomPanel.add(searchField);
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(statusPanel);

		logger.info("middle of GUI");
		//
		//  Create Menubar Toolbar and Popupmenu
		//
		aliViewMenuBar = menuBarFactory.create(this);
		logger.info("done create menubar");
		this.setJMenuBar(aliViewMenuBar);
		aliViewMenuBar.disableAllButEssentialButtons();
		Settings.addSettingsListener(aliViewMenuBar);
		AlignmentPopupMenu poupMenu = new AlignmentPopupMenu(aliViewWindow, aliViewMenuBar);
		alignmentPane.addMouseListener(poupMenu);
		sequenceJList.addMouseListener(poupMenu);


		translationPanel = new TranslationToolPanel(aliViewWindow);
		alignment.addAlignmentListener(translationPanel);
		alignment.addAlignmentDataListener(translationPanel);
		alignment.addAlignmentSelectionListener(translationPanel);
		searchPanel = new SearchPanel();
		searchPanel.getSearchField().addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent e) {
				performFind();	
			}
		});
		searchPanel.getSearchField().addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
			}
			public void focusGained(FocusEvent e) {
				alignment.clearSelection();	
			}
		});

		aliToolbar = new AssseqToolBar(aliViewMenuBar, searchPanel, translationPanel);
		aliToolbar.setFloatable(false);
		aliToolbar.setBorder(new EmptyBorder(0,0,0,0));
		// toolbar layout might be different in som plaf but since it is not floating doesn't matter
		this.getContentPane().add(aliToolbar, BorderLayout.NORTH);
		// Press default - this is because change listener is overwriting property in AlignmentPane when button is created in AliJMenuBar
		boolean isHighlightNonCons = true;
		aliViewMenuBar.getHighlightNonConsButtonModel().setSelected(isHighlightNonCons);
		// End toolbar
		//

		aliViewMenuBar.updateAllMenuEnabled();
		alignment.addAlignmentListener(aliViewMenuBar);
		alignment.addAlignmentDataListener(aliViewMenuBar);
		alignment.addAlignmentSelectionListener(aliViewMenuBar);

		//		JPanel anotherGlaggP = (JPanel) this.getGlassPane();
		//		anotherGlaggP.setLayout(new FlowLayout());	
		//		JPanel message = new JPanel();
		//		message.setPreferredSize(new Dimension(400,200));
		//		message.setMaximumSize(new Dimension(400,200));
		//		message.setOpaque(true);
		//		message.add(new JLabel("A Small label on top"));
		//		anotherGlaggP.add(message);
		//		anotherGlaggP.setVisible(true);

		// Set a default ColorScheme (has to be done after all pnels are created)
		setColorSchemeNucleotide(Settings.getColorSchemeNucleotide());


		AlignmentPaneMouseListener ml = new AlignmentPaneMouseListener(alignment, alignmentPane, this, sequenceJList);
		alignmentPane.addMouseListener(ml);
		alignmentPane.addMouseMotionListener(ml);
		alignmentPane.addMouseWheelListener(ml); 

		AlignmentKeyListener kl = new AlignmentKeyListener();
		alignmentPane.addKeyListener(kl);

		AlignmentRulerMouseListener rl = new AlignmentRulerMouseListener();
		alignmentPane.getRulerComponent().addMouseListener(rl);
		alignmentPane.getRulerComponent().addMouseMotionListener(rl);

		AlignmentPaneMouseListener tracePanelML = new AlignmentPaneMouseListener(alignment, tracePanel, this, traceSequenceJList);
		tracePanel.addMouseListener(tracePanelML);
		tracePanel.addMouseMotionListener(tracePanelML);
		tracePanel.addMouseWheelListener(tracePanelML); 

		AlignmentConsensusRulerMouseListener crl = new AlignmentConsensusRulerMouseListener();
		alignmentPane.getConsensusRulerComponent().addMouseListener(crl);
		alignmentPane.getConsensusRulerComponent().addMouseMotionListener(crl);

		AlignmentKeyListener tracePanelKL = new AlignmentKeyListener();
		tracePanel.addKeyListener(tracePanelKL);


		logger.info("init() finished");
	}


	public AssseqJMenuBar getAliMenuBar() {
		return aliViewMenuBar;
	}


	/*
	 * 
	 * This
	 * 
	 */
	public void findNamesFromClipboard() {

		// get clipboard
		String clipboard = getClipboard();

		if(clipboard != null){
			String[] lines = clipboard.split(LF);
			List<Integer> allFoundIndices = new ArrayList<Integer>();

			for(String line: lines){
				FindObject findObj = new FindObject(line,true);
				findObj = alignment.findInNames(findObj);	
				allFoundIndices.addAll(findObj.getFoundIndices());
			}

			alignment.clearSelection();
			if(allFoundIndices.size() > 0){
				alignment.selectIndices(allFoundIndices);
				sequenceJList.ensureIndexIsVisible(allFoundIndices.get(0));
			}
		}
	}



	/*
	 * 
	 * This method is a bonus - not really needed for alignment program.... 
	 * 
	 */
	public void reverseComplementClipboard(){

		String clipData = AssseqWindow.getClipboard();

		if(clipData == null){
			return;
		}

		// trim indata if it ends or starts with newline etc.
		clipData = clipData.trim();

		String revComp = "";

		// check if fasta
		if(clipData.startsWith(">")){
			try {
				AlignmentListModel sequences = seqFactory.createFastaSequences(new StringReader(clipData));
				// TODO change everywhere to fixed new-line
				for(Sequence seq: sequences){
					seq.reverseComplement();
					revComp += ">" + seq.getName() + LF;
					revComp += seq.getBasesAsString() + LF;
				}
			} catch (AlignmentImportException e) {
				e.printStackTrace();
				Messenger.showOKOnlyMessage(Messenger.COMPLEMENT_FUNCTION_ERROR, LF + e.getLocalizedMessage(), this);

			}

		}
		// no fasta just revcomp brutally
		else{
			revComp = assseq.NucleotideUtilities.reverse(clipData);
			revComp = assseq.NucleotideUtilities.complement(revComp);
		}

		// set clipboard
		StringSelection ss = new StringSelection(revComp);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

	}

	public void saveAlignmentFile() {
		// If alignment only is temp-file then save via chooser - otherwise without chooser
		if(alignment.getAlignmentFile().isAliViewTempFile()){
			saveAlignmentAsFileViaChooser();
		}else{
			saveAlignmentAsFile();
		}
	}

	public void showMessageLog() {
		startDebug();
		MessageLogFrame messFrame = new MessageLogFrame(this);
		messFrame.setVisible(true);
	}

	public void updateWindowTitle() {
		AlignmentFile aliFile = alignment.getAlignmentFile();
		if(aliFile == null || aliFile.getName().length() == 0){
			this.setTitle("Assseq");
		}else{
			// Add a symbol if unsaved
			if(hasUnsavedUndoableEdits()){
				this.setTitle("Assseq - " + "*" + aliFile.getNameWithoutTempPrefix());
			}
			else{
				this.setTitle("Assseq - " + aliFile.getNameWithoutTempPrefix());
			}
		}
	}

	private boolean hasUnsavedUndoableEdits() {
		if(hasUnsavedUndoableEdits == true){
			return true;
		}
		if(alignment.getAlignmentFile() != null){
			if(alignment.getAlignmentFile().isAliViewTempFile()){
				return true;
			}
		}
		return false;
	}

	protected boolean hasUnsavedEdits() {
		return hasUnsavedUndoableEdits();
	}


	public void performFind(String searchText) {
		searchPanel.setText(searchText);
		alignment.clearFindLastPos();
		performFind();
	}

	public void performFind() {
		alignment.clearSelection();
		// if the searchterm is new then create a new obj and start from beginning
		String searchTerm = searchPanel.getText();
		if(findObj == null){
			findObj = new FindObject(searchTerm);
			// First try finding in names
			findObj.setFindNextInNames(true);
		}
		if(!findObj.getSearchTerm().equalsIgnoreCase(searchTerm)){
			findObj = new FindObject(searchTerm);
			// First try finding in names
			findObj.setFindNextInNames(true);
		}

		findObj.setIsFound(false);
		findObj.setFindAll(false);

		if(findObj.findNextInNames() == true){
			findObj = alignment.findInNames(findObj);
			if(findObj.isFound()){
				searchPanel.setFoundMessage();
				alignment.selectSequencesWithIndex(findObj.getFoundIndices());
				sequenceJList.ensureIndexIsVisible(findObj.getFoundIndices().get(0).intValue());

				// TODO Move this to a ScrollPaneSynchronizer.class
				//				JScrollPane source = listScrollPane;
				//				JScrollPane dest = alignmentScrollPane;
				//				Point viewPos = new Point( dest.getViewport().getViewPosition().x, source.getViewport().getViewPosition().y );
				//				dest.getViewport().setViewPosition(viewPos);

			}
			else{
				// nothing found try in sequences
				findObj.setFindNextInSequences(true);
			}
		}

		if(findObj.findNextInSequences() == true){
			//findObj.setNextFindSeqNumber(0);
			alignment.clearSelection();
			//sequenceJList.clearSelection(); // clear selection is not sending a listselectionevent
			findObj = alignment.findAndSelectInSequences(findObj);

			if(findObj.isFound()){
				searchPanel.setFoundMessage();
				alignmentPane.scrollRectToSelectionCenter();
			}
			else{
				// nothing found try in names
				findObj.setFindNextInNames(true);
			}
		}

		if(findObj.isFound() == false){
			searchPanel.setNoFoundMessage();
		}

		// requestPaneRepaint();
	}


	public void requestPaneRepaint(){
		alignmentPane.revalidate();
		alignmentPane.repaint();
		tracePanel.revalidate();
		tracePanel.repaint();
	}

	public void requestPaneAndRulerRepaint(){
		alignmentPane.revalidate();
		alignmentPane.repaintAndForceRuler();
		tracePanel.revalidate();
		tracePanel.repaint();
	}

	public void requestScrollToVisibleSelection(){
		alignmentPane.scrollRectToSelection();
	}

	public void requestRepaintAndRevalidateALL() {
		// logger.info("requestRepaintAndRevalidateALL");

		// revalidate is an invalidate and validate

		alignmentAndRulerPanel.revalidate();
		listAndTopOffset.revalidate();
		rulerPanel.revalidate();
		alignmentPane.getCharsetRulerComponent().revalidate();
		alignmentPane.validateSequenceOrder();
		alignmentPane.validateSize();
		alignmentPane.revalidate();
		sequenceJList.revalidate();
		alignmentPane.repaintAndForceRuler();
		sequenceJList.repaint();
		listAndTopOffset.repaint();

		tracePanel.getCharsetRulerComponent().revalidate();
		tracePanel.validateSequenceOrder();
		tracePanel.validateSize();
		tracePanel.revalidate();
		traceSequenceJList.revalidate();
		tracePanel.repaintAndForceRuler();
		tracePanel.repaint();
		//listAndTopOffset.repaint();
	}


	/*
	public void requestPaneRepaint(){
		alignmentPane.revalidate();
		alignmentPane.repaint();
	}

	public void requestPaneAndListRepaint(){
		alignmentPane.revalidate();
		alignmentPane.repaint();
		sequenceJList.repaint();
	}

	public void requestPaneAndRulerRepaint(){
		alignmentPane.revalidate();
		alignmentPane.repaintForceRuler();
	}

	private void requestRepaintCursor() {
		requestRepaintSelection();
	}

	private void requestRepaintSelection() {
		// repaint selection then cursor will be redrawn
		Rectangle rect = alignment.getSelectionAsMinRect();
		requestPaneRepaintMatrixRect(rect);
	}

	private void requestRepaintSelectedSequences() {
		Rectangle selectRect = alignment.getSelectionAsMinRect();
		Rectangle converted = alignmentPane.matrixCoordToPaneCoord(selectRect);
		// 2000 should make sure the whole window is covered
		converted.grow(2000,30);
		requestPaneRepaintRect(converted);
	}

	private void requestPaneRepaintMatrixRect(Rectangle rect) {
		//rect.grow(2,2);
		Rectangle converted = alignmentPane.matrixCoordToPaneCoord(rect);
		requestPaneRepaintRect(converted);
	}

	private void requestPaneRepaintRect(Rectangle rect){
		rect.grow((int)alignmentPane.getCharWidth()*3,(int)alignmentPane.getCharHeight()*3);
		alignmentPane.repaint(rect);
	}

	private void requestRepaintAndRevalidateALL() {
		alignmentPane.validateSequenceOrder();
		alignmentPane.validateSize();
		alignmentPane.revalidate();
		//sequenceJList.validateSelection();
		sequenceJList.revalidate();
		alignmentPane.repaint();
		sequenceJList.repaint();
	}
	 */

	public static final String getClipboard() {
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		try {
			if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)){
				String text = (String)t.getTransferData(DataFlavor.stringFlavor);
				return text;
			}
		} catch (UnsupportedFlavorException e){
		} catch (IOException e) {
		}
		return null;
	}

	/*
	 * These methods should be moved out to parent container
	 */





	/*
	 * End t hese methods should be moved out to parent container
	 */


	/**
	 * Initialize the contents of the frame.
	 */
	public boolean saveAlignmentAsFileViaChooser(){
		return saveAlignmentAsFileViaChooser(alignment.getFileFormat(), false);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public boolean saveAlignmentAsFileViaChooser(FileFormat fileFormat, boolean saveAsCopy){

		// Get dir for saving
		String suggestedDir = null;
		String suggestedFileName = null;

		if(alignment.getAlignmentFile() == null) {
			try {
				alignment.setAlignmentFile(AlignmentFile.createAliViewTempFile("files", ""));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(alignment.getAlignmentFile().isAliViewTempFile()){		
			File lastRecent = Settings.getLastRecentFile();
			File lastRecentDir = null;
			if(lastRecent == null){
				lastRecentDir = AlignmentFile.createUserHomeFile();
			}else{
				lastRecentDir = lastRecent.getParentFile();
			}			
			suggestedDir = lastRecentDir.getAbsolutePath();
			suggestedFileName = alignment.getAlignmentFile().getNameWithoutTempPrefix();
		}else{
			suggestedDir = alignment.getAlignmentFile().getParent();
			suggestedFileName = alignment.getFileName();	
		}


		// if file format not is same as alignment strip surrent suffix and add new one
		if(fileFormat != alignment.getFileFormat()){		
			suggestedFileName = FileFormat.stripFileSuffixFromName(suggestedFileName);
			suggestedFileName += "." + fileFormat.getSuffix();	
		}

		// make sure there is a file name
		if(suggestedFileName == null || suggestedFileName.length() < 1){
			suggestedFileName = "alignment" + "." + fileFormat.getSuffix();
		}

		File suggestedFile = new File(suggestedDir, suggestedFileName);
		Component parent = this.getParent();

		File selectedFile = FileUtilities.selectSaveFileViaChooser(suggestedFile,parent);

		// här borde det vara alignment getAlignmentAsFastaStream

		if(selectedFile != null){

			// Ask user if file exists
			if(selectedFile.exists()){
				String message = "File already exists - do you want to overwrite?";
				int retVal = JOptionPane.showConfirmDialog(this, message, "Overwrite?", JOptionPane.OK_CANCEL_OPTION);
				if(retVal != JOptionPane.OK_OPTION){									
					return false;
				}
			}
			try {

				if(fileFormat == fileFormat.IMAGE_PNG){
					ImageExporter.writeComponentAsImageToFile(selectedFile, fileFormat.getSuffix(), alignmentPane);
				}else{
					saveAlignmentAsFileAskIfNotEqualLength(selectedFile, fileFormat);
				}
				if(! saveAsCopy){
					alignment.setAlignmentFile(selectedFile);
					alignment.setAlignmentFormat(fileFormat);
					aliViewWindow.updateWindowTitle();
					Settings.putSaveAlignmentDirectory(selectedFile.getParent());
					hasUnsavedUndoableEdits = false;
					this.updateWindowTitle();
					// reload so names get updated
					if(fileFormat == FileFormat.NEXUS_SIMPLE){
						this.reloadCurrentFile();
					}
				}

				if(fileFormat != fileFormat.IMAGE_PNG){
					Settings.addRecentFile(selectedFile);
				}


			} catch (IOException e) {
				e.printStackTrace();
				// Meddela användaren om fel				
				Messenger.showOKOnlyMessage(Messenger.FILE_SAVE_ERROR, LF + e.getLocalizedMessage(), this);
			}

		}
		else{
			return false;
		}

		return true;
	}


	public void exportRaxMLFile() {
		exportRaxMLFileViaChooser();
	}

	public boolean exportRaxMLFileViaChooser(){

		// Get dir for saving
		String suggestedDir = null;
		String suggestedFileName = null;

		if(alignment.getAlignmentFile().isAliViewTempFile()){		
			File lastRecent = Settings.getLastRecentFile();
			File lastRecentDir = null;
			if(lastRecent == null){
				lastRecentDir = AlignmentFile.createUserHomeFile();
			}else{
				lastRecentDir = lastRecent.getParentFile();
			}			
			suggestedDir = lastRecentDir.getAbsolutePath();
			suggestedFileName = alignment.getAlignmentFile().getNameWithoutTempPrefix();
		}else{
			suggestedDir = alignment.getAlignmentFile().getParent();
			suggestedFileName = alignment.getFileName();	
		}

		// make sure there is a file name
		if(suggestedFileName == null){
			suggestedFileName = "";
		}

		// strip previous suffix
		suggestedFileName = FileFormat.stripFileSuffixFromName(suggestedFileName);

		suggestedFileName += ".partitions";

		File suggestedFile = new File(suggestedDir, suggestedFileName);
		Component parent = this.getParent();

		File selectedFile = FileUtilities.selectSaveFileViaChooser(suggestedFile,parent);

		// här borde det vara alignment getAlignmentAsFastaStream

		if(selectedFile != null){

			// Ask user if file exists
			if(selectedFile.exists()){
				String message = "File already exists - do you want to overwrite?";
				int retVal = JOptionPane.showConfirmDialog(this, message, "Overwrite?", JOptionPane.OK_CANCEL_OPTION);
				if(retVal != JOptionPane.OK_OPTION){									
					return false;
				}
			}
			try {

				alignment.exportPartitionsFileRaxMLFormat(selectedFile);


			} catch (IOException e) {
				e.printStackTrace();
				// Meddela användaren om fel				
				Messenger.showOKOnlyMessage(Messenger.FILE_SAVE_ERROR, LF + e.getLocalizedMessage(), this);
			}

		}
		else{
			return false;
		}

		return true;
	}


	/**
	 * Initialize the contents of the frame.
	 */
	public void saveAlignmentAsFile(){

		// Get dir for saving
		String saveDir = alignment.getAlignmentFile().getParent();

		// and filename
		String saveFileName = alignment.getFileName();

		// make sure there is a file name
		if(saveFileName == null || saveFileName.length() < 1){
			Messenger.showOKOnlyMessage(Messenger.SAVE_NOT_POSSIBLE_TRY_SAVE_AS, aliViewWindow);
			return;
		}

		File saveFile = new File(saveDir, saveFileName);

		try {

			saveAlignmentAsFileAskIfNotEqualLength(saveFile, alignment.getFileFormat());
			// many of this below should not be necessary 
			alignment.setAlignmentFile(saveFile);
			alignment.setAlignmentFormat(alignment.getFileFormat());
			aliViewWindow.updateWindowTitle();
			Settings.putSaveAlignmentDirectory(saveFile.getAbsoluteFile().getParent());
			hasUnsavedUndoableEdits = false;
			this.updateWindowTitle();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// Meddela användaren om fel
			Messenger.showOKOnlyMessage(Messenger.FILE_SAVE_ERROR, LF + e.getLocalizedMessage(), this);	
		}

	}

	private void saveAlignmentAsFileAskIfNotEqualLength(File outFile, FileFormat fileFormat) throws IOException {

		// If sequences are editable and not of equal length, ask user if they should be padded first
		boolean rightPadOrTrimIfNeeded = false;
		if(alignment.isEditable() && !alignment.isSequencesEqualLength()){

			boolean hideMessage = Settings.getHidePadOrTrimToEqualLength().getBooleanValue();
			if(! hideMessage){
				boolean hideMessageNextTime = Messenger.showYesNoCancelMessageWithCbx(Messenger.PAD_OR_TRIM_ALIGNMENT_TO_EQUAL_LENGTH, false, aliViewWindow);
				Settings.getHidePadOrTrimToEqualLength().putBooleanValue(hideMessageNextTime);
				int choise = Messenger.getLastSelectedOption();
				if(choise == JOptionPane.CANCEL_OPTION){
					return;
				}
				else if(choise == JOptionPane.YES_OPTION){
					rightPadOrTrimIfNeeded = true;
				}
				else if(choise == JOptionPane.NO_OPTION){
					rightPadOrTrimIfNeeded = false;
				}else{
					// if dialog is closed with window close then save but don't pad 
					rightPadOrTrimIfNeeded = false;
				}
			}	
		}

		alignment.saveAlignmentAsFile(outFile, fileFormat, rightPadOrTrimIfNeeded);
	}

	public void saveSelectionAsFastaFileViaChooser() {

		String suggestedDir = alignment.getAlignmentFile().getParent();
		String suggestedFileName = alignment.getAlignmentFile().getName();

		suggestedFileName = FileFormat.stripFileSuffixFromName(suggestedFileName);
		suggestedFileName += ".selection." + FileFormat.FASTA.getSuffix();

		File suggestedFile = new File(suggestedDir,  suggestedFileName);
		Component parentComponent = this.getParent();

		File selectedFile = FileUtilities.selectSaveFileViaChooser(suggestedFile,parentComponent);

		if(selectedFile != null){
			alignment.saveSelectionAsFastaFile(selectedFile);
			Settings.putSaveSelectionDirectory(selectedFile.getParent());
		}
	}


	public void reloadCurrentFile() {
		boolean isOKGoAhead = requestReloadWindow();
		if(isOKGoAhead){
			getUndoControler().pushUndoState();
			File currentFile = alignment.getAlignmentFile();
			loadNewAlignmentFile(currentFile);
			hasUnsavedUndoableEdits = false;
			this.updateWindowTitle();
		}
	}


	// TODO maybe it should not be possible
	// to open alignment in old window
	// would be easier to make it right
	public void loadNewAlignmentFile(File selectedFile){
		loadNewAlignmentFile(selectedFile, SequenceUtils.TYPE_UNKNOWN);
	}

	public void loadNewAlignmentFile(File selectedFile, int sequenceType){
		alignment = AlignmentFactory.createNewAlignment(selectedFile, sequenceType);
		setupNewAlignment(alignment);
		hasUnsavedUndoableEdits = false;
		this.updateWindowTitle();
	}

	/*
	 * 
	 *  TODO Maybe not setup new - but create new somehow
	 * 
	 */
	private void setupNewAlignment(Alignment newAlignment){

		alignment = newAlignment;	

		setupListeners();


		alignmentPane.setAlignment(alignment);
		tracePanel.setAlignment(alignment);
		sequenceJList.setModel(alignment.getSequences());
		sequenceJList.setSelectionModel(alignment.getSequences().getAlignmentSelectionModel().getSequenceListSelectionModel());
		traceSequenceJList.setModel(alignment.getSequences());
		traceSequenceJList.setSelectionModel(alignment.getSequences().getAlignmentSelectionModel().getSequenceListSelectionModel());
		//		alignment.getSequences().addAlignmentDataListener(aliListener);
		//		alignment.getSequences().addAlignmentSelectionListener(aliListener);
		statusPanel.setAlignment(alignment);
		statusPanel.updateAll();

		alignmentPane.validateSize();
		tracePanel.validateSize();
		aliViewWindow.updateWindowTitle();
		aliViewMenuBar.updateAllMenuEnabled();

		// Show dialog if sequence type was not detected
		if(alignment != null && alignment.isEmptyAlignment() &&  alignment.isUnknownAlignment()){
			boolean hideMessage = Settings.getHideUnknownAlignmentType().getBooleanValue();
			if(! hideMessage){
				boolean hideMessageNextTime = Messenger.showOKOnlyMessageWithCbx(Messenger.FAILED_SEQUENCE_DETECTION, false, aliViewWindow);
				Settings.getHideUnknownAlignmentType().putBooleanValue(hideMessageNextTime);
			}
		}
	}


	private void setupListeners() {
		alignment.addAlignmentListener(this);
		alignment.addAlignmentDataListener(this);
		alignment.addAlignmentSelectionListener(this);

		alignment.addAlignmentSelectionListener(alignmentPane);

		alignment.addAlignmentSelectionListener(tracePanel);

		alignment.addAlignmentSelectionListener(sequenceJList);

		alignment.addAlignmentSelectionListener(traceSequenceJList);

		alignment.addAlignmentListener(statusPanel);
		alignment.addAlignmentDataListener(statusPanel);
		alignment.addAlignmentSelectionListener(statusPanel);

		alignment.addAlignmentListener(aliViewMenuBar);
		alignment.addAlignmentDataListener(aliViewMenuBar);
		alignment.addAlignmentSelectionListener(aliViewMenuBar);

		alignment.addAlignmentListener(translationPanel);
		alignment.addAlignmentDataListener(translationPanel);
		alignment.addAlignmentSelectionListener(translationPanel);

	}

	protected void incCharSize(){
		alignmentPane.incCharSize();
	}

	protected boolean decCharSize() {
		boolean didChange = alignmentPane.decCharSize();
		return didChange;
	}

	public void restoreWindowGeometry(){
		// Restore window geometry
		Rectangle bounds = new Rectangle();
		bounds.x = prefs.getInt("window.x",DEFAULT_WIN_GEOMETRY.x);
		bounds.y = prefs.getInt("window.y",DEFAULT_WIN_GEOMETRY.y);
		bounds.width = prefs.getInt("window.width",DEFAULT_WIN_GEOMETRY.width);
		bounds.height = prefs.getInt("window.height",DEFAULT_WIN_GEOMETRY.height);
		this.setExtendedState(prefs.getInt("window.extendedState", DEFAULT_WIN_EXTENDED_STATE));
		this.setBounds(bounds); // Do not use pack()!	
	}

	public void saveWindowGeometry(){
		// Restore window geometry
		Rectangle bounds = this.getBounds();
		prefs.putInt("window.x",bounds.x);
		prefs.putInt("window.y",bounds.y);
		prefs.putInt("window.width",bounds.width);
		prefs.putInt("window.height",bounds.height);
		prefs.putInt("window.extendedState", this.getExtendedState());
		try {
			prefs.flush();
		} catch (BackingStoreException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void reAlignSelectedSequences(){

		if(alignment.isTranslatedOnePos()){
			Messenger.showOKOnlyMessage(Messenger.SUGGEST_ALIGN_AS_TRANSLATED, aliViewWindow);	
			return;
		}

		CommandItem firstSelected = null;
		for(CommandItem item: Settings.getAlignADDCommands()){
			if(item.isActivated()){
				firstSelected = item;
				firstSelected.reParseCommand();
				break;
			}
		}
		if(firstSelected != null){
			try {
				// check that a full sequence is selected
				if(alignment.hasFullySelectedSequences()){
					// Save selected sequences in one file and other in another
					final File unselectedAlignmentTempFile = AlignmentFile.createAliViewTempFile("unselected-alignment", FileFormat.FASTA.getSuffix());
					final File selectedAlignmentTempFile = AlignmentFile.createAliViewTempFile("selected-alignment", FileFormat.FASTA.getSuffix());	
					alignment.saveSelectedSequencesAsFastaFile(selectedAlignmentTempFile, false);
					alignment.saveUnSelectedSequencesAsFastaFile(unselectedAlignmentTempFile, false);
					alignAndAddSequences(firstSelected, unselectedAlignmentTempFile, selectedAlignmentTempFile);
				}else{
					Messenger.showOKOnlyMessage(Messenger.NO_FULLY_SELECTED_SEQUENCES, this);
				}

			} catch (IOException e) {
				// Meddela användaren om fel
				Messenger.showOKOnlyMessage(Messenger.ALIGNER_SOMETHING_PROBLEM_ERROR,
						LF + "Message: " + e.getLocalizedMessage(),
						aliViewWindow);	
				e.printStackTrace();
			}
		}
	}

	public synchronized void alignAndAddSequences(File additionalSequencesFile) throws IOException {

		if(alignment.isTranslatedOnePos()){
			Messenger.showOKOnlyMessage(Messenger.SUGGEST_ALIGN_AS_TRANSLATED, aliViewWindow);	
			return;
		}

		CommandItem firstSelected = null;
		for(CommandItem item: Settings.getAlignADDCommands()){
			if(item.isActivated()){
				firstSelected = item;
				firstSelected.reParseCommand();
				break;
			}
		}
		if(firstSelected != null){
			// Save current alignment in tempdir (to be sure all unsaved changes are there)
			FileFormat currentTempFileFormat = firstSelected.getCurrentAlignmentFileFormat();
			final File currentAlignmentTempFile = AlignmentFile.createAliViewTempFile("current-alignment", currentTempFileFormat.getSuffix());
			alignment.saveAlignmentAsFile(currentAlignmentTempFile, currentTempFileFormat, true);	
			alignAndAddSequences(firstSelected, currentAlignmentTempFile, additionalSequencesFile);
		}
	}


	public synchronized void alignAndAddSequences(final CommandItem alignItem, File origSequences, File newSeqs){

		if(alignment.isTranslatedOnePos()){
			Messenger.showOKOnlyMessage(Messenger.SUGGEST_ALIGN_AS_TRANSLATED, aliViewWindow);	
			return;
		}

		if(StringUtils.containsIgnoreCase(alignItem.getCommand(), "profile")){
			boolean hideMessage = Settings.getHideMuscleProfileAlignInfoMessage().getBooleanValue();
			if(! hideMessage){
				boolean hideMessageNextTime = Messenger.showOKOnlyMessageWithCbx(Messenger.MUSCLE_PROFILE_INFO_MESSAGE, true, this);
				Settings.getHideMuscleProfileAlignInfoMessage().putBooleanValue(hideMessageNextTime);
			}
		}

		// warn if invalid characters
		String invalidChars = alignment.getFirstAlignmentProgInvalidCharacter();
		if(invalidChars.length() > 0 ){

			Messenger.showHideAlignmentProgramInvalidCharsInfoMessage(invalidChars);
			int choise = Messenger.getLastSelectedOption();
			if(choise == JOptionPane.CANCEL_OPTION){
				return;
			}
		}
		try {
			// Create a tempFile for new alignment
			final File newAlignmentTempFile = AlignmentFile.createAliViewTempFile("alignment", ".fasta");


			final SubProcessWindow subProcessWin = SubProcessWindow.getProcessProgressWindow(aliViewWindow, true);
			subProcessWin.setCloseWhenDoneCbxSelection(Settings.getHideProcessProgressWindowWhenDone().getBooleanValue());
			subProcessWin.setTitle("Align and add sequences with " + alignItem.getName());
			subProcessWin.setAlwaysOnTop(false);
			subProcessWin.show();

			alignItem.setParameterCurrentFile(origSequences);
			alignItem.setParameterSecondFile(newSeqs);
			alignItem.setParameterOutputFile(newAlignmentTempFile);

			Thread thread = new Thread(new Runnable(){
				public void run(){
					try {
						ExternalCommandExecutor.executeMultiple(alignItem, subProcessWin);
						//Aligner.mafftAlign(currentAlignmentTempFile, newAlignmentTempFile, subProcessWin);
						// aligning is done the new thread should activate GUI again before it is finished
						SwingUtilities.invokeLater(new Runnable() {
							public void run(){
								boolean wasProcessInterruptedByUser = subProcessWin.wasSubProcessDestrouedByUser();
								aliViewWindow.realignmentOfSelectedSeqsDone(wasProcessInterruptedByUser, newAlignmentTempFile);
								subProcessWin.appendOutput(LF + "Done" + LF);

								// close window automatically if that is what is wanted
								if(Settings.getHideProcessProgressWindowWhenDone().getBooleanValue()){
									subProcessWin.dispose();
								}

								setSoftLockGUIThroughMenuDisable(false);
								//glassPane.setVisible(false);
							}
						});
					} catch (Exception e) {
						setSoftLockGUIThroughMenuDisable(false);
						//glassPane.setVisible(false);
						// Meddela användaren om fel
						subProcessWin.appendOutput(e.getMessage());
						Messenger.showOKOnlyMessage(Messenger.ALIGNER_SOMETHING_PROBLEM_ERROR,
								LF + "Message: " + e.getLocalizedMessage(),
								aliViewWindow);	
						e.printStackTrace();
					}
					finally {  
						setSoftLockGUIThroughMenuDisable(false);
					}
				}
			});
			// Lock GUI while second thread is working
			setSoftLockGUIThroughMenuDisable(true);
			//glassPane.setVisible(true);
			thread.start();
		} catch (Exception e) {
			setSoftLockGUIThroughMenuDisable(false);
			Messenger.showOKOnlyMessage(Messenger.ALIGNER_SOMETHING_PROBLEM_ERROR,
					LF + "Message: " + e.getLocalizedMessage(),
					aliViewWindow);	
			e.printStackTrace();
		}
	}



	public void reAssembleEverythingWithDefaultProgram() {

		CommandItem firstSelected = null;
		for(CommandItem item: Settings.getAlignALLCommands()){
			if(item.isActivated()){
				firstSelected = item;
				firstSelected.reParseCommand();
				break;
			}
		}
		if(firstSelected != null){
			reAssembleEverythingWithAlignCommand(firstSelected, false,false);
		}


	}

	public void reAlignEverythingAsTranslatedAA() {
		CommandItem firstSelected = null;
		for(CommandItem item: Settings.getAlignALLCommands()){
			if(item.isActivated()){
				firstSelected = item;
				firstSelected.reParseCommand();
				break;
			}
		}
		if(firstSelected != null){
			reAssembleEverythingWithAlignCommand(firstSelected, true,false);
		}
	}

	public synchronized void reAlignSelectionInSeparateThread() {

		if(alignment.isTranslatedOnePos()){
			Messenger.showOKOnlyMessage(Messenger.SUGGEST_ALIGN_AS_TRANSLATED, aliViewWindow);	
			return;
		}

		CommandItem firstSelected = null;
		for(CommandItem item: Settings.getAlignALLCommands()){
			if(item.isActivated()){
				firstSelected = item;
				firstSelected.reParseCommand();
				break;
			}
		}
		if(firstSelected != null){
			reAssembleEverythingWithAlignCommand(firstSelected, false,true);
		}
	}

	public void reAssembleEverythingWithAlignCommand(final CommandItem alignItem, final boolean asTranslatedAA, final boolean selection){

		// ask if realign everything
		if(! selection){

			boolean hideMessage = Settings.getHideRealignEverythingMessage().getBooleanValue();
			if(! hideMessage){
				boolean hideMessageNextTime = Messenger.showOKCancelMessageWithCbx(Messenger.REALIGN_EVERYTHING, hideMessage, aliViewWindow);
				Settings.getHideRealignEverythingMessage().putBooleanValue(hideMessageNextTime);
				int choise = Messenger.getLastSelectedOption();
				if(choise == JOptionPane.CANCEL_OPTION){
					return;
				}
			}
		}

		// warn if invalid characters
		/*
		String invalidChars = alignment.getFirstAlignmentProgInvalidCharacter();
		if(invalidChars.length() > 0){
			String invalCharMessage = "Some aligners (e.g. Muscle, Mafft) are sensiteive to invalid characters," + LF + "the following were found and you might need to replace them with X in your alignment: " + invalidChars;
			int choise = JOptionPane.showConfirmDialog(this, invalCharMessage, "Problem characters", JOptionPane.OK_CANCEL_OPTION);
			if(choise == JOptionPane.CANCEL_OPTION){
				return;
			}
		}
		 */
		try {
			logger.info("assembleWithDefault");

			// Strip all padding

			// Save current alignment in tempdir (to be sure all unsaved changes are there)
			FileFormat currentTempFileFormat = alignItem.getCurrentAlignmentFileFormat();

			final File currentAlignmentTempFile = AlignmentFile.createAliViewAssemblyInputFile(alignment.getAlignmentFile());

			// save selection if user changes it during alignment
			final Rectangle selectionBounds = alignment.getSelectionAsMinRect();

			alignment.saveAlignmentAsFile(currentAlignmentTempFile, currentTempFileFormat, false);

			// Create a tempFile for new alignment
			final File newAlignmentTempFile = new File(currentAlignmentTempFile.getAbsolutePath() + ".cap.ace");

			// Replace static parameters in command
			alignItem.setParameterCurrentFile(currentAlignmentTempFile);
			alignItem.setParameterOutputFile(newAlignmentTempFile);

			final SubProcessWindow subProcessWin = SubProcessWindow.getProcessProgressWindow(aliViewWindow, true);
			subProcessWin.setCloseWhenDoneCbxSelection(Settings.getHideProcessProgressWindowWhenDone().getBooleanValue());
			subProcessWin.setTitle("Align with " + alignItem.getName());
			subProcessWin.setAlwaysOnTop(false);
			subProcessWin.show();

			Thread thread = new Thread(new Runnable(){
				public void run(){
					try {
						ExternalCommandExecutor.executeMultiple(alignItem, subProcessWin);
						//Aligner.mafftAlign(currentAlignmentTempFile, newAlignmentTempFile, subProcessWin);
						logger.info("donerealign");
						// aligning is done the new thread should activate GUI again before it is finished
						SwingUtilities.invokeLater(new Runnable() {
							public void run(){
								boolean wasProcessInterruptedByUser = subProcessWin.wasSubProcessDestrouedByUser();
								if(asTranslatedAA){
									aliViewWindow.realignmentAsAADone(wasProcessInterruptedByUser, newAlignmentTempFile);
								}else if(selection){
									aliViewWindow.realignmentOfSelectionDone(wasProcessInterruptedByUser, newAlignmentTempFile, selectionBounds);
								}else{
									aliViewWindow.reassemblyDone(wasProcessInterruptedByUser, newAlignmentTempFile);	
								}
								subProcessWin.appendOutput(LF + "Done" + LF);
								logger.info("before-set-visible-false");

								// close window automatically if that is what is wanted
								if(Settings.getHideProcessProgressWindowWhenDone().getBooleanValue()){
									subProcessWin.dispose();
								}

								//glassPane.setVisible(false);
								setSoftLockGUIThroughMenuDisable(false);
							}
						});
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//glassPane.setVisible(false);
						setSoftLockGUIThroughMenuDisable(false);
						subProcessWin.appendOutput(e.getMessage());
						Messenger.showOKOnlyMessage(Messenger.ALIGNER_SOMETHING_PROBLEM_ERROR,
								LF + "Message: " + e.getLocalizedMessage(),
								aliViewWindow);	
						e.printStackTrace();		
					}
					finally {  
						setSoftLockGUIThroughMenuDisable(false);
					}
				}
			});
			// Lock GUI while second thread is working
			//glassPane.setVisible(true);
			setSoftLockGUIThroughMenuDisable(true);
			thread.start();
		} catch (Exception e) {
			setSoftLockGUIThroughMenuDisable(false);
			Messenger.showOKOnlyMessage(Messenger.ALIGNER_SOMETHING_PROBLEM_ERROR,
					LF + "Error message:" + e.getLocalizedMessage(),
					aliViewWindow);	
			e.printStackTrace();
		}
	}


	public void reAssembleEverythingWithAssseqAssembler() {

		Messenger.showOKOnlyMessage(Messenger.NOT_IMPLEMENTED);
		if(true) {
			return;
		}
		
		// ask if realign everything


		boolean hideMessage = Settings.getHideRealignEverythingMessage().getBooleanValue();
		if(! hideMessage){
			boolean hideMessageNextTime = Messenger.showOKCancelMessageWithCbx(Messenger.REALIGN_EVERYTHING, hideMessage, aliViewWindow);
			Settings.getHideRealignEverythingMessage().putBooleanValue(hideMessageNextTime);
			int choise = Messenger.getLastSelectedOption();
			if(choise == JOptionPane.CANCEL_OPTION){
				return;
			}
		}



		// warn if invalid characters


		try {
			logger.info("assembleWithDefault");

			// Save current alignment in tempdir (to be sure all unsaved changes are there)
			FileFormat currentTempFileFormat = FileFormat.FASTQ;
			final File currentAlignmentTempFile = AlignmentFile.createAliViewTempFile("current-alignment", currentTempFileFormat.getSuffix());
			// save selection if user changes it during alignment
			final Rectangle selectionBounds = alignment.getSelectionAsMinRect();

			alignment.saveAlignmentAsFile(currentAlignmentTempFile, currentTempFileFormat, true);

			// Replace static parameters in command

			final SubProcessWindow subProcessWin = SubProcessWindow.getProcessProgressWindow(aliViewWindow, true);
			subProcessWin.setCloseWhenDoneCbxSelection(Settings.getHideProcessProgressWindowWhenDone().getBooleanValue());
			subProcessWin.setTitle("Align with Assseq assembler");
			subProcessWin.setAlwaysOnTop(false);
			subProcessWin.show();



			int lowestAlScore = 100;
			//int lowestDip = 75;
			int trim_qual = 15;
			String output_format = "ACE";
			SequenceQ[] inputSequences = new SequenceQ[alignment.getSequences().size()];

			for(int n = 0; n < alignment.getSequences().size(); n ++) {
				BasicQualCalledSequence seq = (BasicQualCalledSequence) alignment.getSequences().get(n);
				int[] qualVals = seq.qualCallsAsIntArray();
				inputSequences[n] = new SequenceQ(seq.getName(), seq.getBasesAsString(), seq.getName(), qualVals);		
			}


			Assembler assembler = new assseq.assembler.Assembler();
			assembler.assemble(lowestAlScore, trim_qual, output_format, inputSequences);


			subProcessWin.appendOutput(LF + "Done" + LF);

			if(Settings.getHideProcessProgressWindowWhenDone().getBooleanValue()){
				subProcessWin.dispose();
			}

			//glassPane.setVisible(false);
			setSoftLockGUIThroughMenuDisable(false);

		} catch (Exception e) {
			Messenger.showOKOnlyMessage(Messenger.ALIGNER_SOMETHING_PROBLEM_ERROR,
					LF + "Error message:" + e.getLocalizedMessage(),
					aliViewWindow);	
			e.printStackTrace();
		}
	}

	private void setSoftLockGUIThroughMenuDisable(boolean lock) {
		aliViewMenuBar.setMenuLock(lock);
	}

	// TODO this is a quite dirty method that probably should be pushed into alignment or implemented in an Aligner.class
	// it is also not dealing with changed selection during waitingtime (but it is blocked by glass-pane
	// TODO this and many other methods only if sequences are "editable"
	private void realignmentOfSelectionDone(boolean wasProcessInterruptedByUser, File newRealignedSelectionTempFile, Rectangle selectionBounds){

		if(! wasProcessInterruptedByUser){

			// Reload alignment
			if(newRealignedSelectionTempFile.length() > 0){
				aliViewWindow.getUndoControler().pushUndoState();
				// load realigned into alignment
				Alignment realignment = AlignmentFactory.createNewAlignment(newRealignedSelectionTempFile);
				// restore selection (if changed by user during time)
				alignment.setSelectionWithin(selectionBounds);
				alignment.replaceSelectedCharactersWithThis(realignment);
				alignment.padAndTrimSequences();
				requestPaneRepaint();

			}else{
				//glassPane.setVisible(false);
				setSoftLockGUIThroughMenuDisable(false);
				Messenger.showOKOnlyMessage(Messenger.ALIGNER_SOMETHING_PROBLEM_ERROR, aliViewWindow);
				//final String message = "Something did not work out when aligning";
				//JOptionPane.showMessageDialog(this, message, "Problem when aligning", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		logger.info("Finished realignmentOfSelection");
	}

	private void realignmentAsAADone(boolean wasProcessInterruptedByUser, File newRealignedTempFile){

		if(! wasProcessInterruptedByUser){

			// Reload alignment
			if(newRealignedTempFile.length() > 0){
				try{
					aliViewWindow.getUndoControler().pushUndoState();		
					Alignment realignment = AlignmentFactory.createNewAlignment(newRealignedTempFile);			
					alignment.realignNucleotidesUseThisAAAlignmentAsTemplate(realignment);
					alignment.padAndTrimSequences();
				}catch(Exception exc){
					exc.printStackTrace();
					setSoftLockGUIThroughMenuDisable(false);
					Messenger.showGeneralExceptionMessage(exc, aliViewWindow);
				}
			}
			else{
				//glassPane.setVisible(false);
				setSoftLockGUIThroughMenuDisable(false);
				Messenger.showOKOnlyMessage(Messenger.ALIGNER_SOMETHING_PROBLEM_ERROR, aliViewWindow);
				//				String message = "Something did not work out when aligning";
				//				JOptionPane.showMessageDialog(this, message, "Problem when aligning", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		logger.info("Finished realignmentAsAADone");

	}

	protected void realignmentOfSelectedSeqsDone(boolean wasProcessInterruptedByUser, File newRealignedTempFile) {
		if(! wasProcessInterruptedByUser){

			// Reload alignment
			// TO DO HANDLE bad loading of file better
			if(newRealignedTempFile.length() > 0){

				aliViewWindow.getUndoControler().pushUndoState();
				// TODO storing this could be done slightly more unified
				// store path to current working file
				File storedAlignmentFile = alignment.getAlignmentFile();
				AlignmentListModel prevSeqOrder = alignment.getSequences();

				// Keep meta when realigning
				AlignmentMeta storedMeta = alignment.getAlignentMetaCopy();

				loadNewAlignmentFile(newRealignedTempFile);
				// Restore
				alignment.setAlignmentFile(storedAlignmentFile);
				this.updateWindowTitle();
				alignment.setAlignentMeta(storedMeta);
				alignment.sortSequencesByThisModel(prevSeqOrder);
				alignment.padAndTrimSequences();

			}
			else{
				setSoftLockGUIThroughMenuDisable(false);
				Messenger.showOKOnlyMessage(Messenger.ALIGNER_SOMETHING_PROBLEM_ERROR, aliViewWindow);
			}
		}
		logger.info("Finished reAlignWithDefault");
	}

	public void changeAlignmentType(int forcedSequenceType){
		aliViewWindow.getUndoControler().pushUndoState();
		loadNewAlignmentFile(alignment.getAlignmentFile(), forcedSequenceType);		
		logger.info("Finished changeAlignmentType");
	}

	protected void reassemblyDone(boolean wasProcessInterruptedByUser, File newRealignedTempFile) {
		if(! wasProcessInterruptedByUser){

			// Reload new alignment
			if(newRealignedTempFile.length() > 0){

				aliViewWindow.getUndoControler().pushUndoState();
				// TODO storing this could be done slightly more unified
				// store path to current working file
				File storedAlignmentFile = alignment.getAlignmentFile();

				// When realigning all don't keep alignment meta
				// AlignmentMeta storedMeta = alignment.getAlignentMetaCopy();			

				loadNewAlignmentFile(newRealignedTempFile);
				// Restore
				alignment.setAlignmentFile(storedAlignmentFile);
				this.updateWindowTitle();

				// When realigning all dont keep alignment meta
				//alignment.setAlignentMeta(storedMeta);

				//alignment.padAndTrimSequences();
			}
			else{
				//glassPane.setVisible(false);
				setSoftLockGUIThroughMenuDisable(false);
				Messenger.showOKOnlyMessage(Messenger.ALIGNER_SOMETHING_PROBLEM_ERROR, aliViewWindow);
			}
		}

		logger.info("Finished reAlignWithDefault");

	}

	protected void realignmentDone(boolean wasProcessInterruptedByUser, File newRealignedTempFile) {
		if(! wasProcessInterruptedByUser){

			// Reload new alignment
			if(newRealignedTempFile.length() > 0){

				aliViewWindow.getUndoControler().pushUndoState();
				// TODO storing this could be done slightly more unified
				// store path to current working file
				File storedAlignmentFile = alignment.getAlignmentFile();

				// When realigning all don't keep alignment meta
				// AlignmentMeta storedMeta = alignment.getAlignentMetaCopy();			

				loadNewAlignmentFile(newRealignedTempFile);
				// Restore
				alignment.setAlignmentFile(storedAlignmentFile);
				this.updateWindowTitle();

				// When realigning all dont keep alignment meta
				//alignment.setAlignentMeta(storedMeta);

				alignment.padAndTrimSequences();
			}
			else{
				//glassPane.setVisible(false);
				setSoftLockGUIThroughMenuDisable(false);
				Messenger.showOKOnlyMessage(Messenger.ALIGNER_SOMETHING_PROBLEM_ERROR, aliViewWindow);
			}
		}

		logger.info("Finished reAlignWithDefault");

	}


	public static void logAllLogs(){
		Enumeration enumer = Logger.getRootLogger().getAllAppenders();
		while ( enumer.hasMoreElements() ){
			Appender app = (Appender)enumer.nextElement();
			if ( app instanceof FileAppender ){
				System.out.println("File: " + ((FileAppender)app).getFile());
			}
		}
	}


	public static void flushAllLogs()
	{
		try
		{
			Set<FileAppender> flushedFileAppenders = new HashSet<FileAppender>();
			Enumeration currentLoggers = LogManager.getLoggerRepository().getCurrentLoggers();
			while(currentLoggers.hasMoreElements())
			{
				Object nextLogger = currentLoggers.nextElement();
				if(nextLogger instanceof Logger)
				{
					Logger currentLogger = (Logger) nextLogger;
					Enumeration allAppenders = currentLogger.getAllAppenders();
					while(allAppenders.hasMoreElements())
					{
						Object nextElement = allAppenders.nextElement();
						if(nextElement instanceof FileAppender)
						{
							FileAppender fileAppender = (FileAppender) nextElement;
							if(!flushedFileAppenders.contains(fileAppender) && !fileAppender.getImmediateFlush())
							{
								flushedFileAppenders.add(fileAppender);
								//log.info("Appender "+fileAppender.getName()+" is not doing immediateFlush ");
								fileAppender.setImmediateFlush(true);
								currentLogger.info("FLUSH");
								fileAppender.setImmediateFlush(false);
							}
							else
							{
								//log.info("fileAppender"+fileAppender.getName()+" is doing immediateFlush");
							}
						}
					}
				}
			}
		}
		catch(RuntimeException e)
		{
			logger.error("Failed flushing logs",e);
		}
	}

	public void clearSelectedBases(){
		if(! requestEditMode()){
			return;
		}
		List<Sequence> prevState = alignment.clearSelectedBases(isUndoable());
		if(isUndoable()){
			aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateEditedSequences(prevState, alignment.getAlignentMetaCopy()));
		}

		//requestPaneRepaint();
	}


	public boolean isUndoable() {

		//		MemoryUtils.logMem();
		//		logger.info("getPresumableFreeMemory()=" + MemoryUtils.getPresumableFreeMemoryMB());
		//		logger.info("alignment.getApproximateMemorySize()" + alignment.getApproximateMemorySizeMB());

		double presumableFreeMemory = MemoryUtils.getPresumableFreeMemoryMB();
		double alignmentSize = alignment.getApproximateMemorySizeMB();

		double memoryLimit = (2 * alignmentSize) + 100;

		if(presumableFreeMemory < memoryLimit){

			// tell user:
			if(! hasNotifiedUserAboutLimitedUndo){
				Messenger.showOKOnlyMessage(Messenger.LIMITED_UNDO_CAPABILITIES, this);
				hasNotifiedUserAboutLimitedUndo = true;
			}

			return false;
		}
		else{
			return true;
		}
	}

	public void findPrimerInCurrentSelection() {

		long selectionSize = alignment.getSelectionSize();

		// no selection retur
		if(selectionSize == 0){
			Messenger.showOKOnlyMessage(Messenger.NO_SELECTION, this);
			return;
		}

		if(selectionSize > 1000*100){
			// optionpane
			String message = "In a large selection finding primers might take some time, " + LF + "Do you want to go ahead?";
			int retVal = JOptionPane.showConfirmDialog(this, message, "Continue?", JOptionPane.OK_CANCEL_OPTION);
			if(retVal != JOptionPane.OK_OPTION){
				return;
			}
		}

		ArrayList<Primer> primerResult = alignment.findPrimerInSelection();

		// kill old frame
		if(primerResultsFrame != null){
			primerResultsFrame.dispose();
		}

		// if results - else show message
		if(primerResult != null && primerResult.size() > 0){
			primerResultsFrame = new PrimerResultsFrame(primerResult, aliViewWindow);
			primerResultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}else{
			Messenger.showOKOnlyMessage(Messenger.NO_PRIMERS_FOUND, this);
		}

	}


	public void addAndAlignMultipleSeqFromClipOneByOne() {
		String clipData = getClipboard();

		// check if fasta 
		if(clipData != null && FileFormat.isThisFasta(clipData)){
			try {
				AlignmentListModel sequences = seqFactory.createFastaSequences(new StringReader(clipData));

				for(Sequence seq: sequences){
					try {
						// Save sequences one by one into temp file
						File clipboardSequenceFile = AlignmentFile.createAliViewTempFile("clipboard_selection", ".fasta");
						BufferedWriter buffWriter = new BufferedWriter(new FileWriter(clipboardSequenceFile));
						String fastaSeqName = ">" + seq.getName() + LF + seq.getBasesAsString();
						buffWriter.append(fastaSeqName);
						seq.writeBases(buffWriter);
						buffWriter.flush();
						buffWriter.close();

						aliViewWindow.alignAndAddSequences(clipboardSequenceFile);

					} catch (IOException e) {
						Messenger.showOKOnlyMessage(Messenger.ALIGNER_SOMETHING_PROBLEM_ERROR,
								LF + "Error message:" + e.getLocalizedMessage(),
								aliViewWindow);	
						e.printStackTrace();
					}
				}
			} catch (AlignmentImportException e) {
				Messenger.showOKOnlyMessage(Messenger.ALIGNER_SOMETHING_PROBLEM_ERROR,
						LF + "Error message:" + e.getLocalizedMessage(),
						aliViewWindow);	
				e.printStackTrace();
			}


		}
		// no fasta // skip
		else{
			Messenger.showOKOnlyMessage(Messenger.NO_FASTA_IN_CLIPBOARD, aliViewWindow);
		}

	}

	public void addAndAlignSeqFromFile() {
		// As default get last used stored directory
		String suggestedDir = Settings.getLoadAlignmentDirectory();
		File suggestedFile = new File(suggestedDir);
		File selectedFile = FileUtilities.selectOpenFileViaChooser(suggestedFile,aliViewWindow);
		if(selectedFile != null){		
			try {
				alignAndAddSequences(selectedFile);
			} catch (IOException e) {
				Messenger.showOKOnlyMessage(Messenger.ALIGNER_SOMETHING_PROBLEM_ERROR,
						LF + "Message: " + e.getLocalizedMessage(),
						aliViewWindow);	
				e.printStackTrace();
			}		
		}	
	}

	public void addAndAlignSeqFromClipboard() {
		try {
			String clipboardSelection = getClipboard();
			logger.info(clipboardSelection);

			// Firstly see if there is a file in clipboard
			if(clipboardSelection != null && clipboardSelection.length() > 0){
				File clipboardSequenceFile = new File(clipboardSelection);

				// if clipboard not was file
				if(! clipboardSequenceFile.exists()){

					// if clipboard not is is fasta try to create fasta out of it
					if(! clipboardSelection.startsWith(">")){
						//clipboardSelection = ">clipboard_sequence" + LF + clipboardSelection;
						// Tell user not a fasta file
						Messenger.showOKOnlyMessage(Messenger.NO_FASTA_IN_CLIPBOARD, aliViewWindow);
						return;

					}
					// save clipboard to file
					clipboardSequenceFile = AlignmentFile.createAliViewTempFile("clipboard_selection", ".fasta");
					FileUtils.writeStringToFile(clipboardSequenceFile, clipboardSelection);			

				}
				File newSequenceFile = clipboardSequenceFile;
				alignAndAddSequences(newSequenceFile);
			}

		} catch (IOException e1) {
			Messenger.showOKOnlyMessage(Messenger.ALIGNER_SOMETHING_PROBLEM_ERROR,
					LF + "Message: " + e1.getLocalizedMessage(),
					aliViewWindow);	
			e1.printStackTrace();
		}
	}

	public void toggleDrawCodonpos() {
		alignmentPane.setDrawCodonPosOnRuler(! alignmentPane.getDrawCodonPosOnRuler());
		requestPaneAndRulerRepaint();
	}

	public void toggleIgnoreGapInTranslation() {
		alignmentPane.setIgnoreGapInTranslation(! alignmentPane.getIgnoreGapInTranslation());
		requestPaneAndRulerRepaint();
	}

	public void toggleTranslationShowBoth() {
		alignmentPane.setShowTranslationAndNuc(! alignmentPane.getShowTranslationAndNuc());
		requestPaneAndRulerRepaint();
	}

	public void decReadingFrame() {
		alignment.decReadingFrame();
		requestPaneAndRulerRepaint();
	}

	public void incReadingFrame() {
		alignment.incReadingFrame();
		requestPaneAndRulerRepaint();
	}

	public void setReadingFrame(int readingFrame) {
		alignment.setReadingFrame(readingFrame);
		requestPaneAndRulerRepaint();
	}

	public void toggleDrawAminoAcidCode() {
		alignmentPane.setDrawAminoAcidCode(! alignmentPane.isDrawAminoAcidCode());
		requestPaneAndRulerRepaint();

	}

	public void setEditMode(boolean allowEdit){
		alignment.setEditMode(allowEdit);
		fireEditModeChanged();
	}

	public void setShowTranslation(boolean selected) {
		alignmentPane.setShowTranslation(selected);
		if(selected == false){

		}
		alignmentPane.setDrawCodonPosOnRuler(alignmentPane.isShowTranslation());
		if(translationPanel != null){
			translationPanel.setVisible(alignmentPane.isShowTranslation());
		}
		requestPaneAndRulerRepaint();
	}

	public void setHighlightNonConsensus(boolean selected) {
		alignmentPane.setHighlightNonCons(selected);
		tracePanel.setHighlightNonCons(selected);
		requestPaneRepaint();
	}

	public void setHighlightConsensus(boolean selected) {
		alignmentPane.setHighlightCons(selected);
		requestPaneRepaint();
	}

	public void setHighlightDiff(boolean selected) {
		alignmentPane.setHighlightDiffTrace(selected);
		requestPaneRepaint();
	}

	//private void setTranslationOnePos



	public void mntmToggleTranslationOnePos(){
		logger.info("toggleOnePos");

		// translated pos to nucleotide pos - save the current vals before changing 
		// translated/nucleotide view, this is for scrolling to similar position
		Point oldTransPosTopLeft = alignmentPane.getVisibleUpperLeftMatrixPos();
		Point oldNucPosTopLeft = alignmentPane.getVisibleUpperLeftMatrixPos();
		Rectangle oldSelectRect = alignment.getSelectionAsMinRect();

		boolean isPrevShowTransOnePos = alignment.isTranslatedOnePos();
		alignment.setTranslationOnePos(! alignment.isTranslatedOnePos());
		boolean isNowShowTransOnePos = alignmentPane.isShowTranslationOnePos();
		aliViewMenuBar.setEditFunctionsEnabled(alignment.isEditable());

		// this is to scroll pane to similair position when changing nucleotide/translationOnePos
		if(isPrevShowTransOnePos != isNowShowTransOnePos){
			if(isPrevShowTransOnePos){

				// adjust to make selection at same place after re-translation
				// get first selected position diff from upper left;
				int selectionDiff = 0;
				if(oldSelectRect != null){
					selectionDiff = oldSelectRect.x - oldTransPosTopLeft.x;
					if(selectionDiff > 0 && selectionDiff < 1000){
						selectionDiff = (int) (2.01 * (double) selectionDiff);
					}else{
						selectionDiff = 0;
					}
				}

				// translated to nucleotide
				CodonPos codonPos = alignment.getAlignmentMeta().getCodonPositions().getCodonInTranslatedPos(oldTransPosTopLeft.x);
				Point nucPosUpperLeft = new Point(codonPos.startPos + selectionDiff, oldTransPosTopLeft.y);

				alignmentPane.scrollToVisibleUpperLeftMatrixPos(nucPosUpperLeft);	


			}
			else{
				// adjust to make selection at same place after translation
				// get first selected position diff from upper left;
				int selectionDiff = 0;
				if(oldSelectRect != null){
					selectionDiff = oldSelectRect.x - oldNucPosTopLeft.x;
					if(selectionDiff > 0 && selectionDiff < 1000){
						selectionDiff = (int) (0.685 * (double) selectionDiff);
					}else{
						selectionDiff = 0;
					}
				}
				// nucleotide pos to translated
				int aaPos = alignment.getAlignmentMeta().getCodonPositions().getAminoAcidPosFromNucleotidePos( oldNucPosTopLeft.x);
				//logger.info("codonPos" + codonPosition);
				Point translatedUpperLeft = new Point(aaPos - selectionDiff, oldNucPosTopLeft.y);
				//logger.info(translatedUpperLeft);

				alignmentPane.scrollToVisibleUpperLeftMatrixPos(translatedUpperLeft);

			}	
		}



		//alignmentPane.repaint();
		//	requestPaneAndRulerRepaint();
		//		if(toPoint != null){
		//			logger.info("alignmentPane.getSize()" + alignmentPane.getSize());
		//			alignmentPane.scrollToVisibleUpperLeftMatrixPos(toPoint);
		//		}
	}

	public void sortSequencesByName() {
		aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateSequenceOrder(alignment.getSequences().getDelegateSequencesCopy(), alignment.getAlignentMetaCopy()));
		alignment.sortSequencesByName();
		alignmentPane.validateSequenceOrder();
		//requestPaneAndListRepaint();
	}

	public void sortSequencesByCharColumn() {
		aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateSequenceOrder(alignment.getSequences().getDelegateSequencesCopy(), alignment.getAlignentMetaCopy()));
		alignment.sortSequencesByCharInSelectedColumn();
		alignmentPane.validateSequenceOrder();
		//requestPaneAndListRepaint();
	}

	public void setSelectionAsNonCoding() {
		//aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateMetaOnly(alignment.getAlignentMetaCopy()));
		alignment.setSelectionAsNonCoding();
		requestPaneAndRulerRepaint();
	}

	public void setSelectionAsCoding(int startPos) {
		//aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateMetaOnly(alignment.getAlignentMetaCopy()));
		alignment.setSelectionAsCoding(startPos);
		requestPaneAndRulerRepaint();
	}

	public void addOrRemoveSelectionToExcludes() {
		//getUndoControler().pushUndoState();
		alignment.addOrRemoveSelectionToExcludes();
		requestPaneRepaint();		
	}

	public void moveSelectedToBottom() {
		aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateSequenceOrder(alignment.getSequences().getDelegateSequencesCopy(), alignment.getAlignentMetaCopy()));
		alignment.moveSelectedSequencesToBottom();
		//requestPaneAndListRepaint();
		//alignmentPane.validateSize();
	}

	public void moveSelectedToTop() {
		aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateSequenceOrder(alignment.getSequences().getDelegateSequencesCopy(), alignment.getAlignentMetaCopy()));
		alignment.moveSelectedSequencesToTop();
		//requestPaneAndListRepaint();
		//alignmentPane.validateSize();
	}

	public void moveSelectedDown() {
		aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateSequenceOrder(alignment.getSequences().getDelegateSequencesCopy(), alignment.getAlignentMetaCopy()));
		alignment.moveSelectedSequencesDown();
		requestScrollToVisibleSelection();
		//alignmentPane.validateSize();
		//requestRepaintAndRevalidateALL();
	}

	public void moveSelectedUp() {
		logger.info("move sel up");
		aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateSequenceOrder(alignment.getSequences().getDelegateSequencesCopy(), alignment.getAlignentMetaCopy()));
		alignment.moveSelectedSequencesUp();
		requestScrollToVisibleSelection();
		//alignmentPane.validateSize();
	}

	public void moveSelectedTo(int index) {
		aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateSequenceOrder(alignment.getSequences().getDelegateSequencesCopy(), alignment.getAlignentMetaCopy()));
		alignment.moveSelectedSequencesTo(index);
		requestScrollToVisibleSelection();	
	}

	public void selectAll() {
		int size = sequenceJList.getModel().getSize();
		if(size > 0){
			sequenceJList.setSelectionInterval(0, size-1);
		}
		//alignment.selectAll();
		//alignmentPane.repaint();
	}

	public void deleteSelected(){
		if(! requestEditMode()){
			return;
		}


		if(alignment.hasFullySelectedSequences()){

			// first confirm
			boolean hideMessage = Settings.getHideDeleteAllSelectedSequences().getBooleanValue();
			if(! hideMessage){
				boolean hideMessageNextTime = Messenger.showOKCancelMessageWithCbx(Messenger.DELETE_SELECTED_SEQUENCES, false, aliViewWindow);
				Settings.getHideDeleteAllSelectedSequences().putBooleanValue(hideMessageNextTime);
				int choise = Messenger.getLastSelectedOption();
				if(choise == JOptionPane.CANCEL_OPTION){
					return;
				}
			}

			if(isUndoable()){
				aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateSequenceOrder(alignment.getSequences().getDelegateSequencesCopy(), alignment.getAlignentMetaCopy()));
			}
			alignment.deleteFullySelectedSequences();
		}
		else if(alignment.hasSelection()){

			// first confirm
			boolean hideMessage = Settings.getHideDeleteAllSelectedBases().getBooleanValue();
			if(! hideMessage){
				boolean hideMessageNextTime = Messenger.showOKCancelMessageWithCbx(Messenger.DELETE_SELECTED_BASES, false, aliViewWindow);
				Settings.getHideDeleteAllSelectedBases().putBooleanValue(hideMessageNextTime);
				int choise = Messenger.getLastSelectedOption();
				if(choise == JOptionPane.CANCEL_OPTION){
					return;
				}
			}

			if(isUndoable()){
				aliViewWindow.getUndoControler().pushUndoState();
			}
			alignment.deleteSelectedBases();
		}
		MemoryUtils.logMem();

		requestRepaintAndRevalidateALL();

	}

	public void deleteExludedBases() {
		aliViewWindow.getUndoControler().pushUndoState();
		alignment.deleteAllExsetBases();
	}

	public void deleteEmptySequences() {
		aliViewWindow.getUndoControler().pushUndoState();
		alignment.deleteEmptySequences();
	}

	public void copySelectionAsFasta() {
		logger.info("copy selection as fasta");
		alignment.copySelectionToClipboardAsFasta();
	}

	public void copyNames() {
		alignment.copySelectionNames();
	}

	public void copySelectionAsNucleotides() {
		alignment.copySelectionToClipboardAsNucleotides();
	}

	public void renameFirstSelected(){
		String name = alignment.getFirstSelectedSequenceName();
		if(name != null){
			// Position the edit dialog close to the sequence to be renamed
			Point pos = sequenceJList.getFirstSelectedCellPos();
			pos.translate(130, -60);
			//logger.info("pos" + pos);
			TextEditDialog txtEdit = new TextEditDialog(pos);
			txtEdit.showOKCancelTextEditor(name, TextEditDialog.TITLE_EDIT_SEQUENCE_NAME, this);
			if(txtEdit.getSelectedValue() == JOptionPane.OK_OPTION){
				String newName = txtEdit.getEditText();

				List<Sequence> prevState = alignment.setFirstSelectedSequenceName(newName);
				if(isUndoable()){
					aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateEditedSequences(prevState, alignment.getAlignmentMeta()));
				}
				requestRepaintAndRevalidateALL();
			}
		}
	}

	public void addSequencesFromFile(int atIndex){
		// As default get last used stored directory
		String suggestedDir = Settings.getLoadAlignmentDirectory();
		File suggestedFile = new File(suggestedDir);
		File selectedFile = FileUtilities.selectOpenFileViaChooser(suggestedFile,aliViewWindow);
		if(selectedFile != null){		
			addSequencesFromFile(selectedFile, atIndex);
		}	
	}

	public void addSequencesFromFiles(int atIndex){
		// As default get last used stored directory
		String suggestedDir = Settings.getLoadAlignmentDirectory();
		File suggestedFile = new File(suggestedDir);
		File[] selectedFiles = FileUtilities.selectOpenFilesViaChooser(suggestedFile,aliViewWindow);
		if(selectedFiles != null){		
			addSequencesFromFiles(selectedFiles, atIndex);
		}	
	}

	public void addSequencesFromFile(File seqFile, int atIndex){
		if(seqFile == null || !seqFile.exists()){
			return;
		}
		aliViewWindow.getUndoControler().pushUndoState();
		// if alignment is empty create a new one from the file
		// TODO this should not be needed to check, but handled gracefully as one method 
		if(alignment.getSize() == 0){
			Alignment newAlignment = AlignmentFactory.createNewAlignment(seqFile);
			if(newAlignment != null){
				//initWindow(newAlignment);
				setupNewAlignment(newAlignment);
			}
		}
		else{		
			alignment.addSequences(seqFile, atIndex);
			//alignment.addFasta(clipboardSelection);
		}
		//requestRepaintAndRevalidateALL();
	}

	public void addSequencesFromFiles(File[] seqFiles, int atIndex){
		if(seqFiles == null){
			return;
		}
		aliViewWindow.getUndoControler().pushUndoState();
		// if alignment is empty create a new one from the file
		// TODO this should not be needed to check, but handled gracefully as one method 
		if(alignment.getSize() == 0){
			Alignment newAlignment = AlignmentFactory.createNewAlignment(seqFiles[0]);
			if(newAlignment != null){
				setupNewAlignment(newAlignment);
			}
			if(seqFiles.length > 1) {
				File[] restOfFiles = new File[seqFiles.length -1];
				for(int n = 0; n < restOfFiles.length; n++) {
					restOfFiles[n] = seqFiles[n + 1];
				}
				alignment.addSequences(restOfFiles, atIndex);
			}

		}
		else{		
			alignment.addSequences(seqFiles, atIndex);
			//alignment.addFasta(clipboardSelection);
		}	

		//requestRepaintAndRevalidateALL();
	}

	public void pasteFasta(int pasteAtIndex) {
		String clipboardSelection = getClipboard();

		// If empty or null return
		if(clipboardSelection == null || clipboardSelection.length() == 0){			
			// Clipboard is empty - return
			Messenger.showOKOnlyMessage(Messenger.CLIPBOARD_EMPTY, aliViewWindow);
			return;
		}

		try {
			File clipAsFile = AlignmentFile.createAliViewTempFile("clipboard-alignment", ".fasta");

			// If selection is sequences or sequence-file then create temp clip-file from this 
			if(FileFormat.isThisFasta(clipboardSelection) || FileFormat.isThisSequenceFile(clipboardSelection)){
				if(FileFormat.isThisSequenceFile(clipboardSelection)){
					clipAsFile = new File(clipboardSelection);
				}else{
					FileUtils.writeStringToFile(clipAsFile, clipboardSelection);
				}
			}
			// If not fasta or sequence file name - Ask if paste anyway and rewrite clipboard to fasta
			else{
				boolean hideMessage = Settings.getHidePasteAnywayMessage().getBooleanValue();
				if(! hideMessage){
					boolean hideMessageNextTime = Messenger.showOKCancelMessageWithCbx(Messenger.NO_FASTA_IN_CLIPBOARD_PASTE_ANYWAY, false, aliViewWindow);
					Settings.getHidePasteAnywayMessage().putBooleanValue(hideMessageNextTime);
					int choise = Messenger.getLastSelectedOption();
					if(choise == JOptionPane.CANCEL_OPTION){

						// cancel paste
						return;
					}
				}

				// rewrite clipboard to fasta
				StringBuffer newClip = new StringBuffer();
				String lines[] = clipboardSelection.split(LF);
				for(String line: lines){
					newClip.append(">pasted-seq_" + this.pastedSeqCounter + LF);
					newClip.append(line + LF);
					this.pastedSeqCounter ++;
				}
				FileUtils.writeStringToFile(clipAsFile, newClip.toString());
			}

			// Paste sequences
			addSequencesFromFile(clipAsFile, pasteAtIndex);

		} catch (IOException e) {
			e.printStackTrace();
			Messenger.showOKOnlyMessage(Messenger.ERROR_PASTE, LF + e.getLocalizedMessage(), aliViewWindow);
		}
	}

	public void pasteFasta() {
		// I commented out this because I find it more intuitive to paste at
		// beginning with ctrl + v command
		// int index = alignment.getFirstSelectedSequenceIndex();
		// if(index < 0){
		// index = 0;
		//		}
		pasteFasta(0);
	}

	public void merge2SelectedSequences() {
		List<Sequence> selected = alignment.getSelectedSequences();
		if(selected.size() == 2){
			aliViewWindow.getUndoControler().pushUndoState();
			boolean isMerged = alignment.mergeTwoSequences(selected, true);
			if(isMerged){
				alignment.deleteSequence(selected.get(1));	
			}
		}
		//requestRepaintAndRevalidateALL();
	}

	public void deleteVerticalGaps() {

		// first confirm
		boolean hideMessage = Settings.getHideDeleteVerticalGapsMessage().getBooleanValue();
		if(! hideMessage){
			boolean hideMessageNextTime = Messenger.showOKCancelMessageWithCbx(Messenger.DELETE_VERTICAL_GAPS, false, aliViewWindow);
			Settings.getHideDeleteVerticalGapsMessage().putBooleanValue(hideMessageNextTime);
			int choise = Messenger.getLastSelectedOption();
			if(choise == JOptionPane.CANCEL_OPTION){
				return;
			}
		}

		aliViewWindow.getUndoControler().pushUndoState();	
		alignment.deleteVerticalGaps();	
		requestPaneRepaint();
		//logger.info("alignment.getMaximumSequenceLength()" + alignment.getMaximumSequenceLength());
	}

	public void find() {
		searchPanel.getSearchField().requestFocus();
		searchPanel.getSearchField().selectAll();
		//	logger.info(searchPanel.getSearchField().getText());
	}

	public void reverseComplementSelectedSequences() {
		aliViewWindow.getUndoControler().pushUndoState();	
		alignment.reverseComplementFullySelectedSequences();
		//requestPaneRepaint();
	}

	public void reverseComplementAlignment() {
		aliViewWindow.getUndoControler().pushUndoState();
		alignment.reverseComplementAlignment();

	}

	public void complementAlignment() {
		aliViewWindow.getUndoControler().pushUndoState();	
		alignment.complementAlignment();

	}

	public void clearSelection(){
		alignment.clearSelection();
	}

	public void invertSelection(){
		//		if(aliCursor != null){
		//			aliCursor.restorePosition();
		//		}

		// get the rect we have to repaint
		//	Rectangle rect = alignment.getSelectionAsMinRect();

		alignment.invertSelection();

		//requestPaneRepaint();
	}

	public void replaceSelectedWithChar(char typed) {
		aliViewWindow.getUndoControler().pushUndoState();
		alignment.replaceSelectedWithChar(typed);
		// This is to repaint selected sequences only
		//				Rectangle selectRect = alignment.getSelectionAsMinRect();
		//				Point paneXY = alignmentPane.matrixCoordToPaneCoord(new Point(selectRect.x, selectRect.y));
		//				Rectangle selectInPaneCoord = new Rectangle(   paneXY.x, paneXY.y,
		//						(int) (selectRect.width * alignmentPane.charWidth),
		//						(int) (selectRect.height * alignmentPane.charHeight + alignmentPane.charHeight));
		//				selectInPaneCoord.grow(100, 100);
		//				alignmentPane.paintImmediately(selectInPaneCoord);

		//	alignmentPane.validateSize();
		//requestRepaintSelectedSequences();
	}



	public void deleteAllGaps() {
		if(! requestEditMode()){
			return;
		}

		// first confirm
		boolean hideMessage = Settings.getHideDeleteAllGapsMessage().getBooleanValue();
		if(! hideMessage){
			boolean hideMessageNextTime = Messenger.showOKCancelMessageWithCbx(Messenger.DELETE_ALL_GAPS, false, aliViewWindow);
			Settings.getHideDeleteAllGapsMessage().putBooleanValue(hideMessageNextTime);
			int choise = Messenger.getLastSelectedOption();
			if(choise == JOptionPane.CANCEL_OPTION){
				return;
			}
		}

		undoControler.pushUndoState();
		alignment.deleteAllGaps();
		//alignment.rightPadSequencesWithGapUntilEqualLength();
		//requestPaneRepaint();;
	}

	public void trimSequences() {
		undoControler.pushUndoState();
		alignment.trimSequences();
		//requestPaneRepaint();
	}

	public void moveSelectionRight() {
		if(! requestEditMode()){
			return;
		}
		List<Sequence> prevState = alignment.moveSelectionRight(isUndoable());
		if(isUndoable()){
			aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateEditedSequences(prevState, alignment.getAlignentMetaCopy()));
		}
		requestScrollToVisibleSelection();
	}


	public void moveSelectionLeft(){
		if(! requestEditMode()){
			return;
		}
		List<Sequence> prevState = alignment.moveSelectionLeft(isUndoable());
		if(isUndoable()){
			aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateEditedSequences(prevState, alignment.getAlignentMetaCopy()));
		}
		requestScrollToVisibleSelection();
	}

	public void deleteGapMoveLeft() {
		if(! requestEditMode()){
			return;
		}
		List<Sequence> prevState = alignment.deleteGapMoveLeft(isUndoable());
		if(isUndoable()){
			aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateEditedSequences(prevState, alignment.getAlignentMetaCopy()));
		}
		alignmentPane.validateSize();
		requestScrollToVisibleSelection();
	}

	public void deleteGapMoveRight() {
		if(! requestEditMode()){
			return;
		}
		List<Sequence> prevState = alignment.deleteGapMoveRight(isUndoable());
		if(isUndoable()){
			aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateEditedSequences(prevState, alignment.getAlignentMetaCopy()));
		}
		alignmentPane.validateSize();
		requestScrollToVisibleSelection();

	}

	public void insertGapMoveRight() {
		if(! requestEditMode()){
			return;
		}
		List<Sequence> prevState = alignment.insertGapLeftOfSelectionMoveRight(isUndoable());
		logger.debug("prevState" + prevState);
		if(isUndoable()){
			aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateEditedSequences(prevState, alignment.getAlignentMetaCopy()));
		}
		alignmentPane.validateSize();
		requestScrollToVisibleSelection();
	}

	public void insertGapMoveLeft() {
		if(! requestEditMode()){
			return;
		}
		List<Sequence> prevState = alignment.insertGapRightOfSelectionMoveLeft(isUndoable());
		if(isUndoable()){
			aliViewWindow.getUndoControler().pushUndoState(new UndoSavedStateEditedSequences(prevState, alignment.getAlignentMetaCopy()));		
		}
		// This movement has to repaint all repaint selected sequences only
		alignmentPane.validateSize();
		alignmentPane.scrollMatrixX(-1);
		requestScrollToVisibleSelection();
	}

	public boolean requestReloadWindow(){
		boolean isReloadOK = true;
		if(this.hasUnsavedEdits()){        	
			// optionpane
			String message = "There might be unsaved edits - do you want to reload and loose changes?";

			this.toFront();
			int retVal = JOptionPane.showConfirmDialog(this, message, "Reload and loose changes?", JOptionPane.YES_NO_CANCEL_OPTION);

			if(retVal == JOptionPane.YES_OPTION){					
				isReloadOK = true;
			}
			if(retVal == JOptionPane.NO_OPTION){			
				isReloadOK = false;
			}
			if(retVal == JOptionPane.CANCEL_OPTION){			
				isReloadOK = false;
			}
		}
		else{
			isReloadOK = true;
		}
		return isReloadOK;
	}

	public boolean requestWindowClose(){
		boolean isCloseOK = true;
		if(this.hasUnsavedEdits()){        	
			// optionpane
			String message = "There might be unsaved edits - save before close?";

			this.toFront();
			int retVal = JOptionPane.showConfirmDialog(this, message, "Save edits?", JOptionPane.YES_NO_CANCEL_OPTION);

			if(retVal == JOptionPane.YES_OPTION){			
				boolean wasFileSaved = this.saveAlignmentAsFileViaChooser();		
				// now is OK
				if(wasFileSaved){
					isCloseOK = true;
				}else{
					isCloseOK = false;
				}
			}
			if(retVal == JOptionPane.NO_OPTION){			
				isCloseOK = true;
			}
			if(retVal == JOptionPane.CANCEL_OPTION){			
				isCloseOK = false;
			}
		}
		else{
			isCloseOK = true;
		}
		return isCloseOK;
	}

	public boolean isEmpty(){
		boolean isEmpty = true;
		if(alignment != null && alignment.getSequences() != null && alignment.getSequences().getSize() > 0){
			isEmpty = false;
		}
		return isEmpty;
	}

	public void selectDuplicates() {
		alignment.clearSelection();
		alignment.selectDuplicates();
		//requestPaneRepaint();
	}

	private void setPaneAndListBGColor(Color color){
		listTopOffset.setBackground(color);
		sequenceJList.setBackground(color);
		alignmentPane.setBackground(color);
		alignmentPane.getRulerComponent().setBackground(color);
		alignmentPane.getCharsetRulerComponent().setBackground(color);
		//alignmentPane.getConsensusRulerComponent().setBackground(color);
		traceSequenceJList.setBackground(color);
		tracePanel.setBackground(color);
	}

	public void setColorSchemeNucleotide(ColorScheme aScheme){
		setPaneAndListBGColor(aScheme.getBaseBackgroundColor(NucleotideUtilities.GAP));
		alignmentPane.setColorSchemeNucleotide(aScheme);
		requestPaneRepaint();
		Settings.setColorSchemeNucleotide(aScheme);
	}

	public void setColorSchemeAminoAcid(ColorScheme aScheme) {
		setPaneAndListBGColor(aScheme.getBaseBackgroundColor(NucleotideUtilities.GAP));
		alignmentPane.setColorSchemeAminoAcid(aScheme);
		requestPaneRepaint();
		Settings.setColorSchemeAminoAcid(aScheme);
	}

	public void setGeneticCode(GeneticCode genCode) {
		alignment.setGeneticCode(genCode);
		requestPaneRepaint();
	}

	public void setDifferenceTraceSequence(Point alignmentPanePos) {
		Point matrixPos = alignmentPane.paneCoordToMatrixCoord(alignmentPanePos);
		alignmentPane.setDifferenceTraceSequence((int)matrixPos.getY());
		requestPaneRepaint();
	}

	public void createStats(){
		//alignment.getSequences().setTranslation(true);
		alignment.getStats();
		MemoryUtils.logMem();

		/* some old stuff
		for(int n = 0; n < 100; n++){
			logger.info("askrepaint");
			alignmentPane.repaint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 */

		//		ArrayList<CharSet> charsets = alignment.getAlignentMeta().getCharsets();
		//		for(CharSet aCharSet: charsets){
		//			selectAll(aCharSet);
		//			logger.info(aCharSet.getName());
		//			break;
		//		}

	}

	public void selectAll(CharSet aCharSet) {
		alignment.selectAll(aCharSet);
	}

	/*

	public void fileSequencesChanged(){
		alignment.fileSequenceContentsChanged();
		createOrUpdateDynamicLoadFileMenu();
		if(aliViewMenuBar != null){
			aliViewMenuBar.updateAllMenuEnabled();
		}
		if(statusPanel != null){
			// TODO move these to somewhere else
			statusPanel.updateAll();
			statusPanel.repaint();
		}
		// Show message about limited edit capabilities
		boolean hideNextTimeSelected = Settings.getHideFileSeqLimitedEditCapabilities().getBooleanValue();
		if(!hideNextTimeSelected){
			hideNextTimeSelected = Messenger.showOKOnlyMessageWithCbx(Messenger.ONLY_VIEW_WHEN_FILESEQUENCES, true, this);
			Settings.getHideFileSeqLimitedEditCapabilities().putBooleanValue(hideNextTimeSelected);
		}
	}
	 */

	private void fireEditModeChanged() {
		aliViewMenuBar.editModeChanged();	
	}

	public void createOrUpdateDynamicLoadFileMenu(){
		logger.info("create");
		/*
		if(alignment.getSequences() instanceof FileSequenceAlignmentListModel && aliViewMenuBar != null){
			final FileSequenceAlignmentListModel seqList = (FileSequenceAlignmentListModel) alignment.getSequences();		
			List<FilePage> pages = seqList.getFilePages();

			if(pages != null && pages.size() > 1){					
				aliViewMenuBar.createDynamicLoadFilePages(seqList, pages);
			}
		}

		 */
	}	 

	/*
	 * 
	 * Undo/Redo, very simple implemented
	 * 
	 */
	public void pushUndoState(){
		if(isUndoable()){
			UndoSavedState state = new UndoSavedStateEverything(alignment.getSequences().getCopy(), alignment.getAlignentMetaCopy());
			pushUndoState(state);
		}
	}

	public void pushUndoState(UndoSavedState state){
		undoList.add(state);
		// TODO this should maybe be handled better than indirect as here
		hasUnsavedUndoableEdits = true;
		this.updateWindowTitle();
		fireUndoRedoChange();
	}

	/*
	private String getUndoSavedStateFastaString(){
		StringWriter fastaWriter = new StringWriter();
		try {
			alignment.storeAlignmetAsFasta(new BufferedWriter(fastaWriter));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String fastaBuffer = fastaWriter.toString();
		return fastaBuffer;
	}
	 */



	private void fireUndoRedoChange() {
		aliViewMenuBar.setUndoButtonEnabled(!isUndoStackEmpty());
		aliViewMenuBar.setRedoButtonEnabled(!isRedoStackEmpty());
	}

	public boolean isUndoStackEmpty(){
		return ! undoList.hasAvailableUndos();
	}

	public boolean isRedoStackEmpty(){
		return ! undoList.hasAvailableRedos();
	}

	private void undoSequenceEdit(UndoSavedStateEditedSequences undoObj) {
		logger.info("undoSequenceEdit");
		for(Sequence previous: undoObj.editedSequences){
			int index = alignment.getSequenceIndex(alignment.getSequenceByID(previous.getID()));
			alignment.getSequences().set(index, previous);
		}
	}

	private void undoMetaOnly(UndoSavedStateMetaOnly state) {
		logger.info("undoMetaOnly");
		alignment.setAlignentMeta(state.meta);
	}

	private void undoSequenceOrder(UndoSavedStateSequenceOrder state) {
		logger.info("undoSequenceOrder");
		alignment.getSequences().setSequences(state.sequencesBackend);
		//		sequenceJList.setModel(alignment.getSequences());
		//		sequenceJList.setSelectionModel(alignment.getSequences().getAlignmentSelectionModel().getSequenceListSelectionModel());
		alignment.setAlignentMeta(state.meta);
	}


	private void undoEverything(UndoSavedStateEverything state) throws AlignmentImportException {
		logger.info("undoEverything");
		//		alignment.setNewSequences(seqFactory.createSequences(new StringReader(state.fastaAlignment)));
		alignment.setNewSequencesFromUndo(state.sequences);
		//		sequenceJList.setModel(alignment.getSequences());
		alignment.setAlignentMeta(state.meta);
	}

	public void undo() {	
		if(undoList.hasAvailableUndos()){

			// Before first undo is performed save current state so it is possible to redo
			if(undoList.isCurrentStateNeeded()){
				// Save current state
				undoList.addCurrentState(new UndoSavedStateEverything(alignment.getSequences().getCopy(), alignment.getAlignentMetaCopy()));
			}

			UndoSavedState undoObj = undoList.getUndoState();
			logger.info("inne i undo");
			if(undoObj instanceof UndoSavedStateEverything){
				// No longer used
				try {
					logger.info("undo everyt");
					undoEverything((UndoSavedStateEverything)undoObj);
				} catch (AlignmentImportException e) {
					Messenger.showOKOnlyMessage(Messenger.UNDO_REDO_PROBLEM, LF + e.getLocalizedMessage(), aliViewWindow);
					e.printStackTrace();
				}
			}else if(undoObj instanceof UndoSavedStateSequenceOrder){
				undoSequenceOrder((UndoSavedStateSequenceOrder)undoObj);
			}else if(undoObj instanceof UndoSavedStateEditedSequences){
				undoSequenceEdit((UndoSavedStateEditedSequences)undoObj);
			}else if(undoObj instanceof UndoSavedStateMetaOnly){
				undoMetaOnly((UndoSavedStateMetaOnly)undoObj);
			}



			requestRepaintAndRevalidateALL();
		}
		if(isUndoStackEmpty()){
			logger.info("updatingTitl");
			hasUnsavedUndoableEdits = false;
			updateWindowTitle();
		}
		fireUndoRedoChange();
	}


	public void redo() {
		if(undoList.hasAvailableRedos()){

			UndoSavedState redoObj = undoList.getRedoState();

			if(redoObj instanceof UndoSavedStateEverything){
				try {
					undoEverything((UndoSavedStateEverything)redoObj);
				} catch (AlignmentImportException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Messenger.showOKOnlyMessage(Messenger.UNDO_REDO_PROBLEM, LF + e.getLocalizedMessage(), aliViewWindow);
				}
			}else if(redoObj instanceof UndoSavedStateSequenceOrder){
				undoSequenceOrder((UndoSavedStateSequenceOrder)redoObj);
			}else if(redoObj instanceof UndoSavedStateEditedSequences){
				undoSequenceEdit((UndoSavedStateEditedSequences)redoObj);
			}else if(redoObj instanceof UndoSavedStateMetaOnly){
				undoMetaOnly((UndoSavedStateMetaOnly)redoObj);
			}
			requestRepaintAndRevalidateALL();;	
		}
		fireUndoRedoChange();
	}


	/*
	public static byte[] compress(String inString){
		long startTime = System.currentTimeMillis();
		// logger.info(inString.length());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			GZIPOutputStream gzipOS = new GZIPOutputStream(baos);
			byte[] uncompressed = inString.getBytes();
			//	logger.info(uncompressed.length);
			gzipOS.write(uncompressed);
			gzipOS.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] compressed = baos.toByteArray();   
		long endTime = System.currentTimeMillis();
		logger.info("Compress took " + (endTime - startTime) + " milliseconds"); 
		return compressed;

	}

	public static String decompress(byte[] compressed){
		String decompressed = null;
		try {
			GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed)); 	
			decompressed = IOUtils.toString(gis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		return decompressed;
	}

	 */	



	/*
	 * 
	 * End Undo
	 * 
	 */






	//
	//
	//
	//
	// TODO could be moved to alignment ruler (but it is ok here)
	private class AlignmentRulerMouseListener implements MouseListener, MouseMotionListener{
		private Point startPoint;	
		private Point startPointOnAlignmentPane;
		private Rectangle maxRepaintRect;

		public void mousePressed(MouseEvent e) {

			// Save some startpoints
			startPoint = e.getPoint();
			startPointOnAlignmentPane = new Point(alignmentPane.getVisibleRect().x + e.getPoint().x,0);

			// if shift is down something is selected already make a new rect selection
			if(e.isShiftDown()){

			}
			// clear and make new single point select
			else{
				alignment.clearTempSelection();
				sequenceJList.clearSelection();
				alignment.clearSelection();
				alignmentPane.selectColumnAt(startPointOnAlignmentPane);
				//requestPaneRepaint();
			}

		}

		public void mouseReleased(MouseEvent e){

			if(startPoint != null){
				// if shift is down something is selected already make a new rect selection
				if(e.isShiftDown()){
					Point firstPos = alignment.getFirstSelectedPosition();
					Point paneFirstPoint = alignmentPane.matrixCoordToPaneCoord(firstPos);

					Rectangle selectRect = new Rectangle(paneFirstPoint);	
					Point endPointOnAlignmentPane = new Point(alignmentPane.getVisibleRect().x + e.getPoint().x,alignmentPane.getHeight());	
					selectRect.add(endPointOnAlignmentPane);

					int selectionSize = alignmentPane.selectWithin(selectRect);

				}
				else{
					Point endPointOnAlignmentPane = new Point(alignmentPane.getVisibleRect().x + e.getPoint().x,alignmentPane.getHeight());
					Rectangle selectRect = new Rectangle(startPointOnAlignmentPane);
					selectRect.add(endPointOnAlignmentPane);

					logger.info(selectRect);

					int selectionSize = 0;


					alignment.clearSelection();
					if(startPointOnAlignmentPane.x == endPointOnAlignmentPane.x){
						alignmentPane.selectColumnAt(endPointOnAlignmentPane);
						int x = alignmentPane.getColumnAt(e.getPoint());
						//						logger.info(aliCursor);
						//						logger.info(aliCursor.x);
						//						logger.info(aliCursor.y);
						alignment.getAliCursor().setPosition(x,0);
					}else{
						selectionSize = alignmentPane.selectColumnsWithin(selectRect);
						int x = alignmentPane.getColumnAt(e.getPoint());
						//						logger.info(aliCursor);
						//						logger.info(aliCursor.x);
						//						logger.info(aliCursor.y);
						alignment.getAliCursor().setPosition(x,0);
					}

					alignment.clearTempSelection();
				}

			}

			Point posOnPaneNotRuler = new Point(alignmentPane.getVisibleRect().x + e.getPoint().x,0);
			Point matrixPoint = alignmentPane.paneCoordToMatrixCoord(posOnPaneNotRuler);
			logger.info("x=" + matrixPoint.x);
			alignment.getAliCursor().setPosition(matrixPoint.x,0);
			// request focus after aliCursor change otherwise not really working
			alignmentPane.requestFocus();

			// Clear startpoint
			startPoint = null;
			startPointOnAlignmentPane = null;
			maxRepaintRect = null;
			//requestPaneRepaint();

		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseDragged(MouseEvent e) {
			// Theese lines makes sure pane is scrolling when user selects
			// and moves outside current visible rect
			int alignmentYPos = alignmentPane.getVisibleRect().y;
			int alignmentXPos = alignmentPane.getVisibleRect().x + e.getPoint().x;
			Rectangle preferredVisisble = new Rectangle(new Point(alignmentXPos,alignmentYPos));
			if(! alignmentPane.getVisibleRect().contains(e.getPoint())){	
				// grow little extra so it scrolls quickly in beginning
				preferredVisisble.grow(30,0);
				alignmentPane.scrollRectToVisible(preferredVisisble);
			}


			if(startPoint != null){

				Point endPointOnAlignmentPane = new Point(alignmentPane.getVisibleRect().x + e.getPoint().x,alignmentPane.getHeight());
				Rectangle selectRect = new Rectangle(startPointOnAlignmentPane);
				selectRect.add(endPointOnAlignmentPane);
				Rectangle selectRectMatrixCoords = alignmentPane.paneCoordToMatrixCoord(selectRect);
				alignment.setTempSelection(selectRectMatrixCoords);

				if(maxRepaintRect == null){	
					maxRepaintRect = new Rectangle(selectRect);
				}else{
					maxRepaintRect.add(selectRect);
				}
				//requestPaneRepaintRect(new Rectangle(maxRepaintRect));

			}
		}

		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub

		}	
	}
	//
	//          END AlignmentRulerMouseListener
	//

	//
	//
	//
	//
	// TODO could be moved to alignment ruler (but it is ok here)
	private class AlignmentConsensusRulerMouseListener implements MouseListener, MouseMotionListener{
		private Point startPoint;	
		private Point startPointOnAlignmentPane;
		private Rectangle maxRepaintRect;

		public void mousePressed(MouseEvent e) {

			// Save some startpoints
			startPoint = e.getPoint();
			startPointOnAlignmentPane = new Point(alignmentPane.getVisibleRect().x + e.getPoint().x,0);
			

			// if shift is down something is selected already make a new rect selection
			if(e.isShiftDown()){

			}
			// clear and make new single point select
			else{
				alignment.clearTempSelection();
				sequenceJList.clearSelection();
				alignment.clearSelection();
				
				
				Point matrixPoint = alignmentPane.paneCoordToMatrixCoord(startPointOnAlignmentPane);
				
				alignmentPane.selectConsensusAt(matrixPoint);
				//alignmentPane.selectColumnAt(startPointOnAlignmentPane);
			}
		}

		public void mouseReleased(MouseEvent e){

			if(startPoint != null){
				// if shift is down something is selected already make a new rect selection
				if(e.isShiftDown()){
					Point firstPos = alignment.getFirstSelectedPosition();
					Point paneFirstPoint = alignmentPane.matrixCoordToPaneCoord(firstPos);

					Rectangle selectRect = new Rectangle(paneFirstPoint);	
					Point endPointOnAlignmentPane = new Point(alignmentPane.getVisibleRect().x + e.getPoint().x,alignmentPane.getHeight());	
					selectRect.add(endPointOnAlignmentPane);

				//	int selectionSize = alignmentPane.selectWithin(selectRect);

				}
				else{
					Point endPointOnAlignmentPane = new Point(alignmentPane.getVisibleRect().x + e.getPoint().x,alignmentPane.getHeight());
					Rectangle selectRect = new Rectangle(startPointOnAlignmentPane);
					selectRect.add(endPointOnAlignmentPane);

					logger.info(selectRect);

					int selectionSize = 0;


					alignment.clearSelection();
					if(startPointOnAlignmentPane.x == endPointOnAlignmentPane.x){
			//			alignmentPane.selectColumnAt(endPointOnAlignmentPane);
						int x = alignmentPane.getColumnAt(e.getPoint());
						//						logger.info(aliCursor);
						//						logger.info(aliCursor.x);
						//						logger.info(aliCursor.y);
						alignment.getAliCursor().setPosition(x,0);
					}else{
			//			selectionSize = alignmentPane.selectColumnsWithin(selectRect);
						int x = alignmentPane.getColumnAt(e.getPoint());
						//						logger.info(aliCursor);
						//						logger.info(aliCursor.x);
						//						logger.info(aliCursor.y);
						alignment.getAliCursor().setPosition(x,0);
					}

					alignment.clearTempSelection();
				}

			}

			Point posOnPaneNotRuler = new Point(alignmentPane.getVisibleRect().x + e.getPoint().x,0);
			Point matrixPoint = alignmentPane.paneCoordToMatrixCoord(posOnPaneNotRuler);
			logger.info("x=" + matrixPoint.x);
			alignment.getAliCursor().setPosition(matrixPoint.x,0);
			// request focus after aliCursor change otherwise not really working
	//		alignmentPane.requestFocus();

			// Clear startpoint
			startPoint = null;
			startPointOnAlignmentPane = null;
			maxRepaintRect = null;
			//requestPaneRepaint();

		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseDragged(MouseEvent e) {
			// Theese lines makes sure pane is scrolling when user selects
			// and moves outside current visible rect
			int alignmentYPos = alignmentPane.getVisibleRect().y;
			int alignmentXPos = alignmentPane.getVisibleRect().x + e.getPoint().x;
			Rectangle preferredVisisble = new Rectangle(new Point(alignmentXPos,alignmentYPos));
			if(! alignmentPane.getVisibleRect().contains(e.getPoint())){	
				// grow little extra so it scrolls quickly in beginning
				preferredVisisble.grow(30,0);
				alignmentPane.scrollRectToVisible(preferredVisisble);
			}


			if(startPoint != null){

				Point endPointOnAlignmentPane = new Point(alignmentPane.getVisibleRect().x + e.getPoint().x,alignmentPane.getHeight());
				Rectangle selectRect = new Rectangle(startPointOnAlignmentPane);
				selectRect.add(endPointOnAlignmentPane);
				Rectangle selectRectMatrixCoords = alignmentPane.paneCoordToMatrixCoord(selectRect);
//				alignment.setTempSelection(selectRectMatrixCoords);

				if(maxRepaintRect == null){	
					maxRepaintRect = new Rectangle(selectRect);
				}else{
					maxRepaintRect.add(selectRect);
				}
				//requestPaneRepaintRect(new Rectangle(maxRepaintRect));

			}
		}

		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub

		}	
	}
	//
	//          END AlignmentRulerMouseListener
	//


	private class AlignmentKeyListener implements KeyListener{

		public void keyTyped(KeyEvent e){
			logger.info("is typed");

			if(e.isControlDown() || e.isAltDown() || e.isAltGraphDown() || e.isMetaDown()){
				// Skip
			}else{
				char typed = e.getKeyChar();
				String allowedChars = "QWERTYUIOPASDFGHJKLZXCVBNM?qwertyuiopasdfghjklzxcvbnm";
				if(allowedChars.indexOf(typed) > -1){
					if(aliViewWindow.requestEditMode()){		
						replaceSelectedWithChar(typed);
					}
				}
			}
		}

		public void keyPressed(KeyEvent e) {

			//logger.info("keyPressed Time from last endTim " + (System.currentTimeMillis() - alignmentPane.getEndTime()) + " milliseconds");

			// Skip if any modifier but shift is down
			if(e.isControlDown() || e.isAltDown() || e.isAltGraphDown() || e.isMetaDown()){
				return;
			}

			// only if selection - otherwise key-press should be forwarded to scrollbars
			if(alignment.hasSelection()){
				if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_KP_UP){
					e.consume();
					scrollToCursor(KeyEvent.VK_UP);
					moveCursorUp(e.isShiftDown());
				}
				if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_KP_DOWN){
					e.consume();
					scrollToCursor(KeyEvent.VK_DOWN);
					moveCursorDown(e.isShiftDown());
				}
				if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_KP_LEFT){
					e.consume();
					scrollToCursor(KeyEvent.VK_LEFT);
					moveCursorLeft(e.isShiftDown());
				}
				if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_KP_RIGHT){
					e.consume();
					scrollToCursor(KeyEvent.VK_RIGHT);
					moveCursorRight(e.isShiftDown());
				}
			}
		}

		public void keyReleased(KeyEvent e) {

		}
	}

	public void scrollToPos(Point matrixPos) {
		alignmentPane.scrollToPos(matrixPos);
	}

	public void scrollToCursor(int keyDirection) {

		AliCursor aliCursor = getAliCursor();
		if(aliCursor != null){
			Point pointInPaneCoord = alignmentPane.matrixCoordToPaneCoord(new Point(aliCursor.getX(), aliCursor.getY()));
			Rectangle visiRect = new Rectangle(pointInPaneCoord);
			if(keyDirection == KeyEvent.VK_LEFT){
				visiRect.add(visiRect.getMinX() - 40, visiRect.getCenterY());
			}
			if(keyDirection == KeyEvent.VK_RIGHT){
				visiRect.add(visiRect.getMaxX() + 40, visiRect.getCenterY());
			}
			if(keyDirection == KeyEvent.VK_UP){
				visiRect.add(visiRect.getCenterX(), visiRect.getMinY() - 40);
			}
			if(keyDirection == KeyEvent.VK_DOWN){
				visiRect.add(visiRect.getCenterX(), visiRect.getMaxY() + 40);
			}			
			if(! alignmentPane.getVisibleRect().contains(visiRect)){
				alignmentPane.scrollRectToVisible(visiRect);
			}
		}
	}

	public void scrollToVisibleCursor() {
		AliCursor aliCursor = getAliCursor();
		logger.info(aliCursor);
		if(aliCursor != null){
			logger.info("point" + new Point(aliCursor.getX(), aliCursor.getY()));
			Point pointInPaneCoord = alignmentPane.matrixCoordToPaneCoord(new Point(aliCursor.getX(), aliCursor.getY()));
			Rectangle visiRect = new Rectangle(pointInPaneCoord);
			visiRect.grow(40, 40);
			logger.info("alignmentPane.getVisibleRect()" + alignmentPane.getVisibleRect());
			logger.info("alignmentPane.getVisibleRect().contains(visiRect)" + alignmentPane.getVisibleRect().contains(visiRect));
			if(! alignmentPane.getVisibleRect().contains(visiRect)){
				alignmentPane.scrollRectToVisible(visiRect);
			}
		}
	}

	public AliCursor getAliCursor(){
		return alignment.getAliCursor();
	}


	public void moveCursorUp(boolean isShiftDown){
		logger.info("Before Up Time from last endTim " + (System.currentTimeMillis() - alignmentPane.getEndTime()) + " milliseconds");
		getAliCursor().moveUp(isShiftDown);
		logger.info("DoneUp Time from last endTim " + (System.currentTimeMillis() - alignmentPane.getEndTime()) + " milliseconds");
		//requestRepaintCursor();
	}

	public void moveCursorDown(boolean isShiftDown){
		getAliCursor().moveDown(isShiftDown);
		//requestRepaintCursor();
	}

	public void moveCursorLeft(boolean isShiftDown){
		getAliCursor().moveLeft(isShiftDown);
		//requestRepaintCursor();
	}

	public void moveCursorRight(boolean isShiftDown){
		getAliCursor().moveRight(isShiftDown);
		//requestRepaintCursor();
	}


	public void editAlignerALLSettings() {
		SettingsFrame settingsFrame = new SettingsFrame(this);
		settingsFrame.selectTab(SettingsFrame.TAB_ALIGN_ALL);
		settingsFrame.setVisible(true);

	}

	public void editAlignerADDSettings() {
		SettingsFrame settingsFrame = new SettingsFrame(this);
		settingsFrame.selectTab(SettingsFrame.TAB_ALIGN_ADD);
		settingsFrame.setVisible(true);
	}

	public void editExternalCommands() {
		SettingsFrame settingsFrame = new SettingsFrame(this);
		settingsFrame.selectTab(SettingsFrame.TAB_EXTERNAL_COMMANDS);
		settingsFrame.setVisible(true);
	}

	public void findPrimerSettings(){
		SettingsFrame settingsFrame = new SettingsFrame(this);
		settingsFrame.selectTab(SettingsFrame.TAB_PRIMER);
		settingsFrame.setVisible(true);
	}

	public void openPreferencesGeneral() {
		SettingsFrame settingsFrame = new SettingsFrame(this);
		settingsFrame.selectTab(SettingsFrame.TAB_GENERAL);
		settingsFrame.setVisible(true);
	}

	public void runExternalCommand(CommandItem cmdItem){
		cmdItem.reParseCommand();
		runExternalCommandImplementation(cmdItem);
	}

	public void runExternalCommandImplementation(final CommandItem cmdItem){

		// Output window
		final SubProcessWindow subProcessWin = new SubProcessWindow(aliViewWindow);
		//subProcessWin.init();
		subProcessWin.setTitle(cmdItem.getName());
		subProcessWin.setAlwaysOnTop(false);
		subProcessWin.placeFrameupperLeftLocationOfThis(aliViewWindow);
		if(cmdItem.isShowCommandWindow()){
			subProcessWin.show();
		}

		try {
			// Save current alignment in tempdir to make sure unsaved edits are included
			//File currentAlignmentTempFile = File.createTempFile("current-alignment", ".fasta");
			//alignment.saveAlignmentAsFile(currentAlignmentTempFile, FileFormat.FASTA);

			// Save current alignment in tempdir (to be sure all unsaved changes are there)
			FileFormat currentTempFileFormat = cmdItem.getCurrentAlignmentFileFormat();
			File currentAlignmentTempFile = AlignmentFile.createAliViewTempFile("current-alignment", currentTempFileFormat.getSuffix());
			alignment.saveAlignmentAsFile(currentAlignmentTempFile, currentTempFileFormat, true);	

			// Create a tempFile for new alignment
			File emptyTempFile = AlignmentFile.createAliViewTempFile("tempfile-for-new-alignment", ".tmp");

			cmdItem.setParameterCurrentFile(currentAlignmentTempFile);
			cmdItem.setParameterOutputFile(emptyTempFile);

			Thread thread = new Thread(new Runnable(){
				public void run(){
					try {
						ExternalCommandExecutor.executeMultiple(cmdItem, subProcessWin);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Messenger.showOKOnlyMessage(Messenger.ERROR_RUNNING_EXTERNAL_COMMAND, LF + e.getLocalizedMessage(), aliViewWindow);
					}
					logger.info("done external");

					SwingUtilities.invokeLater(new Runnable() {
						public void run(){
							boolean wasProcessInterruptedByUser = subProcessWin.wasSubProcessDestrouedByUser();
							aliViewWindow.externalCommandCallback(cmdItem);			
							subProcessWin.dispose();
						}
					});
				}
			});
			// No locking of the GUI when running personal command
			// glassPane.setVisible(true);
			thread.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void externalCommandCallback(CommandItem cmdItem) {
		logger.info("this method is not implemented yet");
	}

	public boolean isEditMode(){	
		return alignment.isEditMode();
	}

	public boolean requestEditMode(){	
		if(isEditMode() == false){

			boolean allowEdit = Messenger.askAllowEditMode();
			if(allowEdit){
				aliViewWindow.setEditMode(true);
			}else{
				// do nothing
			}

		}
		return isEditMode();
	}

	//
	// AlignmentListener
	//
	public void newSequences(AlignmentEvent alignmentEvent) {		
		logger.debug("New sequences");	
	}

	public void alignmentMetaChanged(AlignmentEvent alignmentEvent) {
	}

	//
	// AlignmentDataListener
	//
	public void intervalAdded(ListDataEvent e) {
		if(e instanceof AlignmentDataEvent){
			contentsChanged((AlignmentDataEvent)e);
		}
	}

	public void intervalRemoved(ListDataEvent e) {
		if(e instanceof AlignmentDataEvent){
			contentsChanged((AlignmentDataEvent)e);
		}
	}  	

	public void contentsChanged(ListDataEvent e) {
		if(e instanceof AlignmentDataEvent){
			contentsChanged((AlignmentDataEvent)e);
		}
	}

	public void contentsChanged(AlignmentDataEvent e) {
		logger.info("selectionChanged");
		requestRepaintRect(e.getBounds());
	}

	//
	// AlignmentSelectionListener
	//

	public void selectionChanged(AlignmentSelectionEvent e) {
		logger.info("selectionChanged");
		//requestRepaintRect(e.getBounds());
	}


	//
	//
	//  TODO this repaint method should be changed so that pane and list automatically redraws
	//  because of their own listening events
	//
	//
	public void requestRepaintRect(Rectangle rect) {

		// Repaint alipane
		int dx =(int) (alignmentPane.getCharWidth() * 3);
		int dy =(int) (alignmentPane.getCharHeight() * 1);
		// if small chars, redraw at least a few pix
		if(dx < 3 || dy < 1){
			dx = 6;
			dy = 2;
		}
		Rectangle aliPaneBounds = alignmentPane.matrixCoordToPaneCoord(rect);
		Rectangle grown = new Rectangle(aliPaneBounds.x - dx, aliPaneBounds.y - dy, aliPaneBounds.getBounds().width + 2*dx, aliPaneBounds.getBounds().height + 2*dy);
		alignmentPane.validateSize();
		alignmentPane.validateSequenceOrder();
		alignmentPane.repaint(grown);

		// Repaint alilist
		Rectangle visiRect = sequenceJList.getVisibleRect();
		Rectangle drawListBounds = new Rectangle(visiRect.x,grown.y, visiRect.width, grown.height);
		sequenceJList.repaint(drawListBounds);

		// Repaint trace
		int tpdx =(int) (tracePanel.getCharWidth() * 3);
		int tpdy =(int) (tracePanel.getCharHeight() * 1);
		// if small chars, redraw at least a few pix
		if(tpdx < 3 || tpdy < 1){
			tpdx = 6;
			tpdy = 2;
		}
		Rectangle tracePanelBounds = tracePanel.matrixCoordToPaneCoord(rect);
		Rectangle traceGrown = new Rectangle(tracePanelBounds.x - tpdx, tracePanelBounds.y - tpdy, tracePanelBounds.getBounds().width + 2*tpdx, tracePanelBounds.getBounds().height + 2*tpdy);
		tracePanel.validateSize();
		tracePanel.validateSequenceOrder();
		tracePanel.repaint(traceGrown);

		// Repaint alilist
		Rectangle traceVisiRect = traceSequenceJList.getVisibleRect();
		Rectangle traceListBounds = new Rectangle(traceVisiRect.x,traceGrown.y, traceVisiRect.width, traceGrown.height);
		traceSequenceJList.repaint(traceListBounds);

	}

	public Alignment getAlignment() {
		return alignment;
	}

	public void showAbout() {
		String version = Assseq.getVersion();
		logger.info("version=" + version);
		String message = "Assseq version: " + version;
		JOptionPane.showMessageDialog(aliViewWindow, message, "About", JOptionPane.INFORMATION_MESSAGE);
	}

	public void checkNewVersion() {
		String version = Assseq.getVersion();
		HelpUtils.displayVersionDownload(version);
	}

	public void displayVersionHistory() {
		HelpUtils.displayVersionHistory();
	}

	public void openBugReportPage() {
		HelpUtils.display(HelpUtils.BUG_OR_FEATURE, this);
	}

	public void openHelp() {
		HelpUtils.display(HelpUtils.TOP_HELP, this);
	}

	public void countStopCodons() {
		int count = alignment.countStopCodons();
		Messenger.showCountStopCodonMessage(count, this);
	}

	public void startDebug() {
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	public void requestGB() {
		System.gc();
	}

	public void saveFastaIndex() {
		alignment.saveFastaIndex();
	}


	public void expandSelectionRight() {
		alignment.selectionExtendRight();
	}

	public void expandSelectionLeft() {
		alignment.selectionExtendLeft();
	}	

	public void expandSelectionDown() {
		alignment.selectionExtendDown();
	}

	public void expandSelectionTop() {
		alignment.selectionExtendTop();
	}

	public void phenotype2genotype() {
		// As default get last used stored directory
		String suggestedDir = Settings.getLoadAlignmentDirectory();
		File suggestedFile = new File(suggestedDir);
		File selectedFile = FileUtilities.selectOpenFileViaChooser(suggestedFile,aliViewWindow.getParent());
		if(selectedFile != null){

			try {
				File fastaGenResultFile = new File(selectedFile.getParentFile(), selectedFile.getName() + "_genotype" + "." + FileFormat.FASTA.getSuffix());
				Phenotype2Genootype gen2phen = new Phenotype2Genootype();
				gen2phen.createGenotypeFasta(selectedFile, fastaGenResultFile);
				Assseq.openAlignmentFile(fastaGenResultFile);		
				//Settings.putLoadAlignmentDirectory(selectedFile.getParent());
			} catch (Exception e) {
				e.printStackTrace();
				Messenger.showOKOnlyMessage(Messenger.PHENOTYPE_IMAGE_OPEN_ERROR, LF + e.getLocalizedMessage(), this);
			}

		}
	}

	public void addNewSequence() {
		alignment.addNewSequence();
		//requestRepaintAndRevalidateALL();
	}

	public void setFontCaseUpper(boolean selected) {
		int fontCase = CharPixels.CASE_UNTOUCHED;
		if(selected){
			fontCase = CharPixels.CASE_UPPER;
		}
		alignmentPane.setFontCase(fontCase);
		Settings.getFontCase().putIntValue(fontCase);		
		requestRepaintAndRevalidateALL();
	}


	public void showCharsetsRuler(boolean selected) {
		alignmentPane.setShowCharsetRuler(selected);
		Settings.getShowCharsetRuler().putBooleanValue(selected);
		requestRepaintAndRevalidateALL();
	}


	public void editCharsets() {
		TextEditFrame frame = new TextEditFrame(this);
		TextEditPanelCharsets panel = new TextEditPanelCharsets(frame, this);
		frame.init(panel);
		frame.setVisible(true);
	}

	private String goToPosTextFieldValue = "";
	public void goToPos() {
		TextEditDialog goToPosDlg = new TextEditDialog();
		goToPosDlg.showOKCancelTextEditor(goToPosTextFieldValue, TextEditDialog.TITLE_GO_TO_POS, this);

		if(goToPosDlg.getSelectedValue() == JOptionPane.OK_OPTION){
			String posText = goToPosDlg.getEditText();
			goToPosTextFieldValue=posText;

			Point currentPos = alignmentPane.getVisibleCenterMatrixPos();
			int newX = currentPos.x;
			int newY = currentPos.y;
			try {
				String xPos = StringUtils.substringBefore(posText, ",");
				newX = Integer.parseInt(xPos);
			} catch (NumberFormatException e) {
				// TODO maybe err handling
			}
			try {
				String yPos = StringUtils.substringAfter(posText, ",");
				newY = Integer.parseInt(yPos);
			} catch (NumberFormatException e) {
				// TODO maybe err handling
			}

			Point newPos = new Point(newX,newY);
			scrollToPos(newPos);
		}
	}

	public void adjustReadingFrameMinimizeStop() {

		AlignmentListModel origModel = alignment.getSequences();

		for(Sequence origSequence: origModel){
			Sequence seq = origSequence.getCopy();
			ArrayList<Sequence> newSeqs = new ArrayList<Sequence>(1);
			AlignmentListModel model = new AlignmentListModel(newSeqs);
			Alignment newAliment = new Alignment(model);
			int minStops = Integer.MAX_VALUE;
			GeneticCode bestCode = GeneticCode.DEFAULT;
			int bestFrame = 0;
			for(int frame = 0; frame < 3; frame ++){
				seq.insertGapAt(0);
				for(GeneticCode genCode: GeneticCode.allCodesArray){
					newAliment.setGeneticCode(genCode);				

					int stops = seq.countStopCodon();	  
					if(stops < minStops){
						minStops = stops;
						bestCode = genCode;
						bestFrame = frame;
					} 
				}				
			}

			logger.info("stops = " + minStops + " genCode = " + bestCode.name + " readingFrame = " + bestFrame);

			for(int n = 0; n < bestFrame; n++){
				origSequence.insertGapAt(0);
			}
		}

		repaint();
	}


	//	public void adjustReadingFrameMinimizeStopv2() {
	//
	//		Alignment newAlignment = new Alignment(alignment.getSequences().getCopy());
	//		
	//		for(int frame = 0; frame < 3; frame ++){
	//			
	//			for(GeneticCode genCode: GeneticCode.allCodesArray){
	//		
	//				for(Sequence seq: newAlignment.getSequences()){
	//					
	//					if(genCode == GeneticCode.DEFAULT){
	//						seq.insertGapAt(0);
	//					}
	//					
	//					int stops = seq.countStopCodon();
	//					  					  
	//				}
	//			}
	//		}
	//				
	//		repaint();
	//	}


	public void terminalGAPtoMissing() {
		getUndoControler().pushUndoState();
		alignment.terminalGAPtoMissing();
	}


	public void missingToGAP() {
		getUndoControler().pushUndoState();
		alignment.missingToGAP();
	}


	public void zoomOut() {

		// TODO Get Active Panel?
		alignmentPane.zoomOut();

	}

	public void zoomIn() {
		// TODO Get Active Panel?
		alignmentPane.zoomIn();

	}


}




