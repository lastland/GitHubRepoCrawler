package com.liyaos.metabenchmark.disl

/**
 * Created by lastland on 15/9/10.
 */
import java.io.File
import com.typesafe.config._
import com.liyaos.metabenchmark.tools.ScriptInstaller

object DiSLConfig {
  private lazy val config = ConfigFactory.load()
  lazy val dislProgram = installationDir + "/src/main/python/disl.py"
  lazy val dislHome = config.getString("disl.home")
  lazy val instDir = installationDir + "/target/scala-2.11/classes"
  lazy val instProgram = instDir + "/com/liyaos/metabenchmark/instrumentation/Instrumentation.class"
  lazy val installationDir = new File(System.getProperty("user.dir")).getAbsolutePath

  lazy val javaHome = config.getString("disl.java_home")
}