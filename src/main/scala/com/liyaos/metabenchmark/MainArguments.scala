package com.liyaos.metabenchmark

/**
 * Created by salucl on 23/10/15.
 */
object MainArguments {

  val outputFolder: String = "./actors/";
  val classToFilter: Array[String] =  Array("akka.actor.Actor", "akka.actor.UntypedActor",
    "scala.actors.Actor", "org.jetlang.fibers.Fiber", "fj.control.parallel.Actor",
    "groovyx.gpars.actor.Actor", "edu.rice.hj.api.HjActor", "fi.jumi.actors.Actors",
    "net.liftweb.actor.LiftActor","scalaz.consurrent.Actor");
//  val classToFilter: Array[String] =  Array("java.util.concurrent.Executor",
//    "java.util.concurrent.ExecutorService", "java.util.concurrent.AbstractExecutorService",
//    "java.util.concurrent.ThreadPoolExecutor", "java.util.concurrent.ForkJoinPool");

  val mode = FilterMode.Body

  val regexPattern = "*[][]*"
}
