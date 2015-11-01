package com.liyaos.metabenchmark.tools

/**
 * Created by lastland on 15/10/19.
 */

import java.nio.file.{Paths, Path}
import java.io.File


import scala.io.Source
import fastparse.all._

case class NoViableDetectorException(file: File) extends Exception

abstract class BodyPatternDetector(path: Path) extends BodyDetector

object BodyPatternDetector {
  def getDetector(file: File): BodyDetector = {
    if (file.isDirectory)
      BodyRepoDirPatternDetector(Paths.get(file.getAbsolutePath))
    else if ((file.getName.toLowerCase.endsWith(".java")) || (file.getName.toLowerCase.endsWith(".scala")))
      BodyRepoPatternDetector(Paths.get(file.getAbsolutePath))
    else throw NoViableDetectorException(file)
  }
}

case class BodyRepoPatternDetector(path: Path) extends BodyPatternDetector(path) {
  override def declarations: Set[String] = {
    val file = new File(path.toString)
    BodyPatternDetector.getDetector(file).declarations
  }
}

case class BodyRepoDirPatternDetector(path: Path) extends BodyPatternDetector(path) {
  override def declarations: Set[String] = {
    val file = new File(path.toString)
    file.listFiles().flatMap { f =>
      try {
        BodyPatternDetector.getDetector(f).declarations
      } catch {
        case NoViableDetectorException(eFile) =>
          Set[String]()
      }
    }.toSet
  }
}

case class PatternDetector(path: Path, regex: String) extends BodyDetector {
  override def declarations: Set[String] = {
    Source.fromFile(path.toFile).getLines().filter(_.trim.matches(regex)).map { line =>
      line.trim
    }.toSet
  }
}

trait ScalaBasics {
  val word: P[Unit]= P(CharIn('a' to 'z' ) ~ CharIn(('a' to 'z') ++ ('0' to '9')).rep(0))
}