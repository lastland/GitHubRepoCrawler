package com.liyaos.metabenchmark.database

import java.io._
import java.util.Scanner

import com.liyaos.metabenchmark.database.Tables._
import com.liyaos.metabenchmark.tools.GitHubRepo
import com.liyaos.metabenchmark.database.Tables._
import com.liyaos.metabenchmark.tools.GitHubRepo
import com.typesafe.scalalogging.LazyLogging

import scala.slick.driver.H2Driver.simple._
import scalaz.std.int

/**
 * Created by salucl on 28/10/15.
 */
object TestedRepoDatabase extends RepoDatabase {
  override val dburl = "jdbc:h2:./already.tb"
  val driver = "org.h2.Driver"
  lazy val DB = Database.forURL(dburl, driver = driver)
  val file = new File("already-tested.txt" )
  val writer = new PrintWriter(new FileWriter(file, true));
  val newline = System.getProperty("line.separator");

  def addRepo(repo: GitHubRepo)(implicit session: Session) = {
    logger.debug("inserting " + repo)
    writer.write(repo.toString+"\n");
    writer.flush();
  }

  def existsRepo(repo: GitHubRepo)(implicit session: Session): Boolean = {
    val scanner = new Scanner(file)
      try {
//      scanner.findWithinHorizon(" " + repo.toString + "\n", 0);
        var lineNum = 0;
        while (scanner.hasNextLine()) {
          var line = scanner.nextLine()+newline;
          lineNum+=1;
          if(line.contains(repo.toString + newline)) {
            return true;
          }
        }
      } catch {
        case e: FileNotFoundException => System.out.println("File contining already tested repos not found")
      }
    return false;
    }
}
