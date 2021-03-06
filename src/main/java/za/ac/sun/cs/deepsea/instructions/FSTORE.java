package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

/**
 * UNIMPLEMENTED &amp; BROKEN
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class FSTORE extends Instruction {

	private final int index;

	private final int size;

	public FSTORE(Stepper stepper, int position, int index) {
		super(stepper, position, 56);
		this.index = index;
		this.size = 1;
	}

	public FSTORE(Stepper stepper, int position, int index, boolean dummy) {
		super(stepper, position, 56);
		this.index = index;
		this.size = 2;
	}
	
	public int getIndex() {
		return index;
	}

	@Override
	public int getSize() {
		return size;
	}

}
