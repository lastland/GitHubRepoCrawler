/**
 * Created by lastland on 15/6/22.
 */
import scala.io.Source
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import scala.xml.{XML, Elem}

class NoRecognizableBuildException extends Exception
class PomEmptyException extends Exception

abstract class Build {
  def dependencies: Stream[String]
}

object Build {
  def createBuild(repo: GitHubRepo): Build = {
    val browser = new Browser
    val page = browser.get(repo.link)
    val branch = (page >> element("span.select-menu-button")).attr("title")
    val files = (page >> element("table.files") >> elements("td.content")).map(_ >?> text("a"))
    val builds = for {
      f <- files
      file <- f if file == "pom.xml" || file == "build.sbt"
    } yield file
    if (!builds.isEmpty) {
      builds(0) match {
        case "pom.xml" =>
          val pomLink = s"https://raw.githubusercontent.com/${repo.owner}/${repo.name}/${branch}/pom.xml"
          val content = Source.fromURL(pomLink).mkString.trim
          if (content.size > 0) {
            val pom = XML.loadString(content)
            new MavenBuild(repo, pom)
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

class MavenBuild(repo: GitHubRepo, pom: Elem) extends Build {
  override def dependencies = {
    (pom \\ "dependency").flatMap { d =>
      (d \ "artifactId").map(_.text)
    }.toStream
  }
}