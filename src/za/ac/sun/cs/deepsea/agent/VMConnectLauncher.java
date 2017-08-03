package za.ac.sun.cs.deepsea.agent;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;

public class VMConnectLauncher {

	private static final String CONNECTOR = "com.sun.jdi.CommandLineLaunch";

	public static VirtualMachine launchTarget(final String[] args) {
		LaunchingConnector connector = findLaunchingConnector(CONNECTOR);
		Map<String, Connector.Argument> arguments = connector.defaultArguments();
		Connector.Argument mainArg = (Connector.Argument) arguments.get("main");
		if (mainArg == null) {
			throw new Error("Bad launching connector");
		}
		mainArg.setValue(String.join(" ", args));
		try {
			VirtualMachine vm = connector.launch(arguments);
			vm.setDebugTraceMode(0);
			return vm;
		} catch (IOException x) {
			throw new Error("Unable to launch target VM: " + x);
		} catch (IllegalConnectorArgumentsException x) {
			throw new Error("Internal error: " + x);
		} catch (VMStartException x) {
			throw new Error("Target VM failed to initialize: " + x.getMessage());
		}
	}

	private static LaunchingConnector findLaunchingConnector(String name) {
		List<Connector> connectors = Bootstrap.virtualMachineManager().allConnectors();
		for (Connector connector : connectors) {
			if (connector.name().equals(name)) {
				return (LaunchingConnector) connector;
			}
		}
		throw new Error("No launching connector");
	}

}
