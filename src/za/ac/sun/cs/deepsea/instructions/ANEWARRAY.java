package za.ac.sun.cs.deepsea.instructions;

public class ANEWARRAY extends Instruction {

	private final int index;

	public ANEWARRAY(final int index) {
		super(189);
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
