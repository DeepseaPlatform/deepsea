package za.ac.sun.cs.deepsea.explorer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import za.ac.sun.cs.deepsea.diver.Dive;
import za.ac.sun.cs.deepsea.diver.Diver;
import za.ac.sun.cs.green.expr.Constant;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;

public class DepthFirstExplorer extends AbstractExplorer {

	/**
	 * A logger for communicating with users.
	 */
	private final Logger log;

//	private final Green solver;
	/**
	 * Constructs an instance of the depth-first explorer, given the associated diver.
	 * 
	 * @param diver the associated diver
	 */
	public DepthFirstExplorer(Diver diver) {
		super(diver);
		this.log = diver.getLog();
//		this.solver = new Green("DEEPSEA");
//		Properties props = new Properties();
//		props.setProperty("green.services", "model");
//		props.setProperty("green.service.model", "(bounder z3java)");
//		props.setProperty("green.service.model.bounder", "za.ac.sun.cs.green.service.bounder.BounderService");				
//		props.setProperty("green.service.model.z3java", "za.ac.sun.cs.green.service.z3.ModelZ3JavaService");
//		// props.setProperty("", "/Users/jaco/Documents/RESEARCH/01/SYMEXE/Z3/build/z3");
//		Configuration config = new Configuration(solver, props);
//		config.configure();
	}

	private int counter = 0;
	
	@Override
	public Map<String, Constant> refine(Dive dive) {
		String signature = dive.getSignature();
		log.fine("signature: " + signature);
		Expression pathCondition = dive.getPathCondition();
		log.fine("PC: " + pathCondition);
		if (counter == 0) {
			counter++;
			Map<String, Constant> model = new HashMap<>();
			model.put("X", new IntConstant(5));
			return model;
		}

		//		Instance instance = new Instance(solver, null, d.getPathCondition());
		//		@SuppressWarnings({ "unchecked", "unused" })
		//		Map<IntVariable,Object> model = (Map<IntVariable,Object>) instance.request("model"); 
		return null;
	}

	@Override
	public void report() {
		// TODO Auto-generated method stub		
	}
	
}
