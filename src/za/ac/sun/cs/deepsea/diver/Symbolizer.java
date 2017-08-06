package za.ac.sun.cs.deepsea.diver;

import java.util.Stack;

import com.sun.jdi.Location;

import za.ac.sun.cs.deepsea.instructions.Instruction;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;

//import java.util.logging.Logger;

public class Symbolizer {

//	private final Diver diver;
//	private final Logger log;

	private boolean inSymbolicMode;

	private final Stack<SymbolicFrame> frames = new Stack<>();

	private Expression pathCondition = Operation.TRUE;

	private Expression pendingConjunct = null;

	private int pendingTarget = -1;

	public Symbolizer(final Diver diver) {
//		this.diver = diver;
//		this.log = this.diver.getLog();
		inSymbolicMode = false;
	}

	public boolean inSymbolicMode() {
		return inSymbolicMode;
	}

	public void enterSymbolicMode() {
		assert !inSymbolicMode;
		assert frames.isEmpty();
		inSymbolicMode = true;
	}

	public SymbolicFrame pushNewFrame() {
		return frames.push(new SymbolicFrame());
	}

	public boolean popFrame() {
		assert inSymbolicMode;
		assert !frames.isEmpty();
		frames.pop();
		if (frames.isEmpty()) {
			inSymbolicMode = false;
		}
		return inSymbolicMode;
	}

	public SymbolicFrame getTopFrame() {
		return frames.peek();
	}

	public Expression getPathCondition() {
		return pathCondition;
	}

	public void pushConjunct(Expression conjunct, int target) {
		assert pendingConjunct == null;
		pendingConjunct = conjunct;
		pendingTarget = target;
	}

	public void execute(Location loc, Instruction ins) {
		if (inSymbolicMode) {
			if (pendingConjunct != null) {
				if (loc.codeIndex() == pendingTarget) {
					pendingConjunct = new Operation(Operator.NOT, pendingConjunct);
				}
				pathCondition = new Operation(Operator.AND, pendingConjunct, pathCondition);
				pendingConjunct = null;
			}
			ins.execute(loc, this);
		}
	}

}
