/**
 * Created by lastland on 15/6/16.
 */

import org.xml.sax.SAXParseException
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
      GitHubRepoDatabase.DB.withDynSession {
        var current = 0
        var result: List[GitHubRepo] = List()
        for (repo <- gitHubRepos) {
          println("trying " + repo.toGitHubRepo)
          try {
            val r = repo.toGitHubRepo
            val build = Build.createBuild(r)
            if (build.dependencies.exists(_.contains("akka"))) {
              result = r :: result
            }
            current = current + 1
            if (current % 100 == 0)
              println(s"Current result = ${result.size}")
          } catch {
            case ex: NoRecognizableBuildException => ()
            case ex: PomEmptyException => ()
            case ex: SAXParseException => ()
          }
        }
        println(result.size)
        println(result)
      }
  }
}
