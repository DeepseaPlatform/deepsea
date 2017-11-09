package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.IntConstant;

/**
 * The {@code ARRAYLENGTH} instruction.
 * 
 * <h3>Description</h3>
 * 
 * <p>
 * replace the top stack element, which is an array, with its length
 * </p>
 * 
 * <h3>Effect</h3>
 * 
 * <p>
 * ..., a &rArr; ..., a.length
 * </p>
 * 
 * <h3>Symbolic effect</h3>
 * 
 * <p>
 * Peeks in the memory of the "real" VM and push the length of the actual array
 * parameter onto the symbolic stack (after removing the array address).
 * </p>
 */
public class ARRAYLENGTH extends Instruction {

	public ARRAYLENGTH(Stepper stepper, int position) {
		super(stepper, position, 190);
	}

	@Override
	public void execute(StepEvent event, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		frame.pop();
		frame.push(new IntConstant(Symbolizer.getRealArrayLength(event.thread(), 0)));
	}

	@Override
	public String toString() {
		return "arraylength ";
	}

}
