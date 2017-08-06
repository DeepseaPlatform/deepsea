package za.ac.sun.cs.deepsea.instructions;

public class INVOKESPECIAL extends Instruction {

	private final int index;

	public INVOKESPECIAL(final int index) {
		super(183);
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
