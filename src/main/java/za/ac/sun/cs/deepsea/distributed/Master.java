package za.ac.sun.cs.deepsea.distributed;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import redis.clients.jedis.Jedis;
import za.ac.sun.cs.deepsea.BuildConfig;
import za.ac.sun.cs.deepsea.distributed.Master;
import za.ac.sun.cs.deepsea.diver.Configuration;
import za.ac.sun.cs.deepsea.diver.Diver;
import za.ac.sun.cs.deepsea.reporting.Banner;
import za.ac.sun.cs.green.expr.Constant;

/**
 * Master controller for the DEEPSEA project distributed version. It expects a single
 * command-line argument: the filename of a properties file. It creates a
 * logger, and an instance of {@link Configuration}, and uses these to create an
 * instance of {@link Diver}. It reads the settings from the properties file,
 * and runs the diver.
 */
public class Master {

	/**
	 * The main function.
	 * 
	 * @param args
	 *            command-line arguments.
	 * @throws InterruptedException
	 *             if the 1-second delay is interrupted
	 */
	public static void main(String[] args) throws InterruptedException {
		String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		Logger LOGGER = LogManager.getLogger(jvmName);
		if (args.length < 1) {
			new Banner('@').println("DEEPSEA PROBLEM\nMISSING PROPERTIES FILE\n").println("USAGE: deepsea <properties file>").display(LOGGER, Level.FATAL);
			return;
		}
		LOGGER.info("loading configuration file {}",  args[0]);
		Configuration config = new Configuration();
		if (!config.processProperties(args[0])) {
			new Banner('@').println("DEEPSEA PROBLEM\n").println("COULD NOT READ PROPERTY FILE \"" + args[0] + "\"").display(LOGGER, Level.FATAL);
			return;
		}
		if (config.getTarget() == null) {
			new Banner('@').println("SUSPICIOUS PROPERTIES FILE\n").println("ARE YOU SURE THAT THE ARGUMENT IS A .properties FILE?").display(LOGGER, Level.FATAL);
			return;
		}
		new Banner('#').println("DEEPSEA version " + BuildConfig.VERSION + " DISTRIBUTED MASTER").display(LOGGER, Level.INFO);
		LOGGER.info("");
		try (Jedis jedis = new Jedis("redis")) {
			jedis.lpush("TASKS", TaskResult.EMPTY.intoString());
			int nrOfIncompleteTasks = 1;
			while (nrOfIncompleteTasks > 0) {
				nrOfIncompleteTasks--;
				int N = Integer.parseInt(jedis.brpop(0, "RESULTS").get(1));
				while (N-- > 0) {
					String resultString = jedis.brpop(0, "RESULTS").get(1);
					TaskResult result = TaskResult.fromString(resultString);
				//   if (result is new) {
				//     push(TASKS, R)
				//   }
				}
			}
		} catch (ClassNotFoundException x) {
			LOGGER.fatal("class-not-found while de-serializing result", x);
		} catch (IOException x) {
			LOGGER.fatal("IO problem while de-serializing result", x);
		}
		LOGGER.info("");
		new Banner('#').println("DEEPSEA DONE").display(LOGGER, Level.INFO);
	}

}
