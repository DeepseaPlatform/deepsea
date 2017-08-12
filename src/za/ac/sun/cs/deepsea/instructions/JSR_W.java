package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class JSR_W extends Instruction {

	private final int offset;

	public JSR_W(Stepper stepper, int position, int offset) {
		super(stepper, position, 201);
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public int getSize() {
		return 5;
	}

}
