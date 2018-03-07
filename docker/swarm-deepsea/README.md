# DEEPSEA on Docker

This directory contains files needed to build and run docker images for DEEPSEA.  There are a few important points to note:

* The files are intended for a UNIX environment.  They were written and tested in Darwin 16.7.0 on OSX 10.12.6.  They may require "adjustment" before they run on Windows.


* The ``build.sh`` script builds the Docker image.  It is constructed to avoid the expensive process of compiling Z3, but it downloads and compiles the latest version of DEEPSEA.  However, the first time the image is built will still take ~40 minutes.  The following commands assume that ``DEEPSEA`` is the absolute directory where DEEPSEA was checked out from git.

      $ cd DEEPSEA/docker
      $ ./build.sh
      [ Docker image is built ]


* The ``deepsea`` shell script will invoke DEEPSEA using the Docker image.

      $ cd SOMEDIR
      $ javac -g MyProgram.java
      $ [ edit MyProgram.properties ]
      $ DEEPSEA/docker/deepsea MyProgram.properties
      [ DEEPSEA runs ]


* A pre-built image is available on the DEEPSEA repository:

      git.cs.sun.ac.za:4567/deepsea/deepsea:latest

