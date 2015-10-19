package com.liyaos.metabenchmark.tools

/**
 * Created by lastland on 15/10/19.
 */

import java.nio.file.Path

import better.files._
import better.files.Cmds._

case class NoViableDetectorException(file: File) extends Exception

abstract class LocalImportDetector(path: Path) extends ImportDetector

object LocalImportDetector {
  def getDetector(file: File) = {
    if (file.isDirectory) LocalRepoDirDetector(file.path)
    else if (file.extension == Some(".java")) LocalRepoJavaImportDetector(file.path)
    else throw NoViableDetectorException(file)
  }
}

case class LocalRepoImportDetector(path: Path) extends LocalImportDetector(path) {
  override def imports: Set[String] = {
    val file = File(path.toString)
    LocalImportDetector.getDetector(file).imports
  }
}

case class LocalRepoDirDetector(path: Path) extends LocalImportDetector(path) {
  override def imports: Set[String] = {
    val file = File(path.toString)
    ls(file).flatMap { f =>
      try {
        LocalImportDetector.getDetector(f).imports
      } catch {
        case NoViableDetectorException(eFile) =>
          Set[String]()
      }
    }.toSet
  }
}

case class LocalRepoJavaImportDetector(path: Path) extends LocalImportDetector(path) {

  override def imports: Set[String] = {
    val file = File(path.toString)
    file.lines.filter(_.trim.startsWith("import ")).map { line =>
      line.trim.substring(line.indexOfSlice("import ") + 7, line.size - 1)
    }.map { line =>
      if (line.contains("static "))
        line.trim.substring(line.indexOfSlice("static") + 7)
      else line.trim
    }.toSet
  }
}