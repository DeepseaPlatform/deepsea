package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

/**
 * UNIMPLEMENTED &amp; BROKEN
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class DSTORE extends Instruction {

	private final int index;

	private final int size;

	public DSTORE(Stepper stepper, int position, int index) {
		super(stepper, position, 57);
		this.index = index;
		this.size = 1;
	}

	public DSTORE(Stepper stepper, int position, int index, boolean dummy) {
		super(stepper, position, 57);
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
