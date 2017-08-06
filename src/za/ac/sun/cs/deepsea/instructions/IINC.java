package za.ac.sun.cs.deepsea.instructions;

public class IINC extends Instruction {

	private final int index;

	private final int value;
	
	public IINC(final int index, final int value) {
		super(132);
		this.index = index;
		this.value = value;
	}

	public int getIndex() {
		return index;
	}
	
	public int getValue() {
		return value;
	}

	@Override
	public int getSize() {
		return 3;
	}

}
