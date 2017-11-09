package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Operation;

/**
 * The {@code ACONST_NULL} instruction.
 * 
 * <h3>Description</h3>
 * 
 * <p>
 * push a null reference onto the stack
 * </p>
 * 
 * <h3>Effect</h3>
 * 
 * <p>
 * ... &rArr; ..., null
 * </p>
 * 
 * <h3>Symbolic effect</h3>
 * 
 * <p>
 * Pushes the integer constant {@link za.ac.sun.cs.green.expr.Operation}{@code .ZERO} onto the stack.
 * </p>
 */
public class ACONST_NULL extends Instruction {

	/**
	 * Creates an {@code ACONST_NULL} instruction.
	 * 
	 * @param stepper
	 *            the {@link Stepper} associated with this instruction
	 * @param position
	 *            offset of instruction in its method's bytecode
	 */
	public ACONST_NULL(Stepper stepper, int position) {
		super(stepper, position, 1);
	}

	/**
	 * Responds to the {@link StepEvent} by executing the instruction. This
	 * method pushes the integer constant {@code 0} ({@link Operation#ZERO})
	 * onto the symbolic stack.
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
		frame.push(Operation.ZERO);
	}

	@Override
	public String toString() {
		return "aconst_null";
	}

}
