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

public class AbstractEventListener implements EventListener {

	@Override
	public boolean accessWatchpoint(AccessWatchpointEvent event) {
		return true;
	}

	@Override
	public boolean breakpoint(BreakpointEvent event) {
		return true;
	}

	@Override
	public boolean classPrepare(ClassPrepareEvent event) {
		return true;
	}

	@Override
	public boolean classUnload(ClassUnloadEvent event) {
		return true;
	}

	@Override
	public boolean exception(ExceptionEvent event) {
		return true;
	}

	@Override
	public boolean methodEntry(MethodEntryEvent event) {
		return true;
	}

	@Override
	public boolean methodExit(MethodExitEvent event) {
		return true;
	}

	@Override
	public boolean modificationWatchpoint(ModificationWatchpointEvent event) {
		return true;
	}

	@Override
	public boolean step(StepEvent event) {
		return true;
	}

	@Override
	public boolean threadDeath(ThreadDeathEvent event) {
		return true;
	}

	@Override
	public boolean threadStart(ThreadStartEvent event) {
		return true;
	}

	@Override
	public boolean vmDeath(VMDeathEvent event) {
		return true;
	}

	@Override
	public boolean vmDisconnect(VMDisconnectEvent event) {
		return true;
	}

}
