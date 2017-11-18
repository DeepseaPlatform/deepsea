package za.ac.sun.cs.deepsea.diver;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.request.StepRequest;

import za.ac.sun.cs.deepsea.agent.AbstractEventListener;
import za.ac.sun.cs.deepsea.agent.RequestManager;
import za.ac.sun.cs.deepsea.constantpool.ConstantFieldref;
import za.ac.sun.cs.deepsea.constantpool.ConstantMethodref;
import za.ac.sun.cs.deepsea.constantpool.ConstantNameAndType;
import za.ac.sun.cs.deepsea.constantpool.ConstantPool;
import za.ac.sun.cs.deepsea.constantpool.ConstantString;
import za.ac.sun.cs.deepsea.instructions.Instruction;
import za.ac.sun.cs.deepsea.reporting.Banner;
import za.ac.sun.cs.green.expr.Constant;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.IntVariable;

public class Stepper extends AbstractEventListener {

	public static class IntArray {

		private final int length;

		public IntArray(int length) {
			this.length = length;
		}

		public int getLength() {
			return length;
		}

	}

	/**
	 * The logger.
	 */
	private final Logger log;

	/**
	 * The settings that control and apply to this session.
	 */
	private final Configuration config;

	private final Dive dive;

	private final VirtualMachine vm;

	private final RequestManager mgr;

	private final Symbolizer symbolizer;

	private final Set<String> visitedMethods = new HashSet<>();

	private final Map<String, Instruction> instructionMap = new HashMap<>();

	private static final Stack<Expression> args = new Stack<>();

	private static final StringBuilder sb = new StringBuilder();

	private static final int SHOW_STACK_ENTRY_COUNT = 4;

	private final Map<ReferenceType, ConstantPool> cpMap = new HashMap<>();

	private Object delegate = null;

	private java.lang.reflect.Method delegateMethod = null;

	private ThreadReference delegateThread = null;

	private boolean errorFree = true;

	public Stepper(Logger log, Configuration config, Dive dive, VirtualMachine vm, RequestManager mgr) {
		this.log = log;
		this.config = config;
		this.dive = dive;
		this.vm = vm;
		this.mgr = mgr;
		this.symbolizer = dive.getSymbolizer();
	}

	public RequestManager getRequestManager() {
		return mgr;
	}

	public boolean isErrorFree() {
		return errorFree;
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
			log.trace("class: {} method: {} bci: {}", clsName, methodSign, bci);
		} else {
			symbolizer.execute(event, loc, ins);
			if (log.getLevel().isMoreSpecificThan(Level.TRACE)) {
				sb.setLength(0);
				if (symbolizer.inSymbolicMode()) {
					sb.append("$$$ ");
				}
				sb.append('[').append(methodName);
				sb.append('@').append(bci);
				sb.append("] ").append(ins.toString());
				if (SHOW_STACK_ENTRY_COUNT > 0) {
					SymbolicFrame frame = symbolizer.getTopFrame();
					if (frame != null) {
						sb.append(" {");
						int k = frame.size();
						for (int i = 0; i < SHOW_STACK_ENTRY_COUNT && k > 0; i++) {
							sb.append(' ').append(frame.peek(--k));
						}
						if ((SHOW_STACK_ENTRY_COUNT > 0) && (k > 0)) {
							sb.append(" ...");
						}
						sb.append(" }");
					}
				}
				log.trace(sb.toString());
			}
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
		// Collect the bytecodes for the method, if necessary
		if (!visitedMethods.contains(fullKey)) {
			visitedMethods.add(fullKey);
			byte[] bytecodes = method.bytecodes();
			Instruction.map(this, bytecodes, fullKey, instructionMap);
		}
		// If in symbolic mode, ...
		if (symbolizer.inSymbolicMode()) {
			// ...execute the delegate if available...
			if (delegateMethod != null) {
				boolean delegateSuccess = false;
				try {
					Object[] arguments = { symbolizer, delegateThread };
					delegateSuccess = (Boolean) delegateMethod.invoke(delegate, arguments);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException x) {
					// This should never happen!
					x.printStackTrace();
				}
				delegateMethod = null;
				if (delegateSuccess) {
					// Delegate executed successfully
					return true;
				} else {
					// We need to throw a runtime exception here because the symbolic frame may be damaged
					throw new RuntimeException("DELEGATE FAILED");
				}
			} else if (mgr.isFiltered(className)) {
				// ...or do nothing is this is a sometimes-delegated method...
				return true;
			} else {
				// ...or synchronize the concrete and symbolic frames
				return symbolicInvocation(method);
			}
		}
		// If not in symbolic mode, check if the method is a trigger
		Trigger trigger = config.findTrigger(method, className);
		if (trigger != null) {
			return triggerSymbolicMode(trigger, event, method);
		}
		// Nothing happened; just return true
		return true;
	}

