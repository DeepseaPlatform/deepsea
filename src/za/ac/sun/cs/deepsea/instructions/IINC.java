package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class IINC extends Instruction {

	private final int index;

	private final int value;
	
	public IINC(Stepper stepper, int position, int index, int value) {
		super(stepper, position, 132);
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
