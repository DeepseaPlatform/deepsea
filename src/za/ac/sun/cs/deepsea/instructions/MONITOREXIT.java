package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class MONITOREXIT extends Instruction {

	public MONITOREXIT(Stepper stepper, int position) {
		super(stepper, position, 195);
	}
	
}
