package za.ac.sun.cs.deepsea.instructions;

public class BIPUSH extends Instruction {

	private final int index;

	public BIPUSH(final int index) {
		super(16);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int getSize() {
		return 2;
	}

}
