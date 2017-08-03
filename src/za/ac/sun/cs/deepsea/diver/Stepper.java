package za.ac.sun.cs.deepsea.diver;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.util.ClassLoaderRepository;
import org.apache.bcel.util.Repository;

import com.sun.jdi.Location;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.request.StepRequest;

import za.ac.sun.cs.deepsea.agent.AbstractEventListener;
import za.ac.sun.cs.deepsea.agent.RequestManager;

public class Stepper extends AbstractEventListener {

	// private final Diver diver;

	private final Logger log;

	private final RequestManager mgr;

	private final Symbolizer symbolizer;

	private final Repository repo = new ClassLoaderRepository(this.getClass().getClassLoader());

	private final Map<String, InstructionHandle> insMap = new HashMap<>();

	private final StringBuilder sb = new StringBuilder();
	
	public Stepper(final Diver diver, Symbolizer symbolizer, final RequestManager mgr) {
		// this.diver = diver;
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
			log.fine(sb.toString());
		} else {
			symbolizer.execute(loc, handle);
//			sb.setLength(0);
//			sb.append(methodName).append(' ').append(handle.toString());
//			log.fine(sb.toString());
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
		try {
			JavaClass cls = repo.loadClass(event.referenceType().name());
			for (Method mth : cls.getMethods()) {
				String prefix = event.referenceType().name() + ":" + mth.getName();
				InstructionList insList = new InstructionList(mth.getCode().getCode());
				int[] insOfs = insList.getInstructionPositions();
				InstructionHandle[] insHdl = insList.getInstructionHandles();
				int n = insList.getLength();
				for (int i = 0; i < n; i++) {
					insMap.put(prefix + ":" + insOfs[i], insHdl[i]);
				}
			}
			log.fine(">--> " + event.referenceType().name() + " " + cls.getFileName());
		} catch (ClassNotFoundException e) {
			log.fine(">>>> " + event.referenceType().name());
		}
		return true;
	}

}
