package bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

@BenchmarkMode(Array(Mode.AverageTime))
@Fork(2)
@Threads(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class SampleBenchmark {

  var state: Double = _

  @Setup
  def prepare: Unit = {
    state = Math.E
  }

  @TearDown
  def cleanup: Unit = {}

  @Benchmark
  def measure(bh: Blackhole): Unit = {
    bh.consume(Math.log(state))
  }
}
