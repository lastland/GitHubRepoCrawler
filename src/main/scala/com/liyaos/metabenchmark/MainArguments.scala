package com.liyaos.metabenchmark

/**
 * Created by salucl on 23/10/15.
 */
object MainArguments {

  val outputFolder: String = "./threadpool/";
  val classToFilter: Array[String] =  Array("java.util.concurrent.Executor",
    "java.util.concurrent.ExecutorService", "java.util.concurrent.AbstractExecutorService",
    "java.util.concurrent.ThreadPoolExecutor", "java.util.concurrent.ForkJoinPool");

}
