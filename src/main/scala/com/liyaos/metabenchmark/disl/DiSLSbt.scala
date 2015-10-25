package com.liyaos.metabenchmark.disl

import java.io.File
import com.liyaos.metabenchmark.tools.ScriptInstaller

/**
 * Created by lastland on 15/10/23.
 */
object DiSLSbt extends ScriptInstaller {
  def content =
    s"""|#!/bin/bash
        |${DiSLConfig.installationDir}/libexec/sbt -java-home ${DiSLJava.fakeJavaHome} "$$@"
     """.stripMargin

  val dir = DiSLConfig.installationDir + "/sbt"

  def isInstalled = new File(dir).exists()

  def install() {
    install(dir, content)
  }
}
