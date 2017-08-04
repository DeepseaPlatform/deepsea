package za.ac.sun.cs.deepsea.agent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.sun.jdi.Field;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.AccessWatchpointRequest;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.ClassUnloadRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ExceptionRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.ModificationWatchpointRequest;
import com.sun.jdi.request.MonitorContendedEnterRequest;
import com.sun.jdi.request.MonitorContendedEnteredRequest;
import com.sun.jdi.request.MonitorWaitRequest;
import com.sun.jdi.request.MonitorWaitedRequest;
import com.sun.jdi.request.StepRequest;
import com.sun.jdi.request.ThreadDeathRequest;
import com.sun.jdi.request.ThreadStartRequest;
import com.sun.jdi.request.VMDeathRequest;

import za.ac.sun.cs.deepsea.diver.Diver;

public class RequestManager {

	@SuppressWarnings("unused")
	private final Diver diver;

	private final EventRequestManager mgr;
	
	private final Set<String> excludes = new HashSet<>();

	public RequestManager(final Diver diver, final EventRequestManager mgr) {
		this.diver = diver;
		this.mgr = mgr;
	}

	public void addExclude(String... excludes) {
		for (String exclude : excludes) {
			this.excludes.add(exclude);
		}
	}

	public void removeExclude(String exclude) {
		excludes.remove(exclude);
	}

	public void filterExcludes(MethodEntryRequest eventRequest) {
		for (String exclude : excludes) {
			eventRequest.addClassExclusionFilter(exclude);
		}
	}

	public void filterExcludes(MethodExitRequest eventRequest) {
		for (String exclude : excludes) {
			eventRequest.addClassExclusionFilter(exclude);
		}
	}
	
	public void filterExcludes(ClassPrepareRequest eventRequest) {
		for (String exclude : excludes) {
			eventRequest.addClassExclusionFilter(exclude);
		}
	}
	
	public void filterExcludes(StepRequest eventRequest) {
		for (String exclude : excludes) {
			eventRequest.addClassExclusionFilter(exclude);
		}
	}

	public void removeRequest(EventRequest eventRequest) {
		mgr.deleteEventRequest(eventRequest);
	}

	public VirtualMachine getVM() {
		return mgr.virtualMachine();
	}

	public AccessWatchpointRequest createAccessWatchpointRequest(Field field) {
		AccessWatchpointRequest req = mgr.createAccessWatchpointRequest(field);
		req.enable();
		return req;
	}

	public BreakpointRequest createBreakpointRequest(Location location) {
		BreakpointRequest req = mgr.createBreakpointRequest(location);
		req.enable();
		return req;
	}
	
	public ClassPrepareRequest createClassPrepareRequest(Consumer<ClassPrepareRequest> refine) {
		ClassPrepareRequest req = mgr.createClassPrepareRequest();
		refine.accept(req);
		req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
		req.enable();
		return req;
	}
	
	public ClassPrepareRequest createClassPrepareRequest() {
		ClassPrepareRequest req = mgr.createClassPrepareRequest();
		req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
		req.enable();
		return req;
	}
	
	public ClassUnloadRequest createClassUnloadRequest() {
		ClassUnloadRequest req = mgr.createClassUnloadRequest();
		req.enable();
		return req;
	}
	
	public ExceptionRequest createExceptionRequest(ReferenceType refType, boolean notifyCaught, boolean notifyUncaught) {
		ExceptionRequest req = mgr.createExceptionRequest(refType, notifyCaught, notifyUncaught);
		req.enable();
		return req;
	}
	
	public MethodEntryRequest createMethodEntryRequest(Consumer<MethodEntryRequest> refine) {
		MethodEntryRequest req = mgr.createMethodEntryRequest();
		refine.accept(req);
		req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
		req.enable();
		return req;
	}
	
	public MethodEntryRequest createMethodEntryRequest() {
		MethodEntryRequest req = mgr.createMethodEntryRequest();
		req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
		req.enable();
		return req;
	}
	
	public MethodExitRequest createMethodExitRequest(Consumer<MethodExitRequest> refine) {
		MethodExitRequest req = mgr.createMethodExitRequest();
		refine.accept(req);
		req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
		req.enable();
		return req;
	}
	
	public MethodExitRequest createMethodExitRequest() {
		MethodExitRequest req = mgr.createMethodExitRequest();
		req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
		req.enable();
		return req;
	}
	
	public ModificationWatchpointRequest createModificationWatchpointRequest(Field field) {
		ModificationWatchpointRequest req = mgr.createModificationWatchpointRequest(field);
		req.enable();
		return req;
	}

	public MonitorContendedEnteredRequest createMonitorContendedEnteredRequest() {
		MonitorContendedEnteredRequest req = mgr.createMonitorContendedEnteredRequest();
		req.enable();
		return req;
	}
	
	public MonitorContendedEnterRequest createMonitorContendedEnterRequest() {
		MonitorContendedEnterRequest req = mgr.createMonitorContendedEnterRequest();
		req.enable();
		return req;
	}
	
	public MonitorWaitedRequest createMonitorWaitedRequest() {
		MonitorWaitedRequest req = mgr.createMonitorWaitedRequest();
		req.enable();
		return req;
	}
	
	public MonitorWaitRequest createMonitorWaitRequest() {
		MonitorWaitRequest req = mgr.createMonitorWaitRequest();
		req.enable();
		return req;
	}
	
	public StepRequest createStepRequest(ThreadReference thread, int size, int depth, Consumer<StepRequest> refine) {
		StepRequest req = mgr.createStepRequest(thread, size, depth);
		refine.accept(req);
		req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
		req.enable();
		return req;
	}
	
	public StepRequest createStepRequest(ThreadReference thread, int size, int depth) {
		StepRequest req = mgr.createStepRequest(thread, size, depth);
		req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
		req.enable();
		return req;
	}
	
	public ThreadDeathRequest createThreadDeathRequest() {
		ThreadDeathRequest req = mgr.createThreadDeathRequest();
		req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
		req.enable();
		return req;
	}
	
	public ThreadStartRequest createThreadStartRequest() {
		ThreadStartRequest req = mgr.createThreadStartRequest();
		req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
		req.enable();
		return req;
	}
	
	public VMDeathRequest createVMDeathRequest() {
		VMDeathRequest req = mgr.createVMDeathRequest();
		req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
		req.enable();
		return req;
	}

	public static ThreadReference findThread(VirtualMachine vm, String threadName) {
		for (ThreadReference thread : vm.allThreads()) {
			if (thread.name().equals(threadName)) {
				return thread;
			}
		}
		return null;
	}

}
