package za.ac.sun.cs.deepsea.diver;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.sun.jdi.AbsentInformationException;
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
import za.ac.sun.cs.deepsea.instructions.Instruction;
import za.ac.sun.cs.green.expr.Constant;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.IntVariable;

public class Stepper extends AbstractEventListener {

	private final Dive dive;

	private final Logger log;

	private final VirtualMachine vm;

	private final RequestManager mgr;

	private final Symbolizer symbolizer;

	private final Set<String> visitedMethods = new HashSet<>();

	private final Map<String, Instruction> instructionMap = new HashMap<>();

	private static final Stack<Expression> args = new Stack<>();

	private static final StringBuilder sb = new StringBuilder();

	private static final int SHOW_STACK_ENTRY_COUNT = 4;

	private final Map<ReferenceType, ConstantPool> cpMap = new HashMap<>();

	public Stepper(Dive dive, VirtualMachine vm, RequestManager mgr) {
		this.dive = dive;
		this.log = dive.getDiver().getLog();
		this.vm = vm;
		this.mgr = mgr;
		this.symbolizer = dive.getSymbolizer();
	}

	public Dive getDive() {
		return dive;
	}

	public RequestManager getRequestManager() {
		return mgr;
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
		if (!visitedMethods.contains(fullKey)) {
			visitedMethods.add(fullKey);
			byte[] bytecodes = method.bytecodes();
			Instruction.map(this, bytecodes, fullKey, instructionMap);
		}
		if (symbolizer.inSymbolicMode()) {
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
		} else {
			Trigger trigger = dive.getDiver().findTrigger(method, className);
			if (trigger != null) {
				Map<String, Constant> concreteValues = dive.getConcreteValues();
				int n = trigger.getParameterCount();
				symbolizer.enterSymbolicMode();
				SymbolicFrame sframe = symbolizer.pushNewFrame();
				try {
					VirtualMachine vm = event.virtualMachine();
					StackFrame frame = event.thread().frame(0);
					List<Value> actualValues = frame.getArgumentValues();
					List<LocalVariable> args = method.arguments();
					for (int i = 0; i < n; i++) {
						Value actualValue = actualValues.get(i);
						Object type = trigger.getParameterType(i);
						boolean symbolic = trigger.isParameterSymbolic(i);
						String name = trigger.getParameterName(i);
						Constant concrete = ((name == null) || (concreteValues == null)) ? null
								: concreteValues.get(name);
						Expression expr = null;
						Expression varValue = null;
						if (type == Boolean.class) {
							expr = new IntConstant(((BooleanValue) actualValue).intValue());
							varValue = expr;
							if (symbolic) {
								expr = new IntVariable(trigger.getParameterName(i), 0, 1);
								if ((concrete != null) && (concrete instanceof IntConstant)) {
									boolean value = ((IntConstant) concrete).getValue() != 0;
									frame.setValue(args.get(i), vm.mirrorOf(value));
									varValue = concrete;
								}
							}
						} else if (type == Integer.class) {
							expr = new IntConstant(((IntegerValue) actualValue).intValue());
							varValue = expr;
							if (symbolic) {
								String var = trigger.getParameterName(i);
								int min = dive.getDiver().getMinBound(var);
								int max = dive.getDiver().getMaxBound(var);
								expr = new IntVariable(var, min, max);
								if ((concrete != null) && (concrete instanceof IntConstant)) {
									int value = ((IntConstant) concrete).getValue();
									frame.setValue(args.get(i), vm.mirrorOf(value));
									varValue = concrete;
								}
							}
						} else {
							throw new Error("Unhandled symbolic type: " + type);
						}
						sframe.setLocal(i, expr);
						if (name != null) {
							dive.setActualValue(name, varValue);
						}
					}
				} catch (IncompatibleThreadStateException x) {
					x.printStackTrace();
				} catch (AbsentInformationException x) {
					x.printStackTrace();
				} catch (InvalidTypeException x) {
					x.printStackTrace();
				} catch (ClassNotLoadedException x) {
					x.printStackTrace();
				}
			}
		}
		return true;
	}

	@Override
	public boolean classPrepare(ClassPrepareEvent event) {
		if (dive.getDiver().getTarget().equals(event.referenceType().name())) {
			mgr.createMethodEntryRequest(r -> mgr.filterExcludes(r));
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
