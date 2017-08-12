package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class IF_ICMPLT extends Instruction {

	private final int offset;

	public IF_ICMPLT(Stepper stepper, int position, int offset) {
		super(stepper, position, 161);
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
