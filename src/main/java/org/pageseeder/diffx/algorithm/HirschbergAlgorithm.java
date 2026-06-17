/*
 * Copyright (c) 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.algorithm;

import org.pageseeder.diffx.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

/**
 * An implementation of the Hirschberg algorithm to find the longest common subsequence (LCS).
 *
 * <p>Hirschberg proposed a linear space algorithm for the LCS using a divide and conquer approach.
 * This algorithm (Algorithm C) finds the intersecting point of the LCS sequence with the m/2 th row and solve
 * the problem recursively. It solves an LCS problem in O(mn) time and in O(m+n) space.
 *
 * <p>See "A linear space algorithm for computing maximal common subsequences"
 *
 * <p>The algorithm has been altered slightly to be able to compute the Shortest Edit Script (SES).
 *
 * <p>Implementation note: uses a {@code Session} object to grow and reuse the four {@code int[]}
 * working arrays across all recursive calls, eliminating the O(n log n) allocation cascade of the
 * naive divide-and-conquer approach.
 *
 * @see <a href="https://www.ics.uci.edu/~dan/pubs/p341-hirschberg.pdf">Algorithm for Computing Maximal Common Subsequences D.S. Hirschberg</a>
 *
 * @author Christophe Lauret
 *
 * @version 1.3.3
 * @since 0.9.0
 */
public final class HirschbergAlgorithm<T> implements DiffAlgorithm<T>, MatchPreferenceConfigurable {

  /**
   * Determines the strategy to compare elements for equality within the diff algorithm.
   */
  private final Equality<T> eq;

  /**
   * Determines which side's element to emit when elements match.
   */
  private boolean preferFrom = false;

  /**
   * Default constructor using token equality.
   */
  public HirschbergAlgorithm() {
    this.eq = T::equals;
  }

  /**
   * Constructor specifying the equality strategy.
   *
   * @param eq The strategy to compare elements for equality.
   */
  public HirschbergAlgorithm(Equality<T> eq) {
    this.eq = eq;
  }

  /**
   * Whether to keep matching elements from the from list (true) or to list (false).
   *
   * @return <code>true</code> if matching elements should be kept from the "from" list,
   *         <code>false</code> otherwise.
   */
  @Override
  public boolean isPreferFrom() {
    return this.preferFrom;
  }

  /**
   * Whether to keep matching elements from the from list (true) or to list (false).
   *
   * @param preferFrom True to keep matching elements from the from list, false to keep from the to list.
   */
  @Override
  public void setPreferFrom(boolean preferFrom) {
    this.preferFrom = preferFrom;
  }

  @Override
  public void diff(List<? extends T> from, List<? extends T> to, DiffHandler<T> handler) {
    List<? extends T> a = (from instanceof RandomAccess) ? from : new ArrayList<>(from);
    List<? extends T> b = (to instanceof RandomAccess) ? to : new ArrayList<>(to);

    Session s = new Session();
    algorithmC(s, a.size(), b.size(), a, 0, b, 0, handler);
  }

  /**
   * Algorithm B as described by Hirschberg.
   *
   * <p>The results are stored in the given output array.
   *
   * @param s       The session object that manages storage for dynamic programming arrays.
   * @param m       The length of the first list (sublist of 'a').
   * @param n       The length of the second list (sublist of 'b').
   * @param a       The first list of elements to compare.
   * @param aOffset The starting index in the first list to consider in this computation.
   * @param b       The second list of elements to compare.
   * @param bOffset The starting index in the second list to consider in this computation.
   * @param out     The output array where the computed longest common subsequence lengths are stored.
   */
  @SuppressWarnings("java:S107")
  private void algorithmB(Session s, int m, int n, List<? extends T> a, int aOffset, List<? extends T> b, int bOffset, int[] out) {
    final int size = n + 1;
    s.ensureDpCapacity(size);

    int[] prev = s.prev;
    int[] curr = s.curr;

    for (int j = 0; j <= n; j++) prev[j] = 0;

    for (int i = 1; i <= m; i++) {
      curr[0] = 0;
      T ai = a.get(aOffset + i - 1);

      for (int j = 1; j <= n; j++) {
        T bj = b.get(bOffset + j - 1);
        if (this.eq.equals(ai, bj)) {
          curr[j] = prev[j - 1] + 1;
        } else {
          int left = curr[j - 1];
          int up = prev[j];
          curr[j] = Math.max(left, up);
        }
      }

      int[] tmp = prev;
      prev = curr;
      curr = tmp;
    }

    System.arraycopy(prev, 0, out, 0, size);
  }

