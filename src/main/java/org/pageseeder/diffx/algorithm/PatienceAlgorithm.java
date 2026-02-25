/*
 * Copyright 2010-2026 Allette Systems (Australia)
 *    http://www.allette.com.au
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

import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Equality;
import org.pageseeder.diffx.api.Operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Patience Diff implementation (2025 variant) with Myers greedy fallback.
 *
 * <p>This algorithm identifies unique elements in both sequences, computes the
 * longest increasing subsequence of those unique matches, and recurses on the
 * gaps. When no unique anchors are found, it falls back to a Myers greedy diff.</p>
 *
 * <p><b>Note:</b> This implementation uses {@code equals}/{@code hashCode} for
 * uniqueness detection. If a custom {@link Equality} is provided, it should be
 * consistent with {@code equals} to avoid unexpected behavior.</p>
 *
 * @param <T> The type of token being compared
 *
 * @author Christophe Lauret
 *
 * @version 1.3.3
 * @since 1.3.3
 */
public final class PatienceAlgorithm<T> implements DiffAlgorithm<T> {

  /**
   * Determines the strategy to compare elements for equality within the diff algorithm.
   */
  private final Equality<T> eq;

  /**
   * Fallback algorithm used when no unique anchors are found.
   */
  private final DiffAlgorithm<T> fallback;

  /**
   * Default constructor using token equality.
   */
  public PatienceAlgorithm() {
    this.eq = T::equals;
    this.fallback = new MyersGreedyAlgorithm<>(this.eq);
  }

  /**
   * Constructor specifying the equality strategy.
   *
   * @param eq The strategy to compare elements for equality.
   */
  public PatienceAlgorithm(Equality<T> eq) {
    this.eq = eq;
    this.fallback = new MyersGreedyAlgorithm<>(this.eq);
  }

  @Override
  public void diff(List<? extends T> from, List<? extends T> to, DiffHandler<T> handler) {
    diffRange(from, 0, from.size(), to, 0, to.size(), handler);
  }

  private void diffRange(List<? extends T> a, int aStart, int aEnd,
                         List<? extends T> b, int bStart, int bEnd,
                         DiffHandler<T> handler) {
    if (aStart >= aEnd) {
      for (int i = bStart; i < bEnd; i++) {
        handler.handle(Operator.INS, b.get(i));
      }
      return;
    }
    if (bStart >= bEnd) {
      for (int i = aStart; i < aEnd; i++) {
        handler.handle(Operator.DEL, a.get(i));
      }
      return;
    }

    int prefix = 0;
    int maxPrefix = Math.min(aEnd - aStart, bEnd - bStart);
    while (prefix < maxPrefix && this.eq.equals(a.get(aStart + prefix), b.get(bStart + prefix))) {
      handler.handle(Operator.MATCH, b.get(bStart + prefix));
      prefix++;
    }

    aStart += prefix;
    bStart += prefix;

    if (aStart >= aEnd || bStart >= bEnd) {
      diffRange(a, aStart, aEnd, b, bStart, bEnd, handler);
      return;
    }

    int suffix = 0;
    int maxSuffix = Math.min(aEnd - aStart, bEnd - bStart);
    while (suffix < maxSuffix
        && this.eq.equals(a.get(aEnd - 1 - suffix), b.get(bEnd - 1 - suffix))) {
      suffix++;
    }

    int aMidEnd = aEnd - suffix;
    int bMidEnd = bEnd - suffix;

    if (aStart >= aMidEnd || bStart >= bMidEnd) {
      diffRange(a, aStart, aMidEnd, b, bStart, bMidEnd, handler);
    } else {
      List<Match> anchors = uniqueAnchors(a, aStart, aMidEnd, b, bStart, bMidEnd);
      if (anchors.isEmpty()) {
        this.fallback.diff(a.subList(aStart, aMidEnd), b.subList(bStart, bMidEnd), handler);
      } else {
        int aPos = aStart;
        int bPos = bStart;
        for (Match anchor : anchors) {
          diffRange(a, aPos, anchor.aIndex, b, bPos, anchor.bIndex, handler);
          handler.handle(Operator.MATCH, b.get(anchor.bIndex));
          aPos = anchor.aIndex + 1;
          bPos = anchor.bIndex + 1;
        }
        diffRange(a, aPos, aMidEnd, b, bPos, bMidEnd, handler);
      }
    }

    for (int i = 0; i < suffix; i++) {
      handler.handle(Operator.MATCH, b.get(bMidEnd + i));
    }
  }

  private List<Match> uniqueAnchors(List<? extends T> a, int aStart, int aEnd,
                                    List<? extends T> b, int bStart, int bEnd) {
    Map<T, Occurrence> aOcc = new HashMap<>();
    Map<T, Occurrence> bOcc = new HashMap<>();

    for (int i = aStart; i < aEnd; i++) {
      T token = a.get(i);
      Occurrence occ = aOcc.get(token);
      if (occ == null) {
        aOcc.put(token, new Occurrence(i));
      } else {
        occ.count++;
      }
    }

    for (int i = bStart; i < bEnd; i++) {
      T token = b.get(i);
      Occurrence occ = bOcc.get(token);
      if (occ == null) {
        bOcc.put(token, new Occurrence(i));
      } else {
        occ.count++;
      }
    }

    List<Match> matches = new ArrayList<>();
    for (Map.Entry<T, Occurrence> entry : aOcc.entrySet()) {
      Occurrence aEntry = entry.getValue();
      if (aEntry.count == 1) {
        Occurrence bEntry = bOcc.get(entry.getKey());
        if (bEntry != null && bEntry.count == 1) {
          matches.add(new Match(aEntry.index, bEntry.index));
        }
      }
    }

    if (matches.isEmpty()) return Collections.emptyList();

    matches.sort((m1, m2) -> Integer.compare(m1.aIndex, m2.aIndex));
    return longestIncreasingByB(matches);
  }

  private List<Match> longestIncreasingByB(List<Match> matches) {
    int n = matches.size();
    int[] tails = new int[n];
    int[] tailsIndex = new int[n];
    int[] prev = new int[n];
    int len = 0;

    for (int i = 0; i < n; i++) {
      int bIndex = matches.get(i).bIndex;
      int pos = lowerBound(tails, len, bIndex);
      tails[pos] = bIndex;
      tailsIndex[pos] = i;
      prev[i] = pos > 0 ? tailsIndex[pos - 1] : -1;
      if (pos == len) len++;
    }

    List<Match> lis = new ArrayList<>(len);
    int idx = tailsIndex[len - 1];
    while (idx >= 0) {
      lis.add(matches.get(idx));
      idx = prev[idx];
    }
    Collections.reverse(lis);
    return lis;
  }

  private static int lowerBound(int[] values, int length, int target) {
    int lo = 0;
    int hi = length;
    while (lo < hi) {
      int mid = (lo + hi) >>> 1;
      if (values[mid] < target) {
        lo = mid + 1;
      } else {
        hi = mid;
      }
    }
    return lo;
  }

  private static final class Occurrence {
    private int count = 1;
    private final int index;

    private Occurrence(int index) {
      this.index = index;
    }
  }

  private static final class Match {
    private final int aIndex;
    private final int bIndex;

    private Match(int aIndex, int bIndex) {
      this.aIndex = aIndex;
      this.bIndex = bIndex;
    }
  }
}
