package za.ac.sun.cs.deepsea.instructions;

public class IF_ICMPEQ extends Instruction {

	private final int offset;

	public IF_ICMPEQ(final int offset) {
		super(159);
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
