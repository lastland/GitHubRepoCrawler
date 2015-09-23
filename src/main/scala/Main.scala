/**
 * Created by lastland on 15/6/16.
 */

import java.io.File
import java.util.concurrent.ConcurrentHashMap

import com.liyaos.metabenchmark.disl.{DiSLRun, DiSLMvn, DiSLJava}
import com.typesafe.scalalogging.StrictLogging
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.slick.driver.H2Driver.simple._
import Database.dynamicSession
import scala.slick.jdbc.StaticQuery._

import com.liyaos.metabenchmark.database.{GitHubRepoDatabase, Tables}
import com.liyaos.metabenchmark.tools._
import Tables._

object Main extends App with StrictLogging {
  args.toList match {
    case "init" :: Nil =>
      try {
        GitHubRepoDatabase.DB.withDynSession {
          gitHubRepos.ddl.create
        }
      } catch {
        case e => println(e)
      }
      DiSLJava.install()
      DiSLMvn.install()
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
      val disl = new DiSLRun
      disl.setup()
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
              if (flag) {
                logger.info(s"Found $r")
                r
              } else {
                logger.info(s"$r does not contain target imports")
                throw new RuntimeException("does not contain target imports")
              }
            } else {
              throw new RuntimeException("does not get through qualifications")
            }
          }
          f onSuccess {
            case r =>
              try {
                logger.info(s"Downloading $r")
                val download = new GitHubDownloader(r)
                val tester = new MavenRepoTester(download.downloadTo("./tmp/").path, Some(DiSLMvn.dir))
                logger.info(s"$r download complete")
                logger.debug(s"disl failed = ${disl.failed}, disl started = ${disl.serverStarted}")
                disl.run {
                  logger.info(s"Testing $r")
                  val exitCode = tester.test()
                  logger.info(s"Test results for $r: $exitCode")
                }
              } catch {
                case e => logger.info(s"Exception on $r: $e")
              }
          }
        }
      }
  }
}