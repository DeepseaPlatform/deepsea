package za.ac.sun.cs.deepsea.instructions;

public class PUTSTATIC extends Instruction {

	private final int index;

	public PUTSTATIC(final int position, final int index) {
		super(position, 179);
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
