package com.liyaos.metabenchmark.disl

/**
 * Created by lastland on 15/9/10.
 */
import java.io.File
import scala.sys.process._
import com.liyaos.metabenchmark.tools.ScriptInstaller

object DiSLJava extends ScriptInstaller {

  private val libSuffix = if (System.getProperty("os.name") == "Mac OS X") "jnilib" else "so"
  private val agent = s"${DiSLConfig.dislHome}/lib/libdislagent.$libSuffix"
  private val bypass = s"${DiSLConfig.dislHome}/lib/disl-bypass.jar"
  private val bootClass = s"-Xbootclasspath/a:$bypass:${DiSLConfig.instDir}"
  private val java = "which java".!!.stripLineEnd

  def content =
    s"""|#!/bin/bash
        |${java} -agentpath:$agent $bootClass $$@
     """.stripMargin

  val dir = DiSLConfig.installationDir + "/java"

  def isInstalled = new File(dir).exists()

  def install() {
    install(dir, content)
  }
}
