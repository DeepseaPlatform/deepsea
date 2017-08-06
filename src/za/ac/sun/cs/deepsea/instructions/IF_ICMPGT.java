package za.ac.sun.cs.deepsea.instructions;

public class IF_ICMPGT extends Instruction {

	private final int offset;

	public IF_ICMPGT(final int offset) {
		super(163);
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
