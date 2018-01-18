package za.ac.sun.cs.deepsea.distributed;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFJob;
import org.jppf.management.JMXNodeConnectionWrapper;
import org.jppf.node.policy.Equal;
import org.jppf.node.policy.ExecutionPolicy;
import org.jppf.node.protocol.AbstractTask;
import org.jppf.node.protocol.DataProvider;
import org.jppf.node.protocol.Task;
import org.jppf.node.provisioning.JPPFNodeProvisioningMBean;
import org.jppf.utils.JPPFConfiguration;
import org.jppf.utils.TypedProperties;

import redis.clients.jedis.Jedis;
import za.ac.sun.cs.deepsea.diver.Configuration;
import za.ac.sun.cs.deepsea.reporting.Banner;

public class Worker extends AbstractTask<Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2315056117721879746L;

	private static final int DEBUG_PORT_BASE = 8120;

	private static final int REDIS_PORT = 6379;
	
	private static final int REDIS_DELAY = 5000;
	
	private static final ExecutionPolicy slaveNodePolicy = new Equal("jppf.node.provisioning.slave", true);

	private final AtomicBoolean stopMonitoring = new AtomicBoolean(false);
	
	private final String host;
	
	public Worker(String host) {
		super();
		this.host = host;
	}

	@Override
	public void run() {
		Configuration config = readConfiguration(getDataProvider());
		Logger logger = config.getLogger();
		configureClient();
		try (JPPFClient client = new JPPFClient(); Jedis jedis = new Jedis(host, REDIS_PORT, REDIS_DELAY)) {
			int nrRuns = 0;
			while (true) {
				int port = DEBUG_PORT_BASE + nrRuns;
				String input = getNextInputs(jedis);
				if (input == null) { break; }
				logger.info("New input");
				nrRuns++;
				JPPFJob job = new JPPFJob();
				job.setName("SUT");
				job.add(new StarterTask(config));
				job.setBlocking(false);
				stopMonitoring.set(false);
				startSlaveNode(port, job);
				client.submitJob(job);
				logger.info("Started SUT");
				try {
					Thread monitor = startMonitoring(port, nrRuns, logger, config);
					logger.info("Waiting for SUT to terminate");
					List<Task<?>> results = job.awaitResults();
					stopMonitoring.set(true);
					logger.info("Waiting for monitor to terminate");
					monitor.join();
					for (Task<?> task : results) {
						if (task.getThrowable() != null) {
							logger.error("Exception on " + getId(), task.getThrowable());
						}
					}
				} finally {
					stopSlaveNode();
				}
			}
		} catch (Exception x) {
			setThrowable(x);
		}
	}

	private Thread startMonitoring(int port, int nrRuns, Logger logger, Configuration config) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				new Banner('#').println("DEEPSEA worker " + getId() + " run " + nrRuns).display(logger, Level.INFO);
				Urinator urinator = new Urinator(getId(), port, logger, config);
				logger.info("");
				urinator.start();
				logger.info("");
				new Banner('#').println("DEEPSEA DONE").display(logger, Level.INFO);
			}
		}, "MonitorThread");
		t.start();
		return t;
	}

	private String getNextInputs(Jedis jedis) {
		List<String> inputPair = jedis.blpop(10000, "FARSEA");
		String input = inputPair.get(1);
		return input.equals("Q") ? null : input;
	}

	private void configureClient() {
		TypedProperties nodeConfig = JPPFConfiguration.getProperties();
		nodeConfig.setString("jppf.drivers", "driver1")
			.setString("driver1.jppf.server.host", host)
			.setInt("driver1.jppf.server.port", 11111);
	}

	private Configuration readConfiguration(DataProvider dataProvider) {
		Properties properties = new Properties();
		for (Object key : dataProvider.getAll().keySet()) {
			if (key instanceof String) {
				String k = (String) key;
				if (k.startsWith("deepsea.")) {
					properties.put(k, dataProvider.getParameter(key));
				}
			}
		}
		Configuration config = new Configuration();
		config.processProperties(properties);
		return config;
	}

	private void startSlaveNode(int port, JPPFJob job) throws Exception {
		String debug = "-agentlib:jdwp=transport=dt_socket,server=y,timeout=10000,address=" + port;
		String uuid = job.getUuid();
		ExecutionPolicy policy = slaveNodePolicy.and(new Equal("job.uuid", false, uuid));
		job.getSLA().setExecutionPolicy(policy);
		try (JMXNodeConnectionWrapper jmx = new JMXNodeConnectionWrapper()) {
			jmx.connect();
			TypedProperties configOverrides = new TypedProperties()
					.setString("job.uuid", uuid)
					.setString("jppf.jvm.options", "-Xmx64m -Xms64m " + debug);
			JPPFNodeProvisioningMBean provisioner = jmx.getJPPFNodeProvisioningProxy();
			provisioner.provisionSlaveNodes(1, configOverrides);
			while (provisioner.getNbSlaves() < 1) {
				Thread.sleep(10L);
			}
		}
	}

	private void stopSlaveNode() throws Exception {
		try (JMXNodeConnectionWrapper jmx = new JMXNodeConnectionWrapper()) {
			jmx.connect();
			JPPFNodeProvisioningMBean provisioner = jmx.getJPPFNodeProvisioningProxy();
			provisioner.provisionSlaveNodes(0);
			while (provisioner.getNbSlaves() > 0) {
				Thread.sleep(10L);
			}
		}
	}

}
