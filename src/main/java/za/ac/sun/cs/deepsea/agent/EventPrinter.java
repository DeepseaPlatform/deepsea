package za.ac.sun.cs.deepsea.agent;

import org.apache.logging.log4j.Logger;

import com.sun.jdi.event.AccessWatchpointEvent;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;

public class EventPrinter extends AbstractEventListener {

	private final Logger log;

	public EventPrinter(Logger log) {
		this.log = log;
	}

	@Override
	public boolean accessWatchpoint(AccessWatchpointEvent event) {
		log.trace(event.toString());
		return true;
	}

	@Override
	public boolean breakpoint(BreakpointEvent event) {
		log.trace(event.toString());
		return true;
	}

	@Override
	public boolean classPrepare(ClassPrepareEvent event) {
		log.trace(event.toString());
		return true;
	}

	@Override
	public boolean classUnload(ClassUnloadEvent event) {
		log.trace(event.toString());
		return true;
	}

	@Override
	public boolean exception(ExceptionEvent event) {
		log.trace(event.toString());
		return true;
	}

	@Override
	public boolean methodEntry(MethodEntryEvent event) {
		log.trace(event.toString());
		return true;
	}

	@Override
	public boolean methodExit(MethodExitEvent event) {
		log.trace(event.toString());
		return true;
	}

	@Override
	public boolean modificationWatchpoint(ModificationWatchpointEvent event) {
		log.trace(event.toString());
		return true;
	}

	@Override
	public boolean step(StepEvent event) {
		log.trace(event.toString());
		return true;
	}

	@Override
	public boolean threadDeath(ThreadDeathEvent event) {
		log.trace(event.toString());
		return true;
	}

	@Override
	public boolean threadStart(ThreadStartEvent event) {
		log.trace(event.toString());
		return true;
	}

	@Override
	public boolean vmDeath(VMDeathEvent event) {
		log.trace(event.toString());
		return true;
	}

	@Override
	public boolean vmDisconnect(VMDisconnectEvent event) {
		log.trace(event.toString());
		return true;
	}

}
