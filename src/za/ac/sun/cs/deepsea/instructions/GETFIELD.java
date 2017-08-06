package za.ac.sun.cs.deepsea.instructions;

public class GETFIELD extends Instruction {

	private final int index;

	public GETFIELD(final int position, final int index) {
		super(position, 180);
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
