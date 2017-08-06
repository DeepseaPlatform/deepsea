package za.ac.sun.cs.deepsea.instructions;

public class INVOKEVIRTUAL extends Instruction {

	private final int index;

	public INVOKEVIRTUAL(final int index) {
		super(182);
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
