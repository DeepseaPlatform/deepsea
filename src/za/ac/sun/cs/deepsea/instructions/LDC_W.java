package za.ac.sun.cs.deepsea.instructions;

public class LDC_W extends Instruction {

	private final int index;

	public LDC_W(final int index) {
		super(19);
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
