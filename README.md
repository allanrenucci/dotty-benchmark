# Dotty Lazy Vals Benchmarks

## Prerequisite:

```
$ git clone https://github.com/dotty-staging/dotty -b lazy-vals-bench
$ sbt dotty-bootstrapped/publishLocal
```

## Single Threaded. Initialized lazy val access

```
sbt:dotty-benchmark> jmh:run InitializedAccess

Benchmark        Mode  Cnt  Score   Error  Units
measureBaseline  avgt   10  2.361 ± 0.069  ns/op
measureScala2    avgt   10  2.385 ± 0.053  ns/op
measureVolatile  avgt   10  3.160 ± 0.107  ns/op
```

## Single Threaded. Object creation times and lazy field initialization

```
sbt:dotty-benchmark> jmh:run LazyVals.UncontendedInitialization

Benchmark        Mode  Cnt   Score   Error  Units
measureBaseline  avgt   10   5.744 ± 0.071  ns/op
measureScala2    avgt   10  24.194 ± 0.646  ns/op
measureVolatile  avgt   10  22.238 ± 0.853  ns/op
```

## Multi Threaded. Initialization with contention

```
sbt:dotty-benchmark> jmh:run LazyVals.ContendedInitialization


Benchmark        (nThreads)   (size)  Mode  Cnt     Score    Error  Units

measureScala2             2  1000000  avgt   10   102.284 ±  1.205  ms/op
measureVolatile           2  1000000  avgt   10    60.873 ±  4.390  ms/op
measureScala2             2  2000000  avgt   10   204.885 ±  5.771  ms/op
measureVolatile           2  2000000  avgt   10   125.163 ± 14.573  ms/op
measureScala2             2  5000000  avgt   10   522.363 ± 11.232  ms/op
measureVolatile           2  5000000  avgt   10   321.897 ± 44.718  ms/op

measureScala2             4  1000000  avgt   10   175.387 ±  6.119  ms/op
measureVolatile           4  1000000  avgt   10    68.574 ±  1.627  ms/op
measureScala2             4  2000000  avgt   10   347.702 ± 13.258  ms/op
measureVolatile           4  2000000  avgt   10   134.031 ±  2.958  ms/op
measureScala2             4  5000000  avgt   10   888.126 ± 16.495  ms/op
measureVolatile           4  5000000  avgt   10   341.845 ±  3.959  ms/op

measureScala2             8  1000000  avgt   10   219.899 ± 11.106  ms/op
measureVolatile           8  1000000  avgt   10    51.434 ±  0.285  ms/op
measureScala2             8  2000000  avgt   10   433.975 ± 26.804  ms/op
measureVolatile           8  2000000  avgt   10   100.898 ±  1.437  ms/op
measureScala2             8  5000000  avgt   10  1087.304 ± 25.183  ms/op
measureVolatile           8  5000000  avgt   10   271.857 ± 14.559  ms/op
```