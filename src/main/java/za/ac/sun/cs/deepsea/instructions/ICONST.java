package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.IntConstant;

/**
 * The {@code ICONST_M1}, {@code ICONST_0}, {@code ICONST_1}, {@code ICONST_2},
 * {@code ICONST_3}, {@code ICONST_4}, and {@code ICONST_5} instructions. In
 * other words, this instruction represent several actual JVM instructions.
 * 
 * <h3>Description</h3>
 * 
 * <p>
 * load an integer value onto the stack
 * </p>
 * 
 * <h3>Effect</h3>
 * 
 * <p>
 * ... &rArr; ..., value
 * </p>
 * 
 * <h3>Symbolic effect</h3>
 * 
 * <p>
 * Pushes the corresponding integer constant onto the stack.
 * </p>
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class ICONST extends Instruction {

	/**
	 * The value to push onto the stack.
	 */
	private final int value;

	/**
	 * Creates an {@code ICONST} instruction.
	 * 
	 * @param stepper
	 *            the {@link Stepper} associated with this instruction
	 * @param position
	 *            offset of instruction in its method's bytecode
	 * @param value
	 *            TODO
	 */
	public ICONST(Stepper stepper, int position, int value) {
		super(stepper, position, 3);
		this.value = value;
	}

	/**
	 * Returns the value that this instruction pushes onto the stack.
	 * 
	 * @return TODO
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Responds to the {@link StepEvent} by executing the instruction. This
	 * method pushes an integer constant that represents {@link #value} onto the
	 * symbolic stack.
	 * 
	 * @param event
	 *            the triggering event
	 * @param symbolizer
	 *            the symbolic state
	 * @see za.ac.sun.cs.deepsea.instructions.Instruction#execute(com.sun.jdi.event.StepEvent,
	 *      za.ac.sun.cs.deepsea.diver.Symbolizer)
	 */
	@Override
	public void execute(StepEvent event, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		frame.push(new IntConstant(value));
	}

	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("iconst_");
		if (value == -1) {
			sb.append("m1");
		} else {
			sb.append(value);
		}
		return sb.toString();
	}

}
