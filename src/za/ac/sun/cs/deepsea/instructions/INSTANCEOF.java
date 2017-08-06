package za.ac.sun.cs.deepsea.instructions;

public class INSTANCEOF extends Instruction {

	private final int index;

	public INSTANCEOF(final int position, final int index) {
		super(position, 193);
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
