package za.ac.sun.cs.deepsea.diver;

import java.util.Stack;

import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;

/**
 * Class to store a path condition as a linked list of triples.
 * 
 * Each triple contains (1) an active conjunct that represents a branch
 * condition, (2) a passive conjunct that represents additional constraints on
 * the variables, and (3) a signal that indicates whether the branch was taken
 * ("<code>0</code>") or not ("<code>1</code>").
 *
 * For efficiency, this data structure also stores (1) the accumulated path
 * condition which is the conjunction of all path condition conjuncts of this
 * instance and its parent instances, and (2) the signature which is the
 * concatenation of this instance's signal and those of all its parents.
 */
public class SegmentedPathCondition {

	protected final SegmentedPathCondition parent;

	protected final Expression activeConjunct;

	protected final Expression passiveConjunct;

	protected final char signal;

	protected final Expression pathCondition;

	protected final String signature;

	protected String stringRep = null, stringRep0 = null;

	public SegmentedPathCondition(SegmentedPathCondition parent, Expression activeConjunct, Expression passiveConjunct,
			char signal) {
		this.parent = parent;
		this.activeConjunct = activeConjunct;
		this.passiveConjunct = passiveConjunct;
		this.signal = signal;
		Stack<Expression> conjuncts = new Stack<>();
		Expression pc = null;
		String sig = "";
		for (SegmentedPathCondition spc = this; spc != null; spc = spc.parent) {
			if (spc.signal != 'x') {
				sig += spc.signal;
			}
			if ((spc.activeConjunct != Operation.TRUE) && (spc.activeConjunct != null)) {
				conjuncts.push(spc.activeConjunct);
			}
			if ((spc.passiveConjunct != Operation.TRUE) && (spc.passiveConjunct != null)) {
				conjuncts.push(spc.passiveConjunct);
			}
		}
		if (conjuncts.isEmpty()) {
			pc = Operation.TRUE;
		} else {
			pc = conjuncts.pop();
			while (!conjuncts.isEmpty()) {
				pc = new Operation(Operator.AND, conjuncts.pop(), pc); 
			}
		}
		this.pathCondition = pc;
		this.signature = sig;
	}

	public SegmentedPathCondition getParent() {
		return parent;
	}

	public Expression getActiveConjunct() {
		return activeConjunct;
	}

	public Expression getPassiveConjunct() {
		return passiveConjunct;
	}

	public char getSignal() {
		return signal;
	}

	public Expression getPathCondition() {
		return pathCondition;
	}

	public String getSignature() {
		return signature;
	}

	public SegmentedPathCondition getNegated1st() {
		return new SegmentedPathCondition(parent, negate(activeConjunct), passiveConjunct, (signal == '0') ? '1' : '0');
	}

	/**
	 * Negates an expression without adding too many {@code NOT} operators. If
	 * the expression is already of the form "{@code NOT x}", then this method
	 * simply returns "{@code x}".
	 * 
	 * @param expression
	 *            the expression to negate
	 * @return the negation of the expression
	 */
	public static Expression negate(Expression expression) {
		if (expression instanceof Operation) {
			Operation operation = (Operation) expression;
			switch (operation.getOperator()) {
			case NOT:
				return operation.getOperand(0);
			case EQ:
				return new Operation(Operator.NE, operation.getOperand(0), operation.getOperand(1));
			case NE:
				return new Operation(Operator.EQ, operation.getOperand(0), operation.getOperand(1));
			case LT:
				return new Operation(Operator.GE, operation.getOperand(0), operation.getOperand(1));
			case LE:
				return new Operation(Operator.GT, operation.getOperand(0), operation.getOperand(1));
			case GT:
				return new Operation(Operator.LE, operation.getOperand(0), operation.getOperand(1));
			case GE:
				return new Operation(Operator.LT, operation.getOperand(0), operation.getOperand(1));
			default:
				break;
			}
		}
		return new Operation(Operator.NOT, expression);
	}

	@Override
	public String toString() {
		if (stringRep == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(toString0());
			sb.append("\npc: ").append(pathCondition.toString());
			sb.append("\nsig: ").append(signature);
			stringRep = sb.toString();
		}
		return stringRep;
	}

	public String toString0() {
		if (stringRep0 == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(signal);
			if (activeConjunct == null) {
				sb.append("  ").append(String.format("%-30s", "null"));
			} else {
				sb.append("  ").append(String.format("%-30s", activeConjunct.toString()));
			}
			if (passiveConjunct == null) {
				sb.append("  null");
			} else {
				sb.append("  ").append(passiveConjunct.toString());
			}
			if (parent != null) {
				sb.append('\n').append(parent.toString0());
			}
			stringRep0 = sb.toString();
		}
		return stringRep0;
	}
	
}
