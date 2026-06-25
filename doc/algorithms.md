Algorithms
===

Diffx provides multiple implementations of the Shortest Edit Script (SES) / Longest Common Subsequence (LCS) problem.
Each algorithm has different performance characteristics, memory requirements, and trade-offs.

Generic algorithms
---

Generic algorithms work on any token type by checking equality between tokens. They report matches, insertions,
and deletions but are not XML-aware — the resulting token sequence may not always produce well-formed XML.

### `WuAlgorithm`

Based on Sun Wu, Udi Manber, Gene Myers and Webb Miller's *"An O(NP) sequence comparison algorithm"*, this algorithm
uses a diagonal frontier approach.

- **Time:** O(NP) where P is the number of deletions in the SES
- **Space:** O(D^2) where D is the edit distance
- **Best for:** Similar sequences of any size. Faster than Myers when the sequences are close in length.

### `MyersGreedyAlgorithm`

Based on Eugene Myers' paper *"An O(ND) Difference Algorithm and its Variations"*, this is the default generic
algorithm. It computes forward snakes greedily and then backtraces to produce the edit script.

- **Time:** O((M+N)D) where D is the edit distance
- **Space:** O(D^2) (stores frontier arrays for each D)
- **Best for:** Similar sequences. Performance degrades with more differences but is competitive up to moderate edit distances.

### `MyersLinearAlgorithm`

Also based on Eugene Myers' paper, this implements the linear space refinement using a divide-and-conquer strategy.
It trades a small amount of speed for significantly better memory usage on large inputs.

- **Time:** O((M+N)D) where D is the edit distance
- **Space:** O(M+N) — linear space
- **Best for:** Large sequences where memory is a concern. Slightly slower than the greedy variant but scales better.

### `PatienceAlgorithm`

Based on the Patience Diff algorithm, which identifies elements that are unique to both sequences, computes the
Longest Increasing Subsequence (LIS) over those unique matches, then recurses on the gaps between anchors. Falls
back to Myers greedy when no unique anchors are found.

- **Time:** Varies — LIS over unique matches + recursive fallback
- **Space:** Varies
- **Best for:** Sequences with a good distribution of unique tokens. Tends to produce more human-readable diffs by anchoring on unique elements.

### `HistogramAlgorithm`

Based on the Histogram diff technique (as used in JGit/Git), this algorithm identifies low-frequency tokens as anchors,
computes the LIS over them, and recurses on the gaps. Falls back to Myers greedy when no suitable anchors are found.

- **Time:** Varies — anchoring + LIS + recursive fallback
- **Space:** Varies
- **Best for:** Sequences with repeated content where low-frequency tokens provide natural alignment points.

### `KumarRanganAlgorithm`

Based on S. Kiran Kumar and C. Pandu Rangan's *"A linear space algorithm for the LCS problem"* (Acta Informatica, 1987).
This algorithm generates the edit script in linear space.

- **Time:** O(MN) worst case
- **Space:** O(M+N) — linear space
- **Best for:** Sequences with many differences, where its performance is more predictable than Myers-based algorithms. However, it is slower than Myers for similar sequences.

### `HirschbergAlgorithm`

Based on Dan Hirschberg's paper *"A linear space algorithm for computing maximal common subsequences"*, this algorithm
uses dynamic programming combined with a divide-and-conquer approach. It solves the LCS problem in O(MN) time and
O(M+N) space. This implementation extends the original algorithm to report matches as well as differences.

- **Time:** O(MN)
- **Space:** O(M+N) — linear space
- **Best for:** Situations where memory is very constrained. Performance is constant regardless of the number of differences, but baseline speed is slow. Allocates very little memory.

### `WagnerFischerAlgorithm`

Based on dynamic programming, this algorithm computes a full comparison matrix to find the shortest edit script.
It is the simplest implementation but requires O(MN) time and space, and does not scale well for large inputs.

- **Time:** O(MN)
- **Space:** O(MN) — full matrix
- **Best for:** Small sequences or educational/reference purposes. Not recommended for production use with large inputs due to quadratic memory usage.

XML-specific algorithms
---

XML-specific algorithms take into account XML structure to ensure that the resulting token sequence is well-formed.
They only accept `XMLToken` implementations.

### `MyersGreedyXMLAlgorithm`

An adaptation of Myers' greedy algorithm that is XML-aware. It enforces structural XML validity during computation
by tracking the element stack.

- **Time:** O((M+N)D) where D is the edit distance
- **Space:** O(D^2)
- **Best for:** XML sequences where the generic algorithm cannot produce a well-formed result.

### `MatrixXMLAlgorithm`

An adaptation of the classic dynamic programming algorithm for XML. Uses a full matrix combined with an element stack
to eliminate invalid XML paths during computation.

- **Time:** O(MN)
- **Space:** O(MN) — full matrix
- **Threshold:** A configurable threshold (default: 64,000,000 comparisons) prevents the algorithm from exceeding system capacity. A `DataLengthException` is thrown when exceeded.
- **Best for:** Fallback when `MyersGreedyXMLAlgorithm` cannot find a solution. Supports sequence slicing (removing common prefix/suffix) to reduce the comparison space.


Recommendations
---

For most use cases, the `OptimisticXMLProcessor` is the recommended entry point (see [architecture](architecture.md)).
It uses a fast generic algorithm first, applies the `PostXMLFixer` to reorder tokens, and only falls back to
an XML-specific algorithm when the result is not well-formed.

When choosing a generic algorithm directly:

