package za.ac.sun.cs.deepsea.distributed;

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
import org.jppf.node.protocol.DataProvider;
import org.jppf.node.protocol.MemoryMapDataProvider;
import org.jppf.node.protocol.Task;

import redis.clients.jedis.Jedis;
import za.ac.sun.cs.deepsea.diver.Configuration;
import za.ac.sun.cs.deepsea.reporting.Banner;

public class Master {

	public static final int TASKS = 10;

	private final Logger logger;

	private final Configuration config;

	public Master(Logger logger, Configuration config) {
		this.logger = logger;
		this.config = config;
	}

	public void executeJob(final JPPFClient jppfClient) {
		int nrNodes = 0;
		logger.info("Connecting to JPPF driver");
		try (JMXDriverConnectionWrapper wrapper = new JMXDriverConnectionWrapper("localhost", 11198)) {
			wrapper.connectAndWait(0);
			logger.info("Setting load balancing policy");
			Map<String, String> params = new HashMap<>();
			params.put("size", "1");
			wrapper.changeLoadBalancerSettings("manual", params);
			nrNodes = wrapper.nbNodes();
			logger.info("Number of nodes: " + nrNodes);
		} catch (Exception x) {
			x.printStackTrace();
		}
		if (nrNodes == 0) {
			new Banner('@').println("NO JPPF NODES FOUND").display(logger, Level.INFO);
			return;
		}

		logger.info("Connecting to redis server");
		Jedis jedis = new Jedis("localhost", 6379, 2000);

		logger.info("Creating JPPF job");
		JPPFJob job = new JPPFJob();
		job.setName("DEEPSEA");
		job.setDataProvider(publishProperties(config));
		logger.info("Adding JPPF tasks");
		for (int i = 0; i < nrNodes; i++) {
			try {
				job.add(new Worker(i));
			} catch (JPPFException x) {
				x.printStackTrace();
			}
		}
		job.setBlocking(false);
		jppfClient.submitJob(job);

		logger.info("Queueing data");
		delay(1000);
//		for (int i = 0; i < TASKS; i++) {
//			jedis.rpush("FARSEA", "" + i);
//			logger.info("Pushing {}", i);
//			delay(1000);
//		}
//		for (int i = 0; i < nrNodes; i++) {
//			logger.info("Pushing Q{}", i);
//			jedis.rpush("FARSEA", "Q");
//		}
		logger.info("Queue monitoring done");

		List<Task<?>> results = job.awaitResults();
		for (Task<?> task : results) {
			String taskName = task.getId();
			logger.info("Worker {}: result {}", taskName, task.getResult());
//			if (task.getThrowable() != null) {
//				System.out.println(taskName + ", an exception was raised: " + task.getThrowable().getMessage());
//			} else {
//				System.out.println(taskName + ", execution result: " + task.getResult());
//			}
		}
		jedis.close();
		logger.info("All jobs completed -- shutting down");
	}

	private DataProvider publishProperties(Configuration config) {
		DataProvider dataProvider = new MemoryMapDataProvider();
		Properties properties = config.produceProperties();
		for (Object key : properties.keySet()) {
			dataProvider.setParameter(key, properties.get(key));
		}
		dataProvider.setParameter("deepsea.target", config.getTarget());
		return dataProvider;
	}

	private void delay(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException x) {
			x.printStackTrace();
		}
	}

}
