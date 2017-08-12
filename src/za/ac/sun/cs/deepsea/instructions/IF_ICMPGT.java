package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class IF_ICMPGT extends Instruction {

	private final int offset;

	public IF_ICMPGT(Stepper stepper, int position, int offset) {
		super(stepper, position, 163);
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
