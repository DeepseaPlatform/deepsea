package za.ac.sun.cs.deepsea.diver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.sun.jdi.Location;
import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.instructions.Instruction;
import za.ac.sun.cs.green.expr.Constant;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntVariable;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;

public class Symbolizer {

	//	private final Diver diver;
	//	private final Logger log;

	private boolean inSymbolicMode;

	private final Stack<SymbolicFrame> frames = new Stack<>();

	private int objectIdCount = 0;

	private int newVariableCount = 0;
	
	private final Map<String, Expression> instanceData = new HashMap<>();

	private Expression pathCondition = Operation.TRUE;

	private String signature = "";

	private Expression pendingConjunct = null;

	private Expression pendingExtraConjunct = null;

	private int pendingTarget = -1;

	private Set<String> conjunctSet = new HashSet<>();

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
		if (frames.isEmpty()) {
			return null;
		} else {
			return frames.peek();
		}
	}

	public Expression getPathCondition() {
		return pathCondition;
	}

	public String getSignature() {
		return signature;
	}

	private boolean isConstantConjunct(Expression conjunct) {
		if (conjunct instanceof Operation) {
			Operation operation = (Operation) conjunct;
			int n = operation.getOperatandCount();
			for (int i = 0; i < n; i++) {
				if (!isConstantConjunct(operation.getOperand(i))) {
					return false;
				}
			}
			return true;
		} else {
			return (conjunct instanceof Constant); 
		}
	}

	public void pushConjunct(Expression conjunct, int target) {
		assert pendingConjunct == null;
		if (!isConstantConjunct(conjunct) && conjunctSet.add(conjunct.toString())) {
			pendingConjunct = conjunct;
			pendingTarget = target;
		}
	}

	public void pushExtraConjunct(Expression extraConjunct) {
		if (!isConstantConjunct(extraConjunct)) {
			if (pendingExtraConjunct == null) {
				pendingExtraConjunct = extraConjunct;
			} else {
				pendingExtraConjunct = new Operation(Operator.AND, extraConjunct, pendingExtraConjunct);
			}
		}
	}

	public void execute(StepEvent event, Location loc, Instruction ins) {
		if (inSymbolicMode) {
			if (pendingConjunct != null) {
				char branch = '1';
				if (loc.codeIndex() != pendingTarget) {
					branch = '0';
					pendingConjunct = new Operation(Operator.NOT, pendingConjunct);
				}
				signature = branch + signature;
				if (pendingExtraConjunct != null) {
					pathCondition = new Operation(Operator.AND, pendingExtraConjunct, pathCondition);
				}
				pathCondition = new Operation(Operator.AND, pendingConjunct, pathCondition);
				pendingConjunct = null;
				pendingExtraConjunct = null;
			}
			ins.execute(event, this);
		}
	}

	public int incrAndGetNewObjectId() {
		return ++objectIdCount;
	}

	public void putField(int objectId, String fieldName, Expression value) {
		String fullFieldName = objectId + ":::" + fieldName;
		instanceData.put(fullFieldName, value);
	}

	public Expression getField(int objectId, String fieldName) {
		String fullFieldName = objectId + ":::" + fieldName;
		Expression value = instanceData.get(fullFieldName);
		if (value == null) {
			// TODO create bounds on fields
			value = new IntVariable("$q" + newVariableCount++, 0, 999);
			instanceData.put(fullFieldName, value);
		}
		return value;
	}

}
