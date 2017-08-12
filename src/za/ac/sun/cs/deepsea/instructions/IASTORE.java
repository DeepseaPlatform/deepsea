package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class IASTORE extends Instruction {

	public IASTORE(Stepper stepper, int position) {
		super(stepper, position, 79);
	}
	
}
