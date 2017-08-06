package za.ac.sun.cs.deepsea.diver;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.request.StepRequest;

import za.ac.sun.cs.deepsea.agent.AbstractEventListener;
import za.ac.sun.cs.deepsea.agent.RequestManager;
import za.ac.sun.cs.deepsea.instructions.Instruction;

public class Stepper extends AbstractEventListener {

	private final Logger log;

	private final RequestManager mgr;

	private final Map<String, InsnList> instructionListMap = new HashMap<>();

	private final Map<String, Instruction> instructionMap = new HashMap<>();

	private final StringBuilder sb = new StringBuilder();

	public Stepper(final Diver diver, Symbolizer symbolizer, final RequestManager mgr) {
		this.log = diver.getLog();
		this.mgr = mgr;
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
			sb.append("class:" + clsName);
			sb.append(" method:" + methodSign);
			sb.append(" bci:" + bci);
			log.finest(sb.toString());
		} else {
//			ClassGen cgen = genMap.get(clsName);
//			symbolizer.execute(loc, cgen, handle);
			log.finest(methodName + " " + bci + " " + ins.toString());
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
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(className);
			classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
			@SuppressWarnings("unchecked")
			List<MethodNode> methods = classNode.methods;
			for (MethodNode methodNode : methods) {
				instructionListMap.put(methodNode.name + methodNode.desc, methodNode.instructions);
			}
			log.finer(">>>> loaded " + className);
		} catch (IOException e) {
			log.finer(">>>> could not load " + className);
		}
		return true;
	}
	
	@Override
	public boolean methodEntry(MethodEntryEvent event) {
		Method method = event.method();
		String key = method.name() + method.signature();
		String fullKey = method.declaringType().name() + "." + key;
		if (instructionListMap.containsKey(key)) {
			InsnList insnList = instructionListMap.remove(key);
			byte[] bytecodes = method.bytecodes();
			Instruction.map(bytecodes, insnList, fullKey, instructionMap);
		}
		return true;
	}

}
