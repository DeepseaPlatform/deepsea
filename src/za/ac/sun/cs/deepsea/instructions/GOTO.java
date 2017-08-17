package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class GOTO extends Instruction {

	private final int offset;

	public GOTO(Stepper stepper, int position, int offset) {
		super(stepper, position, 167);
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public int getSize() {
		return 3;
	}

	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("goto -> ").append(position + offset);
		return sb.toString();
	}

}
