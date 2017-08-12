package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class IF_ACMPEQ extends Instruction {

	private final int offset;

	public IF_ACMPEQ(Stepper stepper, int position, int offset) {
		super(stepper, position, 165);
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
