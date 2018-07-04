package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;

public class IADD extends Instruction {

	public IADD(Stepper stepper, int position) {
		super(stepper, position, 96);
	}
	
	@Override
	public void execute(StepEvent event, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		Expression e1 = frame.pop();
		Expression e0 = frame.pop();
		if ((e0 instanceof IntConstant) && (e1 instanceof IntConstant)) {
			frame.push(new IntConstant(((IntConstant) e0).getValue() + ((IntConstant) e1).getValue()));
		} else {
			frame.push(new Operation(Operator.ADD, e0, e1));
		}
	}
	
	@Override
	public String toString() {
		return "iadd";
	}

}
