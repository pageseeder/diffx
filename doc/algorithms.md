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

Performance comparing random strings of various lengths with 25% difference.

| Algorithm               |   500 |  1,000 |  2,000 |   5,000 |  10,000 |   20,000 |
| ----------------------- | ----- | ------ | ------ | ------- | ------- | -------- |
| MyersGreedyAlgorithm    |   0ms |  1.8ms |  3.2ms |  19.9ms |  74.3ms |  384.5ms |
| MyersLinearAlgorithm    | 0.2ms |  2.6ms |  7.0ms |  22.6ms |  79.8ms |  342.6ms |
| KumarRanganAlgorithm    | 0.8ms |  2.0ms |  7.8ms |  46.0ms | 174.0ms |  732.6ms |
| HirschbergAlgorithm     | 2.9ms |  5.0ms | 19.8ms | 120.8ms | 479.7ms | 1917.4ms |
| WagnerFischerAlgorithm  | 3.2ms |  7.0ms | 25.7ms | 168.5ms | 720.6ms |          |
