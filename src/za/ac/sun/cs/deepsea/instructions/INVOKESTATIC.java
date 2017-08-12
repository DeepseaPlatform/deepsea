package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class INVOKESTATIC extends Instruction {

	private final int index;

	public INVOKESTATIC(Stepper stepper, int position, int index) {
		super(stepper, position, 184);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int getSize() {
		return 3;
	}

	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("invokestatic ").append(index);
		return sb.toString();
	}

}
