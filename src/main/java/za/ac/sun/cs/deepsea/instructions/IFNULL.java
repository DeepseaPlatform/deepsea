package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class IFNULL extends Instruction {

	private final int offset;

	public IFNULL(Stepper stepper, int position, int offset) {
		super(stepper, position, 198);
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
