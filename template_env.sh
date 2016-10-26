
export JTOOL_ROOT=/data/java/jtool-sct
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64

# You can find the repo location (assuming mvn is on your path) using: mvn help:evaluate -Dexpression=settings.localRepository
export M2_REPO=~/.m2/repository

export PATH=$JAVA_HOME/bin:/data/java/apache-maven-3.3.3/bin:$PATH


build_all() {
	cd $JTOOL_ROOT/projects/runparent
	mvn clean install
	echo NOTE: changed directory
}

instr_rt() {
	cd $JTOOL_ROOT/temp
	java -cp $M2_REPO/org/jtool/jtool-runtime/0.1/jtool-runtime-0.1.jar org.jtool.jdkinstr.JDKInstrumenter
	echo NOTE: changed directory
}

install_rt() {
	cd $JTOOL_ROOT/temp
	mvn install:install-file -Dfile=rt_instr.jar -DgroupId=org.jtool.local -DartifactId=rt -Dversion=1 -Dpackaging=jar
	echo NOTE: changed directory
}

alias jtool="java -Xbootclasspath/p:$M2_REPO/org/jtool/local/rt/1/rt-1.jar -javaagent:$M2_REPO/org/jtool/jtool-runtime/0.1/jtool-runtime-0.1.jar"



