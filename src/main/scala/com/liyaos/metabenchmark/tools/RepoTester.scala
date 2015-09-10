package com.liyaos.metabenchmark.tools

/**
 * Created by lastland on 15/7/23.
 */
import java.io.File
import scala.sys.process._

abstract class RepoTester(path: String) {
  def test(): Int
}

class MavenRepoTester(path: String, mvn: Option[String] = None) extends RepoTester(path) {
  override def test() = {
    val mvnProgram = mvn match {
      case None => "mvn"
      case Some(s) => s
    }
    Process(Seq(mvnProgram, "test"), new File(path)).!
  }
}
