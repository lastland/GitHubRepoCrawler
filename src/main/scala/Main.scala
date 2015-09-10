/**
 * Created by lastland on 15/6/16.
 */

import java.io.File
import java.util.concurrent.ConcurrentHashMap

import com.typesafe.scalalogging.StrictLogging
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.slick.driver.H2Driver.simple._
import Database.dynamicSession
import scala.slick.jdbc.StaticQuery._

import com.liyaos.metabenchmark.database.{GitHubRepoDatabase, Tables}
import com.liyaos.metabenchmark.tools.{GitHubRepoImportDetector, GitHubRepo, GitHubRepos, GitHubDownloader}
import Tables._

object Main extends App with StrictLogging {
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
      new File("./tmp/").mkdir
      GitHubRepoDatabase.DB.withDynSession {
        for (repo <- gitHubRepos) {
          val r = repo.toGitHubRepo
          val f = Future {
            if (r.commitNum >= 100 && r.releaseNum >= 5 && r.branchNum > 1) {
              logger.info(s"trying $r")
              val detector = new GitHubRepoImportDetector(r.owner + "/" + r.name)
              val flag = detector.imports exists { im =>
                im.contains("java.util.concurrent.ThreadPoolExecutor")
              }
              if (flag) r else throw new RuntimeException("does not contain target imports")
            } else {
              throw new RuntimeException("does not get through qualifications")
            }
          }
          f onSuccess {
            case r: GitHubRepo =>
              val download = new GitHubDownloader(r)
              val tester = download.downloadTo("./tmp/").getTester()
              val exitCode = tester.test()
              println(s"Test results for $r: $exitCode")
          }
        }
      }
  }
}