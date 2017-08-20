package za.ac.sun.cs.deepsea.explorer;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import za.ac.sun.cs.deepsea.diver.Dive;
import za.ac.sun.cs.deepsea.diver.Diver;
import za.ac.sun.cs.green.Green;
import za.ac.sun.cs.green.Instance;
import za.ac.sun.cs.green.expr.Constant;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.IntVariable;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;
import za.ac.sun.cs.green.util.Configuration;

/**
 * An explorer that tries to explore paths in a depth-first pattern.
 * Occasionally it may be that the program behaviour causes this explorer to
 * jump from one path to an unrelated path. Despite this, the explorer tries to
 * recover and explore all paths in the same order that they are visited during
 * a depth-first search.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class DepthFirstExplorer extends AbstractExplorer {

	//	/**
	//	 * Prefix for all properties that apply to this explorer. For example, to
	//	 * control the Green instance created by this class, it reads properties of
	//	 * the form
	//	 * 
	//	 * <pre>
	//	 * PROPERTY_PREFIX + ".green..."
	//	 * </pre>
	//	 * 
	//	 * <p>
	//	 * from the configuration file. This allows the DEEPSEA system to
	//	 * distinguish different settings for different instances of Green (in this
	//	 * specific case).
	//	 * </p>
	//	 */
	//	private static final String PROPERTY_PREFIX = "deepsea.dfexplorer.";

	/**
	 * A logger for communicating with users.
	 */
	private final Logger log;

	/**
	 * An instance of Green used for generating models. Any properties specified
	 * in the configuration file overrides the defaults used in this explorer.
	 */
	private final Green solver;

	/**
	 * Signatures for those paths we have visited.
	 */
	private final Set<String> visitedSignatures = new HashSet<>();

	/**
	 * Signatures for those path that have already been found to be unreachable.
	 */
	private final Set<String> infeasibleSignatures = new HashSet<>();

	/**
	 * A count of the number of paths explored.
	 */
	private int pathCounter = 0;

	/**
	 * A count of the number of paths revisited (i.e., the number of visited
	 * signatures that are re-encountered).
	 */
	private int revisitCounter = 0;

	/**
	 * A count of the number of generated (modified) path conditions that are
	 * UNSAT.
	 */
	private int unSatCounter = 0;

	/**
	 * Constructs an instance of the depth-first explorer, given the associated
	 * diver.
	 * 
	 * @param diver
	 *            the associated diver
	 */
	public DepthFirstExplorer(Diver diver, Properties properties) {
		super(diver);
		this.log = diver.getLog();
		this.solver = new Green("DEEPSEA");
		// properties.setProperty("green.log.level", "OFF");
		properties.setProperty("green.services", "model");
		properties.setProperty("green.service.model", "(bounder (canonizer modeller))");
		properties.setProperty("green.service.model.bounder", "za.ac.sun.cs.green.service.bounder.BounderService");
		properties.setProperty("green.service.model.canonizer",
				"za.ac.sun.cs.green.service.canonizer.ModelCanonizerService");
		properties.setProperty("green.service.model.modeller", "za.ac.sun.cs.green.service.z3.ModelZ3JavaService");
		// props.setProperty("green.service.model.modeller", "za.ac.sun.cs.green.service.choco3.ModelChoco3Service");
		Configuration config = new Configuration(solver, properties);
		config.configure();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "DepthFirstExplorer";
	}

	/**
	 * Proposes new concrete values in response to a newly discovered path
	 * condition. This method attempts to work in a depth-first fashion, but
	 * surprisingly (or perhaps not?), it can be difficult to force the program
	 * to behave in certain ways. This method keeps track of all path signatures
	 * that have been seen before (visited) or attempted before (but found to be
	 * infeasible). It follows the following procedure:
	 * 
	 * <ol>
	 * 
	 * <li>
	 * <p>
	 * Add the current signature to the set of visited signatures.
	 * </p>
	 * </li>
	 * 
	 * <li>
	 * <p>
	 * If it is already in the set of visited signatures:
	 * </p>
	 * 
	 * <ol>
	 * 
	 * <li>
	 * <p>
	 * Drop the first (most recently added) conjunct of the path condition (and
	 * the first character of the path signature).
	 * </p>
	 * </li>
	 * 
	 * <li>
	 * <p>
	 * If the resulting signature is empty, terminate
	 * </p>
	 * </li>
	 * 
	 * <li>
	 * <p>
	 * If the resulting signature has been visited before, or has been found to
	 * be infeasible, repeat from Step 2.1.
	 * </p>
	 * </li>
	 * 
	 * </ol>
	 * </li>
	 * 
	 * <li>
	 * <p>
	 * Complement the first (most recently added) conjunct of the path condition
	 * (and the corresponding character of the path signature).
	 * </p>
	 * </li>
	 * 
	 * <li>
	 * <p>
	 * If the complemented path signature has been visited:
	 * </p>
	 * 
	 * <ol>
	 * 
	 * <li>
	 * <p>
	 * Drop the first conjunct.
	 * </p>
	 * </li>
	 * 
	 * <li>
	 * <p>
	 * If the truncated path condition is empty, terminate.
	 * </p>
	 * </li>
	 *
	 * <li>
	 * <p>
	 * Add the truncated path signature to the set of visited signatures and go
	 * to Step 3.
	 * </p>
	 * </li>
	 * 
	 * </ol>
	 * </li>
	 * 
	 * <li>
	 * <p>
	 * Find a model for the complemented path condition.
	 * </p>
	 * </li>
	 * 
	 * <li>
	 * <p>
	 * If there is no model for the complemented path condition (it is
	 * unsatisfiable):
	 * </p>
	 * 
	 * <ol>
	 * 
	 * <li>
	 * <p>
	 * Drop the first conjunct.
	 * </p>
	 * </li>
	 * 
	 * <li>
	 * <p>
	 * If the truncated path condition is empty, terminate.
	 * </p>
	 * </li>
	 *
	 * <li>
	 * <p>
	 * Add the truncated path signature to the set of infeasible signatures.
	 * </p>
	 * </li>
	 * 
	 * <li>
	 * <p>
	 * Go to step 3.
	 * </p>
	 * </li>
	 * 
	 * </ol>
	 * </li>
	 * 
	 * <li>
	 * <p>
	 * Return the model found.
	 * </p>
	 * </li>
	 * 
	 * </ol>
	 * 
	 * @param dive
	 *            the newly completed dive
	 * @return a mapping from variables names to values, or {@code null}
	 * @see za.ac.sun.cs.deepsea.explorer.Explorer#refine(za.ac.sun.cs.deepsea.diver.
	 *      Dive)
	 */
	@Override
	public Map<String, Constant> refine(Dive dive) {
		pathCounter++;
		String signature = dive.getSignature();
		Expression pathCondition = dive.getPathCondition();
		if (!visitedSignatures.add(signature)) {
			/*
			 * If we are revisiting this path, we truncate it by removing
			 * conjuncts one by one, until we get a path condition that we have
			 * not visited before and that we have not classified as unfeasible.
			 */
			log.finest("revisit of signature \"" + signature + "\", truncating");
			revisitCounter++;
			while ((signature.length() > 0)
					&& (visitedSignatures.contains(signature) || infeasibleSignatures.contains(signature))) {
				signature = signature.substring(1);
				assert pathCondition instanceof Operation;
				Operation pc = (Operation) pathCondition;
				assert pc.getOperator() == Operator.AND;
				pathCondition = pc.getOperand(1);
			}
		}
		log.finest("path signature: " + signature);
		log.fine("path condition: " + pathCondition);
		while (signature.length() > 0) {
			/*
			 * Flip the first char ('0' <-> '1') of signature.
			 */
			char firstSignature = (signature.charAt(0) == '0') ? '1' : '0';
			String restSignature = signature.substring(1);
			String candidateSignature = firstSignature + restSignature;
			if (visitedSignatures.contains(candidateSignature)) {
				/*
				 * We now know that both this path condition and the
				 * complementary path condition (i.e., with the first conjunct
				 * negated) have been visited. We therefore record that there
				 * common prefix has been visited, and we drop the first
				 * conjunct and try to negate the new first conjunct.
				 */
				visitedSignatures.add(restSignature);
				signature = restSignature;
				assert pathCondition instanceof Operation;
				Operation pc = (Operation) pathCondition;
				assert pc.getOperator() == Operator.AND;
				pathCondition = pc.getOperand(1);
				log.finest("dropping first conjunct -> " + pathCondition);
			} else {
				// negate first conjunct of path condition and check if it has a model
				assert pathCondition instanceof Operation;
				Operation pc = (Operation) pathCondition;
				assert pc.getOperator() == Operator.AND;
				Expression firstPC = new Operation(Operator.NOT, pc.getOperand(0));
				Expression restPC = pc.getOperand(1);
				pathCondition = new Operation(Operator.AND, firstPC, restPC);
				log.finest("trying <" + candidateSignature + "> " + pathCondition);
				Instance instance = new Instance(solver, null, pathCondition);
				@SuppressWarnings("unchecked")
				Map<IntVariable, Object> model = (Map<IntVariable, Object>) instance.request("model");
				if (model == null) {
					// again drop the first conjunct
					signature = restSignature;
					pathCondition = pc.getOperand(1);
					log.finest("no model");
					infeasibleSignatures.add(signature);
					unSatCounter++;
				} else {
					// translate model
					Map<String, Constant> newModel = new HashMap<>();
					for (IntVariable variable : model.keySet()) {
						String name = variable.getName();
						Constant value = new IntConstant((Integer) model.get(variable));
						newModel.put(name, value);
					}
					log.finest("new model: " + newModel);
					return newModel;
				}
			}
		}
		log.finest("all signatures explored");
		return null;
	}

	/**
	 * Writes a report that summarizes the execution of a component at the end
	 * of a DEEPSEA run. This version reports the number of total, revisited,
	 * and unique paths explored, and the number of path conditions that were
	 * found to be unsatisfiable.
	 * 
	 * @param out
	 *            the destination to which the report must be written
	 * @see za.ac.sun.cs.deepsea.Reporter#report(java.io.PrintWriter)
	 */
	@Override
	public void report(PrintWriter out) {
		out.println("# paths explored: " + pathCounter);
		out.println("# paths revisited: " + revisitCounter);
		out.println("# unique paths: " + pathCounter + "-" + revisitCounter + "=" + (pathCounter - revisitCounter));
		out.println("# UNSAT path conditions: " + unSatCounter);
	}

}
