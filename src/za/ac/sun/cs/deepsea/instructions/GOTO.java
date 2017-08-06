package za.ac.sun.cs.deepsea.instructions;

public class GOTO extends Instruction {

	private final int offset;

	public GOTO(final int offset) {
		super(167);
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public int getSize() {
		return 3;
	}

}
