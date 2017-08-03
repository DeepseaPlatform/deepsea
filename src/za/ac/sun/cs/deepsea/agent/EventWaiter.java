package za.ac.sun.cs.deepsea.agent;

import com.sun.jdi.event.AccessWatchpointEvent;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.EventRequest;

/**
 * Listen for a specific kind of event.
 */
public class EventWaiter implements EventListener {

	protected final EventRequest request;

	protected final boolean shouldGo;

	protected Event prevEvent;

	/**
	 * Creates a new EventWaiter for the given request. Sets whether it should
	 * let the VM go after it got the event.
	 * 
	 * @param request
	 * @param shouldGo
	 */
	public EventWaiter(final EventRequest request, final boolean shouldGo) {
		this.request = request;
		this.shouldGo = shouldGo;
	}

	/**
	 * @see za.ac.sun.cs.deepsea.agent.EventListener#accessWatchpoint(com.sun.jdi.event.AccessWatchpointEvent)
	 */
	public boolean accessWatchpoint(AccessWatchpointEvent event) {
		return handleEvent(event);
	}

	/**
	 * @see za.ac.sun.cs.deepsea.agent.EventListener#methodEntry(com.sun.jdi.event.MethodEntryEvent)
	 */
	public boolean methodEntry(MethodEntryEvent event) {
		return handleEvent(event);
	}

	/**
	 * @see za.ac.sun.cs.deepsea.agent.EventListener#methodExit(com.sun.jdi.event.MethodExitEvent)
	 */
	public boolean methodExit(MethodExitEvent event) {
		return handleEvent(event);
	}

	/**
	 * @see za.ac.sun.cs.deepsea.agent.EventListener#breakpoint(com.sun.jdi.event.BreakpointEvent)
	 */
	public boolean breakpoint(BreakpointEvent event) {
		return handleEvent(event);
	}

	/**
	 * @see za.ac.sun.cs.deepsea.agent.EventListener#classPrepare(com.sun.jdi.event.ClassPrepareEvent)
	 */
	public boolean classPrepare(ClassPrepareEvent event) {
		return handleEvent(event);
	}

	/**
	 * @see za.ac.sun.cs.deepsea.agent.EventListener#classUnload(com.sun.jdi.event.ClassUnloadEvent)
	 */
	public boolean classUnload(ClassUnloadEvent event) {
		return handleEvent(event);
	}

	/**
	 * @see za.ac.sun.cs.deepsea.agent.EventListener#exception(com.sun.jdi.event.ExceptionEvent)
	 */
	public boolean exception(ExceptionEvent event) {
		return handleEvent(event);
	}

	/**
	 * Handles an incoming event. Returns whether the VM should be resumed if it
	 * was suspended.
	 */
	protected boolean handleEvent(Event event) {
		if ((event.request() != null) && (event.request().equals(request))) {
			notifyEvent(event);
			return shouldGo;
		}
		return true;
	}

	/**
	 * @see za.ac.sun.cs.deepsea.agent.EventListener#modificationWatchpoint(com.sun.jdi.event.ModificationWatchpointEvent)
	 */
	public boolean modificationWatchpoint(ModificationWatchpointEvent event) {
		return handleEvent(event);
	}

	/**
	 * Notify any object that is waiting for an event.
	 */
	synchronized protected void notifyEvent(Event event) {
		notify();
		prevEvent = event;
	}

	/**
	 * @see za.ac.sun.cs.deepsea.agent.EventListener#step(com.sun.jdi.event.StepEvent)
	 */
	public boolean step(StepEvent event) {
		return handleEvent(event);
	}

	/**
	 * @see za.ac.sun.cs.deepsea.agent.EventListener#threadDeath(com.sun.jdi.event.ThreadDeathEvent)
	 */
	public boolean threadDeath(ThreadDeathEvent event) {
		return handleEvent(event);
	}

	/**
	 * @see za.ac.sun.cs.deepsea.agent.EventListener#threadStart(com.sun.jdi.event.ThreadStartEvent)
	 */
	public boolean threadStart(ThreadStartEvent event) {
		return handleEvent(event);
	}

	/**
	 * @see za.ac.sun.cs.deepsea.agent.EventListener#vmDeath(com.sun.jdi.event.VMDeathEvent)
	 */
	public boolean vmDeath(VMDeathEvent event) {
		if (prevEvent == null) { // Last ever event but not what we expected
			notifyEvent(null);
			return true;
		}
		return handleEvent(event);
	}

	/**
	 * @see za.ac.sun.cs.deepsea.agent.EventListener#vmDisconnect(com.sun.jdi.event.VMDisconnectEvent)
	 */
	public boolean vmDisconnect(VMDisconnectEvent event) {
		return handleEvent(event);
	}

	/**
	 * Waits for the first event corresponding to this waiter's request.
	 * 
	 * @return if the vm should be restarted
	 * @throws InterruptedException
	 */
	synchronized public Event waitEvent() throws InterruptedException {
		if (prevEvent == null) { // No event as yet
			wait();
		}
		Event result = prevEvent;
		prevEvent = null;
		return result;
	}

	/**
	 * Waits for the first event corresponding to this waiter's request for the
	 * given time (in ms). If it times out, return null.
	 * 
	 * @param time
	 * @return if the vm should be restarted or not
	 * @throws InterruptedException
	 */
	synchronized public Event waitEvent(long time) throws InterruptedException {
		if (prevEvent == null) { // No event as yet
			wait(time);
		}
		Event result = prevEvent;
		prevEvent = null;
		return result;
	}

}
