package za.ac.sun.cs.deepsea.instructions;

public class INVOKEDYNAMIC extends Instruction {

	private final int index;

	public INVOKEDYNAMIC(final int index) {
		super(186);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int getSize() {
		return 5;
	}

}
