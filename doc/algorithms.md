Algorithms
===

Implementations
---

### `MyersGreedyAlgorithm`

Based on Eugene Myers' paper *"An O(ND) Difference Algorithm and its Variations"*, this is the default generic
algorithm as it performs well when there are few differences between the sequences (when they are similar).

### `MyersLinearAlgorithm`

Also based on the same Eugene Myers' paper, this algorithm is a little slower than its greedy counterpart, but scales
better for larger files. It implements a divide-and-conquer strategy as described in the linear space refinement
described in the Myers' paper.

### `MyersGreedyXMLAlgorithm`

This is an adaptation of Myers' greedy algorithm that is XML-aware to ensure that reported events can be used to
produce a well-formed XML.

### `Kumar-RanganAlgorithm`

Based on S. Kiran Kumar and C. Pandu Rangan. *"A linear space algorithm for the LCS problem"*, this algorithm
generates the edit script in linear time and space. This algorithm is not as efficient as Myers' but can perform
better when there are may differences between two sequences.

### `HirschbergAlgorithm`

Based on Dan Hirschberg's paper *"A linear space algorithm for computing maximal common subsequences"*, this is a
linear space algorithm for the LCS that uses dynamic programming combined with a divide and conquer approach to make it
more space effient.

It solves LCS problem in O(mn) time and in O(m+n) space. This implementation extends the original algorithm to report
matches as well as differences.

### `WagnerFischerAlgorithm`

Based on dynamic programming, this algorithm computes all possible comparisons points in the sequences and finds the
shortest edit script. This algorithm requires O(mn) time and space and does not scale well.

A `threshold` parameter can be specified to let the algorithm throw an exception when the number of comparison points is
likely to exceed the capacity of the system.

### `MatrixXMLAlgorithm`

This algorithm is adaptation on the classic dynamic programming algorithm for XML. This algorithm requires O(mn) time
and space and does not scale well.

A `threshold` parameter can be specified to let the algorithm throw an exception when the number of comparison points is
likely to exceed the capacity of the system.


Comparison
---

### Generic algorithms

#### Effect of size

Performance comparing random strings of various lengths with 5% difference.

| Algorithm               |   1,000 |   2,000 |   5,000 |  10,000 |   20,000 |   50,000 |
| ----------------------- | ------- | ------- | ------- | ------- | -------- | -------- |
| MyersGreedyAlgorithm    |   0.0ms |   0.0ms |   2.0ms |   7.0ms |   14.0ms |  212.0ms |
| MyersLinearAlgorithm    |   0.0ms |   0.0ms |   2.0ms |   9.0ms |   26.0ms |   82.0ms |
| KumarRanganAlgorithm    |   1.0ms |   1.0ms |   7.0ms |  30.0ms |  117.0ms |  670.0ms |
| HirschbergAlgorithm     |   3.0ms |  15.0ms |  89.0ms | 359.0ms | 1468.0ms | 9337.0ms |
| WagnerFischerAlgorithm  |   5.0ms |  23.0ms | 135.0ms | 541.0ms | 2150.0ms |      N/A |

Performance comparing random strings of various lengths with 25% difference.

| Algorithm               |   1,000 |   2,000 |   5,000 |  10,000 |   20,000 |   50,000 |
| ----------------------- | ------- | ------- | ------- | ------- | -------- | -------- |
| MyersGreedyAlgorithm    |   0.0ms |   7.0ms |  16.0ms |  62.0ms |  244.0ms | 1841.0ms |
| MyersLinearAlgorithm    |   0.0ms |   3.0ms |  20.0ms |  77.0ms |  302.0ms | 1899.0ms |
| KumarRanganAlgorithm    |   1.0ms |   6.0ms |  64.0ms | 147.0ms |  593.0ms | 3643.0ms |
| HirschbergAlgorithm     |   3.0ms |  14.0ms | 102.0ms | 367.0ms | 1453.0ms | 9345.0ms |
| WagnerFischerAlgorithm  |   5.0ms |  20.0ms | 128.0ms | 516.0ms | 2033.0ms |      N/A |


#### Effect of differences

Myers' algorithms and Kumar-Rangan are sensitive to the number of differences.
Below are the results when comparing strings of length about 10,000.

| Algorithm               |      1% |      5% |     25% |     50% |     75% |     99% |
| ----------------------- | ------- | ------- | ------- | ------- | ------- | ------- |
| MyersGreedyAlgorithm    |   0.1ms |   2.0ms |  55.9ms | 238.0ms | 534.0ms | 758.7ms |
| MyersLinearAlgorithm    |   1.8ms |   5.6ms |  63.9ms | 260.8ms | 604.7ms | 919.9ms |
| KumarRanganAlgorithm    |   5.9ms |  26.0ms | 140.2ms | 307.4ms | 473.8ms | 582.6ms |
| HirschbergAlgorithm     | 341.6ms | 336.7ms | 337.8ms | 337.5ms | 334.2ms | 342.4ms |
| WagnerFischerAlgorithm  | 575.4ms | 526.4ms | 528.3ms | 533.7ms | 543.8ms | 552.7ms |
