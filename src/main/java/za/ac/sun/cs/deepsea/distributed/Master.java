package za.ac.sun.cs.deepsea.distributed;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jppf.JPPFException;
import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFJob;
import org.jppf.management.JMXDriverConnectionWrapper;
import org.jppf.node.policy.Equal;
import org.jppf.node.policy.ExecutionPolicy;
import org.jppf.node.protocol.DataProvider;
import org.jppf.node.protocol.MemoryMapDataProvider;
import org.jppf.node.protocol.Task;
import org.jppf.utils.ExceptionUtils;

import redis.clients.jedis.Jedis;
import za.ac.sun.cs.deepsea.diver.Configuration;
import za.ac.sun.cs.deepsea.reporting.Banner;

public class Master {

	private static final int TASKS = 5;

	private static final int REDIS_PORT = 6379;
	
	private static final int REDIS_DELAY = 5000;
	
	private static final int JPPF_DRIVER_PORT = 11198;
	
	private final static ExecutionPolicy masterNodePolicy = new Equal("jppf.node.provisioning.master", true);

	public static void executeJob(Logger logger, Configuration config) {
		String host = getHostIp();
		int nrNodes = getNrOfNodes(host);
		if (nrNodes == 0) {
			new Banner('@').println("NO JPPF NODES FOUND").display(logger, Level.INFO);
			return;
		}
		try (JPPFClient client = new JPPFClient(); Jedis jedis = new Jedis(host, REDIS_PORT, REDIS_DELAY)) {
			logger.info("Creating JPPF job");
			JPPFJob job = new JPPFJob();
			job.setName("DEEPSEA");
			job.setDataProvider(publishProperties(config));
			logger.info("Adding JPPF tasks");
			for (int i = 0; i < nrNodes; i++) {
				try {
					job.add(new Worker(host)).setId("W" + i);
				} catch (JPPFException x) {
					x.printStackTrace();
				}
			}
			job.getSLA().setExecutionPolicy(masterNodePolicy);
			job.setBlocking(false);
			client.submitJob(job);
			logger.info("Queueing data");
			for (int i = 0; i < TASKS; i++) {
				jedis.rpush("FARSEA", "" + i);
				logger.info("Pushing {}", i);
			}
			for (int i = 0; i < nrNodes; i++) {
				logger.info("Pushing Q{}", i);
				jedis.rpush("FARSEA", "Q");
			}
			logger.info("Queue monitoring done");
			List<Task<?>> results = job.awaitResults();
			for (Task<?> task : results) {
				if (task.getThrowable() != null) {
					logger.error("task '{}' got exception: {}", task.getId(),
							ExceptionUtils.getStackTrace(task.getThrowable()));
				} else {
					logger.info("task '{}' got result: {}", task.getId(), task.getResult());
				}
			}
		}
		logger.info("All jobs completed -- shutting down");
	}

	private static int getNrOfNodes(String host) {
		try (JMXDriverConnectionWrapper wrapper = new JMXDriverConnectionWrapper(host, JPPF_DRIVER_PORT)) {
			wrapper.connectAndWait(0);
			Map<String, String> params = new HashMap<>();
			params.put("size", "1");
			wrapper.changeLoadBalancerSettings("manual", params);
			return wrapper.nbNodes();
		} catch (Exception x) {
			x.printStackTrace();
		}
		return 0;
	}

	private static String getHostIp() {
		try {
			String ipAddr = InetAddress.getLocalHost().getHostAddress();
			return ipAddr;
		} catch (UnknownHostException x) {
			x.printStackTrace();
		}
		return "localhost";
	}

	private static DataProvider publishProperties(Configuration config) {
		DataProvider dataProvider = new MemoryMapDataProvider();
		Properties properties = config.produceProperties();
		for (Object key : properties.keySet()) {
			dataProvider.setParameter(key, properties.get(key));
		}
		dataProvider.setParameter("deepsea.target", config.getTarget());
		return dataProvider;
	}

}
