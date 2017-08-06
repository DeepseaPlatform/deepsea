package za.ac.sun.cs.deepsea.instructions;

public class NEW extends Instruction {

	private final int index;

	public NEW(final int index) {
		super(187);
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