| Scenario                              | Recommended algorithm    |
| ------------------------------------- | ------------------------ |
| General purpose (default)             | `WuAlgorithm`            |
| Similar sequences, small edit distance | `WuAlgorithm` or `MyersGreedyAlgorithm` |
| Large sequences, memory constrained   | `MyersLinearAlgorithm`   |
| Human-readable diffs                  | `PatienceAlgorithm`      |
| Sequences with many repeated tokens   | `HistogramAlgorithm`     |
| Many differences, predictable time    | `KumarRanganAlgorithm`   |
| Small sequences, simplicity           | `WagnerFischerAlgorithm` |


Complexity summary
---

| Algorithm              | Time       | Space  | Difference-sensitive |
| ---------------------- | ---------- | ------ | -------------------- |
| WuAlgorithm            | O(NP)      | O(D^2) | Yes                  |
| MyersGreedyAlgorithm   | O((M+N)D)  | O(D^2) | Yes                  |
| MyersLinearAlgorithm   | O((M+N)D)  | O(M+N) | Yes                  |
| PatienceAlgorithm      | Varies     | Varies | Yes                  |
| HistogramAlgorithm     | Varies     | Varies | Yes                  |
| KumarRanganAlgorithm   | O(MN)      | O(M+N) | Moderate             |
| HirschbergAlgorithm    | O(MN)      | O(M+N) | No                   |
| WagnerFischerAlgorithm | O(MN)      | O(MN)  | No                   |
| MyersGreedyXMLAlgorithm | O((M+N)D)  | O(D^2) | Yes                  |
| MatrixXMLAlgorithm     | O(MN)      | O(MN)  | No                   |

Where M and N are the lengths of the two sequences and D is the edit distance (number of differences).
"Difference-sensitive" means the algorithm is faster when the sequences are more similar.


Performance benchmarks
---

The following benchmarks were generated using JMH on a single machine. Times are in milliseconds.
The benchmarks compare random character sequences; actual XML performance will vary depending on token
distribution and structure.

### Effect of size

Performance comparing random character sequences of various lengths with 5% difference.

| Algorithm              |   1,000 |   5,000 |  10,000 |    25,000 |
| ---------------------- | ------- | ------- | ------- | --------- |
| WuAlgorithm            |  0.01ms |  0.12ms |  0.47ms |    5.1ms  |
| MyersGreedyAlgorithm   |  0.01ms |  0.17ms |  0.68ms |    5.2ms  |
| PatienceAlgorithm      |  0.02ms |  0.22ms |  0.91ms |    6.0ms  |
| HistogramAlgorithm     |  0.11ms |  0.33ms |  1.12ms |    5.8ms  |
| MyersLinearAlgorithm   |  0.01ms |  0.27ms |  1.26ms |    7.7ms  |
| KumarRanganAlgorithm   |  0.09ms |  2.83ms | 10.33ms |   63.2ms  |
| WagnerFischerAlgorithm |  1.59ms | 43.0ms  | 174.1ms | 1661.7ms  |
| HirschbergAlgorithm    |  1.80ms | 46.6ms  | 187.2ms | 1307.4ms  |

Wu, Myers, Patience, and Histogram all scale well with size. KumarRangan is about 10x slower. Hirschberg and
WagnerFischer are O(MN) and become impractical above ~10,000 tokens.


### Effect of differences

Performance comparing random character sequences of length ~10,000 with varying amounts of difference.

| Algorithm              |    5%   |    25%   |    50%   |    75%   |    95%   |
| ---------------------- | ------- | -------- | -------- | -------- | -------- |
| WuAlgorithm            |  0.5ms  |  19.0ms  |  77.5ms  | 179.8ms  | 248.1ms  |
| MyersGreedyAlgorithm   |  0.7ms  |  19.3ms  |  82.1ms  | 186.1ms  | 245.4ms  |
| PatienceAlgorithm      |  0.9ms  |  22.6ms  |  84.1ms  | 201.9ms  | 245.4ms  |
| HistogramAlgorithm     |  1.1ms  |  22.6ms  |  80.7ms  | 225.5ms  | 255.6ms  |
| MyersLinearAlgorithm   |  1.3ms  |  29.2ms  | 112.4ms  | 250.9ms  | 371.6ms  |
| KumarRanganAlgorithm   | 10.3ms  |  64.3ms  | 144.5ms  | 241.9ms  | 311.3ms  |
| WagnerFischerAlgorithm | 174.1ms | 217.8ms  | 257.1ms  | 288.8ms  | 293.7ms  |
| HirschbergAlgorithm    | 187.2ms | 189.2ms  | 186.5ms  | 195.1ms  | 203.3ms  |

The difference-sensitive algorithms (Wu, Myers, Patience, Histogram) are dramatically faster for similar sequences
but converge towards the O(MN) algorithms as differences increase. Hirschberg and WagnerFischer have nearly
constant performance regardless of similarity.


### Memory allocation

Approximate heap allocation per diff operation at 10,000 characters with 5% difference.

| Algorithm              | Allocation |
| ---------------------- | ---------- |
| HirschbergAlgorithm    |     0.2 MB |
| KumarRanganAlgorithm   |     0.9 MB |
| MyersLinearAlgorithm   |     1.2 MB |
| WuAlgorithm            |     1.5 MB |
| PatienceAlgorithm      |     1.9 MB |
| MyersGreedyAlgorithm   |     1.9 MB |
| HistogramAlgorithm     |     2.4 MB |
| WagnerFischerAlgorithm |    95.6 MB |

The linear-space algorithms (Hirschberg, KumarRangan, MyersLinear) allocate significantly less memory.
WagnerFischer allocates a full O(MN) matrix and is unsuitable for large inputs.
