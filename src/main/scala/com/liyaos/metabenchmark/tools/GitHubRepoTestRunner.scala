package com.liyaos.metabenchmark.tools

import java.nio.file.{Paths, Path}

import com.liyaos.metabenchmark.{FilterMode, MainArguments}
import com.liyaos.metabenchmark.disl.{DiSLMvn, DiSLRun}
import com.typesafe.scalalogging.StrictLogging
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by lastland on 15/10/19.
 */

object Phase extends Enumeration {
  type Phase = Value
  val Qualification, Filtering = Value
}

import Phase._
case class FilterOutException(phase: Phase) extends Exception

object GitHubRepoTestRunner extends StrictLogging {
  def run(disl: DiSLRun, r: GitHubRepo, matchImports: Array[String],
          downloadDir: String = MainArguments.outputFolder, deleteFailed: Boolean = true) = Future {
    if (r.commitNum >= 100 && r.releaseNum >= 5 && r.branchNum > 1) {
      logger.info(s"trying $r")
      val d = new GitHubDownloader(r)
      try {
        d.downloadTo(downloadDir)
        var flag = false
        MainArguments.mode match {
          case FilterMode.Imports => {
            val imports = new LocalRepoImportDetector(
              Paths.get(downloadDir, r.name).toAbsolutePath).imports
            logger.debug(s"$r imports: $imports")
            flag = imports exists {
              im =>
                matchImports.exists(matchImp
                => im.contains(matchImp))
            }
          }
          case FilterMode.Body => {
            val lines = BodyRepoPatternDetector(
              Paths.get(downloadDir, r.name).toAbsolutePath, MainArguments.regex).lines
            lines foreach { line =>
               logger.info("======> Located Pattern = " + line)
            }
          }
        }
        if (flag) {
          logger.info(s"Found $r")
          r
        } else {
          logger.info(s"$r does not contain target imports")
          if (deleteFailed) {
            logger.debug(s"deleting $r")
            d.delete()
          }
          throw FilterOutException(Filtering)
        }
      } catch {
        case e: NoRecognizableBuildException =>
          d.delete()
          throw e
        case e: Throwable => throw e
      }
    } else {
      throw FilterOutException(Qualification)
    }
  } map { r =>
    try {
      val download = new GitHubDownloader(r)
      val tester = download.downloadTo(downloadDir).getTester()
      disl.run {
        val exitCode = tester.test()
        logger.info(s"Test results for $r: $exitCode")
      }
    } catch {
      case e: Throwable => logger.info(s"Exception on $r: $e")
    }
  }
}
