package za.ac.sun.cs.deepsea.agent;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.event.AccessWatchpointEvent;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
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

/**
 * An event reader that continuously reads events coming from the VM and
 * dispatch them to the registered listeners.
 */

public class EventReader extends AbstractReader {

	private final Logger log;

	private EventQueue eventQueue;

	private List<EventListener> eventListeners = new LinkedList<>();

	public EventReader(final Diver diver, EventQueue queue) {
		super();
		this.log = diver.getLog();
		eventQueue = queue;
	}

	/**
	 * Registers the given event listener.
	 * 
	 * @param listener
	 */
	public synchronized void addEventListener(EventListener listener) {
		eventListeners.add(listener);
	}

	/**
	 * Dispatches the given event to the given listener. Returns whether the VM
	 * should be resumed.
	 */
	private boolean dispatch(Event event, EventListener listener) {
		if (event instanceof AccessWatchpointEvent) {
			return listener.accessWatchpoint((AccessWatchpointEvent) event);
		} else if (event instanceof BreakpointEvent) {
			return listener.breakpoint((BreakpointEvent) event);
		} else if (event instanceof ClassPrepareEvent) {
			return listener.classPrepare((ClassPrepareEvent) event);
		} else if (event instanceof ClassUnloadEvent) {
			return listener.classUnload((ClassUnloadEvent) event);
		} else if (event instanceof ExceptionEvent) {
			return listener.exception((ExceptionEvent) event);
		} else if (event instanceof MethodEntryEvent) {
			return listener.methodEntry((MethodEntryEvent) event);
		} else if (event instanceof MethodExitEvent) {
			return listener.methodExit((MethodExitEvent) event);
		} else if (event instanceof ModificationWatchpointEvent) {
			return listener.modificationWatchpoint((ModificationWatchpointEvent) event);
		} else if (event instanceof StepEvent) {
			return listener.step((StepEvent) event);
		} else if (event instanceof ThreadDeathEvent) {
			return listener.threadDeath((ThreadDeathEvent) event);
		} else if (event instanceof ThreadStartEvent) {
			return listener.threadStart((ThreadStartEvent) event);
		} else if (event instanceof VMDisconnectEvent) {
			return listener.vmDisconnect((VMDisconnectEvent) event);
		} else if (event instanceof VMDeathEvent) {
			return listener.vmDeath((VMDeathEvent) event);
		} else {
			return true;
		}
	}

	/**
	 * Continuously reads events that are coming from the event queue.
	 */
	protected void readerLoop() {
		while (!isStopping) {
			try {
				if (!isStopping) {
					EventSet eventSet = eventQueue.remove();
					boolean resumeVM = true;
					EventIterator iterator = eventSet.eventIterator();
					while (iterator.hasNext()) {
						Event event = iterator.nextEvent();
						for (int i = 0; i < eventListeners.size(); i++) {
							EventListener listener = (EventListener) eventListeners.get(i);
							resumeVM = resumeVM & dispatch(event, listener);
						}
						if ((event instanceof VMDeathEvent) || (event instanceof VMDisconnectEvent)) {
							stop();
						}
					}
					if (!isStopping && resumeVM) {
						eventSet.resume();
					}
//					if ((!isStopping) && (eventSet != null) && (eventSet.suspendPolicy() == EventRequest.SUSPEND_ALL)
//							&& resumeVM) {
//						synchronized (this) {
//							eventQueue.virtualMachine().resume();
//						}
//					}
				}
			} catch (InterruptedException e) {
				if (!isStopping) {
					log.debug("Event reader loop was interrupted");
					return;
				}
			} catch (VMDisconnectedException e) {
				stop();
				return;
			}
		}
	}

	/**
	 * De-registers the given event listener.
	 * 
	 * @param listener
	 */
	public synchronized void removeEventListener(EventListener listener) {
		eventListeners.remove(listener);
	}

}
