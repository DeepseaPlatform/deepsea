package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class DCONST extends Instruction {
	
	private final int index;

	public DCONST(Stepper stepper, int position, int index) {
		super(stepper, position, 14);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

}
