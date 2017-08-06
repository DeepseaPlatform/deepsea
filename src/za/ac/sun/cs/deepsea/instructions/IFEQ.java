package za.ac.sun.cs.deepsea.instructions;

public class IFEQ extends Instruction {

	private final int offset;

	public IFEQ(final int offset) {
		super(153);
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
