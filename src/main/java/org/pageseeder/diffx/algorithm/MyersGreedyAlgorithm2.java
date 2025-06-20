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

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the greedy algorithm as outlined in Eugene Myers' paper
 * "An O(ND) Difference Algorithm and its Variations".
 *
 * <p><b>Implementation note:</b> this alternative version does not compute te snakes: it computes backwards first
 * and reports to the handler during backtrace.
 *
 * @param <T> The type of token being compared
 *
 * @author Christophe Lauret
 * @version 0.9.0
 *
 * @see <a href="https://neil.fraser.name/writing/diff/myers.pdf">An O(ND) Difference Algorithm and its Variations</a>
 * @see <a href="http://simplygenius.net/Article/DiffTutorial1">Myers' Diff Algorithm: The basic greedy algorithm</a>
 */
public final class MyersGreedyAlgorithm2<T> implements DiffAlgorithm<T> {

  @Override
  public void diff(@NotNull List<? extends T> from, @NotNull List<? extends T> to, @NotNull DiffHandler<T> handler) {
    MyersGreedyAlgorithm2.Instance<T> instance = new MyersGreedyAlgorithm2.Instance<>(from, to);
    instance.diff(handler);
  }

  /**
   * An instance of this algorithm for the sequences being compared.
   *
   * @param <T> The type of token
   */
  private static class Instance<T> {

    private final List<? extends T> a;
    private final List<? extends T> b;
    private final int sizeA;
    private final int sizeB;

    Instance(List<? extends T> a, List<? extends T> b) {
      this.a = a;
      this.b = b;
      this.sizeA = a.size();
      this.sizeB = b.size();
    }

    /**
     * Compute the path to generate the shortest edit sequence (SES) between the two lists.
     *
     * @param handler receives notifications of edits
     *
     * @throws IllegalStateException If no solution was found.
     */
    private void diff(DiffHandler<T> handler) {
      // Maximum length for the path (N + M)
      final int max = this.sizeA + this.sizeB;
      final int delta = this.sizeA - this.sizeB;

      Vector vector = Vector.create(this.sizeA, this.sizeB, false, max);
      List<Vector> vectors = new ArrayList<>();

      // Find the endpoint of the furthest reaching D-path in diagonal k
      int diff = -1;
      for (int d = 0; d <= max; d++) {
        diff = reverse(vector, d);
        vectors.add(vector.snapshot(d, false, delta));
        if (diff >= 0) {
          break;
        }
      }

      if (diff < 0)
        throw new IllegalStateException("Unable to find a solution!");

      // Compute the snakes from the vectors
      solve(vectors, handler);
    }

    /**
     * @return the number of differences found
     */
    private int reverse(Vector vector, int d) {
      int delta = this.sizeA - this.sizeB;
      for (int k = -d + delta; k <= d + delta; k += 2) {

        // UP (insertion) or LEFT (deletion)
        boolean up = (k == d + delta || (k != -d + delta && vector.getX(k - 1) < vector.getX(k + 1)));
        int x = up ? vector.getX(k - 1) : vector.getX(k + 1) - 1;
        int y = x - k;

        // Follow diagonals
        while (x > 0 && y > 0 && a.get(x - 1).equals(b.get(y - 1))) {
          x--;
          y--;
        }

        // Save end points
        vector.setX(k, x);

        // Check if we've reached the end
        if (x <= 0 && y <= 0) {
          return d;
        }
      }

      return -1;
    }

    /**
     * @throws IllegalStateException If no solution could be found
     */
    private void solve(@NotNull List<Vector> vectors, DiffHandler<T> handler) {
      Point target = new Point(0, 0);
      final int delta = this.sizeA - this.sizeB;
      int x = 0;
      int y = 0;

      // We are following the vectors to get the snakes
      for (int d = vectors.size() - 1; target.x() < sizeA || target.y() > sizeB; d--) {
        Vector vector = vectors.get(d);
        int k = target.x() - target.y();
        int startX = vector.getX(k);
        int startY = startX - k;

        if (target.isNotSame(startX, startY))
          throw new IllegalStateException("No solution for d:" + d + " k:" + k + " p:" + target + " V:( " + startX + ", " + startY + " )");

        boolean up = (k == d + delta || (k != -d + delta && vector.getX(k - 1) < vector.getX(k + 1)));
        int endX = up ? vector.getX(k - 1) : vector.getX(k + 1);
        int endY = endX - (up ? k - 1 : k + 1);

        int matching = Math.min(endX - startX, endY - startY);
        // Reverse: matching first
        for (int i = 0; i < matching; i++) {
          handler.handle(Operator.MATCH, a.get(x));
          x++;
          y++;
        }
        // Insertions and deletions
        while (x < endX && x < sizeA) {
          handler.handle(Operator.DEL, a.get(x));
          x++;
        }
        while (y < endY && y < sizeB) {
          handler.handle(Operator.INS, b.get(y));
          y++;
        }
        target = new Point(endX, Math.min(endY, sizeB));
      }
      // Insertions and deletions remaining at the end
      while (x < sizeA) {
        handler.handle(Operator.DEL, a.get(x));
        x++;
      }
      while (y < sizeB) {
        handler.handle(Operator.INS, b.get(y));
        y++;
      }
    }
  }

}
