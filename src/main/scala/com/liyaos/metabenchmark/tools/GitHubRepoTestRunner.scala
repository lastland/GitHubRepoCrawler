package com.liyaos.metabenchmark.tools

import java.nio.file.{Paths, Path}

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
  def run(disl: DiSLRun, r: GitHubRepo, deleteFailed: Boolean = true) = Future {
    if (r.commitNum >= 100 && r.releaseNum >= 5 && r.branchNum > 1) {
      logger.info(s"trying $r")
      val d = new GitHubDownloader(r)
      d.downloadTo("./tmp/")
      val imports = new LocalRepoImportDetector(Paths.get("./tmp", r.name).toAbsolutePath).imports
      logger.debug(s"$r imports: $imports")
      val flag = imports exists { im =>
        im.contains("java.util.concurrent.ThreadPoolExecutor")
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
    } else {
      throw FilterOutException(Qualification)
    }
  } map { r =>
    try {
      val download = new GitHubDownloader(r)
      val tester = new MavenRepoTester(download.downloadTo("./tmp/").path, Some(DiSLMvn.dir))
      disl.run {
        val exitCode = tester.test()
        logger.info(s"Test results for $r: $exitCode")
      }
    } catch {
      case e: Throwable => logger.info(s"Exception on $r: $e")
    }
  }
}
