/**
 * Customized support for DEEPSEA logging, by extending the Java platform's core
 * functionality.
 * 
 * It consists of two features:
 * 
 * <ol>
 * 
 * <li>
 * <p>
 * A {@link za.ac.sun.cs.deepsea.logging.LogHandler} that writes all message to
 * standard output, and
 * </p>
 * </li>
 * 
 * <li>
 * <p>
 * A {@link za.ac.sun.cs.deepsea.logging.LogFormatter} and
 * {@linkplain za.ac.sun.cs.deepsea.logging.VerboseFormatter} that formats
 * messages, the former tersely and the latter verbosely, as its name implies.
 * </p>
 * </li>
 * 
 * </ol>
 * 
 */
package za.ac.sun.cs.deepsea.logging;