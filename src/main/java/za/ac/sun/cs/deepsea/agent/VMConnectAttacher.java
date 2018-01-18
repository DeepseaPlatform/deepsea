package za.ac.sun.cs.deepsea.agent;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.Map;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

public class VMConnectAttacher {

	public static VirtualMachine launchTarget(int port) {
		AttachingConnector connector = findAttachingConnector();
		Map<String, Connector.Argument> arguments = connector.defaultArguments();
		Connector.IntegerArgument portArg = (Connector.IntegerArgument) arguments.get("port");
		if (portArg == null) {
			throw new Error("Bad attaching connector");
		}
		portArg.setValue(port);
		Connector.IntegerArgument timeoutArg = (Connector.IntegerArgument) arguments.get("timeout");
		if (timeoutArg == null) {
			throw new Error("Bad attaching connector");
		}
		timeoutArg.setValue(5000);
		try {
			VirtualMachine vm = null;
			while (vm == null) {
				try {
					vm = connector.attach(arguments);
				} catch (ConnectException x) {
					delay(100);
				}
			}
			vm.setDebugTraceMode(0);
			return vm;
		} catch (IOException x1) {
			throw new Error("Unable to launch target VM: " + x1);
		} catch (IllegalConnectorArgumentsException x1) {
			throw new Error("Internal error: " + x1);
		}
	}

	private static AttachingConnector findAttachingConnector() {
		List<AttachingConnector> connectors = Bootstrap.virtualMachineManager().attachingConnectors();
		for (AttachingConnector connector : connectors) {
			if ("com.sun.jdi.SocketAttach".equals(connector.name())) {
				return connector;
			}
		}
		throw new Error("No attaching connector");
	}

	private static void delay(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException x) {
			// IGNORE
			// x.printStackTrace();
		}
	}

}
