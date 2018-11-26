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

## LazyValsV2

```
sbt:dotty-benchmark> jmh:run LazyValsV2.InitializedAccess

Benchmark  Mode  Cnt  Score   Error  Units
measureV1  avgt   10  2.419 ± 0.122  ns/op
measureV2  avgt   10  3.038 ± 0.040  ns/op
```

```
sbt:dotty-benchmark> jmh:run LazyValsV2.UncontendedInitialization

Benchmark  Mode  Cnt   Score   Error  Units
measureV1  avgt   10  22.582 ± 0.684  ns/op
measureV2  avgt   10  22.432 ± 0.158  ns/op
```

---------

## Experiment

```
sbt:dotty-benchmark> jmh:run LazyValsV2.InitializedAccess

Benchmark     Mode  Cnt  Score   Error  Units
measureV0     avgt   10  2.288 ± 0.064  ns/op
measureV1     avgt   10  2.524 ± 0.039  ns/op
measureV1Bis  avgt   10  2.418 ± 0.067  ns/op
measureV1AFU  avgt   10  2.409 ± 0.041  ns/op
measureV2     avgt   10  3.275 ± 0.109  ns/op
measureV2Bis  avgt   10  2.873 ± 0.039  ns/op
measureV3     avgt   10  2.479 ± 0.111  ns/op
```

```
sbt:dotty-benchmark> jmh:run LazyValsV2.UncontendedInitialization

Benchmark     Mode  Cnt   Score   Error  Units
measureV0     avgt   10  25.752 ± 2.359  ns/op
measureV1     avgt   10  25.003 ± 3.297  ns/op
measureV1Bis  avgt   10  21.575 ± 0.587  ns/op
measureV1AFU  avgt   10  20.811 ± 0.485  ns/op
measureV2     avgt   10  23.140 ± 0.634  ns/op
measureV2Bis  avgt   10  23.920 ± 0.058  ns/op
measureV3     avgt   10  25.049 ± 0.733  ns/op
```


```
sbt:dotty-benchmark> jmh:run LazyValsV2.ContendedInitialization

Benchmark     (nThreads)   Mode  Cnt    Score   Error  Units

measureV1              2   avgt   10  140.491 ± 4.583  ms/op
measureV1              4   avgt   10  222.325 ± 6.265  ms/op
measureV1              8   avgt   10  219.948 ± 4.976  ms/op

measureV1Bis           2   avgt   10  125.305 ± 1.213  ms/op
measureV1Bis           4   avgt   10  182.845 ± 4.319  ms/op
measureV1Bis           8   avgt   10  196.730 ± 2.871  ms/op

measureV1AFU           2   avgt   10  145.055 ± 2.134  ms/op
measureV1AFU           4   avgt   10  187.982 ± 2.687  ms/op
measureV1AFU           8   avgt   10  199.753 ± 4.896  ms/op

// single shared lock
measureV2              2   avgt   10  108.820 ± 1.103  ms/op
measureV2              4   avgt   10  118.227 ± 2.606  ms/op
measureV2              8   avgt   10  112.733 ± 2.269  ms/op

// 1 lock per class
measureV2              2   avgt   10  133.578 ± 4.935  ms/op
measureV2              4   avgt   10  141.516 ± 4.939  ms/op
measureV2              8   avgt   10  203.674 ± 4.344  ms/op

// with a fast pass
measureV2Bis           2   avgt   10  134.790 ±  6.605  ms/op
measureV2Bis           4   avgt   10  140.413 ±  2.096  ms/op
measureV2Bis           8   avgt   10  228.924 ± 11.743  ms/op
```
