package za.ac.sun.cs.deepsea.instructions;

public class GETFIELD extends Instruction {

	private final int index;

	public GETFIELD(final int index) {
		super(180);
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
