package com.liyaos.metabenchmark.tools

/**
 * Created by lastland on 15/6/22.
 */

import scala.io.Source
import scala.xml.{Elem, XML}
import better.files._
import better.files.Cmds.ls

class NoRecognizableBuildException extends Exception
class PomEmptyException extends Exception

abstract class Build {
  def dependencies: Stream[String]
}

object Build {
  def createBuild(path: String): Build = {
    val files = ls(File(path)).toList
    val builds = files.filter(_.name == "pom.xml").toList
    if (!builds.isEmpty) {
      val f = builds(0)
      f.name match {
        case "pom.xml" =>
          val content = f.lines.mkString("\n").trim
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