	/**
	 * Arrange the frames of the symbolic stack. This method is invoked when we
	 * are in symbolic mode, and a methodEntry event has occurred.
	 *
	 * @param method the method that has been entered
	 * @return {@code true} if and only if everything has executed OK
	 */
	private boolean symbolicInvocation(Method method) {
		try {
			int n = method.argumentTypes().size();
			if (!method.isStatic()) {
				n++;
			}
			SymbolicFrame frame = symbolizer.getTopFrame();
			assert args.isEmpty();
			for (int i = 0; i < n; i++) {
				args.push(frame.pop());
			}
			frame = symbolizer.pushNewFrame();
			for (int i = 0; i < n; i++) {
				frame.setLocal(i, args.pop());
			}
		} catch (ClassNotLoadedException x) {
			x.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean triggerSymbolicMode(Trigger trigger, MethodEntryEvent event, Method method) {
		Map<String, Constant> concreteValues = dive.getConcreteValues();
		int n = trigger.getParameterCount();
		symbolizer.enterSymbolicMode();
		SymbolicFrame sframe = symbolizer.pushNewFrame();
		try {
			VirtualMachine vm = event.virtualMachine();
			StackFrame frame = event.thread().frame(0);
			List<Value> actualValues = frame.getArgumentValues();
			List<LocalVariable> args = null;
			try {
				args = method.arguments();
			} catch (AbsentInformationException x) {
				new Banner('@')
					.println("CANNOT OBTAIN APPLICATION INFORMATION")
					.println("")
					.println("COMPILE WITH THE -g FLAG (javac -g ...)")
					.display(log, Level.FATAL);
				errorFree = false;
				return false;
			}
			for (int i = 0; i < n; i++) {
				Object type = trigger.getParameterType(i);
				boolean symbolic = trigger.isParameterSymbolic(i);
				String name = trigger.getParameterName(i);
				if (type == Boolean.class) {
					Value actualValue = actualValues.get(i);
					Expression expr = new IntConstant(((BooleanValue) actualValue).intValue());
					Expression varValue = expr;
					if (symbolic) {
						Constant concrete = ((name == null) || (concreteValues == null)) ? null
								: concreteValues.get(name);
						expr = new IntVariable(trigger.getParameterName(i), 0, 1);
						if ((concrete != null) && (concrete instanceof IntConstant)) {
							boolean value = ((IntConstant) concrete).getValue() != 0;
							frame.setValue(args.get(i), vm.mirrorOf(value));
							varValue = concrete;
						}
					}
					sframe.setLocal(i, expr);
					dive.setActualValue(name, varValue);
				} else if (type == Integer.class) {
					Value actualValue = actualValues.get(i);
					Expression expr = new IntConstant(((IntegerValue) actualValue).intValue());
					Expression varValue = expr;
					if (symbolic) {
						Constant concrete = ((name == null) || (concreteValues == null)) ? null
								: concreteValues.get(name);
						int min = config.getMinBound(name);
						int max = config.getMaxBound(name);
						expr = new IntVariable(name, min, max);
						if ((concrete != null) && (concrete instanceof IntConstant)) {
							int value = ((IntConstant) concrete).getValue();
							frame.setValue(args.get(i), vm.mirrorOf(value));
							varValue = concrete;
						}
					}
					sframe.setLocal(i, expr);
					dive.setActualValue(name, varValue);
				} else if (type instanceof IntArray) {
					Value actualValue = actualValues.get(i);
					assert actualValue instanceof ArrayReference;
					ArrayReference actualArray = (ArrayReference) actualValue;
					int arrayLength = actualArray.length();
					int arrayId = symbolizer.incrAndGetNewObjectId();
					Expression expr = new IntConstant(arrayId);
					if (symbolic) {
						for (int j = 0; j < arrayLength; j++) {
							String entryName = name + "$" + j;
							Constant concrete = ((entryName == null) || (concreteValues == null)) ? null
									: concreteValues.get(entryName);
							int min = config.getMinBound(entryName, config.getMinBound(name));
							int max = config.getMaxBound(entryName, config.getMaxBound(name));
							Expression entryExpr = new IntVariable(entryName, min, max);
							if ((concrete != null) && (concrete instanceof IntConstant)) {
								int value = ((IntConstant) concrete).getValue();
								actualArray.setValue(j, vm.mirrorOf(value));
								IntConstant valueExpr = new IntConstant(value);
								dive.setActualValue(entryName, valueExpr);
							} else {
								dive.setActualValue(entryName, entryExpr);
							}
							symbolizer.putField(arrayId, "" + j, entryExpr);
						}
					} else {
						for (int j = 0; j < arrayLength; j++) {
							IntConstant value = new IntConstant(
									((IntegerValue) actualArray.getValue(j)).intValue());
							symbolizer.putField(arrayId, "" + j, value);
							dive.setActualValue(name + "$" + j, value);
						}
					}
					sframe.setLocal(i, expr);
				} else if (type == String.class) {
					Value actualValue = actualValues.get(i);
					assert actualValue instanceof StringReference;
					String stringValue = ((StringReference) actualValue).value();
					int stringLength = stringValue.length();
					int stringId = symbolizer.incrAndGetNewObjectId();
					symbolizer.putField(stringId, "length", new IntConstant(stringLength));
					Expression expr = new IntConstant(stringId);
					if (symbolic) {
						char[] newStringChars = new char[stringLength];
						stringValue.getChars(0, stringLength, newStringChars, 0);
						for (int j = 0; j < stringLength; j++) {
							String entryName = name + "$" + j;
							Constant concrete = ((entryName == null) || (concreteValues == null)) ? null
									: concreteValues.get(entryName);
							Expression entryExpr = new IntVariable(entryName, 0, 255);
							if ((concrete != null) && (concrete instanceof IntConstant)) {
								newStringChars[j] = (char) ((IntConstant) concrete).getValue();
								dive.setActualValue(entryName, concrete);
							} else {
								dive.setActualValue(entryName, entryExpr);
							}
							symbolizer.putField(stringId, "" + j, entryExpr);
						}
						String newStringValue = new String(newStringChars);
						if (!stringValue.equals(newStringValue)) {
							frame.setValue(args.get(i), vm.mirrorOf(newStringValue));
						}
					} else {
						for (int j = 0; j < stringLength; j++) {
							IntConstant value = new IntConstant(stringValue.charAt(j));
							symbolizer.putField(stringId, "" + j, value);
							dive.setActualValue(name + "$" + j, value);
						}
					}
					sframe.setLocal(i, expr);
				} else {
					throw new Error("Unhandled symbolic type: " + type);
				}
			}
		} catch (IncompatibleThreadStateException x) {
			x.printStackTrace();
		} catch (InvalidTypeException x) {
			x.printStackTrace();
		} catch (ClassNotLoadedException x) {
			x.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean classPrepare(ClassPrepareEvent event) {
		if (config.getTarget().equals(event.referenceType().name())) {
			mgr.createMethodEntryRequest(r -> mgr.filterExcludes(r));
			for (String delegateTarget : config.getDelegateTargets()) {
				mgr.createMethodEntryRequest(r -> { r.addClassFilter(delegateTarget); });
			}
			ThreadReference mt = RequestManager.findThread(vm, "main");
			mgr.createStepRequest(mt, StepRequest.STEP_MIN, StepRequest.STEP_INTO, r -> {
				mgr.filterExcludes(r);
				r.addCountFilter(1);
			});
		}
		return true;
	}

	public za.ac.sun.cs.deepsea.constantpool.Constant getConstant(ReferenceType clas, int index, byte tag) {
		ConstantPool cp = cpMap.get(clas);
		if (cp == null) {
			try {
				cp = new ConstantPool(clas.constantPoolCount(), clas.constantPool());
			} catch (IOException x) {
				x.printStackTrace();
				return null;
			}
			cpMap.put(clas, cp);
		}
		return cp.getConstant(index, tag);
	}

	public za.ac.sun.cs.deepsea.constantpool.Constant getConstant(ReferenceType clas, int index) {
		ConstantPool cp = cpMap.get(clas);
		if (cp == null) {
			try {
				cp = new ConstantPool(clas.constantPoolCount(), clas.constantPool());
			} catch (IOException x) {
				x.printStackTrace();
				return null;
			}
			cpMap.put(clas, cp);
		}
		return cp.getConstant(index);
	}

	public String getConstantString(ReferenceType clas, int index) {
		ConstantPool cp = cpMap.get(clas);
		if (cp == null) {
			try {
				cp = new ConstantPool(clas.constantPoolCount(), clas.constantPool());
			} catch (IOException x) {
				x.printStackTrace();
				return null;
			}
			cpMap.put(clas, cp);
		}
		return ((ConstantString) cp.getConstant(index)).getString(cp);
	}
	
	private static final Class<?>[] argumentTypes = { Symbolizer.class, ThreadReference.class };

	public int delegateMethod(ReferenceType clas, int index, Symbolizer symbolizer, ThreadReference thread) {
		ConstantPool cp = cpMap.get(clas);
		if (cp == null) {
			try {
				cp = new ConstantPool(clas.constantPoolCount(), clas.constantPool());
			} catch (IOException x) {
				x.printStackTrace();
				return 0;
			}
			cpMap.put(clas, cp);
		}
		ConstantMethodref m = (ConstantMethodref) cp.getConstant(index,
				za.ac.sun.cs.deepsea.constantpool.Constants.CONSTANT_Methodref);
		String className = m.getClass(cp).replace('/', '.');
		delegate = config.findDelegate(className);
		if (delegate != null) {
			ConstantNameAndType nt = (ConstantNameAndType) cp.getConstant(m.getNameAndTypeIndex(),
					za.ac.sun.cs.deepsea.constantpool.Constants.CONSTANT_NameAndType);
			String methodName = nt.getName(cp);
			String signature = nt.getAsciiSignature(cp);
			
			try {
				delegateMethod = delegate.getClass().getDeclaredMethod(methodName + signature, argumentTypes);
			} catch (NoSuchMethodException | SecurityException e) {
				// Do nothing and leave delegateMethod == null
			}
			if (delegateMethod != null) {
				log.trace("@@@ found delegate: {}", methodName + signature);
				delegateThread = thread;
				return -2;
			} else {
				log.trace("@@@ no delegate: {}", methodName + signature);
			}
		}
		if (!mgr.isFiltered(className)) {
			return -1;
		}
		ConstantNameAndType nt = (ConstantNameAndType) cp.getConstant(m.getNameAndTypeIndex(),
				za.ac.sun.cs.deepsea.constantpool.Constants.CONSTANT_NameAndType);
		return countArguments(nt.getSignature(cp));
	}
	
	public int getArgumentCount(ReferenceType clas, int index) {
		ConstantPool cp = cpMap.get(clas);
		if (cp == null) {
			try {
				cp = new ConstantPool(clas.constantPoolCount(), clas.constantPool());
			} catch (IOException x) {
				x.printStackTrace();
				return 0;
			}
			cpMap.put(clas, cp);
		}
		ConstantMethodref m = (ConstantMethodref) cp.getConstant(index,
				za.ac.sun.cs.deepsea.constantpool.Constants.CONSTANT_Methodref);
		String name = m.getClass(cp).replace('/', '.');
		if (!mgr.isFiltered(name)) {
			return -1;
		}
		ConstantNameAndType nt = (ConstantNameAndType) cp.getConstant(m.getNameAndTypeIndex(),
				za.ac.sun.cs.deepsea.constantpool.Constants.CONSTANT_NameAndType);
		return countArguments(nt.getSignature(cp));
	}

	public static int countArguments(String signature) {
		int count = 0;
		int i = 0;
		if (signature.charAt(i++) != '(') {
			return 0;
		}
		while (true) {
			char ch = signature.charAt(i++);
			if (ch == ')') {
				return count;
			} else if (ch == '[') {
				// we can just ignore this
			} else if ((ch == 'B') || (ch == 'C') || (ch == 'D') || (ch == 'F') || (ch == 'I') || (ch == 'J')
					|| (ch == 'S') || (ch == 'Z')) {
				count++;
			} else if (ch == 'L') {
				i = signature.indexOf(';', i);
				if (i == -1) {
					return 0; // missing ';'
				}
				i++;
				count++;
			} else {
				return 0; // unknown character in signature 
			}
		}
	}

	public String getReturnType(ReferenceType clas, int index) {
		ConstantPool cp = cpMap.get(clas);
		if (cp == null) {
			try {
				cp = new ConstantPool(clas.constantPoolCount(), clas.constantPool());
			} catch (IOException x) {
				x.printStackTrace();
				return "?";
			}
			cpMap.put(clas, cp);
		}
		ConstantMethodref m = (ConstantMethodref) cp.getConstant(index,
				za.ac.sun.cs.deepsea.constantpool.Constants.CONSTANT_Methodref);
		String name = m.getClass(cp).replace('/', '.');
		if (!mgr.isFiltered(name)) {
			return "?";
		}
		ConstantNameAndType nt = (ConstantNameAndType) cp.getConstant(m.getNameAndTypeIndex(),
				za.ac.sun.cs.deepsea.constantpool.Constants.CONSTANT_NameAndType);
		return extractReturnType(nt.getSignature(cp));
	}

	public static String extractReturnType(String signature) {
		int i = 0;
		if (signature.charAt(i++) != '(') {
			return "?"; // missing '('
		}
		i = signature.indexOf(')', i);
		if (i == -1) {
			return "?"; // missing ')'
		}
		return signature.substring(i + 1);
	}

	public String getFieldName(ReferenceType clas, int index) {
		ConstantPool cp = cpMap.get(clas);
		if (cp == null) {
			try {
				cp = new ConstantPool(clas.constantPoolCount(), clas.constantPool());
			} catch (IOException x) {
				x.printStackTrace();
				return "?";
			}
			cpMap.put(clas, cp);
		}
		ConstantFieldref f = (ConstantFieldref) cp.getConstant(index,
				za.ac.sun.cs.deepsea.constantpool.Constants.CONSTANT_Fieldref);
		ConstantNameAndType nt = (ConstantNameAndType) cp.getConstant(f.getNameAndTypeIndex(),
				za.ac.sun.cs.deepsea.constantpool.Constants.CONSTANT_NameAndType);
		return nt.getName(cp);
	}

	public String getMethodName(ReferenceType clas, int index) {
		ConstantPool cp = cpMap.get(clas);
		if (cp == null) {
			try {
				cp = new ConstantPool(clas.constantPoolCount(), clas.constantPool());
			} catch (IOException x) {
				x.printStackTrace();
				return "?";
			}
			cpMap.put(clas, cp);
		}
		ConstantMethodref m = (ConstantMethodref) cp.getConstant(index,
				za.ac.sun.cs.deepsea.constantpool.Constants.CONSTANT_Methodref);
		ConstantNameAndType nt = (ConstantNameAndType) cp.getConstant(m.getNameAndTypeIndex(),
				za.ac.sun.cs.deepsea.constantpool.Constants.CONSTANT_NameAndType);
		return nt.getName(cp);
	}

}
