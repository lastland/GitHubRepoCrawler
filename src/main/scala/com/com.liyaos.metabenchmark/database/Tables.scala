package com.liyaos.metabenchmark.database

/**
 * Created by lastland on 15/6/16.
 */

import com.liyaos.metabenchmark.tools.GitHubRepo

import scala.slick.driver.H2Driver.simple._

object Tables {
  case class GitHubRepoRow(id: Option[Int], owner: String, repo: String) {
    def toGitHubRepo = GitHubRepo(owner, repo)
  }

  class GitHubRepoTable(tag: Tag) extends Table[GitHubRepoRow](tag, "GITHUB_REPO") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def owner = column[String]("OWNER")
    def repo = column[String]("REPO")
    def * = (id.?, owner, repo) <> (GitHubRepoRow.tupled, GitHubRepoRow.unapply)
  }

  val gitHubRepos = TableQuery[GitHubRepoTable]
}
