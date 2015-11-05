package com.liyaos.metabenchmark

import java.util.regex.Pattern

/**
 * Created by salucl on 23/10/15.
 */
object MainArguments {

  val outputFolder: String = "./spark/";
  val classToFilter: Array[String] =  Array("org.apache.spark", "from pyspark")
//  val classToFilter: Array[String] =  Array("akka.actor.Actor", "akka.actor.UntypedActor",
//    "scala.actors.Actor", "org.jetlang.fibers.Fiber", "fj.control.parallel.Actor",
//    "groovyx.gpars.actor.Actor", "edu.rice.hj.api.HjActor", "fi.jumi.actors.Actors",
//    "net.liftweb.actor.LiftActor","scalaz.consurrent.Actor");
//  val classToFilter: Array[String] =  Array("java.util.concurrent.Executor",
//    "java.util.concurrent.ExecutorService", "java.util.concurrent.AbstractExecutorService",
//    "java.util.concurrent.ThreadPoolExecutor", "java.util.concurrent.ForkJoinPool");


  val matrixSizeTreshold = 1;
  val mode = FilterMode.Imports

  val star=".*?";	// Non-greedy match on filler
  val numType="(BigDecimal|BigInteger|Double|Float|Integer|Long|Short|double|float|int|long|short)";
  val squareBrackets="(\\[.*?\\])";	// Square Braces 1
  val assign="(=)";	// Any Single Character 1
  val newKey="(new)";	// Word 1
  val regex = star + numType + star + squareBrackets + squareBrackets +
      star + assign + star + newKey + star + numType + star;
}
