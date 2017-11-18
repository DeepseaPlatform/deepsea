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
import za.ac.sun.cs.deepsea.reporting.Banner;
import za.ac.sun.cs.green.expr.Constant;
import za.ac.sun.cs.green.expr.Expression;

/**
 * TODO
 */
public class Dive {

	/**
	 * TODO
	 */
	private final String name;

	/**
	 * The logger.
	 */
	private final Logger log;

	/**
	 * The settings that control and apply to this dive.
	 */
	private final Configuration config;

	/**
	 * TODO
	 */
	private final Map<String, Constant> concreteValues;

	/**
	 * TODO
	 */
	private final Symbolizer symbolizer;

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
	public Dive(String name, Logger log, Configuration config, Map<String, Constant> concreteValues) {
		this.name = name;
		this.log = log;
		this.config = config;
		this.symbolizer = new Symbolizer(log);
		this.concreteValues = concreteValues;
	}

	/**
	 * TODO
	 * @return whether a serious error has occurred
	 */
	public boolean dive() {
		Banner.displayBannerLine("starting dive " + name, '-', log);

		log.trace("launching vm");
		VirtualMachine vm = VMConnectLauncher.launchTarget(new String[] { config.getTarget(), config.getArgs() });
		log.trace("target vm details:\n" + vm.description());

		log.trace("redirecting output");
		Process pr = vm.process();
		InputStream es = pr.getErrorStream();
		InputStream is = pr.getInputStream();
		StreamRedirector er = new StreamRedirector(es, System.err, config.getEchoOutput());
		StreamRedirector or = new StreamRedirector(is, System.out, config.getEchoOutput());
		er.start();
		or.start();

		log.trace("issuing monitor requests");
		RequestManager m = new RequestManager(vm.eventRequestManager());
		m.addExclude("java.*", "javax.*", "sun.*", "com.sun.*");
		m.addExclude(config.getDelegateTargets());
		m.createClassPrepareRequest(r -> m.filterExcludes(r));

		log.trace("setting up event monitoring");
		EventReader ev = new EventReader(log, vm.eventQueue());
		Stepper st = new Stepper(log, config, this, vm, m);
		ev.addEventListener(st);
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
		return st.isErrorFree();
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
