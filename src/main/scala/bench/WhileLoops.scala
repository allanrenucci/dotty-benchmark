package bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

import scala.annotation.tailrec

@BenchmarkMode(Array(Mode.AverageTime))
@Fork(2)
@Threads(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class WhileLoops {

  @Param(Array("1", "10", "100", "1000", "10000"))
  var size: Int = _

  var as: Array[Int] = _

  @Setup
  def prepare: Unit = {
    as = (0 until size).toArray
  }

  inline def WhileI(condition: => Boolean)(action: => Unit): Unit = {
    @tailrec
    def go: Unit = if (condition) { action; go }
    go
  }

  @tailrec
  final def WhileT(condition: => Boolean)(action: => Unit): Unit =
    if (condition) { action; WhileT(condition)(action) }

  @Benchmark
  def nativeSum(bh: Blackhole): Unit = {
    var i = 0
    while (i < as.length) {
      bh.consume(as(i))
      i += 1
    }
  }

  @Benchmark
  def inlineSum(bh: Blackhole): Unit = {
    var i = 0
    WhileI (i < as.length) {
      bh.consume(as(i))
      i += 1
    }
  }

  @Benchmark
  def tailRecSum(bh: Blackhole): Unit = {
    var i = 0
    WhileT (i < as.length) {
      bh.consume(as(i))
      i += 1
    }
  }
}
