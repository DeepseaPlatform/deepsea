package za.ac.sun.cs.deepsea.instructions;

public class SIPUSH extends Instruction {

	private final int value;

	public SIPUSH(final int value) {
		super(17);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public int getSize() {
		return 3;
	}

}
