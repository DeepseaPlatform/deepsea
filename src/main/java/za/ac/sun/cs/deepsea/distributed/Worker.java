package za.ac.sun.cs.deepsea.distributed;

import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jppf.node.protocol.AbstractTask;
import org.jppf.node.protocol.DataProvider;

import redis.clients.jedis.Jedis;
import za.ac.sun.cs.deepsea.diver.Configuration;
import za.ac.sun.cs.deepsea.reporting.Banner;

public class Worker extends AbstractTask<Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2315056117721879746L;

	private final int id;
	
	private Logger logger;
	
	private Configuration config;
	
	public Worker(int id) {
		super();
		this.id = id;
	}

	@Override
	public void run() {
		System.out.println("WORKER " + id + ": Starting");
		setResult(false);
		System.out.println("WORKER " + id + ": Loading config");
		config = readConfiguration(getDataProvider());
		System.out.println("WORKER " + id + ": Config loaded");
		logger = config.getLogger();
//		logger.info("WORKER {}: Starting", id);
//		System.out.println("WORKER " + id + ": target = " + config.getTarget());
		Jedis jedis = new Jedis("192.168.1.154", 6379, 10000);
//		while (true) {
//			List<String> pcs = jedis.blpop(10000, "FARSEA");
//			System.out.println("WORKER " + id + ": <<pcs == " + pcs + ">>");
//			String pc = pcs.get(1);
//			if (pc.equals("Q")) {
//				break;
//			}
//			System.out.println("WORKER " + id + ": Fetched task " + pc);
//			delay(1000);
//		}
		new Banner('#').println("DEEPSEA worker " + id).display(logger, Level.INFO);
		Urinator urinator = new Urinator("DEEPSEA-" + id, logger, config);
		logger.info("");
		urinator.start();
		logger.info("");
		new Banner('#').println("DEEPSEA DONE").display(logger, Level.INFO);
		jedis.close();
		System.out.println("WORKER " + id + ": Done");
		setResult(true);
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

//	private void delay(long ms) {
//		try {
//			Thread.sleep(ms);
//		} catch (InterruptedException x) {
//			x.printStackTrace();
//		}
//	}

}
