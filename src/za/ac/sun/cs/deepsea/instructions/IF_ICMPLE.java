package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.Location;

import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;

public class IF_ICMPLE extends Instruction {

	private final int offset;

	public IF_ICMPLE(final int position, final int offset) {
		super(position, 164);
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
	public void execute(Location loc, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		Expression e1 = frame.pop();
		Expression e0 = frame.pop();
		symbolizer.pushConjunct(new Operation(Operator.LE, e0, e1), position + offset);
	}
	
	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("if_icmple -> ").append(position + offset);
		return sb.toString();
	}

}
