package com.liyaos.metabenchmark.database

/**
 * Created by salucl on 28/10/15.
 */
object GitHubRepoDatabase extends RepoDatabase {
  override val dburl = "jdbc:h2:./test.tb"
}
