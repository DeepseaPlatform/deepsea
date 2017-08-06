package za.ac.sun.cs.deepsea.instructions;

public class PUTFIELD extends Instruction {

	private final int index;

	public PUTFIELD(final int index) {
		super(181);
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
