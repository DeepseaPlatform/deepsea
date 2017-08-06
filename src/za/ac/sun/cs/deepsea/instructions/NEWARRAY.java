package za.ac.sun.cs.deepsea.instructions;

public class NEWARRAY extends Instruction {

	private final int index;

	public NEWARRAY(final int index) {
		super(188);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int getSize() {
		return 2;
	}

}
