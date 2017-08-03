package agent;

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

import main.DEEPSEA;

public class EventPrinter extends AbstractEventListener {

	@Override
	public boolean accessWatchpoint(AccessWatchpointEvent event) {
		DEEPSEA.log.fine(event.toString());
		return true;
	}

	@Override
	public boolean breakpoint(BreakpointEvent event) {
		DEEPSEA.log.fine(event.toString());
		return true;
	}

	@Override
	public boolean classPrepare(ClassPrepareEvent event) {
		DEEPSEA.log.fine(event.toString());
		return true;
	}

	@Override
	public boolean classUnload(ClassUnloadEvent event) {
		DEEPSEA.log.fine(event.toString());
		return true;
	}

	@Override
	public boolean exception(ExceptionEvent event) {
		DEEPSEA.log.fine(event.toString());
		return true;
	}

	@Override
	public boolean methodEntry(MethodEntryEvent event) {
		DEEPSEA.log.fine(event.toString());
		return true;
	}

	@Override
	public boolean methodExit(MethodExitEvent event) {
		DEEPSEA.log.fine(event.toString());
		return true;
	}

	@Override
	public boolean modificationWatchpoint(ModificationWatchpointEvent event) {
		DEEPSEA.log.fine(event.toString());
		return true;
	}

	@Override
	public boolean step(StepEvent event) {
		DEEPSEA.log.fine(event.toString());
		return true;
	}

	@Override
	public boolean threadDeath(ThreadDeathEvent event) {
		DEEPSEA.log.fine(event.toString());
		return true;
	}

	@Override
	public boolean threadStart(ThreadStartEvent event) {
		DEEPSEA.log.fine(event.toString());
		return true;
	}

	@Override
	public boolean vmDeath(VMDeathEvent event) {
		DEEPSEA.log.fine(event.toString());
		return true;
	}

	@Override
	public boolean vmDisconnect(VMDisconnectEvent event) {
		DEEPSEA.log.fine(event.toString());
		return true;
	}

}
