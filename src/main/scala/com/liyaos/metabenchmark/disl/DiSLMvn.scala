package com.liyaos.metabenchmark.disl

/**
 * Created by lastland on 15/9/10.
 */
import java.io.File
import com.liyaos.metabenchmark.tools.ScriptInstaller

object DiSLMvn extends ScriptInstaller {
  def content =
    s"""|#!/bin/bash
        |export JAVA_HOME="$${JAVA_HOME:-$$(/usr/libexec/java_home)}"
        |export JAVACMD="${DiSLConfig.installationDir}/java"
        |exec "/usr/local/Cellar/maven/3.3.3/libexec/bin/mvn" "$$@"
     """.stripMargin

  val dir = DiSLConfig.installationDir + "/mvn"

  def isInstalled = new File(dir).exists()

  def install() {
    install(dir, content)
  }
}
