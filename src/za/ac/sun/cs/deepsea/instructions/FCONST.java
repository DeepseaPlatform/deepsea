package za.ac.sun.cs.deepsea.instructions;

public class FCONST extends Instruction {

	private final int index;

	public FCONST(final int position, final int index) {
		super(position, 11);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

}
