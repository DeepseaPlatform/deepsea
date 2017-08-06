package za.ac.sun.cs.deepsea.instructions;

public class GOTO_W extends Instruction {

	private final int offset;

	public GOTO_W(final int offset) {
		super(200);
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public int getSize() {
		return 5;
	}

}
