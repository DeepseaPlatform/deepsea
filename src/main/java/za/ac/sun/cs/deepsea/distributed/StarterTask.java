package za.ac.sun.cs.deepsea.distributed;

import java.lang.reflect.Method;

import org.apache.logging.log4j.Logger;
import org.jppf.node.protocol.AbstractTask;

import za.ac.sun.cs.deepsea.diver.Configuration;

public class StarterTask extends AbstractTask<Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6363996114915041520L;

	private final Configuration config;

	public StarterTask(Configuration config) {
		this.config = config;
	}

	@Override
	public void run() {
		try {
			Logger logger = config.getLogger();
			logger.trace("config==" + config);			
			logger.trace("logger==" + logger);			
			logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			logger.info("Loading");
			Class<?> cls = StarterTask.class.getClassLoader().loadClass(config.getTarget());
			logger.trace("cls==" + cls);			
			Method main = cls.getMethod("main", String[].class);
			logger.trace("main==" + main);			
			String[] args = config.getArgs().split(";;");
			//examples.spf.BinTree3.main(args);
			logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			logger.info("Invoking");
			main.invoke(null, (Object) args);
			setResult(false);
		} catch (Exception x) {
			setThrowable(x);
		}
	}

}
