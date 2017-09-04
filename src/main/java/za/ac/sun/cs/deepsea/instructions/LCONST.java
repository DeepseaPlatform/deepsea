package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

/**
 * UNIMPLEMENTED &amp; BROKEN
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class LCONST extends Instruction {

	private final int index;

	public LCONST(Stepper stepper, int position, int index) {
		super(stepper, position, 3);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

}
