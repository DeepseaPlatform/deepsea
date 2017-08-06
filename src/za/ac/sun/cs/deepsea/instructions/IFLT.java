package za.ac.sun.cs.deepsea.instructions;

public class IFLT extends Instruction {

	private final int offset;

	public IFLT(final int position, final int offset) {
		super(position, 155);
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
