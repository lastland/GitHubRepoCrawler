package com.liyaos.metabenchmark.tools

/**
 * Created by lastland on 15/10/19.
 */

import java.nio.file.{Paths, Path}
import java.io.File
import scala.io.Source

case class NoViableDetectorException(file: File) extends Exception

abstract class LocalImportDetector(path: Path) extends ImportDetector

object LocalImportDetector {
  def getDetector(file: File) = {
    if (file.isDirectory)
      LocalRepoDirDetector(Paths.get(file.getAbsolutePath))
    else if (file.getName.toLowerCase.endsWith(".java"))
      LocalRepoJavaImportDetector(Paths.get(file.getAbsolutePath))
    else throw NoViableDetectorException(file)
  }
}

case class LocalRepoImportDetector(path: Path) extends LocalImportDetector(path) {
  override def imports: Set[String] = {
    val file = new File(path.toString)
    LocalImportDetector.getDetector(file).imports
  }
}

case class LocalRepoDirDetector(path: Path) extends LocalImportDetector(path) {
  override def imports: Set[String] = {
    val file = new File(path.toString)
    file.listFiles().flatMap { f =>
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
    Source.fromFile(path.toFile).getLines().filter(_.trim.startsWith("import ")).map { line =>
      line.trim.substring(line.indexOfSlice("import ") + 7, line.size - 1)
    }.map { line =>
      if (line.contains("static "))
        line.trim.substring(line.indexOfSlice("static") + 7)
      else line.trim
    }.toSet
  }
}