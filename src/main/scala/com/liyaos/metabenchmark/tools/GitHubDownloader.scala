package com.liyaos.metabenchmark.tools

/**
 * Created by lastland on 15/7/23.
 */
import java.io.File
import scala.sys.process._

class GitHubDownloader(repo: GitHubRepo) {
  def downloadTo(path: String): LocalGitHubRepo = {
    if (!new File(path + repo.name).exists())
      Process(Seq("git", "clone", repo.link), new File(path)).!
    val build  = Build.createBuild(repo)
    build match {
      case m: MavenBuild =>
        new LocalGitHubMavenRepo(path + "/" + repo.name)
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