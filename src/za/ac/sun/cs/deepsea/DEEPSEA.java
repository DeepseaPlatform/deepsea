package main;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.StepRequest;

import agent.EventReader;
import agent.RequestManager;
import agent.StreamRedirector;
import agent.VMConnectLauncher;
import logging.SEALogHandler;

/**
 * Main class and launcher for the DEEPSEA project. It
 * 
 * <ul>
 * <li>creates a JVM to run the target program in,</li>
 * <li>redirects its input and error streams to the standard output and standard error,</li>
 * <li>sets up the monitoring requests,</li>
 * <li>sets up the event monitoring,</li>
 * <li>activates the JVM,</li>
 * <li>waits for the action to dissipate, and</li>
 * <li>shuts down everything.</li> 
 * </ul>
 * 
 * @author jaco
 */
public class DEEPSEA {

	/**
	 * Public logger for whole of project.
	 */
	public static final Logger log = Logger.getLogger("SEA");

	/**
	 * The main function.
	 * 
	 * TODO: command-line arguments are still in a state of flux
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		log.setUseParentHandlers(false);
		log.setLevel(Level.ALL);
		log.addHandler(new SEALogHandler(Level.ALL));
		log.info("----- DEEPSEA start -----");

		log.fine("launch VM");
		VirtualMachine vm = VMConnectLauncher.launchTarget(args);
		vm.setDebugTraceMode(0);

		log.fine("redirect output");
		Process pr = vm.process();
		InputStream es = pr.getErrorStream();
		InputStream is = pr.getInputStream();
		StreamRedirector errThread = new StreamRedirector("error reader", es, System.err);
		StreamRedirector outThread = new StreamRedirector("output reader", is, System.out);
		errThread.start();
		outThread.start();

		log.fine("issue monitor requests");
		RequestManager mgr = new RequestManager(vm.eventRequestManager());
		mgr.addExclude("java.*", "javax.*", "sun.*", "com.sun.*");
		mgr.createClassPrepareRequest(r -> mgr.filterExcludes(r));
		ThreadReference mt = RequestManager.findThread(vm, "main");
		mgr.createStepRequest(mt, StepRequest.STEP_MIN, StepRequest.STEP_INTO, r -> {
			mgr.filterExcludes(r);
			r.addCountFilter(1);
		});

		log.fine("set up event monitoring");
		EventReader eventReader = new EventReader("x", vm.eventQueue());
		eventReader.addEventListener(new Stepper(mgr));
		eventReader.start();

		log.fine("start VM");
		vm.resume();

		log.fine("waiting for completion");
		while (!eventReader.isStopping()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		log.fine("shutting down threads");
		eventReader.stop();
		errThread.interrupt();
		outThread.interrupt();
		// Don't do this: vm.dispose();
		try {
			es.close();
			is.close();
		} catch (IOException x) {
			x.printStackTrace();
		}

		log.info("----- DEEPSEA done -----");
	}

}
