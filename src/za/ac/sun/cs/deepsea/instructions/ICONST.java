package za.ac.sun.cs.deepsea.instructions;

public class ICONST extends Instruction {

	private final int index;

	public ICONST(final int index) {
		super(3);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

}
