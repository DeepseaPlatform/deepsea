package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

/**
 * UNIMPLEMENTED &amp; BROKEN
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class MULTIANEWARRAY extends Instruction {

	private final int index;

	private final int dimensions;
	
	public MULTIANEWARRAY(Stepper stepper, int position, int index, int dimensions) {
		super(stepper, position, 197);
		this.index = index;
		this.dimensions = dimensions;
	}

	public int getIndex() {
		return index;
	}

	public int getDimensions() {
		return dimensions;
	}
	
	@Override
	public int getSize() {
		return 4;
	}

}
