package za.ac.sun.cs.deepsea.instructions;

import java.util.List;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Value;
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
public class ARRAYLENGTH extends Instruction {

	public ARRAYLENGTH(Stepper stepper, int position) {
		super(stepper, position, 190);
	}
	
	@Override
	public void execute(StepEvent event, Symbolizer symbolizer) {
		try {
			StackFrame frame = event.thread().frame(0);
			List<Value> actualValues = frame.getArgumentValues();
			ArrayReference array = (ArrayReference) actualValues.get(0);
			SymbolicFrame symbolicFrame = symbolizer.getTopFrame();
			symbolicFrame.push(new IntConstant(array.length()));
		} catch (IncompatibleThreadStateException x) {
			x.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return "arraylength ";
	}
	
}
