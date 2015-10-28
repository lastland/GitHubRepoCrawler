package com.liyaos.metabenchmark.database

/**
 * Created by salucl on 28/10/15.
 */
object TestedRepoDatabase extends RepoDatabase {
  override val dburl = "jdbc:h2:./already.tb"
}
