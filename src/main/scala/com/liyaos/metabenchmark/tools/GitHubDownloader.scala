package com.liyaos.metabenchmark.tools

/**
 * Created by lastland on 15/7/23.
 */
import java.io.File
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
    currentPath = Some(path)
    val build  = Build.createBuild(target)
    build match {
      case m: MavenBuild =>
        new LocalGitHubMavenRepo(target)
    }
  }

  def delete() {
    currentPath match {
      case Some(p) => new File(p).delete()
      case None => ()
    }
  }
}

abstract class LocalGitHubRepo(val path: String) {
  def getTester(): RepoTester
}

class LocalGitHubMavenRepo(override val path: String) extends LocalGitHubRepo(path) {
  def getTester(): RepoTester = {
    new MavenRepoTester(path)
  }
}