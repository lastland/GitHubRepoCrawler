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
  def createBuild(path: String): Build = {
    val builds = new File(path).list.filter(_ == "pom.xml")
    if (!builds.isEmpty) {
      val p = builds(0)
      p match {
        case "pom.xml" =>
          val content = Source.fromFile(path + "/pom.xml").mkString
          if (content.size > 0) {
            val pom = XML.loadString(content)
            new MavenBuild(path, pom)
          } else {
            throw new PomEmptyException
          }
        case _ =>
          throw new NoRecognizableBuildException
      }
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