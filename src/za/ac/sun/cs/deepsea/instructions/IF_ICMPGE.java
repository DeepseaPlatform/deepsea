package za.ac.sun.cs.deepsea.instructions;

public class IF_ICMPGE extends Instruction {

	private final int offset;

	public IF_ICMPGE(final int offset) {
		super(162);
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
