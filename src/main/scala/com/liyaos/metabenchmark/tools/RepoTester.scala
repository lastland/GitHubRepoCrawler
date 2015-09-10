package com.liyaos.metabenchmark.tools

/**
 * Created by lastland on 15/7/23.
 */
import java.io.File
import scala.sys.process._

abstract class RepoTester(path: String) {
  def test(): Int
}

class MavenRepoTester(path: String) extends RepoTester(path) {
  override def test() = {
    Process(Seq("mvn", "test"), new File(path)).!
  }
}
