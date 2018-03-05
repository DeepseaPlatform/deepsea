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
import za.ac.sun.cs.deepsea.agent.VMConnectAttacher;
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
	private final Logger logger;

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
	 * @param name name for this dive (displayed in log)
	 * @param logger destination for log messages
	 * @param config configuration settings
	 * @param concreteValues concrete input values
	 */
	public Dive(String name, Logger logger, Configuration config, Map<String, Constant> concreteValues) {
		this.name = name;
		this.logger = logger;
		this.config = config;
		this.symbolizer = new Symbolizer(logger);
		this.concreteValues = concreteValues;
	}

	/**
	 * TODO
	 * @return whether a serious error has occurred
	 */
	public boolean dive(int port) {
		Banner.displayBannerLine("starting dive " + name, '-', logger);
		logger.trace("launching vm");
		VirtualMachine vm = VMConnectAttacher.launchTarget(port);
		return completeDive(vm);
	}

	/**
	 * TODO
	 * @return whether a serious error has occurred
	 */
	public boolean dive() {
		Banner.displayBannerLine("starting dive " + name, '-', logger);
		logger.trace("launching vm");
		VirtualMachine vm = VMConnectLauncher.launchTarget(config.getJar(), new String[] { config.getTarget(), config.getArgs() });
		return completeDive(vm);
	}
	
	/**
	 * TODO
	 * @return whether a serious error has occurred
	 */
	private boolean completeDive(VirtualMachine vm) {
		logger.trace("target vm details:");
		for (String detail : vm.description().split("\n")) {
			logger.trace("  " + detail);
		}

		logger.trace("redirecting output");
		InputStream es = null, is = null;
		StreamRedirector er = null, or = null;
		Process pr = vm.process();
		if (pr != null) {
			es = pr.getErrorStream();
			is = pr.getInputStream();
			er = new StreamRedirector(es, System.err, config.getEchoOutput());
			or = new StreamRedirector(is, System.out, config.getEchoOutput());
			er.start();
			or.start();
		}
		
		logger.trace("issuing monitor requests");
		RequestManager m = new RequestManager(vm.eventRequestManager());
		m.addExclude("java.*", "javax.*", "sun.*", "com.sun.*");
		m.addExclude(config.getDelegateTargets());
		// m.createClassPrepareRequest(r -> m.filterExcludes(r));
		m.createClassPrepareRequest(r -> {});
		
		logger.trace("setting up event monitoring");
		EventReader ev = new EventReader(logger, vm.eventQueue());
		Stepper st = new Stepper(logger, config, this, vm, m);
		ev.addEventListener(st);
		ev.start();

//		List<ThreadReference> threads = vm.allThreads();
//		int i = 0;
//		for (ThreadReference t : threads) {
//			logger.info("[[[ {}: {}", i++, t);
//		}
//		List<ReferenceType> classes = vm.allClasses();
//		i = 0;
//		for (ReferenceType c : classes) {
//			logger.info("((( {}: {}", i++, c);
//		}

		logger.trace("starting vm");
		vm.resume();
		
		logger.trace("waiting for completion");
		while (!ev.isStopping()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException x) {
				// ignore
			}
		}
		
		logger.trace("shutting down event monitoring");
		ev.stop();
		if (pr != null) {
			er.interrupt();
			or.interrupt();
			try {
				es.close();
				is.close();
			} catch (IOException x) {
				x.printStackTrace();
			}
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
