package za.ac.sun.cs.deepsea.instructions;

public class PUTFIELD extends Instruction {

	private final int index;

	public PUTFIELD(final int position, final int index) {
		super(position, 181);
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
