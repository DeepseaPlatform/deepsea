package za.ac.sun.cs.deepsea.distributed;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import redis.clients.jedis.Jedis;
import za.ac.sun.cs.deepsea.BuildConfig;
import za.ac.sun.cs.deepsea.distributed.Master;
import za.ac.sun.cs.deepsea.diver.Configuration;
import za.ac.sun.cs.deepsea.diver.Diver;
import za.ac.sun.cs.deepsea.diver.SegmentedPathCondition;
import za.ac.sun.cs.deepsea.reporting.Banner;
import za.ac.sun.cs.green.Instance;
import za.ac.sun.cs.green.expr.Constant;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.IntVariable;

/**
 * Master controller for the DEEPSEA project distributed version. It expects a single
 * command-line argument: the filename of a properties file. It creates a
 * logger, and an instance of {@link Configuration}, and uses these to create an
 * instance of {@link Diver}. It reads the settings from the properties file,
 * and runs the diver.
 */
public class Master {

	/**
	 * Signatures for those paths we have visited.
	 */
	protected static final Set<String> visitedSignatures = new HashSet<>();

	/**
	 * Signatures for those path that have already been found to be unreachable.
	 */
	protected static final Set<String> infeasibleSignatures = new HashSet<>();

	/**
	 * A count of the number of paths revisited (i.e., the number of visited
	 * signatures that are re-encountered).
	 */
	protected static int revisitCounter = 0;

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
			LOGGER.debug("established jedis connection");
			jedis.lpush("TASKS", TaskResult.EMPTY.intoString());
			LOGGER.debug("sent the first task");
			int nrOfIncompleteTasks = 1;
			while (nrOfIncompleteTasks > 0) {
				LOGGER.debug("waiting for results");
				int N = Integer.parseInt(jedis.brpop(0, "RESULTS").get(1));
				nrOfIncompleteTasks--;
				LOGGER.debug("received the next result set ({} results)", N);
				while (N-- > 0) {
					String resultString = jedis.brpop(0, "RESULTS").get(1);
					TaskResult result = TaskResult.fromString(resultString);
					LOGGER.debug("processing result {}", result);
					if (mustExplore(LOGGER, result.getPath())) {
						jedis.lpush("TASKS", result.intoString());
					}
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

	private static boolean mustExplore(Logger LOGGER, String signature) {
//		char[] signArray = signature.toCharArray();
//		int signLength = signature.length();
//		if (!visitedSignatures.add(signArray.toString())) {
//			LOGGER.debug("revisit of signature \"" + signature + "\", truncating");
//			revisitCounter++;
//			while ((signature.length() > 0) && (visitedSignatures.contains(signature)
//					|| infeasibleSignatures.contains(signature))) {
//				signature = signature.substring(0, signature.length());
//			}
//		}
//		LOGGER.info("path signature: " + signature);
//		while (signature.length() > 0) {
//			/*
//			 * Flip the first char ('0' <-> '1') of signature.
//			 */
//			SegmentedPathCondition npc = spc.getNegated1st();
//			if (visitedSignatures.contains(npc.getSignature())) {
//				/*
//				 * We now know that both this path condition and the
//				 * complementary path condition (i.e., with the first conjunct
//				 * negated) have been visited. We therefore record that their
//				 * common prefix has been visited, and we drop the first
//				 * conjunct and try to negate the new first conjunct.
//				 */
//				visitedSignatures.add(spc.getParent().getSignature());
//				spc = spc.getParent();
//				logger.debug("dropping first conjunct -> " + spc.getPathCondition());
//			} else {
//				// negate first conjunct of path condition and check if it has a model
//				logger.debug("trying <" + npc.getSignature() + "> " + npc.getPathCondition());
//				Instance instance = new Instance(solver, null, npc.getPathCondition());
//				@SuppressWarnings("unchecked")
//				Map<IntVariable, Object> model = (Map<IntVariable, Object>) instance.request("model");
//				if (model == null) {
//					infeasibleSignatures.add(npc.getSignature());
//					logger.debug("no model");
//					unSatCounter++;
//					// again drop the first conjunct
//					spc = spc.getParent();
//				} else {
//					// translate model
//					Map<String, Constant> newModel = new HashMap<>();
//					for (IntVariable variable : model.keySet()) {
//						String name = variable.getName();
//						Constant value = new IntConstant((Integer) model.get(variable));
//						newModel.put(name, value);
//					}
//					String modelString = newModel.entrySet().stream().filter(p -> !p.getKey().startsWith("$"))
//							.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue())).toString();
//					logger.debug("new model: {}", modelString);
//					if (visitedModels.add(modelString)) {
//						return newModel;
//					} else {
//						logger.debug("model {} has been visited before, recurring", modelString);
//						return refine(spc.getParent());
//					}
//				}
//			}
//		}
//		logger.debug("all signatures explored");
//		return null;
//		
//		
//		
		return false;
	}

}
