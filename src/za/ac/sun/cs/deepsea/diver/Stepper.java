package za.ac.sun.cs.deepsea.diver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Value;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.request.StepRequest;

import za.ac.sun.cs.deepsea.agent.AbstractEventListener;
import za.ac.sun.cs.deepsea.agent.RequestManager;
import za.ac.sun.cs.deepsea.instructions.Instruction;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.IntVariable;

public class Stepper extends AbstractEventListener {

    private final Diver diver;
   
    private final Logger log;

	private final RequestManager mgr;

	private final Symbolizer symbolizer;

	private final Set<String> visitedMethods = new HashSet<>();

	private final Map<String, Instruction> instructionMap = new HashMap<>();

	private static final Stack<Expression> args = new Stack<>();

	private static final StringBuilder sb = new StringBuilder();

	public Stepper(final Diver diver, Symbolizer symbolizer, final RequestManager mgr) {
		this.diver = diver;
		this.log = diver.getLog();
		this.mgr = mgr;
		this.symbolizer = symbolizer;
	}

	@Override
	public boolean step(StepEvent event) {
		Location loc = event.location();
		String clsName = loc.declaringType().name();
		String methodName = loc.method().name();
		String methodSign = methodName + loc.method().signature();
		long bci = loc.codeIndex();
		Instruction ins = instructionMap.get(clsName + "." + methodSign + "." + bci);
		if (ins == null) {
			sb.setLength(0);
			sb.append("class:").append(clsName);
			sb.append(" method:").append(methodSign);
			sb.append(" bci:").append(bci);
			log.finest(sb.toString());
		} else {
			sb.setLength(0);
			if (symbolizer.inSymbolicMode()) {
				sb.append("$$$ ");
			}
			sb.append('[').append(methodName);
			sb.append("::").append(bci);
			sb.append("] ").append(ins.toString());
			log.finest(sb.toString());
			symbolizer.execute(loc, ins);
		}

		// ---- Schedule the next StepRequest 
		mgr.removeRequest(event.request());
		mgr.createStepRequest(event.thread(), StepRequest.STEP_MIN, StepRequest.STEP_INTO, r -> {
			mgr.filterExcludes(r);
			r.addCountFilter(1);
		});
		return true;
	}

	@Override
	public boolean methodEntry(MethodEntryEvent event) {
		Method method = event.method();
		String className = method.declaringType().name();
		String methodName = method.name();
		String key = methodName + method.signature();
		String fullKey = className + "." + key;
		if (!visitedMethods.contains(fullKey)) {
			visitedMethods.add(fullKey);
			byte[] bytecodes = method.bytecodes();
			Instruction.map(bytecodes, fullKey, instructionMap);
		}
		if (symbolizer.inSymbolicMode()) {
			try {
				int n = method.arguments().size();
				SymbolicFrame frame = symbolizer.getTopFrame();
				assert args.isEmpty();
				for (int i = 0; i < n; i++) {
					args.push(frame.pop());
				}
				frame = symbolizer.pushNewFrame();
				for (int i = 0; i < n; i++) {
					frame.push(args.pop());
				}
			} catch (AbsentInformationException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			Trigger trigger = diver.findTrigger(method, className);
			if (trigger != null) {
				int n = trigger.getParameterCount();
				symbolizer.enterSymbolicMode();
				SymbolicFrame sframe = symbolizer.pushNewFrame();
				try {
					StackFrame frame = event.thread().frame(0);
					List<Value> actualValues = frame.getArgumentValues();
					for (int i = 0; i < n; i++) {
						Value actualValue = actualValues.get(i);
						Object type = trigger.getParameterType(i);
						boolean symbolic = trigger.isParameterSymbolic(i);
						Expression expr = null;
						if (type == Boolean.class) {
							if (symbolic) {
								expr = new IntVariable(trigger.getParameterName(i), 0, 1);
							} else {
								expr = new IntConstant(((BooleanValue) actualValue).intValue());
							}
						} else if (type == Integer.class) {
							if (symbolic) {
								expr = new IntVariable(trigger.getParameterName(i), 0, 99);
							} else {
								expr = new IntConstant(((IntegerValue) actualValue).intValue());
							}
						} else {
							throw new Error("Unhandled symbolic type: " + type);
						}
						sframe.setLocal(i, expr);
					}
//					LocalVariable var = frame.visibleVariableByName("x");
//					log.info("$$$$ " + var);
//					frame.setValue(var, event.virtualMachine().mirrorOf(99));
//					log.info("$$$$ " + event.thread().frame(0).getArgumentValues());
				} catch (IncompatibleThreadStateException e) {
					e.printStackTrace();
//				} catch (AbsentInformationException e) {
//					e.printStackTrace();
//				} catch (InvalidTypeException e) {
//					e.printStackTrace();
//				} catch (ClassNotLoadedException e) {
//					e.printStackTrace();
				}
			}
		}
		return true;
	}

}
