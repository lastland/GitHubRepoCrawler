package com.liyaos.metabenchmark.disl

/**
 * Created by lastland on 15/9/10.
 */
import java.io.File
import com.liyaos.metabenchmark.tools.ScriptInstaller

object DiSLJava extends ScriptInstaller {
  def content =
    s"""|#!/bin/bash
        |${DiSLConfig.dislProgram} -d ${DiSLConfig.dislHome} -dc ${DiSLConfig.instProgram} -cs ${DiSLConfig.instDir} -- "$$@"
     """.stripMargin

  val dir = DiSLConfig.installationDir + "/java"

  def isInstalled = new File(dir).exists()

  def install() {
    install(dir, content)
  }
}
