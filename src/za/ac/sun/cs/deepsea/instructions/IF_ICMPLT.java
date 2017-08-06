package za.ac.sun.cs.deepsea.instructions;

public class IF_ICMPLT extends Instruction {

	private final int offset;

	public IF_ICMPLT(final int position, final int offset) {
		super(position, 161);
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
