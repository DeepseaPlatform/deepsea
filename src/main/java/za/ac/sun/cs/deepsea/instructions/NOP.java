package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

/**
 * The {@code NOP} instruction.
 * 
 * <h3>Description</h3>
 * 
 * <p>perform no operation</p>
 * 
 * <h3>Effect</h3>
 * 
 * <p>[no change to stack]</p>
 * 
 * <h3>Symbolic effect</h3>
 * 
 * <p>None</p>
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class NOP extends Instruction {

	/**
	 * Creates a {@code NOP} instruction.
	 * 
	 * @param stepper
	 *            the {@link Stepper} associated with this instruction
	 * @param position
	 *            offset of instruction in its method's bytecode
	 */
	public NOP(Stepper stepper, int position) {
		super(stepper, position, 0);
	}

	@Override
	public String toString() {
		return "nop";
	}

}
