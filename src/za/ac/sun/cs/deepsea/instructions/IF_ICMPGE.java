package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.Location;
import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;

public class IF_ICMPGE extends Instruction {

	private final int offset;

	public IF_ICMPGE(Stepper stepper, int position, int offset) {
		super(stepper, position, 162);
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
	public void execute(StepEvent event, Location loc, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		Expression e1 = frame.pop();
		Expression e0 = frame.pop();
		symbolizer.pushConjunct(new Operation(Operator.GE, e0, e1), position + offset);
	}
	
	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("if_icmpge -> ").append(position + offset);
		return sb.toString();
	}

}
