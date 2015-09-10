package com.liyaos.metabenchmark.profiler

/**
 * Created by lastland on 15/9/10.
 */
class TTYDumper extends Dumper {
  override def println(content: String) {
    println(content)
  }

  override def close() {}
}
