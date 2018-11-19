# Sample Benchmark Project for Dotty

```
sbt:dotty-benchmark> jmh:run WhileLoops

Benchmark              (size)  Mode  Cnt        Score    Error  Units

WhileLoops.nativeSum        1  avgt   10      5.594 ±    0.134  ns/op
WhileLoops.nativeSum       10  avgt   10     28.347 ±    1.527  ns/op
WhileLoops.nativeSum      100  avgt   10    263.616 ±   12.052  ns/op
WhileLoops.nativeSum     1000  avgt   10   2507.955 ±  124.737  ns/op
WhileLoops.nativeSum    10000  avgt   10  25221.132 ± 1268.299  ns/op

WhileLoops.inlineSum        1  avgt   10      6.058 ±    0.168  ns/op
WhileLoops.inlineSum       10  avgt   10     28.696 ±    0.857  ns/op
WhileLoops.inlineSum      100  avgt   10    254.801 ±    5.366  ns/op
WhileLoops.inlineSum     1000  avgt   10   2416.736 ±   42.637  ns/op
WhileLoops.inlineSum    10000  avgt   10  25204.005 ±  607.425  ns/op

WhileLoops.tailRecSum       1  avgt   10     12.287 ±    0.430  ns/op
WhileLoops.tailRecSum      10  avgt   10     43.561 ±    1.468  ns/op
WhileLoops.tailRecSum     100  avgt   10    372.701 ±   10.696  ns/op
WhileLoops.tailRecSum    1000  avgt   10   3348.737 ±  770.799  ns/op
WhileLoops.tailRecSum   10000  avgt   10  30113.856 ± 1142.757  ns/op
```
