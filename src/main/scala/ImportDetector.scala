/**
 * Created by lastland on 15/6/22.
 */
import scala.io.Source
import scala.collection.immutable.Set
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

import scala.util.{Failure, Success}

abstract class ImportDetector {
  def imports: Set[String]
}

abstract class GitHubImportDetector(repo: String) extends ImportDetector

class GitHubRepoImportDetector(repo: String) extends GitHubImportDetector(repo) {
  val link = s"https://github.com/$repo"

  protected def imports(branch: String, file: String, fs: Seq[(String, Option[String])]): Set[String] = {
    var res: List[GitHubImportDetector] = List()
    for (f <- fs) {
      f._2 match {
        case None => ()
        case Some(fileName) =>
          val fl: String = if (file != "") file + "/" + fileName else fileName
          if (f._1 contains "octicon-file-directory") {
            res = (new GitHubDirImportDetector(repo, branch, fl)) :: res
          } else if (f._1 contains "octicon-file-text") {
            if (fileName.endsWith(".java") || fileName.endsWith(".Java")) {
              res = (new GitHubJavaImportDetector(repo, branch, fl)) :: res
            }
          }
      }
    }
    res.flatMap(_.imports).toSet
  }

  override def imports = {
    val doc = PageGetter.get(link)
    doc match {
      case Success(page) =>
        val files = page >> element("table.files")
        val tpes = (files >> elements("td.icon") >> elements("span")).map(_.attr("class"))
        val names = (files >> elements("td.content")).map(_ >?> text("a"))
        val fs = tpes.zip(names)
        val branch = (page >> element("span.select-menu-button")).attr("title")
        imports(branch, "", fs)
      case Failure(exception) =>
        throw exception
    }
  }
}

class GitHubDirImportDetector(repo: String, branchName: String, fileName: String)
  extends GitHubRepoImportDetector(repo) {
  override val link = s"https://github.com/$repo/tree/$branchName/$fileName"

  override def imports = {
    val doc = PageGetter.get(link)
    doc match {
      case Success(page) =>
        val browser = new Browser
        val page = browser.get(link)
        val files = page >> element("table.files")
        val tpes = (files >> elements("td.icon") >> elements("span")).map(_.attr("class"))
        val names = (files >> elements("td.content")).map(_ >?> text("a"))
        val fs = tpes.zip(names)
        imports(branchName, fileName, fs)
      case Failure(exception) =>
        throw exception
    }
  }
}

class GitHubJavaImportDetector(repo: String, branch: String, file: String)
  extends GitHubImportDetector(repo) {
  val rawLink = s"https://raw.githubusercontent.com/$repo/$branch/$file"
  lazy val rawContent: String = Source.fromURL(rawLink).mkString
  override def imports = {
    val lines = rawContent.split("\n")
    val importLines = for (line <- lines if line.trim.startsWith("import ")) yield line
    importLines.map(line =>
      line.trim.substring(line.indexOfSlice("import ") + 7, line.size - 1)).map(line =>
      if (line.contains("static "))
        line.trim.substring(line.indexOfSlice("static") + 7)
      else line.trim).toSet
  }
}
