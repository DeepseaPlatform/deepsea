package za.ac.sun.cs.deepsea.instructions;

public class PUTSTATIC extends Instruction {

	private final int index;

	public PUTSTATIC(final int index) {
		super(179);
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
