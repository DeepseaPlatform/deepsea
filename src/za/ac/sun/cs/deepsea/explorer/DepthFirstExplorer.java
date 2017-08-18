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

public class DepthFirstExplorer extends AbstractExplorer {

	/**
	 * A logger for communicating with users.
	 */
	private final Logger log;

	private final Green solver;

	private final Set<String> visitedSignatures = new HashSet<>();

	private final Set<String> infeasibleSignatures = new HashSet<>();
	
	private int pathCounter = 0;

	private int revisitCounter = 0;
	
	private int unSatCounter = 0;
	
	/**
	 * Constructs an instance of the depth-first explorer, given the associated diver.
	 * 
	 * @param diver the associated diver
	 */
	public DepthFirstExplorer(Diver diver, Properties properties) {
		super(diver);
		this.log = diver.getLog();
		this.solver = new Green("DEEPSEA");
		// properties.setProperty("green.log.level", "OFF");
		properties.setProperty("green.services", "model");
		properties.setProperty("green.service.model", "(bounder (canonizer modeller))");
		properties.setProperty("green.service.model.bounder", "za.ac.sun.cs.green.service.bounder.BounderService");				
		properties.setProperty("green.service.model.canonizer", "za.ac.sun.cs.green.service.canonizer.ModelCanonizerService");				
		properties.setProperty("green.service.model.modeller", "za.ac.sun.cs.green.service.z3.ModelZ3JavaService");
		// props.setProperty("green.service.model.modeller", "za.ac.sun.cs.green.service.choco3.ModelChoco3Service");
		Configuration config = new Configuration(solver, properties);
		config.configure();
	}

	@Override
	public String getName() {
		return "DepthFirstExplorer";
	}

	@Override
	public Map<String, Constant> refine(Dive dive) {
		pathCounter++;
		String signature = dive.getSignature();
		Expression pathCondition = dive.getPathCondition();
		if (!visitedSignatures.add(signature)) {
			log.severe("revisit of signature \"" + signature + "\"");
			revisitCounter++;
//			return null;
			
			log.severe("truncating...");
			while ((signature.length() > 0) && (visitedSignatures.contains(signature) || infeasibleSignatures.contains(signature))) {
				signature = signature.substring(1);
				assert pathCondition instanceof Operation;
				Operation pc = (Operation) pathCondition;
				assert pc.getOperator() == Operator.AND;
				pathCondition = pc.getOperand(1);
			}
			
			/* TODO the following replace for the previous line didn't work:
			log.severe("truncating...");
			signature = signature.substring(1);
			assert pathCondition instanceof Operation;
			Operation pc = (Operation) pathCondition;
			assert pc.getOperator() == Operator.AND;
			pathCondition = pc.getOperand(1);
			*/
		} else {
			log.finest("path signature: " + signature);
			log.fine("path condition: " + pathCondition);
		}
		while (signature.length() > 0) {
			// flip the first char ('0' <-> '1') of signature
			char firstSignature = (signature.charAt(0) == '0') ? '1' : '0';
			String restSignature = signature.substring(1);
			String candidateSignature = firstSignature + restSignature;
			if (visitedSignatures.contains(candidateSignature)) {
				visitedSignatures.add(restSignature); // We know that both children are visited
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

	@Override
	public void report(PrintWriter out) {
		out.println("# paths explored: " + pathCounter);
		out.println("# paths revisited: " + revisitCounter);
		out.println("# unique paths: " + pathCounter + "-" + revisitCounter + "=" + (pathCounter - revisitCounter));
		out.println("# UNSAT path conditions: " + unSatCounter);
	}
	
}
