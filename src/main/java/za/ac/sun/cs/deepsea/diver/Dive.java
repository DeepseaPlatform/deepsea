package za.ac.sun.cs.deepsea.diver;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.sun.jdi.VirtualMachine;

import za.ac.sun.cs.deepsea.agent.EventReader;
import za.ac.sun.cs.deepsea.agent.RequestManager;
import za.ac.sun.cs.deepsea.agent.StreamRedirector;
import za.ac.sun.cs.deepsea.agent.VMConnectLauncher;
import za.ac.sun.cs.green.expr.Constant;
import za.ac.sun.cs.green.expr.Expression;

/**
 * TODO
 */
public class Dive {

	/**
	 * TODO 
	 */
	private final Diver diver;

	/**
	 * TODO
	 */
	private final Logger log;

	/**
	 * TODO
	 */
	private final int id;

	/**
	 * TODO
	 */
	private final Symbolizer symbolizer;

	/**
	 * TODO
	 */
	private final Map<String, Constant> concreteValues;

	/**
	 * TODO
	 */
	private final Map<String, Expression> actualValues = new HashMap<>();

	/**
	 * TODO
	 * 
	 * @param diver TODO
	 * @param concreteValues  TODO
	 */
	public Dive(Diver diver, Map<String, Constant> concreteValues) {
		this.diver = diver;
		this.log = diver.getLog();
		this.id = diver.getDiveId();
		this.symbolizer = new Symbolizer(diver);
		this.concreteValues = concreteValues;
	}

	/**
	 * TODO
	 */
	public void dive() {
		log.info("----- starting dive " + diver.getName() + "." + id + " -----");

		log.trace("launching vm");
		VirtualMachine vm = VMConnectLauncher.launchTarget(new String[] { diver.getTarget(), diver.getArgs() });
		log.trace("target vm details:\n" + vm.description());

		log.trace("redirecting output");
		Process pr = vm.process();
		InputStream es = pr.getErrorStream();
		InputStream is = pr.getInputStream();
		StreamRedirector er = new StreamRedirector(es, System.err, diver.isProducingOutput());
		StreamRedirector or = new StreamRedirector(is, System.out, diver.isProducingOutput());
		er.start();
		or.start();

		log.trace("issuing monitor requests");
		RequestManager m = new RequestManager(diver, vm.eventRequestManager());
		m.addExclude("java.*", "javax.*", "sun.*", "com.sun.*");
		for (String delegateTarget : diver.getDelegateTargets()) {
			m.addExclude(delegateTarget);
		}
		m.createClassPrepareRequest(r -> m.filterExcludes(r));
//		m.createMethodEntryRequest(r -> m.filterExcludes(r));
//		ThreadReference mt = RequestManager.findThread(vm, "main");
//		m.createStepRequest(mt, StepRequest.STEP_MIN, StepRequest.STEP_INTO, r -> {
//			m.filterExcludes(r);
//			r.addCountFilter(1);
//		});

		log.trace("setting up event monitoring");
		EventReader ev = new EventReader(diver, vm.eventQueue());
		ev.addEventListener(new Stepper(this, vm, m));
		ev.start();

		log.trace("starting vm");
		vm.resume();

		log.trace("waiting for completion");
		while (!ev.isStopping()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException x) {
				// ignore
			}
		}

		log.trace("shutting down event monitoring");
		ev.stop();
		er.interrupt();
		or.interrupt();
		try {
			es.close();
			is.close();
		} catch (IOException x) {
			x.printStackTrace();
		}
	}

	/**
	 * Returns the path condition and signature collected by {@link #symbolizer} during the
	 * most recent invocation of the target program.
	 * 
	 * @return the segmented path condition for the most recent run
	 */
	public SegmentedPathCondition getSegmentedPathCondition() {
		return symbolizer.getSegmentedPathCondition();
	}

	/**
	 * Returns the path condition collected by {@link #symbolizer} during the
	 * most recent invocation of the target program.
	 * 
	 * @return the path condition for the most recent run
	 */
	@Deprecated
	public Expression getPathCondition() {
		return symbolizer.getPathCondition();
	}
	
	/**
	 * Returns the signature string for the most recent invocation of the target
	 * program. A signature is a sequence of "{@code 0}s" and "{@code 1}s" that
	 * describe the branches ({@code 0}=false, {@code 1}=true) taken along the
	 * path.
	 * 
	 * @return the signature string for the most recent run
	 */
	@Deprecated
	public String getSignature() {
		return symbolizer.getSignature();
	}

	/**
	 * TODO
	 * 
	 * @return TODO
	 */
	public Diver getDiver() {
		return diver;
	}

	/**
	 * TODO
	 * 
	 * @return TODO
	 */
	public Symbolizer getSymbolizer() {
		return symbolizer;
	}

	/**
	 * TODO
	 * 
	 * @return TODO
	 */
	public Map<String, Constant> getConcreteValues() {
		return concreteValues;
	}

	/**
	 * TODO
	 * 
	 * @param name TODO
	 * @param expression TODO
	 */
	public void setActualValue(String name, Expression expression) {
		assert name != null;
		actualValues.put(name, expression);
	}

	/**
	 * TODO
	 * 
	 * @param name TODO
	 * @return TODO
	 */
	public Expression getActualValue(String name) {
		return actualValues.get(name);
	}

}
