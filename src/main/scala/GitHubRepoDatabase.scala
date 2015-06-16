/**
 * Created by lastland on 15/6/16.
 */
import scala.slick.driver.H2Driver.simple._
import Database.dynamicSession

object GitHubRepoDatabase {
  val dburl = "jdbc:h2:./test.tb"
  val driver = "org.h2.Driver"
  lazy val DB = Database.forURL(dburl, driver = driver)

  def addRepo(repo: GitHubRepo)(implicit session: Session) = {
    import Tables._
    println("inserting " + repo)
    val matchingRepo = gitHubRepos.filter { r =>
      r.owner === repo.owner && r.repo === repo.name
    }.firstOption
    val id = matchingRepo match {
      case Some(r) =>
        println(repo + " already exists.")
        r.id match {
          case Some(num) => num
          case None =>
            // not supposed to happen!
            throw new RuntimeException("no id!")
        }
      case None =>
        (gitHubRepos returning gitHubRepos.map(_.id)) += repo.toRow
    }
  }
}
