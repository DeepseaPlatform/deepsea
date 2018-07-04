package za.ac.sun.cs.deepsea.distributed;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import redis.clients.jedis.Jedis;
import za.ac.sun.cs.deepsea.DEEPSEA;
import za.ac.sun.cs.deepsea.diver.Configuration;
import za.ac.sun.cs.deepsea.diver.Dive;
import za.ac.sun.cs.deepsea.reporting.Banner;

public class Worker {

	/**
	 * The main function.
	 * 
	 * @param args
	 *            command-line arguments.
	 */
	public static void main(String[] args) {
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
		new Banner('#').println("DEEPSEA version " + DEEPSEA.VERSION + " DISTRIBUTED WORKER").display(LOGGER, Level.INFO);
		LOGGER.info("");
		try (Jedis jedis = new Jedis("redis")) {
			LOGGER.debug("established jedis connection");
			int diveCounter = 0;
			while (true) {
				String taskString = jedis.brpop(0, "TASKS").get(1);
				if (taskString.equals("QUIT")) {
					LOGGER.debug("removed QUIT task");
					break;
				}
				TaskResult task = TaskResult.fromString(taskString);
				LOGGER.debug("removed task {}", task);
				Dive d = new Dive(jvmName + "-" + diveCounter++, LOGGER, config, task.getValues());
				if (d.dive()) {
					// concreteValues = explorer.refine(d);
				}
			}
		} catch (ClassNotFoundException x) {
			LOGGER.fatal("class-not-found while de-serializing result", x);
		} catch (IOException x) {
			LOGGER.fatal("IO problem while de-serializing result", x);
		}
		new Banner('#').println("DEEPSEA DONE").display(LOGGER, Level.INFO);
	}

}
