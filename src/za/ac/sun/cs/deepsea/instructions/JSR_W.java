package za.ac.sun.cs.deepsea.instructions;

public class JSR_W extends Instruction {

	private final int offset;

	public JSR_W(final int position, final int offset) {
		super(position, 201);
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public int getSize() {
		return 5;
	}

}
