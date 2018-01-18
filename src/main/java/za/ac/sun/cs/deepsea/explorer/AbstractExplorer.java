package za.ac.sun.cs.deepsea.explorer;

import java.io.Serializable;

import org.apache.logging.log4j.Logger;

import za.ac.sun.cs.deepsea.diver.Configuration;

/**
 * Provides a concrete constructor for user-specified instances of
 * {@link Explorer} to conform to.
 */
public abstract class AbstractExplorer implements Explorer, Serializable {

	/**
	 * Generated serial number. 
	 */
	private static final long serialVersionUID = 3728602658179766462L;

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
