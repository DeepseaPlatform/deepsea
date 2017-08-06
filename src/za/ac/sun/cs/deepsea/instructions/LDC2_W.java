package za.ac.sun.cs.deepsea.instructions;

public class LDC2_W extends Instruction {

	private final int index;

	public LDC2_W(final int position, final int index) {
		super(position, 20);
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
