package com.liyaos.metabenchmark.tools

/**
 * Created by lastland on 15/10/19.
 */

import java.nio.file.{Paths, Path}
import java.io.File
import java.util.regex.Pattern;

import scala.io.Source
import fastparse.all._

case class NoDetectorException(file: File) extends Exception

abstract class BodyPatternDetector(path: Path, regex: String) extends BodyDetector

object BodyPatternDetector {
  def getDetector(file: File, regex: String): BodyDetector = {
    if (file.isDirectory)
      BodyRepoDirPatternDetector(Paths.get(file.getAbsolutePath), regex)
    else if ((file.getName.toLowerCase.endsWith(".java")) || (file.getName.toLowerCase.endsWith(".scala")))
      BodyRepoFilePatternDetector(Paths.get(file.getAbsolutePath), regex)
    else throw NoDetectorException(file)
  }
}

case class BodyRepoPatternDetector(path: Path, regex: String) extends BodyPatternDetector(path, regex) {
  override def lines: Set[String] = {
    val file = new File(path.toString)
    BodyPatternDetector.getDetector(file, regex).lines
  }
}

case class BodyRepoFilePatternDetector(path: Path, regex: String) extends BodyPatternDetector(path, regex) {
  override def lines: Set[String] = {
    val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
    Source.fromFile(path.toFile).getLines().filter(line => (!line.trim.startsWith("//")) && (!line.trim.startsWith("*")) && (pattern.matcher(line.trim).matches()) ).toSet
  }
}

case class BodyRepoDirPatternDetector(path: Path, regex: String) extends BodyPatternDetector(path, regex) {
  override def lines: Set[String] = {
    val file = new File(path.toString)
    file.listFiles().flatMap { f =>
      try {
        BodyPatternDetector.getDetector(f, regex).lines
      } catch {
        case NoDetectorException(eFile) =>
          Set[String]()
      }
    }.toSet
  }
}

case class PatternDetector(path: Path, regex: String) extends BodyDetector {
  override def lines: Set[String] = {
    Source.fromFile(path.toFile).getLines().filter(_.trim.matches(regex)).map { line =>
      line.trim
    }.toSet
  }
}