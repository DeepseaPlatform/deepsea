package za.ac.sun.cs.deepsea.instructions;

public class DCONST extends Instruction {
	
	private final int index;

	public DCONST(final int index) {
		super(14);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

}
