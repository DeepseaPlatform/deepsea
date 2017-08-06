package za.ac.sun.cs.deepsea.instructions;

public class IFGT extends Instruction {

	private final int offset;

	public IFGT(final int position, final int offset) {
		super(position, 157);
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
