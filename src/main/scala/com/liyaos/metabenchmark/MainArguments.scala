package com.liyaos.metabenchmark

import java.util.regex.Pattern

/**
 * Created by salucl on 23/10/15.
 */
object MainArguments {

  val outputFolder: String = "./spark/";
//  val classToFilter: Array[String] =  Array("org.apache.spark", )
  val classToFilter: Array[String] =  Array("akka.actor.Actor", "akka.actor.UntypedActor",
    "scala.actors.Actor", "org.jetlang.fibers.Fiber", "fj.control.parallel.Actor",
    "groovyx.gpars.actor.Actor", "edu.rice.hj.api.HjActor", "fi.jumi.actors.Actors",
    "net.liftweb.actor.LiftActor","scalaz.consurrent.Actor");
//  val classToFilter: Array[String] =  Array("java.util.concurrent.Executor",
//    "java.util.concurrent.ExecutorService", "java.util.concurrent.AbstractExecutorService",
//    "java.util.concurrent.ThreadPoolExecutor", "java.util.concurrent.ForkJoinPool");

  val mode = FilterMode.Body
  val star=".*?";	// Non-greedy match on filler
  val re2="(\\[.*?\\])";	// Square Braces 1
  val re3="(\\[.*?\\])";	// Square Braces 2
  val re5="(=)";	// Any Single Character 1
  val re7="(new)";	// Word 1
  val regex = star + re2 + re3 + star + re5 + star + re7 + star;
}
