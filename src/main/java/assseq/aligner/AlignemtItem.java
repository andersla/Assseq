package assseq.aligner;

import assseq.externalcommands.CommandItem;

public class AlignemtItem {

	private CommandItem alignAllCmd;
	private CommandItem addAlignCmd;

	public AlignemtItem(CommandItem alignAllCmd, CommandItem addAlignCmd) {
		this.alignAllCmd = alignAllCmd;
		this.addAlignCmd = addAlignCmd;
	}

	public CommandItem getAddAlignCmd() {
		return addAlignCmd;
	}

	public CommandItem getAlignAllCmd() {
		return alignAllCmd;
	}

}
