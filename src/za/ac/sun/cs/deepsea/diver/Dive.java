package za.ac.sun.cs.deepsea.diver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.StepRequest;

import za.ac.sun.cs.deepsea.agent.EventReader;
import za.ac.sun.cs.deepsea.agent.RequestManager;
import za.ac.sun.cs.deepsea.agent.StreamRedirector;
import za.ac.sun.cs.deepsea.agent.VMConnectLauncher;
import za.ac.sun.cs.green.expr.Constant;
import za.ac.sun.cs.green.expr.Expression;

public class Dive {

	/**
	 * 
	 */
	private final Diver diver;

	/**
	 * 
	 */
	private final Logger log;

	/**
	 * 
	 */
	private final int id;

	/**
	 * 
	 */
	private final Symbolizer symbolizer;

	private final Map<String, Constant> concreteValues;

	/**
	 * @param diver
	 * @param concreteValues
	 */
	public Dive(Diver diver, Map<String, Constant> concreteValues) {
		this.diver = diver;
		this.log = diver.getLog();
		this.id = diver.getDiveId();
		this.symbolizer = new Symbolizer(diver);
		this.concreteValues = concreteValues;
	}

	public void dive() {
		log.fine("----- starting dive " + diver.getName() + "." + id + " -----");

		log.finer("launching vm");
		VirtualMachine vm = VMConnectLauncher.launchTarget(new String[] { diver.getTarget(), diver.getArgs() });
		log.finest("target vm details:\n" + vm.description());

		log.finer("redirecting output");
		Process pr = vm.process();
		InputStream es = pr.getErrorStream();
		InputStream is = pr.getInputStream();
		StreamRedirector er = new StreamRedirector(es, System.err, diver.isProducingOutput());
		StreamRedirector or = new StreamRedirector(is, System.out, diver.isProducingOutput());
		er.start();
		or.start();

		log.finer("issuing monitor requests");
		RequestManager m = new RequestManager(diver, vm.eventRequestManager());
		m.addExclude("java.*", "javax.*", "sun.*", "com.sun.*");
		m.createMethodEntryRequest(r -> m.filterExcludes(r));
		ThreadReference mt = RequestManager.findThread(vm, "main");
		m.createStepRequest(mt, StepRequest.STEP_MIN, StepRequest.STEP_INTO, r -> {
			m.filterExcludes(r);
			r.addCountFilter(1);
		});

		log.finer("setting up event monitoring");
		EventReader ev = new EventReader(diver, vm.eventQueue());
		ev.addEventListener(new Stepper(diver, symbolizer, m, concreteValues));
		ev.start();

		log.finer("starting vm");
		vm.resume();

		log.finer("waiting for completion");
		while (!ev.isStopping()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException x) {
				// ignore
			}
		}

		log.finer("shutting down event monitoring");
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
	 * Returns the path condition collected by {@link #symbolizer} during the
	 * most recent invocation of the target program.
	 * 
	 * @return the path condition for the most recent run
	 */
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
	public String getSignature() {
		return symbolizer.getSignature();
	}

}
