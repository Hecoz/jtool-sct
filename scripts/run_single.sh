#! /bin/bash

source /work/pt1110/jess/jess/scripts/clusterenv.sh

MWD=$(pwd)

cd $ROOT/projects/runbenchmarks

mvn exec:exec -Dexec.workingdir="$MWD" -Dexec.executable="java" -Dexec.args="-Xms5g -Xmx5g -Xbootclasspath/p:$M2_REPO/org/jtool/local/rt/1/rt-1.jar -javaagent:$M2_REPO/org/jtool/jtool-runtime/0.1/jtool-runtime-0.1.jar -cp %classpath runbenchmarks.Main $*"

#java -Xbootclasspath/p:$M2_REPO/org/jtool/local/rt/1/rt-1.jar -javaagent:$M2_REPO/org/jtool/jtool-runtime/0.1/jtool-runtime-0.1.jar -cp $CP runbenchmarks.Main $*
