package za.ac.sun.cs.deepsea.diver;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import za.ac.sun.cs.green.expr.Expression;

public class SymbolicFrame {

	protected final Stack<Expression> stack = new Stack<>();

	protected final Map<Integer, Expression> locals = new HashMap<>();

	public Expression pop() {
		return stack.pop();
	}

	public void push(Expression value) {
		stack.push(value);
	}

	public Expression getLocal(int index) {
		return locals.get(index);
	}

	public void setLocal(int index, Expression value) {
		locals.put(index, value);
	}

}
