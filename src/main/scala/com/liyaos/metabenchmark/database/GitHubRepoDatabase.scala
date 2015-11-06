package com.liyaos.metabenchmark.database

import com.liyaos.metabenchmark.database.Tables._
import com.liyaos.metabenchmark.tools.GitHubRepo
import com.liyaos.metabenchmark.database.Tables._
import com.liyaos.metabenchmark.tools.GitHubRepo
import com.typesafe.scalalogging.LazyLogging

import scala.slick.driver.H2Driver.simple._

/**
 * Created by salucl on 28/10/15.
 */
object GitHubRepoDatabase extends RepoDatabase {
  override val dburl = "jdbc:h2:./test.tb"
  val driver = "org.h2.Driver"
  lazy val DB = Database.forURL(dburl, driver = driver)

  def addRepo(repo: GitHubRepo)(implicit session: Session) = {
    if (!existsRepo(repo))
      (gitHubRepos returning gitHubRepos.map(_.id)) += repo.toRow
  }

  def existsRepo(repo: GitHubRepo)(implicit session: Session): Boolean = {
    import com.liyaos.metabenchmark.database.Tables._
    logger.debug("inserting " + repo)
    val matchingRepo = gitHubRepos.filter { r =>
      r.owner === repo.owner && r.repo === repo.name
    }.firstOption
    val id = matchingRepo match {
      case Some(r) => {
        return true;
      }
      case None => {
        return false;
      }
    }
    return false;
  }
}
