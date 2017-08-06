package za.ac.sun.cs.deepsea.instructions;

public class NEWARRAY extends Instruction {

	private final int index;

	public NEWARRAY(final int position, final int index) {
		super(position, 188);
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
