package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class NOP extends Instruction {

	public NOP(Stepper stepper, int position) {
		super(stepper, position, 0);
	}

}
