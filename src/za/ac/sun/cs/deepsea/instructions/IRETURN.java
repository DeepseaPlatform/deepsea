package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.Location;

import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Expression;

public class IRETURN extends Instruction {

	public IRETURN() {
		super(172);
	}
	
	@Override
	public void execute(Location loc, Symbolizer symbolizer) {
		Expression e = symbolizer.getTopFrame().pop();
		if (	symbolizer.popFrame()) {
			symbolizer.getTopFrame().push(e);
		}
	}

	@Override
	public String toString() {
		return "ireturn";
	}

}
