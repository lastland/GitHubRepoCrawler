/**
 * Created by lastland on 15/6/16.
 */
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.slick.driver.H2Driver.simple._
import Database.dynamicSession
import scala.slick.jdbc.StaticQuery._
import Tables._

object Main extends App {
  args.toList match {
    case "init" :: Nil =>
      GitHubRepoDatabase.DB.withDynSession {
        gitHubRepos.ddl.create
      }
    case "reset" :: Nil =>
      GitHubRepoDatabase.DB.withDynSession {
        updateNA("DROP ALL OBJECTS DELETE FILES").execute
      }
    case "run" :: Nil =>
      val repos = new GitHubRepos
      val stream = repos.searchLanguage("Java") #::: repos.searchLanguage("Scala")
      GitHubRepoDatabase.DB.withDynSession {
        for (repo <- stream) {
          Future {
            GitHubRepoDatabase.addRepo(repo)
          }
        }
      }
  }
}
