/*
 * Copyright 2010-2015 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.core;

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.event.Token;
import org.pageseeder.diffx.handler.DiffHandler;

import java.util.List;

/**
 * An implementation of the Hirschberg algorithm algorithm to find the longest common subsequence (LS).
 *
 * <p>Hirschberg proposed a linear space algorithm for the LCS using a divide and conquer approach.
 * This algorithm (Algorithm C) finds the intersecting point of the LCS sequence with the m/2 th row and solve
 * the problem recursively. It solves LCS problem in O(mn) time and in O(m+n) space.
 *
 * See "A linear space algorithm for computing maximal common subsequences"
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class HirschbergAlgorithm implements DiffAlgorithm {

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = false;

  @Override
  public void diff(List<? extends Token> first, List<? extends Token> second, DiffHandler handler) {
    // It is more efficient to supply the sizes than retrieve from lists
    algorithmC(first.size(), second.size(), first, second, handler);
  }

  /**
   * Algorithm B as described by Hirschberg
   *
   * @return the last line of the Needleman-Wunsch score matrix
   */
  private static int[] algorithmB(int m, int n, List<? extends Token> a, List<? extends Token> b) {
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
   */
  private static int[] algorithmBRev(int m, int n, List<? extends Token> a, List<? extends Token> b) {
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
  private static void algorithmC(int m, int n, List<? extends Token> a, List<? extends Token> b, DiffHandler handler) {
    if (DEBUG) System.out.print("[m=" + m + ",n=" + n + "," + a + "," + b + "] ->");

    if (n == 0) {
      if (DEBUG) System.out.println(" Step1 N=0");
      for (Token token : a) {
        handler.handle(Operator.INS, token);
      }

    } else if (m == 0) {
      if (DEBUG) System.out.println(" Step1 M=0");
      for (Token token : b) {
        handler.handle(Operator.DEL, token);
      }

    } else if (m == 1) {
      if (DEBUG) System.out.println(" Step1 M=1");
      boolean match = false;
      Token a0 = a.get(0);
      for (int j = 0; j < n; j++) {
        if (a0.equals(b.get(j)) && !match) {
          handler.handle(Operator.MATCH, a0);
          match = true;
        } else {
          handler.handle(Operator.DEL, b.get(j));
        }
      }
      if (!match) handler.handle(Operator.INS, a0);

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
