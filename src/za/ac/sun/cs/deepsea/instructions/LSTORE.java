package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

/**
 * UNIMPLEMENTED &amp; BROKEN
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class LSTORE extends Instruction {

	private final int index;

	private final int size;

	public LSTORE(Stepper stepper, int position, int index) {
		super(stepper, position, 55);
		this.index = index;
		this.size = 1;
	}

	public LSTORE(Stepper stepper, int position, int index, boolean dummy) {
		super(stepper, position, 55);
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
