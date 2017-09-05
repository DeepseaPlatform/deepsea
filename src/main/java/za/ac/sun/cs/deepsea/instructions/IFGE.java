package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class IFGE extends Instruction {

	private final int offset;

	public IFGE(Stepper stepper, int position, int offset) {
		super(stepper, position, 156);
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public int getSize() {
		return 3;
	}

}