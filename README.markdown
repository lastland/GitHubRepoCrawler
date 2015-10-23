# GitHubRepo Miner

## Prerequisite

[DiSL](http://disl.projects.ow2.org/), Java (JDK 8 is preferred), Mvn, [sbt](http://scala-sbt.org/)

## How to Use

### Setting up

Edit `src/main/resources/application.conf`, set `disl.home` to your DiSL home (if you build DiSL from source, it should be the `output` directory in your DiSL project root), `disl.java_home` to your `JAVA_HOME`.

After you have set up this, run `sbt` from the project root. In the sbt interactive shell, enter `compile` to compile the program, then enter `run init`: this will set up the database, install a fake `java` and a fake `mvn`.

### Crawling GitHub Repositories

In your sbt interactive shell, enter `run run` to crawl all the GitHub repos written in Java/Scala. These repos will be stored in your database.

You don't need to run this if you already have such a database.

### Filtering and Instrumenting

In your sbt interactive shell, enter `run filter` to start filtering repos and running tests on the repos that have been selected.

## Hacking Guide

### Project Structure

All the Java files can be found in `src/main/java`.

All the Scala files can be found in `src/main/scala`.

The configuration files can be found in `src/main/resources`.

There is also a modified version of `disl.py` in `src/main/python`.

### Filter

Right now, the filter only checks the all the imports from all Java files. The default is `java.util.concurrent.ThreadPoolExecutor`. If you just want to change that, simply change the argument passed to `GitHubRepoTestRunner.run()` in `Main.scala`.

#### Filter on other Conditions

You have to write your own filtering logic if you want to filter these files on other conditions. You can write the logics either in Scala or in Java and import them in Scala.

#### Use other Static Analysis Tool

You can use other static analysis tools as the filter, either by importing them (if they are Java/Scala libraries), or calling them using subprocess.

### Instrumentation

The code concerning instrumentation using DiSL is written in Java. You can find the code in `src/main/java/com/liyaos/metabenchmark/instrumentation`.

### Inspecting Specific Repo

All the repos has been tested will be stored in the `tmp` directory. You can run tests on a single repo by running `mvn test` using the fake `mvn` installed in your project root. You have to start DiSL server before that (by running the `disl.py` from `src/main/python`). The command should be something like this:

    your/project/root/src/main/python/disl.py -d your/disl/home -dc your/project/root/target/scala-2.11/classes/com/liyaos/metabenchmark/instrumentation/Instrumentation.class -cs your/project/root/target/scala-2.11/classes -s_instrumented dump

### Tools

I use IntelliJ with the Scala plugin to inspect and edit both Java and Scala code (However, you must use `sbt` in your shell to run the program because IntelliJ will kill the program early).
