package za.ac.sun.cs.deepsea.distributed;

import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import redis.clients.jedis.Jedis;
import za.ac.sun.cs.deepsea.BuildConfig;
import za.ac.sun.cs.deepsea.diver.Configuration;
import za.ac.sun.cs.deepsea.diver.Dive;
import za.ac.sun.cs.deepsea.reporting.Banner;
import za.ac.sun.cs.green.expr.Constant;

public class Worker {

	/**
	 * The main function.
	 * 
	 * @param args
	 *            command-line arguments.
	 */
	public static void main(String[] args) {
		Configuration config = new Configuration();
		if (args.length < 1) {
			new Banner('@').println("DEEPSEA PROBLEM\nMISSING PROPERTIES FILE\n")
					.println("USAGE: deepsea <properties file>").display(System.out);
			return;
		}
		if (!config.processProperties(args[0])) {
			new Banner('@').println("DEEPSEA PROBLEM\n").println("COULD NOT READ PROPERTY FILE \"" + args[0] + "\"")
					.display(System.out);
			return;
		}
		if (config.getTarget() == null) {
			new Banner('@').println("SUSPICIOUS PROPERTIES FILE\n")
					.println("ARE YOU SURE THAT THE ARGUMENT IS A .properties FILE?").display(System.out);
			return;
		}
		// Configuration has now been loaded and seems OK
		Logger logger = config.getLogger();
		new Banner('#').println("DEEPSEA version " + BuildConfig.VERSION + " DISTRIBUTED WORKER").display(logger, Level.INFO);
		try (Jedis jedis = new Jedis("redis")) {
			while (true) {
				String task = jedis.brpop(0, "TASKS").get(1);
				if (task.equals("QUIT")) {
					break;
				}
				Map<String, Constant> concreteValues = decode(task);
				Dive d = new Dive("????", logger, config, concreteValues);
				if (d.dive()) {
					// concreteValues = explorer.refine(d);
				}
			}
		}
//		Urinator urinator = new Urinator("DEEPSEA", logger, config);
//		logger.info("");
//		urinator.start();
//		logger.info("");
		new Banner('#').println("DEEPSEA DONE").display(logger, Level.INFO);
	}

	private static Map<String, Constant> decode(String task) {
		return null;
	}

}
