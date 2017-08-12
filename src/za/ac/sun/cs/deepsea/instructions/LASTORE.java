package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class LASTORE extends Instruction {

	public LASTORE(Stepper stepper, int position) {
		super(stepper, position, 80);
	}
	
}
