package za.ac.sun.cs.deepsea.instructions;

public class INVOKESTATIC extends Instruction {

	private final int index;

	public INVOKESTATIC(final int index) {
		super(184);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int getSize() {
		return 3;
	}

}
