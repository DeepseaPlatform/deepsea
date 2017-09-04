package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;

public class IMUL extends Instruction {

	public IMUL(Stepper stepper, int position) {
		super(stepper, position, 104);
	}
	
	@Override
	public void execute(StepEvent event, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		Expression e1 = frame.pop();
		Expression e0 = frame.pop();
		Expression r = new Operation(Operator.MUL, e0, e1);
		if (Instruction.isNonlinearExpression(r)) {
			frame.push(approximateNonlinearExpression(r));
		} else {
			frame.push(r);
		}
	}

	@Override
	public String toString() {
		return "imul";
	}

}
