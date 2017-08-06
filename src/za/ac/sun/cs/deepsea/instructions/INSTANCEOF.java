package za.ac.sun.cs.deepsea.instructions;

public class INSTANCEOF extends Instruction {

	private final int index;

	public INSTANCEOF(final int index) {
		super(193);
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
