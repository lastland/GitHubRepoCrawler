package com.liyaos.metabenchmark.profiler

/**
 * Created by lastland on 15/9/10.
 */
import java.io.PrintWriter

class ArchiveDumper(fileName: String) extends Dumper {
  val p = new PrintWriter(fileName)

  override def println(content: String) {
    p.println(content)
  }

  override def close() {
    p.close()
  }
}
