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
package org.pageseeder.diffx.algorithm;

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.handler.DiffHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the Hirschberg algorithm algorithm to find the common LCS.
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
  public void diff(List<? extends DiffXEvent> first, List<? extends DiffXEvent> second, DiffHandler handler) throws IOException {
    // It is more efficient to supply the sizes than retrieve from lists
    algorithmC(first.size(), second.size(), first, second, handler);
  }

  /**
   * Algorithm B as described by Hirschberg
   *
   * @return the last line of the Needleman-Wunsch score matrix
   */
  private static int[] algorithmB(int m, int n, List<? extends DiffXEvent> a, List<? extends DiffXEvent> b) {
    // Step 1
    int[][] k = new int[2][n+1];
    // Step 2
    for (int i=1; i<=m; i++) {
      // Step 3
      if (n + 1 >= 0) System.arraycopy(k[1], 0, k[0], 0, n + 1);
      // Step 4
      for (int j=1; j<=n; j++) {
        if (a.get(i-1).equals(b.get(j-1))) {
          k[1][j] = k[0][j-1] + 1;
        } else {
          k[1][j] = Math.max(k[1][j-1], k[0][j]);
        }
      }
    }
    // Step 5
    return k[1];
  }

  /**
   * Algorithm C as described by Hirschberg
   */
  private static void algorithmC(int m, int n, List<? extends DiffXEvent> a, List<? extends DiffXEvent> b, DiffHandler handler) throws IOException {
    int i;
    int j;
    if (DEBUG) System.out.print("[m="+m+",n="+n+","+a+","+b+"] ->");

    // Step 1
    if (n == 0) {
      if (DEBUG) System.out.println(" Step1 N=0");
      for (DiffXEvent event : a) {
        handler.handle(Operator.INS, event);
      }

    } else if (m == 1) {
      if (DEBUG) System.out.println(" Step1 M=1");
      boolean match = false;
      DiffXEvent a0 = a.get(0);
      for (j=0; j<n; j++) {
        if (a0.equals(b.get(j))) {
          handler.handle(Operator.MATCH, a0);
          match = true;
        } else {
          handler.handle(Operator.DEL, b.get(j));
        }
      }
      if (!match) handler.handle(Operator.INS, a0);

    // Step 2
    } else {
      if (DEBUG)  System.out.println(" Step2");
      i = (int) Math.floor(((double)m)/2);

      // Step 3
      int[] l1 = algorithmB(i, n, a.subList(0, i), b);
      int[] l2 = algorithmB(m-i, n, reverse(a.subList(i, a.size())), reverse(b));

      // Step 4
      int k = findK(l1, l2, n);

      // Step 5
      algorithmC(i, k, a.subList(0, i), b.subList(0, k), handler);
      algorithmC(m-i, n-k, a.subList(i, a.size()), b.subList(k, b.size()), handler);
    }
  }

  /**
   * Returns the reverse list.
   */
  private static List<? extends DiffXEvent> reverse(List<? extends DiffXEvent> events) {
    // We must use an array list to be able to efficiently access the events as the correct indexes
    List<DiffXEvent> reverse = new ArrayList<>(events.size());
    for (int i = events.size()-1; i >= 0; i--) {
      reverse.add(events.get(i));
    }
    return reverse;
  }

  /**
   * Find the index of the maximum sum of L1 and L2, as described by Hirschberg
   */
  private static int findK(int[] l1, int[] l2, int n) {
    int m = 0;
    int k = 0;
    for (int j=0; j <= n; j++) {
      int s = l1[j]+l2[n-j];
      if (m < s) {
        m = s;
        k = j;
      }
    }
    return k;
  }


}
