package za.ac.sun.cs.deepsea.instructions;

public class IFGE extends Instruction {

	private final int offset;

	public IFGE(final int offset) {
		super(156);
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
