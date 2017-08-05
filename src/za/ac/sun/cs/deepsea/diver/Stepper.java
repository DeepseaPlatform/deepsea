package za.ac.sun.cs.deepsea.diver;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.bcel.Const;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.StackMap;
import org.apache.bcel.classfile.StackMapEntry;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NOP;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.ClassLoaderRepository;
import org.apache.bcel.util.Repository;

import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.request.StepRequest;

import za.ac.sun.cs.deepsea.agent.AbstractEventListener;
import za.ac.sun.cs.deepsea.agent.RequestManager;

public class Stepper extends AbstractEventListener {

	private final Diver diver;

	private final Logger log;

	private final RequestManager mgr;

	private final Symbolizer symbolizer;

	private final Repository repo = new ClassLoaderRepository(this.getClass().getClassLoader());

	private final Map<String, InstructionHandle> insMap = new HashMap<>();

	private final Map<String, ClassGen> genMap = new HashMap<>();

	private final StringBuilder sb = new StringBuilder();

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
		long bci = loc.codeIndex();
		InstructionHandle handle = insMap.get(clsName + ":" + methodName + ":" + bci);
		if (handle == null) {
			sb.setLength(0);
			sb.append("class:" + clsName);
			sb.append(" method:" + methodName);
			sb.append(" bci:" + bci);
			log.finest(sb.toString());
		} else {
			ClassGen cgen = genMap.get(clsName);
			symbolizer.execute(loc, cgen, handle);
			//			if (handle.getInstruction() instanceof InvokeInstruction) {
			//				InvokeInstruction iins = (InvokeInstruction) handle.getInstruction();
			//				int argc = iins.getArgumentTypes(cgen.getConstantPool()).length;
			//			}
			sb.setLength(0);
			sb.append(methodName).append(' ').append(handle.toString());
			log.finest(sb.toString());
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
	public boolean classPrepare(ClassPrepareEvent event) {
		final String className = event.referenceType().name();
		try {
			/*
			 * Load the class locally.
			 */
			JavaClass cls = repo.loadClass(className);
			/*
			 * For each method, obtain its bytecode, and store it in insMap.
			 */
			for (Method mth : cls.getMethods()) {
				String prefix = className + ":" + mth.getName();
				InstructionList insList = new InstructionList(mth.getCode().getCode());
				int[] insOfs = insList.getInstructionPositions();
				InstructionHandle[] insHdl = insList.getInstructionHandles();
				int n = insList.getLength();
				for (int i = 0; i < n; i++) {
					insMap.put(prefix + ":" + insOfs[i], insHdl[i]);
				}
			}
			/*
			 * Create a ClassGen object based on this class and store it in
			 * genMap. This is used to obtain a reference to its ConstantPoolGen
			 * component which is needed for determining the number of arguments
			 * passed during function calls. The whole ClassGen is stored in
			 * case it is needed for future extensions.
			 */
			ClassGen cg = new ClassGen(cls);
			genMap.put(className, cg);
			/*
			 * Add a static field to the class to signal that method arguments
			 * must be modified.
			 */
			ConstantPoolGen cpg = cg.getConstantPool();
//			int af = Const.ACC_PRIVATE | Const.ACC_STATIC;
//			FieldGen bypassFlag = new FieldGen(af, BasicType.BOOLEAN, "$dp$bypassFlag", cpg);
//			// bypassFlag.setInitValue(true);
//			cg.addField(bypassFlag.getField());
			/*
			 * For each method, check if it is a trigger. If so, add
			 * instructions
			 */
			for (Method m : cg.getMethods()) {
				MethodGen mg = modifyMethod(m, className, cpg);
				if (mg != null) {
					cg.replaceMethod(m, mg.getMethod());
				}
			}
			/*
			 * Refine the class on the target machine.
			 */
			try {
				FileOutputStream fos = new FileOutputStream("/tmp/x.class");
				fos.write(cg.getJavaClass().getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Map<ReferenceType, byte[]> map = new HashMap<>();
			map.put(event.referenceType(), cg.getJavaClass().getBytes());
			VirtualMachine vm = mgr.getVM();
			vm.redefineClasses(map);
			/*
			 * Log that the class has been loaded.
			 */
			log.finer(">>>> loaded " + className);
		} catch (ClassNotFoundException e) {
			log.finer(">>>> could not load " + className);
		}
		return true;
	}

	private MethodGen modifyMethod(Method m, String className, ConstantPoolGen cpg) {
		Iterator<Trigger> ti = diver.findTriggers(m, className);
		while (ti.hasNext()) {
			Trigger tr = ti.next();
			MethodGen mg = new MethodGen(m, className, cpg);
//			InstructionList ol = new InstructionList(mg.getInstructionList().getByteCode());
			// InstructionList il = new InstructionList(new NOP());
			// InstructionFactory f = new InstructionFactory(cpg);
//			ol.insert(new NOP());
//			ol.setPositions(true);
			// il.append(new NOP());
//			// il.append(f.createGetStatic(className, "$dp$bypassFlag", Type.BOOLEAN));
//			il.append(f.createConstant(0));
//			BranchInstruction b = new IFEQ(null); 
//			il.append(b);
//			il.append(f.createConstant(22));
//			il.append(InstructionFactory.createStore(Type.INT, 0));
//			b.setTarget(ol.getStart());
			// ol.insert(il);
//			mg.setInstructionList(ol);
			// il.dispose();
			// ol.dispose();
//			mg.setMaxLocals();
//			mg.setMaxStack();
			Attribute[] as = mg.getCodeAttributes();
			StackMap sm = null; 
			for (Attribute a : as) {
				if (a instanceof StackMap) {
					sm = (StackMap) a;
					break;
				}
			}
//			StackMapEntry sme = sm.getStackMap()[0];
//			sme.setByteCodeOffset(18);
			System.out.println(sm);
			return mg;
		}
		return null;
	}

}
