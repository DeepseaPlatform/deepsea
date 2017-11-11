/**
 * Abstract symbolic models for complex concrete operations. For example, to
 * analyze a program that manipulates {@link java.lang.String} values, it is
 * possible to "shadow" the native code instruction-by-instruction. But the
 * native libraries -- even for simple operations -- is complex and this
 * approach is not time efficient.
 * An alternative is the idea of a "<strong><em>model</em></strong>": a simpler
 * symbolic implementation of a complex concrete operation that is executed in
 * its place. The model of an operation operates on the method-level, which
 * means that some operations of a class may be modeled, while others are not.
 * 
 * <p>
 * For complex data types, a model may construct and maintain a symbolic
 * representation that is fundamentally different from its concrete counterpart.
 * For example, a model for {@link java.util.LinkedList} may implement instances
 * with a bounded array.
 * </p>
 * 
 * <p>
 * There are not many mechanisms for forcing models to faithfully describe their
 * concrete originals; the whole point of the model is approximate complex
 * behavior and not to merely mirror it. Programmers must therefore be very
 * careful when writing models.
 * </p>
 */
package za.ac.sun.cs.deepsea.models;