  /**
   * Implements the reverse variant of algorithm B.
   *
   * @param s       The session object that manages storage for dynamic programming arrays.
   * @param m       The length of the first list (sublist of 'a').
   * @param n       The length of the second list (sublist of 'b').
   * @param a       The first list of elements to compare.
   * @param aOffset The starting index in the first list to consider in this computation.
   * @param b       The second list of elements to compare.
   * @param bOffset The starting index in the second list to consider in this computation.
   * @param out     The output array where the computed longest common subsequence lengths are stored.
   */
  @SuppressWarnings("java:S107")
  private void algorithmBRev(Session s, int m, int n, List<? extends T> a, int aOffset, List<? extends T> b, int bOffset, int[] out) {
    final int size = n + 1;
    s.ensureDpCapacity(size);

    int[] prev = s.prev;
    int[] curr = s.curr;

    for (int j = 0; j <= n; j++) prev[j] = 0;

    for (int i = m - 1; i >= 0; i--) {
      curr[0] = 0;
      T ai = a.get(aOffset + i);

      for (int j = n - 1, idx = 1; j >= 0; j--, idx++) {
        T bj = b.get(bOffset + j);
        if (this.eq.equals(ai, bj)) {
          curr[idx] = prev[idx - 1] + 1;
        } else {
          int left = curr[idx - 1];
          int up = prev[idx];
          curr[idx] = Math.max(left, up);
        }
      }

      int[] tmp = prev;
      prev = curr;
      curr = tmp;
    }

    System.arraycopy(prev, 0, out, 0, size);
  }

  /**
   * Find the index of the maximum sum of L1 and L2, as described by Hirschberg.
   */
  private static int findK(int[] l1, int[] l2, int n) {
    int m = 0;
    int k = 0;
    for (int j = 0; j <= n; j++) {
      int s = l1[j] + l2[n - j];
      if (m < s) {
        m = s;
        k = j;
      }
    }
    return k;
  }

  /**
   * Algorithm C as described by Hirschberg.
   *
   * @param s       The session object used to manage intermediate storage arrays during the computation.
   * @param m       The length of the first list (sublist of 'a') to process.
   * @param n       The length of the second list (sublist of 'b') to process.
   * @param a       The first list of elements to compare.
   * @param aOffset The starting index within the first list to consider for the current recursion step.
   * @param b       The second list of elements to compare.
   * @param bOffset The starting index within the second list to consider for the current recursion step.
   * @param handler The handler used to process the computed operations (insert, delete, match).
   */
  @SuppressWarnings({"java:S106", "java:S107", "java:S3776"})
  private void algorithmC(Session s, int m, int n, List<? extends T> a, int aOffset, List<? extends T> b, int bOffset, DiffHandler<T> handler) {
    if (n == 0) {
      for (int i = 0; i < m; i++) {
        handler.handle(Operator.DEL, a.get(aOffset + i));
      }
      return;
    }

    if (m == 0) {
      for (int j = 0; j < n; j++) {
        handler.handle(Operator.INS, b.get(bOffset + j));
      }
      return;
    }

    if (m == 1) {
      int matchIndex = -1;
      T a0 = a.get(aOffset);
      for (int j = 0; j < n; j++) {
        if (this.eq.equals(a0, b.get(bOffset + j))) {
          matchIndex = j;
          break;
        }
      }

      if (matchIndex == -1) {
        for (int j = 0; j < n; j++) {
          handler.handle(Operator.INS, b.get(bOffset + j));
        }
        handler.handle(Operator.DEL, a0);
      } else {
        for (int j = 0; j < matchIndex; j++) {
          handler.handle(Operator.INS, b.get(bOffset + j));
        }
        handler.handle(Operator.MATCH, this.preferFrom ? a0 : b.get(bOffset + matchIndex));
        for (int j = matchIndex + 1; j < n; j++) {
          handler.handle(Operator.INS, b.get(bOffset + j));
        }
      }
      return;
    }

    int h = m / 2;

    final int size = n + 1;
    s.ensureSplitCapacity(size);

    algorithmB(s, h, n, a, aOffset, b, bOffset, s.l1);
    algorithmBRev(s, m - h, n, a, aOffset + h, b, bOffset, s.l2);
    int k = findK(s.l1, s.l2, n);

    algorithmC(s, h, k, a, aOffset, b, bOffset, handler);
    algorithmC(s, m - h, n - k, a, aOffset + h, b, bOffset + k, handler);
  }

  /**
   * Manages and grows the four {@code int[]} working arrays shared across all recursive calls.
   *
   * <p>{@code prev} and {@code curr} are swapped in place by algorithm B for the two-row DP
   * computation. {@code l1} and {@code l2} hold the forward and reverse score vectors used by
   * algorithm C to locate the split point before recursing.
   */
  private static final class Session {
    int[] prev = new int[0];
    int[] curr = new int[0];
    int[] l1 = new int[0];
    int[] l2 = new int[0];

    void ensureDpCapacity(int size) {
      if (this.prev.length < size) this.prev = new int[size];
      if (this.curr.length < size) this.curr = new int[size];
    }

    void ensureSplitCapacity(int size) {
      if (this.l1.length < size) this.l1 = new int[size];
      if (this.l2.length < size) this.l2 = new int[size];
    }
  }

}
