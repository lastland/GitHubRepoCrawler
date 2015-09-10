package com.liyaos.metabenchmark.tools

/**
 * Created by lastland on 15/9/10.
 */
import java.io.PrintWriter
import java.nio.file.{Paths, Files}
import java.nio.file.attribute.PosixFilePermissions

trait ScriptInstaller {
  def install(file:String, content: String) {
    val writer = new PrintWriter(file)
    writer.print(content)
    writer.close()
    Files.setPosixFilePermissions(Paths.get(file),
      PosixFilePermissions.fromString("rwxr-xr-x"))
  }
}

object ScriptInstaller