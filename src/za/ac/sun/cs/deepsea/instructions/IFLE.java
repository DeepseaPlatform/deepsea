package za.ac.sun.cs.deepsea.instructions;

public class IFLE extends Instruction {

	private final int offset;

	public IFLE(final int offset) {
		super(158);
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
