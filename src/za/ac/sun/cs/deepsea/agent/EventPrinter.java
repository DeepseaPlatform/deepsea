package za.ac.sun.cs.deepsea.agent;

import java.util.logging.Logger;

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

import za.ac.sun.cs.deepsea.diver.Diver;

public class EventPrinter extends AbstractEventListener {

	private final Logger log;

	public EventPrinter(final Diver diver) {
		this.log = diver.getLog();
	}

	@Override
	public boolean accessWatchpoint(AccessWatchpointEvent event) {
		log.fine(event.toString());
		return true;
	}

	@Override
	public boolean breakpoint(BreakpointEvent event) {
		log.fine(event.toString());
		return true;
	}

	@Override
	public boolean classPrepare(ClassPrepareEvent event) {
		log.fine(event.toString());
		return true;
	}

	@Override
	public boolean classUnload(ClassUnloadEvent event) {
		log.fine(event.toString());
		return true;
	}

	@Override
	public boolean exception(ExceptionEvent event) {
		log.fine(event.toString());
		return true;
	}

	@Override
	public boolean methodEntry(MethodEntryEvent event) {
		log.fine(event.toString());
		return true;
	}

	@Override
	public boolean methodExit(MethodExitEvent event) {
		log.fine(event.toString());
		return true;
	}

	@Override
	public boolean modificationWatchpoint(ModificationWatchpointEvent event) {
		log.fine(event.toString());
		return true;
	}

	@Override
	public boolean step(StepEvent event) {
		log.fine(event.toString());
		return true;
	}

	@Override
	public boolean threadDeath(ThreadDeathEvent event) {
		log.fine(event.toString());
		return true;
	}

	@Override
	public boolean threadStart(ThreadStartEvent event) {
		log.fine(event.toString());
		return true;
	}

	@Override
	public boolean vmDeath(VMDeathEvent event) {
		log.fine(event.toString());
		return true;
	}

	@Override
	public boolean vmDisconnect(VMDisconnectEvent event) {
		log.fine(event.toString());
		return true;
	}

}
