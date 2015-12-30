package bench

import java.util.concurrent.{Executors, ExecutorService}
import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

import scala.annotation.internal.scala2compat

object LazyVals {

  class LazyHolder {
    lazy val baseline: Int = 1
    @volatile lazy val volatile: Int = 1
    @scala2compat lazy val scala2: Int = 1
  }

  class BaselineHolder {
    lazy val value: Int = 1
  }

  class VolatileHolder {
    @volatile lazy val value: Int = 1
  }

  class Scala2Holder {
    @scala2compat lazy val value: Int = 1
  }

  @BenchmarkMode(Array(Mode.AverageTime))
  @Fork(2)
  @Threads(1)
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  @State(Scope.Benchmark)
  class InitializedAccess {

    var holder: LazyHolder = _

    @Setup
    def prepare: Unit = {
      holder = new LazyHolder
      holder.baseline
      holder.volatile
      holder.scala2
    }

    @Benchmark
    def measureBaseline(bh: Blackhole): Unit = bh.consume(holder.baseline)

    @Benchmark
    def measureVolatile(bh: Blackhole): Unit = bh.consume(holder.volatile)

    @Benchmark
    def measureScala2(bh: Blackhole): Unit = bh.consume(holder.scala2)
  }

  @BenchmarkMode(Array(Mode.AverageTime))
  @Fork(2)
  @Threads(1)
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  @State(Scope.Benchmark)
  class UncontendedInitialization {

    @Benchmark
    def measureBaseline(bh: Blackhole): Unit = {
      val holder = new BaselineHolder
      bh.consume(holder)
      bh.consume(holder.value)
    }

    @Benchmark
    def measureVolatile(bh: Blackhole): Unit = {
      val holder = new VolatileHolder
      bh.consume(holder)
      bh.consume(holder.value)
    }

    @Benchmark
    def measureScala2(bh: Blackhole): Unit = {
      val holder = new Scala2Holder
      bh.consume(holder)
      bh.consume(holder.value)
    }
  }

  @BenchmarkMode(Array(Mode.AverageTime))
  @Fork(2)
  @Threads(1)
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @State(Scope.Benchmark)
  class ContendedInitialization {

    @Param(Array("1000000", "2000000", "5000000"))
    var size: Int = _

    @Param(Array("2", "4", "8"))
    var nThreads: Int = _

    var executor: ExecutorService = _

    @Setup
    def prepare: Unit = {
      executor = Executors.newFixedThreadPool(nThreads)
    }

    @TearDown
    def cleanup: Unit = {
      executor.shutdown()
      executor = null
    }

    @Benchmark
    def measureVolatile(bh: Blackhole): Unit = {
      val array = Array.fill(size)(new VolatileHolder)
      val task: Runnable = () =>
        for (elem <- array) bh.consume(elem.value)

      val futures =
        for (_ <- 0 until nThreads) yield
          executor.submit(task)

      futures.foreach(_.get())
    }

    @Benchmark
    def measureScala2(bh: Blackhole): Unit = {
      val array = Array.fill(size)(new Scala2Holder)
      val task: Runnable = () =>
        for (elem <- array) bh.consume(elem.value)

      val futures =
        for (_ <- 0 until nThreads) yield
          executor.submit(task)

      futures.foreach(_.get())
    }
  }
}
