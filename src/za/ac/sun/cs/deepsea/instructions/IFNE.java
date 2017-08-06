package za.ac.sun.cs.deepsea.instructions;

public class IFNE extends Instruction {

	private final int offset;

	public IFNE(final int position, final int offset) {
		super(position, 154);
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
