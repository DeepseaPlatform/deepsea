package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.Location;

import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;

public class IMUL extends Instruction {

	public IMUL(final int position) {
		super(position, 104);
	}
	
	@Override
	public void execute(Location loc, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		Expression e1 = frame.pop();
		Expression e0 = frame.pop();
		frame.push(new Operation(Operator.MUL, e0, e1));
	}
	
	@Override
	public String toString() {
		return "imul";
	}

}
