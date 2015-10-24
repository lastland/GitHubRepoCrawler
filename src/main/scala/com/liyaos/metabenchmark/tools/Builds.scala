package com.liyaos.metabenchmark.tools

/**
 * Created by lastland on 15/6/22.
 */

import scala.io.Source
import java.io.File
import scala.xml.{Elem, XML}

class NoRecognizableBuildException extends Exception
class PomEmptyException extends Exception

abstract class Build {
  def dependencies: Stream[String]
}

object Build {
  val matches: Map[String, (String) => Build] = Map(
    "pom.xml" -> ((path: String) => {
      val content = Source.fromFile(path + "/pom.xml").mkString
      if (content.size > 0) {
        val pom = XML.loadString(content)
        new MavenBuild(path, pom)
      } else {
        throw new PomEmptyException
      }
    }),
    "build.sbt" -> ((path: String) => {
      new SbtBuild(path, new File(path + "/build.sbt"))
    }))
  val list = matches.keys

  def createBuild(path: String): Build = {
    val builds = new File(path).list.filter { f =>
      list.exists(f == _)
    }
    if (!builds.isEmpty) {
      matches(builds(0))(path)
    } else {
      throw new NoRecognizableBuildException
    }
  }
}

class MavenBuild(path: String, pom: Elem) extends Build {
  override def dependencies = {
    (pom \\ "dependency").flatMap { d =>
      (d \ "artifactId").map(_.text)
    }.toStream
  }
}

class SbtBuild(path: String, file: File) extends Build {
  // No need to implement this method for now
  override def dependencies: Stream[String] = ???
}