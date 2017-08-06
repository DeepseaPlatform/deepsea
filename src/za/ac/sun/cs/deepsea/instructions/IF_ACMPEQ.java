package za.ac.sun.cs.deepsea.instructions;

public class IF_ACMPEQ extends Instruction {

	private final int offset;

	public IF_ACMPEQ(final int offset) {
		super(165);
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
