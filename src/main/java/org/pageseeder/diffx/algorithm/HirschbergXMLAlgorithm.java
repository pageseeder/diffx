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
import org.pageseeder.diffx.handler.MuxHandler;
import org.pageseeder.diffx.token.*;

import java.util.Arrays;
import java.util.List;

/**
 * An implementation of the Hirschberg algorithm algorithm to find the longest common subsequence (LS).
 *
 * <p>Hirschberg proposed a linear space algorithm for the LCS using a divide and conquer approach.
 * This algorithm (Algorithm C) finds the intersecting point of the LCS sequence with the m/2 th row and solve
 * the problem recursively. It solves LCS problem in O(mn) time and in O(m+n) space.
 * <p>
 * See "A linear space algorithm for computing maximal common subsequences"
 *
 * <p>This experimental version attempts to align XML tokens to apply this algorithm to XML sequences.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class HirschbergXMLAlgorithm implements DiffAlgorithm {

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = true;

  @Override
  public void diff(List<? extends Token> first, List<? extends Token> second, DiffHandler handler) {
    Instance instance = new Instance(first, second, handler);
    // It is more efficient to supply the sizes than retrieve from lists
    instance.algorithmC(first.size(), second.size(), first, second, handler);
  }

  private static class Instance {

    private final List<? extends Token> first;
    private final List<? extends Token> second;
    private final DiffHandler handler;

    ElementState estate = new ElementState();

    Instance(List<? extends Token> first, List<? extends Token> second, DiffHandler handler) {
      this.first = first;
      this.second = second;
      this.handler = handler;
    }

    public void process() {
      MuxHandler actual = new MuxHandler(this.handler, this.estate);
      System.out.println("--------------------------------");
      algorithmC(this.first.size(), this.second.size(), this.first, this.second, actual);
    }

    private static int score(Token from, Token to, boolean rev) {
//      if (!rev && to instanceof StartElementToken) return 2;
//      if (rev && to instanceof EndElementToken) return 2;
//    if (to instanceof AttributeToken) return 2;
      return 1;
    }

    /**
     * Algorithm B as described by Hirschberg
     *
     * @return the last line of the Needleman-Wunsch score matrix
     */
    private int[] algorithmB(int m, int n, List<? extends Token> a, List<? extends Token> b) {
      System.out.print("__Fwd: ");
      int[][] k = new int[2][n + 1];
      for (int i = 1; i <= m; i++) {
        if (n + 1 >= 0) System.arraycopy(k[1], 0, k[0], 0, n + 1);
        for (int j = 1; j <= n; j++) {
          System.out.print(" "+a.get(i - 1)+"|"+b.get(j - 1)+" ");
          // TODO use state to check if allowed
          if (a.get(i - 1).equals(b.get(j - 1))) {
            k[1][j] = k[0][j - 1] + score(null, a.get(i-1), false);
          } else {
            k[1][j] = Math.max(k[1][j - 1], k[0][j]);
          }
        }
      }
      System.out.println();
      return k[1];
    }

    /**
     * Algorithm B as described by Hirschberg (in reverse)
     */
    private int[] algorithmBRev(int m, int n, List<? extends Token> a, List<? extends Token> b) {
      System.out.print("__Rev ");
      int[][] k = new int[2][n + 1];
      for (int i = m - 1; i >= 0; i--) {
        if (n + 1 >= 0) System.arraycopy(k[1], 0, k[0], 0, n + 1);
        for (int j = n - 1; j >= 0; j--) {
          System.out.print(" "+a.get(i)+"|"+b.get(j)+" ");
          // TODO use state to check if allowed
          if (a.get(i).equals(b.get(j))) {
            k[1][n - j] = k[0][n - j - 1] + score(null, a.get(i), true);
          } else {
            k[1][n - j] = Math.max(k[1][n - j - 1], k[0][n - j]);
          }
        }
      }
      System.out.println();
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
    private void algorithmC(int m, int n, List<? extends Token> a, List<? extends Token> b, DiffHandler handler) {
      if (DEBUG) System.out.print("[m=" + m + ",n=" + n + "," + a + "," + b + "] ->");

      if (n == 0) {
        if (DEBUG) System.out.println(" Step1 N=0 -> +"+a);
        for (Token token : a) {
          handler.handle(Operator.INS, token);
        }

      } else if (m == 0) {
        if (DEBUG) System.out.println(" Step1 M=0 -> -"+b);
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
        System.out.println("L1"+ Arrays.toString(l1));
        System.out.println("L2"+Arrays.toString(l2));
        int k = findK(l1, l2, n);

        // Recursive call
        algorithmC(h, k, a.subList(0, h), b.subList(0, k), handler);
        algorithmC(m - h, n - k, a.subList(h, a.size()), b.subList(k, b.size()), handler);
      }
    }

  }

}
