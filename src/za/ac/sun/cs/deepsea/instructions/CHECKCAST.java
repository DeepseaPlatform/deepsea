package za.ac.sun.cs.deepsea.instructions;

public class CHECKCAST extends Instruction {

	private final int index;

	public CHECKCAST(final int position, final int index) {
		super(position, 192);
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
