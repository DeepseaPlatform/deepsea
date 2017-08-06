package za.ac.sun.cs.deepsea.instructions;

public class RET extends Instruction {

	private final int index;

	public RET(final int index) {
		super(169);
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
