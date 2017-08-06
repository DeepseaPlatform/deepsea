package za.ac.sun.cs.deepsea.instructions;

public class IFNONNULL extends Instruction {

	private final int offset;

	public IFNONNULL(final int position, final int offset) {
		super(position, 199);
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
