#-------------------------------------------------------------------------------
# START WITH UBUNTU
#-------------------------------------------------------------------------------
FROM ubuntu:latest
LABEL maintainer="Jaco Geldenhuys (geld@sun.ac.za)"

#-------------------------------------------------------------------------------
# UPDATE PACKAGE LIBARY & INSTALL VI
#-------------------------------------------------------------------------------

RUN apt-get update \
	&& apt-get upgrade -y \
	&& apt-get install -y vim

#-------------------------------------------------------------------------------
# INSTALL JDK7/8
#-------------------------------------------------------------------------------

RUN apt-get install -y software-properties-common \
	&& add-apt-repository ppa:webupd8team/java -y \
	&& apt-get update \
	&& echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections \
	&& apt-get install -y oracle-java8-installer 

#-------------------------------------------------------------------------------
# INSTALL TOOLS TO COMPILE Z3
#-------------------------------------------------------------------------------

RUN apt-get install -y curl g++ git make openssh-server python

#-------------------------------------------------------------------------------
# DOWNLOAD AND COMPILE Z3
#-------------------------------------------------------------------------------

RUN git clone https://github.com/Z3Prover/z3.git \
	&& cd z3 \
	&& python scripts/mk_make.py --java \
	&& cd build \
	&& make

#-------------------------------------------------------------------------------
# DOWNLOAD AND COMPILE DEEPSEA
#-------------------------------------------------------------------------------

ARG BUILD_DATE
LABEL build_date="$BUILD_DATE"

RUN git clone https://git.cs.sun.ac.za/deepsea/deepsea.git
	&& cd deepsea \
	&& ./gradlew installDist

RUN ( F=/deepsea/build/install/deepsea/bin/deepsea ; cat $F \
	| sed -e 's,^CLASSPATH=,CLASSPATH=/z3/build/com.microsoft.z3.jar:,' \
	| > /tmp/t ; mv -f /tmp/t $F ) \
	&& ln -s /deepsea/build/install/deepsea/bin/deepsea /usr/bin/deepsea \
	&& chmod +x /deepsea/build/install/deepsea/bin/deepsea /usr/bin/deepsea

#-------------------------------------------------------------------------------
# SET A COMMAND-LINE VARIABLE
#-------------------------------------------------------------------------------

ENV LD_LIBRARY_PATH "/z3/build/"
ENV DEEPSEA_OPTS $LD_LIBRARY_PATH

CMD [ "/bin/bash" ]

