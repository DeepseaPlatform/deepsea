package za.ac.sun.cs.deepsea.explorer;

import za.ac.sun.cs.deepsea.diver.Diver;

/**
 * This class provides a concrete constructor for user-specified instances of
 * {@link Explorer} to conform to.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public abstract class AbstractExplorer implements Explorer {

	/**
	 * The {@link Diver} instance associated with this explorer.
	 */
	protected final Diver diver;

	/**
	 * Constructs an {@link Explorer} instance with the given diver.
	 * 
	 * @param diver
	 *            the diver associated with this explorer
	 */
	public AbstractExplorer(Diver diver) {
		this.diver = diver;
		diver.addReporter(this);
	}

}
