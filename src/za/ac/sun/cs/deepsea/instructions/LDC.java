package za.ac.sun.cs.deepsea.instructions;

public class LDC extends Instruction {

	private final int index;

	public LDC(final int position, final int index) {
		super(position, 18);
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
