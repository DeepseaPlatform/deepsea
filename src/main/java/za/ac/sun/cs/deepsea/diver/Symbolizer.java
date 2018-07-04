package za.ac.sun.cs.deepsea.diver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.logging.log4j.Logger;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.instructions.Instruction;
import za.ac.sun.cs.green.expr.Constant;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntVariable;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;

public class Symbolizer {

	private final Logger log;

	private boolean inSymbolicMode;

	private final Stack<SymbolicFrame> frames = new Stack<>();

	private int objectIdCount = 0;

	private static int newVariableCount = 0;
	
	private final Map<String, Expression> instanceData = new HashMap<>();

	private SegmentedPathCondition segmentedPathCondition = new SegmentedPathCondition(null, Operation.TRUE, Operation.TRUE, 'x');

	@Deprecated
	private Expression pathCondition = Operation.TRUE;
	
	@Deprecated
	private String signature = "";

	private Expression pendingConjunct = null;

	private Expression pendingExtraConjunct = null;

	private int pendingTarget = -1;

	private Set<String> conjunctSet = new HashSet<>();

	public Symbolizer(Logger log) {
		this.log = log;
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

	public Map<String, Expression> getInstanceData() {
		return instanceData;
	}

	public static String getNewVariableName() {
		return "$" + newVariableCount++;
	}

	public SegmentedPathCondition getSegmentedPathCondition() {
		return segmentedPathCondition;
	}

	@Deprecated
	public Expression getPathCondition() {
		return pathCondition;
	}

	@Deprecated
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
		String c = conjunct.toString();
		if (isConstantConjunct(conjunct)) {
			log.trace(">>> constant conjunct ignored: {}", c);
		} else if (conjunctSet.add(c)) {
			pendingConjunct = conjunct;
			pendingTarget = target;
			log.trace(">>> adding conjunct: {}", c);
		} else {
			log.trace(">>> duplicate conjunct ignored: {}", c);
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
//				signature = branch + signature;
//				if (pendingExtraConjunct != null) {
//					pathCondition = new Operation(Operator.AND, pendingExtraConjunct, pathCondition);
//				}
//				pathCondition = new Operation(Operator.AND, pendingConjunct, pathCondition);
				segmentedPathCondition = new SegmentedPathCondition(segmentedPathCondition, pendingConjunct, pendingExtraConjunct, branch);
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
			value = new IntVariable(getNewVariableName(), 0, 999);
			instanceData.put(fullFieldName, value);
		}
		return value;
	}
	
	/*
	 * Arrays are just objects and thus we just return a new objectId
	 */
	
	public int createArray() {
		return incrAndGetNewObjectId();
	}
	
	/*
	 * We will treat arrays just like objects with fields, where each index is handled like a field
	 */
	public void addArrayValue(int arrayId, int index, Expression value) {
		/*
		String arrayIndexName = arrayId + ":::" + index;
		instanceData.put(arrayIndexName, value);
		*/
		putField(arrayId,""+index,value);
	}
	
	public Expression getArrayValue(int arrayId, int index) {
		return getField(arrayId,""+index);
		/*
		String arrayIndexName = arrayId + ":::" + index;
		Expression value = instanceData.get(arrayIndexName);
		if (value == null) {
			// TODO create bounds on fields
			value = new IntVariable("$q" + newVariableCount++, 0, 999);
			instanceData.put(arrayIndexName, value);
		}
		return value;
		*/
	}

	/**
	 * Returns the size of an array on the actual expression stack of the given
	 * thread.
	 * 
	 * @param thread
	 *            the thread whose stack will be peeked at
	 * @param index
	 *            the position of the array on the stack
	 * @return the size of the array or 0 if it is invalid
	 */
	public static int getRealArrayLength(ThreadReference thread, int index) {
		try {
			StackFrame frame = thread.frame(0);
			List<Value> actualValues = frame.getArgumentValues();
			Value value = actualValues.get(index);
			if (value instanceof ArrayReference) {
				ArrayReference array = (ArrayReference) actualValues.get(index);
				return array.length();
			}
		} catch (IncompatibleThreadStateException x) {
			x.printStackTrace();
		}
		return 0;
	}

}
