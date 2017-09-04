/**
 * Symbolic implementations for JVM bytecode instructions. Many but not all
 * instructions have an effect on the symbolic state. For many seemingly
 * different instructions, the symbolic equivalents are identical.
 * 
 * The parent class for all of the instructions is
 * {@link za.ac.sun.cs.deepsea.instructions.Instruction}, and it is probably the
 * starting point when trying to understand how the classes in this package
 * work.
 * 
 */
package za.ac.sun.cs.deepsea.instructions;
