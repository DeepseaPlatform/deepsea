package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.IntConstant;

/**
 * UNIMPLEMENTED &amp; BROKEN
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class IALOAD extends Instruction {

	public IALOAD(Stepper stepper, int position) {
		super(stepper, position, 46);
	}
	
	@Override
	public void execute(StepEvent event, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		int index = ((IntConstant) frame.pop()).getValue();
		int arrayId = ((IntConstant) frame.pop()).getValue();
		frame.push(symbolizer.getArrayValue(arrayId, index));
	}
	
	@Override
	public String toString() {
		return "iaload ";
	}
	
}
