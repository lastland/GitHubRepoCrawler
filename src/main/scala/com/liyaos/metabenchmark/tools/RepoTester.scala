package com.liyaos.metabenchmark.tools

/**
 * Created by lastland on 15/7/23.
 */
import java.io.File
import scala.sys.process._

abstract class RepoTester(path: String) {
  var process: Option[Process] = None
  def test(): Int
  def terminate() {
    process match {
      case Some(p) => p.destroy()
      case None => ()
    }
  }
}

class MavenRepoTester(path: String, mvn: Option[String] = None) extends RepoTester(path) {
  override def test() = {
    val mvnProgram = mvn match {
      case None => "mvn"
      case Some(s) => s
    }
    val p = Process(Seq(mvnProgram, "test"), new File(path)).run()
    process = Some(p)
    p.exitValue()
  }
}

class SbtRepoTester(path: String, sbt: Option[String] = None) extends RepoTester(path) {
  override def test() = {
    val sbtProgram = sbt match {
      case None => "sbt"
      case Some(s) => s
    }
    val p = Process(Seq(sbtProgram, "test"), new File(path)).run()
    process = Some(p)
    p.exitValue()
  }
}