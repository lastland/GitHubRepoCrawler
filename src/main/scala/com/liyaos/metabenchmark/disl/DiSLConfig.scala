package com.liyaos.metabenchmark.disl

/**
 * Created by lastland on 15/9/10.
 */
import java.io.File
import com.typesafe.config._
import com.liyaos.metabenchmark.tools.ScriptInstaller

object DiSLConfig {
  private lazy val config = ConfigFactory.load()
  lazy val dislProgram = config.getString("disl.program")
  lazy val dislHome = config.getString("disl.home")
  lazy val instProgram = "/Users/lastland/workspace/graal-profiler/GitHubRepoMiner/build-inst/example-inst.jar"

  lazy val installationDir = new File(System.getProperty("user.dir")).getAbsolutePath
}