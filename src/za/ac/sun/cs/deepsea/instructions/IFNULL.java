package za.ac.sun.cs.deepsea.instructions;

public class IFNULL extends Instruction {

	private final int offset;

	public IFNULL(final int position, final int offset) {
		super(position, 198);
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
