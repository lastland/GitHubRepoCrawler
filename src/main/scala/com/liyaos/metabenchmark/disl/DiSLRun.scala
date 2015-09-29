package com.liyaos.metabenchmark.disl

/**
 * Created by lastland on 15/9/10.
 */

import java.io.FileWriter
import java.util.concurrent.TimeoutException
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.sys.process._

class FailedStartDiSLServerException extends Exception

class DiSLRun {
  var serverStarted = false
  var failed: Option[Throwable] = None
  val cmd = s"${DiSLConfig.dislProgram} -d ${DiSLConfig.dislHome} -dc ${DiSLConfig.instProgram} -cs ${DiSLConfig.instDir}"

  def setup() = {
    val f = Future {
      failed = None
      serverStarted = true
      // the command will block instead of return statusCode in normal conditions
      val statusCode = cmd.!
      if (statusCode != 0) throw new FailedStartDiSLServerException
      statusCode
    }
    f.onFailure { case e =>
      serverStarted = false
      failed = Some(e)
    }
    f
  }

  def run(f: => Unit) {
    synchronized {
      val fu = setup()
      try {
        Await.ready(fu, 1 second)
      } finally {
        while (!serverStarted) {
          failed match {
            case Some(e) =>
              val fu = setup()
              try {
                Await.ready(fu, 1 second)
              } catch {
                case e: TimeoutException => ()
              }
            case None =>
              Thread.sleep(500)
          }
        }
        f
      }
    }
  }
}
