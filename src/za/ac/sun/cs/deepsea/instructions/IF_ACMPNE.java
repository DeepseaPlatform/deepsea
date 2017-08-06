package za.ac.sun.cs.deepsea.instructions;

public class IF_ACMPNE extends Instruction {

	private final int offset;

	public IF_ACMPNE(final int position, final int offset) {
		super(position, 166);
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
