/**
 * Created by lastland on 15/6/16.
 */

import java.util.concurrent.ConcurrentHashMap
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
      for (repo <- stream) {
        GitHubRepoDatabase.DB.withDynSession {
          GitHubRepoDatabase.addRepo(repo)
        }
      }
    case "filter" :: Nil =>
      val result = new ConcurrentHashMap[GitHubRepo, Unit]()
      GitHubRepoDatabase.DB.withDynSession {
        for (repo <- gitHubRepos) {
          val r = repo.toGitHubRepo
          val f = Future {
            println(s"trying $r")
            val detector = new GitHubRepoImportDetector(r.owner + "/" + r.name)
            val t = detector.imports exists { im =>
              im.contains("java.util.concurrent")
            }
            if (t) {
              result.put(r, ())
            }
            r
          }
          f onSuccess {
            case r: GitHubRepo =>
              println(s"$r finished, currently we have ${result.size()}")
          }
        }
      }
  }
}
