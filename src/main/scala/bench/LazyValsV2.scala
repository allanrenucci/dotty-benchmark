package bench

import java.util.concurrent.{Executors, ExecutorService}
import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

import runtime._

object LazyValsV2 {

  @BenchmarkMode(Array(Mode.AverageTime))
  @Fork(2)
  @Threads(1)
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  @State(Scope.Benchmark)
  class InitializedAccess {

    var holder0: LazyBaseline = _
    var holder1: LazyV1       = _
    var holder1Bis: LazyV1Bis = _
    var holder1AFU: LazyV1AFU = _
    var holder2: LazyV2       = _
    var holder2Bis: LazyV2Bis = _
    var holder3: LazyV3       = _

    @Setup
    def prepare: Unit = {
      holder0    = new LazyBaseline(1)
      holder1    = new LazyV1(1)
      holder1Bis = new LazyV1Bis(1)
      holder1AFU = new LazyV1AFU(1)
      holder2    = new LazyV2(1)
      holder2Bis = new LazyV2Bis(1)
      holder3    = new LazyV3(1)
    }

    // @Benchmark
    // def measureV0(bh: Blackhole): Unit = bh.consume(holder0.value)

    // @Benchmark
    // def measureV1(bh: Blackhole): Unit = bh.consume(holder1.value)

    // @Benchmark
    // def measureV1Bis(bh: Blackhole): Unit = bh.consume(holder1Bis.value)

    // @Benchmark
    // def measureV1AFU(bh: Blackhole): Unit = bh.consume(holder1AFU.value)

    // @Benchmark
    // def measureV2(bh: Blackhole): Unit = bh.consume(holder2.value)

    @Benchmark
    def measureV2Bis(bh: Blackhole): Unit = bh.consume(holder2Bis.value)

    // @Benchmark
    // def measureV3(bh: Blackhole): Unit = bh.consume(holder3.value)
  }

  @BenchmarkMode(Array(Mode.AverageTime))
  @Fork(2)
  @Threads(1)
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  @State(Scope.Benchmark)
  class UncontendedInitialization {

    // @Benchmark
    // def measureV0(bh: Blackhole): Unit = {
    //   val holder = new LazyBaseline(1)
    //   bh.consume(holder)
    //   bh.consume(holder.value)
    // }

    // @Benchmark
    // def measureV1(bh: Blackhole): Unit = {
    //   val holder = new LazyV1(1)
    //   bh.consume(holder)
    //   bh.consume(holder.value)
    // }

    // @Benchmark
    // def measureV1Bis(bh: Blackhole): Unit = {
    //   val holder = new LazyV1Bis(1)
    //   bh.consume(holder)
    //   bh.consume(holder.value)
    // }

    @Benchmark
    def measureV1AFU(bh: Blackhole): Unit = {
      val holder = new LazyV1AFU(1)
      bh.consume(holder)
      bh.consume(holder.value)
    }

    // @Benchmark
    // def measureV2(bh: Blackhole): Unit = {
    //   val holder = new LazyV2(1)
    //   bh.consume(holder)
    //   bh.consume(holder.value)
    // }

    // @Benchmark
    // def measureV2Bis(bh: Blackhole): Unit = {
    //   val holder = new LazyV2Bis(1)
    //   bh.consume(holder)
    //   bh.consume(holder.value)
    // }

    // @Benchmark
    // def measureV3(bh: Blackhole): Unit = {
    //   val holder = new LazyV3(1)
    //   bh.consume(holder)
    //   bh.consume(holder.value)
    // }
  }

  @BenchmarkMode(Array(Mode.AverageTime))
  @Fork(2)
  @Threads(1)
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @State(Scope.Benchmark)
  class ContendedInitialization {

    // @Param(Array("1000000", "2000000", "5000000"))
    @Param(Array("2000000"))
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

    // @Benchmark
    // def measureV1(bh: Blackhole): Unit = {
    //   val array = Array.tabulate(size)(new LazyV1(_))
    //   val task: Runnable = () =>
    //     for (elem <- array) bh.consume(elem.computeValue())

    //   val futures =
    //     for (_ <- 0 until nThreads) yield
    //       executor.submit(task)

    //   futures.foreach(_.get())
    // }

    // @Benchmark
    // def measureV1AFU(bh: Blackhole): Unit = {
    //   val array = Array.tabulate(size)(new LazyV1AFU(_))
    //   val task: Runnable = () =>
    //     for (elem <- array) bh.consume(elem.value)

    //   val futures =
    //     for (_ <- 0 until nThreads) yield
    //       executor.submit(task)

    //   futures.foreach(_.get())
    // }

    // @Benchmark
    // def measureV1Bis(bh: Blackhole): Unit = {
    //   val array = Array.tabulate(size)(new LazyV1Bis(_))
    //   val task: Runnable = () =>
    //     for (elem <- array) bh.consume(elem.computeValue())

    //   val futures =
    //     for (_ <- 0 until nThreads) yield
    //       executor.submit(task)

    //   futures.foreach(_.get())
    // }

    // @Benchmark
    // def measureV2(bh: Blackhole): Unit = {
    //   val array = Array.tabulate(size)(new LazyV2(_))
    //   val task: Runnable = () =>
    //     for (elem <- array) bh.consume(elem.value)

    //   val futures =
    //     for (_ <- 0 until nThreads) yield
    //       executor.submit(task)

    //   futures.foreach(_.get())
    // }

    @Benchmark
    def measureV2Bis(bh: Blackhole): Unit = {
      val array = Array.tabulate(size)(new LazyV2Bis(_))
      val task: Runnable = () =>
        for (elem <- array) bh.consume(elem.value)

      val futures =
        for (_ <- 0 until nThreads) yield
          executor.submit(task)

      futures.foreach(_.get())
    }
  }
}
