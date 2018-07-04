package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Expression;

public class IRETURN extends Instruction {

	public IRETURN(Stepper stepper, int position) {
		super(stepper, position, 172);
	}
	
	@Override
	public void execute(StepEvent event, Symbolizer symbolizer) {
		Expression e = symbolizer.getTopFrame().pop();
		if (symbolizer.popFrame()) {
			symbolizer.getTopFrame().push(e);
		}
	}

	@Override
	public String toString() {
		return "ireturn";
	}

}
