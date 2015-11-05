package com.liyaos.metabenchmark.tools

/**
 * Created by lastland on 15/10/19.
 */

import java.nio.file.{Paths, Path}
import java.io.File
import scala.io.Source
import fastparse.all._

case class NoViableDetectorException(file: File) extends Exception

abstract class LocalImportDetector(path: Path) extends ImportDetector

object LocalImportDetector {
  def getDetector(file: File) = {
    if (file.isDirectory)
      LocalRepoDirDetector(Paths.get(file.getAbsolutePath))
    else if (file.getName.toLowerCase.endsWith(".java"))
      LocalRepoJavaImportDetector(Paths.get(file.getAbsolutePath))
    else if (file.getName.toLowerCase.endsWith(".scala"))
      LocalRepoScalaImportDetector(Paths.get(file.getAbsolutePath))
    else if (file.getName.toLowerCase.endsWith(".py"))
      LocalRepoPythonImportDetector(Paths.get(file.getAbsolutePath))
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
      line.trim
    }.map { line =>
      line.substring(line.indexOfSlice("import ") + 7, line.size - 1).trim
    }.map { line =>
      if (line.contains("static "))
        line.substring(line.indexOfSlice("static") + 7)
      else line
    }.toSet
  }
}

case class LocalRepoScalaImportDetector(path: Path) extends LocalImportDetector(path) {
  override def imports: Set[String] = {
    Source.fromFile(path.toFile).getLines().map { line =>
      ScalaImportParser.importExpr.parse(line.trim) match {
        case Result.Success(im, _) => Some(im)
        case Result.Failure(_, _) => None
      }
    }.flatten.flatten
  }.toSet
}

case class LocalRepoPythonImportDetector(path: Path) extends LocalImportDetector(path) {
  override def imports: Set[String] = {
    Source.fromFile(path.toFile).getLines().map(_.trim).filter { trimmedLine =>
      trimmedLine.startsWith("from") || trimmedLine.startsWith("import");
    }.toSet
  }
}

trait ScalaBasics {
  val word: P[Unit]= P(CharIn('a' to 'z' ) ~ CharIn(('a' to 'z') ++ ('0' to '9')).rep(0))
}

object ScalaImportParser extends ScalaBasics {
  val White = fastparse.WhitespaceApi.Wrapper {
    NoTrace(" ".rep)
  }
  import White._
  val libExpr: P[Seq[String]] = P((packageExpr.rep(1).! ~ objExpr).map { x =>
    val pre = x._1
    x._2.map(y => pre + y)
  })
  val packageExpr: P[String] = P((word ~ ".").!)
  val nameExpr: P[String] = P(word.! ~ "=>" ~ word | word.! | "_".!)
  val namesExpr: P[Seq[String]] = P("{" ~ nameExpr.rep(1, sep = ",") ~ "}")
  val objExpr: P[Seq[String]] = P(word.!.map(Seq(_)) | "_".!.map(Seq(_)) | namesExpr)

  val importExpr: P[Seq[String]] = P("import" ~! libExpr )
}