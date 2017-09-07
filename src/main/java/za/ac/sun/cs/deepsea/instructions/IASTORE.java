package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;

/**
 * UNIMPLEMENTED &amp; BROKEN
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class IASTORE extends Instruction {

	public IASTORE(Stepper stepper, int position) {
		super(stepper, position, 79);
	}
	
	@Override
	public void execute(StepEvent event, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		Expression value = frame.pop();
		int index = ((IntConstant) frame.pop()).getValue();
		int arrayId = ((IntConstant) frame.pop()).getValue();
		symbolizer.addArrayValue(arrayId, index, value);
	}
	
	@Override
	public String toString() {
		return "iastore ";
	}
	
}
