/**
 * Created by lastland on 15/6/16.
 */

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

import scala.util.{Failure, Success}

object GitHubRepo {
  def fromName(name: String): GitHubRepo = {
    def names = name.split("/")
    GitHubRepo(names(0), names(1))
  }
}

case class GitHubRepo(owner: String, name: String) {
  def link = s"https://github.com/$owner/$name"
  lazy val doc = PageGetter.get(link)
  lazy val numbersSummary = {
    doc match {
      case Success(page) =>
        (page >> element("ul.numbers-summary") >> elements("span.num")).map(
          _ >> text("span")).map(_.replace(",", "").toInt)
      case Failure(exception) =>
        throw exception
    }
  }
  lazy val commitNum: Int = numbersSummary(0)
  lazy val branchNum: Int = numbersSummary(1)
  lazy val releaseNum: Int = numbersSummary(2)
  lazy val contributorNum: Int = numbersSummary(3)

  def toRow: Tables.GitHubRepoRow = Tables.GitHubRepoRow(None, owner, name)

  override def toString = s"GitHub Repo $owner/$name"
}

class GitHubRepos {
  private lazy val searchLink = "https://github.com/search"

  def searchLanguage(language: String): Stream[GitHubRepo] = {
    val lst = List(s"q=language:$language", "type=Repositories")
    search(lst) #::: search("s=stars" :: "o=desc" :: lst) #::: search("s=forks" :: "o=desc" :: lst)
  }

  def search(queries: List[String]): Stream[GitHubRepo] = {
    val browser = new Browser
    searchPage(browser, queries, 1)
  }

  protected def searchPage(browser: Browser, queries: List[String], pageNum: Int,
                           sleepTime: Int = 1000): Stream[GitHubRepo] = {
    val link = searchLink + "?" + (s"p=$pageNum" :: queries).mkString("&")
    val doc = PageGetter.get(browser, link)
    doc match {
      case Success(page) =>
        val repoNames = (page >> elements("h3.repo-list-name")).map(_ >> text("a"))
        repoNames.map(GitHubRepo.fromName(_)).toStream #::: searchPage(browser, queries, pageNum + 1)
      case Failure(ex) =>
        Stream.empty
    }
  }
}