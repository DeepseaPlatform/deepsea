package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;

public class IFLE extends Instruction {

	private final int offset;

	public IFLE(Stepper stepper, int position, int offset) {
		super(stepper, position, 158);
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public int getSize() {
		return 3;
	}

	@Override
	public void execute(StepEvent event, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		Expression e0 = new IntConstant(0);
		Expression e1 = frame.pop();
		symbolizer.pushConjunct(new Operation(Operator.LE, e1, e0), position + offset);
	}
	
	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("ifle -> ").append(position + offset);
		return sb.toString();
	}

}
