package za.ac.sun.cs.deepsea.explorer;

import org.apache.logging.log4j.Logger;

import za.ac.sun.cs.deepsea.diver.Configuration;

/**
 * Provides a concrete constructor for user-specified instances of
 * {@link Explorer} to conform to.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public abstract class AbstractExplorer implements Explorer {

	/**
	 * The {@link Logger} instance associated with this explorer.
	 */
	protected final Logger logger;

	/**
	 * Constructs an {@link Explorer} instance with the given diver. It stores
	 * the identify of the diver in an instance field..
	 * 
	 * @param logger the log destination
	 * @param config configuration settings 
	 */
	public AbstractExplorer(Logger logger, Configuration config) {
		this.logger = logger;
	}

}
