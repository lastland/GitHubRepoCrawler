package com.liyaos.metabenchmark.tools

/**
 * Created by lastland on 15/7/23.
 */
import java.io.File
import ammonite.ops.{rm, Path}
import com.liyaos.metabenchmark.disl.{DiSLSbt, DiSLMvn}
import scala.sys.process._

case class DownloadFailedException(file: String) extends Exception

class GitHubDownloader(repo: GitHubRepo) {

  var currentPath: Option[String] = None

  def downloadTo(path: String): LocalGitHubRepo = {
    val target = path + "/" + repo.name
    val c = if (!(new File(target).exists())) {
      Process(Seq("git", "clone", repo.link), new File(path)).!
    } else 0
    if (c != 0) throw DownloadFailedException(target)
    currentPath = Some(new File(target).getAbsolutePath)
    val build  = Build.createBuild(target)
    build match {
      case m: MavenBuild =>
        new LocalGitHubMavenRepo(target)
      case s: SbtBuild =>
        new LocalGitHubSbtRepo(target)
    }
  }

  def delete() {
    currentPath match {
      case Some(p) => rm(Path(p))
      case None => ()
    }
  }
}

abstract class LocalGitHubRepo(val path: String) {
  def getTester(): RepoTester
}

class LocalGitHubMavenRepo(override val path: String) extends LocalGitHubRepo(path) {
  def getTester(): RepoTester = {
    new MavenRepoTester(path, Some(DiSLMvn.dir))
  }
}

class LocalGitHubSbtRepo(override val path: String) extends LocalGitHubRepo(path) {
  override def getTester(): RepoTester = {
    new SbtRepoTester(path, Some(DiSLSbt.dir))
  }
}