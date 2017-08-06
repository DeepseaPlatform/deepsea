package za.ac.sun.cs.deepsea.instructions;

public class IF_ICMPLE extends Instruction {

	private final int offset;

	public IF_ICMPLE(final int offset) {
		super(164);
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
