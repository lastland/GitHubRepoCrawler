package com.liyaos.metabenchmark

import java.util.regex.Pattern

import com.liyaos.metabenchmark.FilterMode._

/**
 * Created by salucl on 23/10/15.
 */
object MainArguments {

  var outputFolder: String = ""
  var classToFilter: Array[String] = Array()
  var filterMode: FilterMode = FilterMode.None
  var regex: String = ""
  var mode: FilterMode = FilterMode.None

  var matrixSizeTreshold = 0
  val star=".*?";	// Non-greedy match on filler

  val selectedUseCase = UseCases.ThreadPool;

  selectedUseCase match {
    case UseCases.ThreadPool => {
      outputFolder = "./threadpool/";
      classToFilter =  Array("java.util.concurrent.Executor",
        "java.util.concurrent.ExecutorService", "java.util.concurrent.AbstractExecutorService",
        "java.util.concurrent.ThreadPoolExecutor", "java.util.concurrent.ForkJoinPool");
      mode = FilterMode.Imports
    };

    case UseCases.Actors => {
      outputFolder = "./actors/";
      classToFilter =  Array("akka.actor.Actor", "akka.actor.UntypedActor",
        "scala.actors.Actor", "org.jetlang.fibers.Fiber", "fj.control.parallel.Actor",
        "groovyx.gpars.actor.Actor", "edu.rice.hj.api.HjActor", "fi.jumi.actors.Actors",
        "net.liftweb.actor.LiftActor","scalaz.consurrent.Actor");
      mode = FilterMode.Imports
    };

    case UseCases.Matrix => {
      outputFolder = "./matrix/";
      mode = FilterMode.Body

      val numType="(BigDecimal|BigInteger|Double|Float|Integer|Long|Short|double|float|int|long|short)";
      val squareBrackets="(\\[.*?\\])";	// Square Braces 1
      val assign="(=)";	// Any Single Character 1
      val newKey="(new)";	// Word 1

      matrixSizeTreshold = 1;
      regex = star + numType + star + squareBrackets + squareBrackets +
        star + assign + star + newKey + star + numType + star;
    };

    case UseCases.InvokeDynamic => {
      outputFolder = "./invokedynamic/";
      mode = FilterMode.Body

      regex = star + "(->|::)" + star
    };

    case UseCases.Spark => {
      outputFolder = "./spark/";
      classToFilter =  Array("org.apache.spark", "from pyspark")
      mode = FilterMode.Imports
    };

    case default => {
      println("ERROR: selected use case not recognized")
      System.exit(-1)
    }
  }

}
