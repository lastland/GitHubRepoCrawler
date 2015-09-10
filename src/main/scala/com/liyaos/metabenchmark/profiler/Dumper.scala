package com.liyaos.metabenchmark.profiler

/**
 * Created by lastland on 15/9/10.
 */
trait Dumper extends AutoCloseable {
  def println(content: String): Unit
  def close(): Unit
}
