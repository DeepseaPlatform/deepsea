package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class LAND extends Instruction {

	public LAND(Stepper stepper, int position) {
		super(stepper, position, 127);
	}
	
}
