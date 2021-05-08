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

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.handler.DiffHandler;

import java.util.List;

/**
 * An implementation of the Hirschberg algorithm to find the longest common subsequence (LCS).
 *
 * <p>Hirschberg proposed a linear space algorithm for the LCS using a divide and conquer approach.
 * This algorithm (Algorithm C) finds the intersecting point of the LCS sequence with the m/2 th row and solve
 * the problem recursively. It solves LCS problem in O(mn) time and in O(m+n) space.
 *
 * <p>
 * See "A linear space algorithm for computing maximal common subsequences"
 *
 * <p>The algorithm has been altered slightly to be able to computed the Shortest Edit Script (SES).
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @link https://www.ics.uci.edu/~dan/pubs/p341-hirschberg.pdf
 */
public final class HirschbergAlgorithm<T> implements DiffAlgorithm<T> {

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = false;

  @Override
  public void diff(List<? extends T> from, List<? extends T> to, DiffHandler<T> handler) {
    // It is more efficient to supply the sizes than retrieve from lists
    algorithmC(from.size(), to.size(), from, to, handler);
  }

  /**
   * Algorithm B as described by Hirschberg
   *
   * @return the last line of the Needleman-Wunsch score matrix
   */
  private static <T> int[] algorithmB(int m, int n, List<? extends T> a, List<? extends T> b) {
    int[][] k = new int[2][n + 1];
    for (int i = 1; i <= m; i++) {
      if (n + 1 >= 0) System.arraycopy(k[1], 0, k[0], 0, n + 1);
      for (int j = 1; j <= n; j++) {
        if (a.get(i - 1).equals(b.get(j - 1))) {
          k[1][j] = k[0][j - 1] + 1;
        } else {
          k[1][j] = Math.max(k[1][j - 1], k[0][j]);
        }
      }
    }
    return k[1];
  }

  /**
   * Algorithm B as described by Hirschberg (in reverse)
   *
   * <p>Implementation note: we traverse the list in reverse, it is more efficient than reversing the lists.
   */
  private static <T> int[] algorithmBRev(int m, int n, List<? extends T> a, List<? extends T> b) {
    int[][] k = new int[2][n + 1];
    for (int i = m - 1; i >= 0; i--) {
      if (n + 1 >= 0) System.arraycopy(k[1], 0, k[0], 0, n + 1);
      for (int j = n - 1; j >= 0; j--) {
        if (a.get(i).equals(b.get(j))) {
          k[1][n - j] = k[0][n - j - 1] + 1;
        } else {
          k[1][n - j] = Math.max(k[1][n - j - 1], k[0][n - j]);
        }
      }
    }
    return k[1];
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
  private static <T> void algorithmC(int m, int n, List<? extends T> a, List<? extends T> b, DiffHandler<T> handler) {
    if (DEBUG) System.out.print("[m=" + m + ",n=" + n + "," + a + "," + b + "] ->");

    if (n == 0) {
      if (DEBUG) System.out.println(" Step1 N=0");
      for (T token : a) {
        handler.handle(Operator.DEL, token);
      }

    } else if (m == 0) {
      if (DEBUG) System.out.println(" Step1 M=0");
      for (T token : b) {
        handler.handle(Operator.INS, token);
      }

    } else if (m == 1) {
      if (DEBUG) System.out.println(" Step1 M=1");
      boolean match = false;
      T a0 = a.get(0);
      for (int j = 0; j < n; j++) {
        if (a0.equals(b.get(j)) && !match) {
          handler.handle(Operator.MATCH, a0);
          match = true;
        } else {
          handler.handle(Operator.INS, b.get(j));
        }
      }
      if (!match) handler.handle(Operator.DEL, a0);

    } else {
      if (DEBUG) System.out.println(" Step2");
      int h = (int) Math.floor(((double) m) / 2);

      int[] l1 = algorithmB(h, n, a.subList(0, h), b);
      int[] l2 = algorithmBRev(m - h, n, a.subList(h, a.size()), b);
      int k = findK(l1, l2, n);

      // Recursive call
      algorithmC(h, k, a.subList(0, h), b.subList(0, k), handler);
      algorithmC(m - h, n - k, a.subList(h, a.size()), b.subList(k, b.size()), handler);
    }
  }

}
