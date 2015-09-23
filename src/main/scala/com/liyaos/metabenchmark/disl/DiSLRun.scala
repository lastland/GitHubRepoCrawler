package com.liyaos.metabenchmark.disl

/**
 * Created by lastland on 15/9/10.
 */

import java.io.FileWriter

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.sys.process._

class FailedStartDiSLServerException extends Exception

class DiSLRun {
  var serverStarted = false
  var failed: Option[Throwable] = None
  val cmd = s"${DiSLConfig.dislProgram} -d ${DiSLConfig.dislHome} -dc ${DiSLConfig.instProgram} -cs ${DiSLConfig.instDir}"

  def setup() {
    val f = Future {
      serverStarted = true
      // the command will block instead of return statusCode in normal conditions
      val statusCode = cmd.!
      if (statusCode != 0) throw new FailedStartDiSLServerException
    }
    f.onFailure { case e =>
      serverStarted = false
      failed = Some(e)
    }
  }

  def run(f: => Unit) {
    while (!serverStarted) {
      failed match {
        case Some(e) =>
          throw e
        case None =>
          Thread.sleep(500)
      }
    }
    f
  }
}
