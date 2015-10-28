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
object TestedRepoDatabase extends RepoDatabase {
  override val dburl = "jdbc:h2:./already.tb"
  val driver = "org.h2.Driver"
  lazy val DB = Database.forURL(dburl, driver = driver)

  def addRepo(repo: GitHubRepo)(implicit session: Session) = {
    logger.debug("inserting " + repo)
    if (!existsRepo(repo))
      (gitHubRepos returning gitHubRepos.map(_.id)) += repo.toRow
  }

  def existsRepo(repo: GitHubRepo)(implicit session: Session): Boolean = {
    import com.liyaos.metabenchmark.database.Tables._
    val matchingRepo = gitHubRepos.filter { r =>
      r.owner === repo.owner && r.repo === repo.name
    }.firstOption
    val id = matchingRepo match {
      case Some(r) => {
        logger.info(" =====> " + repo + " EXIST. Already tested");
        return true;
      }
      case None => {
        logger.info(" =====> " + repo + " NOT EXIST. NOT tested already");
        return false;
      }
    }
    return false;
  }
}
