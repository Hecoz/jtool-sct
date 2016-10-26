# JTool SCT (a Java tool for systematic concurrency testing)

We have made changes to the tool since our PPoPP'15 paper.
If you want to reproduce the original evaluation 
(with HBR caching, lazy HBR caching, DPOR, and lazy DPOR),
you can download the original artifact 
[here](https://sites.google.com/site/lazyhbr/).

## Getting started

* git clone this repository to a directory. E.g. `/data/java/jtool-sct`. 

 We will refer to this direcory as `$JTOOL_ROOT`.

* Install JDK 7. E.g.
```bash
$ sudo apt-get install openjdk-7-jdk openjdk-7-source
```
Note: the tool probably won't work with JDK 8. On Windows, you can download and install the latest version of Oracle's JDK 7.

* Download Maven and extract it somewhere. E.g. `/data/java/maven`.

* In `$JTOOL_ROOT`, copy `template_env.sh` (or `template_env.cmd` if on Windows) to `env.sh` (or `env.cmd` if on Windows). Edit the environment variables at the top of the file to match your environment. 

* From a terminal, source `env.sh`:
```bash
$ source env.sh
```
 On Windows, you can "execute" it from a command prompt:
```c
> env.cmd
```
* Java 7 and Maven should now be on your path. E.g.:

```bash
$ java -version
java version "1.7.0_85"
OpenJDK Runtime Environment (IcedTea 2.6.1) (7u85-2.6.1-5ubuntu0.15.04.1)
OpenJDK 64-Bit Server VM (build 24.85-b03, mixed mode)

$ mvn help:evaluate -Dexpression=settings.localRepository
[INFO] Scanning for projects...
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building Maven Stub Project (No POM) 1
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] --- maven-help-plugin:2.2:evaluate (default-cli) @ standalone-pom ---
[INFO] No artifact parameter specified, using 'org.apache.maven:standalone-pom:pom:1' as project.
[INFO] 
/home/paul/.m2/repository
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 0.732 s
[INFO] Finished at: 2015-11-14T12:45:26+00:00
[INFO] Final Memory: 8M/239M
[INFO] ------------------------------------------------------------------------

```

* You should now be able to execute the following commands one after the other (the output has been omitted):

```
$ build_all
$ instr_rt
$ install_rt
```

`build_all` builds jtool and all benchmarks. `instr_rt` instruments the current JDK 7 runtime jar and places it in the `$JTOOL_ROOT/temp` directory. `install_rt` places the instrumented jar in the local maven repository so that it can be found easily. Look at the env file if you want to see what these commands actually do.

* You can now execute a test harness, replacing `java` with the `jtool` command:
```
$ jtool example.SimpleHarness
```

* TODO: Include a simple example harness.

* The included test harness that executes all the benchmarks is:
 
 `$JTOOL_ROOT/projects/runbenchmarks/src/main/java/runbenchmarks/MainHarness.java`

To execute this, you should load the Maven projects in `$JTOOL_ROOT/projects` into an IDE, such as IntelliJ, and run `MainHarness.java`. This approach ensures all the necessary jar files are on the classpath. You should add the following JVM arguments to the run configuration:

`-Xbootclasspath/p:$M2_REPO/org/jtool/local/rt/1/rt-1.jar -javaagent:$M2_REPO/org/jtool/jtool-runtime/0.1/jtool-runtime-0.1.jar`

You will probably need to manually replace $M2_REPO with the actual location of your Maven repository. You may also need to be careful that your IDE runs the harness using JDK 7 and that your Maven installation is used (instead of the IDE's included Maven installation). 

