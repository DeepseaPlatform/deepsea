package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class FCONST extends Instruction {

	private final int index;

	public FCONST(Stepper stepper, int position, int index) {
		super(stepper, position, 11);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

}
