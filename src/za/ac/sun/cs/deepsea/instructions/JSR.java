package za.ac.sun.cs.deepsea.instructions;

public class JSR extends Instruction {

	private final int offset;

	public JSR(final int position, final int offset) {
		super(position, 168);
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
