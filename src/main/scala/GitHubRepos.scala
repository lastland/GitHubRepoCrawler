/**
 * Created by lastland on 15/6/16.
 */

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import org.jsoup.HttpStatusException

object GitHubRepo {
  def fromName(name: String): GitHubRepo = {
    def names = name.split("/")
    GitHubRepo(names(0), names(1))
  }
}

case class GitHubRepo(owner: String, name: String) {
  def link = s"https://github.com/$owner/$name"
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
    try {
      val page = browser.get(link)
      val repoNames = (page >> elements("h3.repo-list-name")).map(_ >> text("a"))
      repoNames.map(GitHubRepo.fromName(_)).toStream #::: searchPage(browser, queries, pageNum + 1)
    } catch {
      case statusException: HttpStatusException =>
        println(statusException)
        if (statusException.getStatusCode == 429) {
          Thread.sleep(sleepTime)
          searchPage(browser, queries, pageNum, List(sleepTime * 2, 60000).min)
        } else {
          Stream.empty
        }
    }
  }
}