package za.ac.sun.cs.deepsea.instructions;

public class IF_ICMPNE extends Instruction {

	private final int offset;

	public IF_ICMPNE(final int offset) {
		super(160);
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
