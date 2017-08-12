package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class INVOKESPECIAL extends Instruction {

	private final int index;

	public INVOKESPECIAL(Stepper stepper, int position, int index) {
		super(stepper, position, 183);
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
