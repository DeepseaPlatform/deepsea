package za.ac.sun.cs.deepsea.instructions;

public class LCONST extends Instruction {

	private final int index;

	public LCONST(final int position, final int index) {
		super(position, 3);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

}
