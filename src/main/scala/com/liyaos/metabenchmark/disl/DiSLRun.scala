package com.liyaos.metabenchmark.disl

/**
 * Created by lastland on 15/9/10.
 */

import java.io.FileWriter

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.sys.process._

class DiSLRun {
  var serverStarted = false
  val cmd = s"${DiSLConfig.dislProgram} -d ${DiSLConfig.dislHome} -dc ${DiSLConfig.instProgram} -cs ${DiSLConfig.instDir}"
  Future {
    cmd.!
  } map { statusCode =>
    if (statusCode == 0)
      serverStarted = true
  }

  def run(f: => Unit) {
    while (!serverStarted) Thread.sleep(500)
    f
  }
}
