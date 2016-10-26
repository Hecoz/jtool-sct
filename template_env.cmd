@echo off

set JTOOL_ROOT=C:\data\java\jtool-sct

set JAVA_HOME=C:\Program Files\Java\jdk1.7.0_79

REM You can find the repo location (assuming mvn is on your path) using: mvn help:evaluate -Dexpression=settings.localRepository
set M2_REPO=C:\Users\Paul\.m2\repository

set PATH=C:\Program Files\Java\jdk1.7.0_79\bin;C:\data\java\apache-maven-3.3.3\bin;%PATH%


doskey build_all=cd %JTOOL_ROOT%\projects\runparent ^&^& mvn clean install ^&^& echo NOTE: changed directory
doskey instr_rt=cd %JTOOL_ROOT%\temp ^&^& java -cp %M2_REPO%\org\jtool\jtool-runtime\0.1\jtool-runtime-0.1.jar org.jtool.jdkinstr.JDKInstrumenter ^&^& echo NOTE: changed directory
doskey install_rt=cd %JTOOL_ROOT%\temp ^&^& mvn install:install-file -Dfile=rt_instr.jar -DgroupId=org.jtool.local -DartifactId=rt -Dversion=1 -Dpackaging=jar ^&^& echo NOTE: changed directory


doskey jtool=java -Xbootclasspath/p:%M2_REPO%\org\jtool\local\rt\1\rt-1.jar -javaagent:%M2_REPO%\org\jtool\jtool-runtime\0.1\jtool-runtime-0.1.jar 


