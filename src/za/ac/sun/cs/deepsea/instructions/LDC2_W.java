package za.ac.sun.cs.deepsea.instructions;

public class LDC2_W extends Instruction {

	private final int index;

	public LDC2_W(final int index) {
		super(20);
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
