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

import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Equality;
import org.pageseeder.diffx.api.Operator;

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
 * <p>
 * See "A linear space algorithm for computing maximal common subsequences"
 *
 * <p>The algorithm has been altered slightly to be able to compute the Shortest Edit Script (SES).
 *
 * @see <a href="https://www.ics.uci.edu/~dan/pubs/p341-hirschberg.pdf">Algorithm for Computing Maximal Common Subsequences D.S. Hirschberg</a>
 *
 * @author Christophe Lauret
 *
 * @version 1.3.3
 * @since 0.9.0
 */
public final class HirschbergAlgorithm<T> implements DiffAlgorithm<T> {

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = false;

  /**
   * Determines the strategy to compare elements for equality within the diff algorithm.
   */
  private final Equality<T> eq;

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

  @Override
  public void diff(List<? extends T> from, List<? extends T> to, DiffHandler<T> handler) {
    // It is more efficient to supply the sizes than retrieve from lists
    List<? extends T> a = (from instanceof RandomAccess) ? from : new ArrayList<>(from);
    List<? extends T> b = (to instanceof RandomAccess) ? to : new ArrayList<>(to);
    algorithmC(a.size(), b.size(), a, b, handler);
  }

  /**
   * Algorithm B as described by Hirschberg
   *
   * @return the last line of the Needleman-Wunsch score matrix
   */
  private int[] algorithmB(int m, int n, List<? extends T> a, int aOffset, List<? extends T> b, int bOffset) {
    int[] prev = new int[n + 1];
    int[] curr = new int[n + 1];
    for (int i = 1; i <= m; i++) {
      curr[0] = 0;
      T ai = a.get(aOffset + i - 1);
      for (int j = 1; j <= n; j++) {
        if (this.eq.equals(ai, b.get(bOffset + j - 1))) {
          curr[j] = prev[j - 1] + 1;
        } else {
          curr[j] = Math.max(curr[j - 1], prev[j]);
        }
      }
      int[] tmp = prev;
      prev = curr;
      curr = tmp;
    }
    return prev;
  }

  /**
   * Algorithm B as described by Hirschberg (in reverse)
   *
   * <p>Implementation note: we traverse the list in reverse, it is more efficient than reversing the lists.
   */
  private int[] algorithmBRev(int m, int n, List<? extends T> a, int aOffset, List<? extends T> b, int bOffset) {
    int[] prev = new int[n + 1];
    int[] curr = new int[n + 1];
    for (int i = m - 1; i >= 0; i--) {
      curr[0] = 0;
      T ai = a.get(aOffset + i);
      for (int j = n - 1; j >= 0; j--) {
        int idx = n - j;
        if (this.eq.equals(ai, b.get(bOffset + j))) {
          curr[idx] = prev[idx - 1] + 1;
        } else {
          curr[idx] = Math.max(curr[idx - 1], prev[idx]);
        }
      }
      int[] tmp = prev;
      prev = curr;
      curr = tmp;
    }
    return prev;
  }

  /**
   * Find the index of the maximum sum of L1 and L2, as described by Hirschberg
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
   * Algorithm C as described by Hirschberg
   */
  @SuppressWarnings("java:S106")
  private void algorithmC(int m, int n, List<? extends T> a, List<? extends T> b, DiffHandler<T> handler) {
    algorithmC(m, n, a, 0, b, 0, handler);
  }

  @SuppressWarnings("java:S106")
  private void algorithmC(int m, int n, List<? extends T> a, int aOffset, List<? extends T> b, int bOffset,
                          DiffHandler<T> handler) {
    if (DEBUG) System.out.print("[m=" + m + ",n=" + n + "] ->");

    if (n == 0) {
      if (DEBUG) System.out.println(" Step1 N=0");
      for (int i = 0; i < m; i++) {
        handler.handle(Operator.DEL, a.get(aOffset + i));
      }

    } else if (m == 0) {
      if (DEBUG) System.out.println(" Step1 M=0");
      for (int j = 0; j < n; j++) {
        handler.handle(Operator.INS, b.get(bOffset + j));
      }

    } else if (m == 1) {
      if (DEBUG) System.out.println(" Step1 M=1");
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
        handler.handle(Operator.MATCH, b.get(bOffset + matchIndex));
        for (int j = matchIndex + 1; j < n; j++) {
          handler.handle(Operator.INS, b.get(bOffset + j));
        }
      }

    } else {
      if (DEBUG) System.out.println(" Step2");
      int h = m / 2;

      int[] l1 = algorithmB(h, n, a, aOffset, b, bOffset);
      int[] l2 = algorithmBRev(m - h, n, a, aOffset + h, b, bOffset);
      int k = findK(l1, l2, n);

      // Recursive call
      algorithmC(h, k, a, aOffset, b, bOffset, handler);
      algorithmC(m - h, n - k, a, aOffset + h, b, bOffset + k, handler);
    }
  }

}
