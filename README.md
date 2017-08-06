# DEEPSEA

Deep Symbolic Execution Agent (DEEPSEA) is a program analysis tool.  It uses:

  * Java Platform Debugger Architecture ([JPDA](https://docs.oracle.com/javase/8/docs/technotes/guides/jpda/index.html))
  * Java Debug Interface (JDI, part of JPDA)
  * Apache Commons Byte Code Engineering Library ([BCEL](https://commons.apache.org/proper/commons-bcel/index.html))
  
With these tools it monitors the execution of a Java program.  As the program executes concretely, DEEPSEA collects information to describe the execution trace symbolically.  At the end of the execution, the path is modified and the [Green](http://www.green.green) library is used to find a model for the modified path condition.

## Installation

To install DEEPSEA, simply import the project in Eclipse.

You will have to make sure that the build path is set correctly.  It must include:

  * `/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/lib/tools.jar`

You may have to *tweak* the location of the library.

Create a run configuration for DEEPSEA:
 
  * In the "Arguments" tab, set the "Working directory" (at the bottom of the panel) to "Other" and fill in
     
        ${workspace_loc:deepsea/bin}

## Usage

To use DEEPSEA:

 1. Highlight a target program properties file (either in an editor window or in the Eclipse navigator).
 2. Launch the DEEPSEA run configuration.

## Properties file

The properties file may contain the following settings:

### `deepsea.log.level`

(String) Set to one of the standard Java logging levels or a number.

### `deepsea.log.dump`

(Boolean) Whether to display the configuration properties in the log.

### `deepsea.target`

(String) Qualified name of the main class to load.  (Must contain a Java `main` routine.)

### `deepsea.args`

(String) Arguments passed to target.  The same arguments will be passed on every invocation.

### `deepsea.triggers`

(String) TBD

### `deepsea.produceoutput`

(Boolean) Whether the output of target program should be displayed or not.

## To do

   * Something
   * Something

## Authors

   * [Jaco Geldenhuys](mailto://geld@sun.ac.za)
   * [Willem Visser](mailto://wvisser@sun.ac.za)
