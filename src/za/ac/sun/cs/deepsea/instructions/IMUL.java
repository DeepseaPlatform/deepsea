package za.ac.sun.cs.deepsea.instructions;

import java.util.HashMap;
import java.util.Map;

import com.sun.jdi.Location;
import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntVariable;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;

public class IMUL extends Instruction {

	private static final Map<IntVariable, Expression> substitutions = new HashMap<>();

	public IMUL(Stepper stepper, int position) {
		super(stepper, position, 104);
	}
	
	@Override
	public void execute(StepEvent event, Location loc, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		Expression e1 = frame.pop();
		Expression e0 = frame.pop();
		Expression r = new Operation(Operator.MUL, e0, e1);
		if (Instruction.simplify(r) == null) {
			// System.out.println("r: " + r);
			// System.out.println("simp(r): " + Instruction.simplify(r));
			// System.out.println("subV(r): " + substituteValues(r));
			// System.out.println("simp(subV(r): " + Instruction.simplify(substituteValues(r)));
			substitutions.clear();
			frame.push(substituteValues(r));
//			if (substitutions.size() > 0) {
//				for (Map.Entry<IntVariable, Expression> s : substitutions.entrySet()) {
//					Operation conjunct = new Operation(Operation.Operator.EQ, s.getKey(), s.getValue());
//					symbolizer.pushExtraConjunct(conjunct);
//				}
//			}
		} else {
			frame.push(r);
		}
	}

	private Expression substituteValues(Expression expression) {
		if (expression instanceof IntVariable) {
			IntVariable variable = (IntVariable) expression;
			Expression e = stepper.getDive().getActualValue(variable.getName());
			if (e != null) {
				substitutions.put(variable, e);
				return e;
			} else {
				return Operation.ZERO;
			}
		} else if (expression instanceof Operation) {
			Operation operation = (Operation) expression;
			Operator operator = operation.getOperator();
			int n = operation.getOperatandCount();
			Expression[] operands = new Expression[n];
			for (int i = 0; i < n; i++) {
				operands[i] = substituteValues(operation.getOperand(i));
			}
			return new Operation(operator, operands);
		} else {
			return expression;
		}
	}

	@Override
	public String toString() {
		return "imul";
	}

}
