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
import org.pageseeder.diffx.api.Operator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * An implementation of the greedy algorithm as outlined in Eugene Myers' paper
 * "An O(ND) Difference Algorithm and its Variations".
 *
 * @param <T> The type of token being compared
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @see <a href="https://neil.fraser.name/writing/diff/myers.pdf">An O(ND) Difference Algorithm and its Variations</a>
 * @see <a href="http://simplygenius.net/Article/DiffTutorial1">Myers' Diff Algorithm: The basic greedy algorithm</a>
 */
public final class MyersGreedyAlgorithm<T> implements DiffAlgorithm<T> {

  @Override
  public void diff(List<? extends T> from, List<? extends T> to, DiffHandler<T> handler) {
    MyersGreedyAlgorithm.Instance<T> instance = new MyersGreedyAlgorithm.Instance<>(from, to);
    List<Snake> snakes = instance.computePath();
    handle(from, to, handler, snakes);
  }

  /**
   * Handles the results of the diff by following the snakes.
   */
  private void handle(List<? extends T> a, List<? extends T> b, DiffHandler<T> handler, List<Snake> snakes) {
    int x = 0;
    int y = 0;
    for (Snake snake : snakes) {
      Point start = snake.getStart();
      while (x < start.x()) {
        handler.handle(Operator.DEL, a.get(x));
        x++;
      }
      while (y < start.y()) {
        handler.handle(Operator.INS, b.get(y));
        y++;
      }
      for (int i = 0; i < snake.length(); i++) {
        handler.handle(Operator.MATCH, a.get(x));
        x++;
        y++;
      }
    }
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
     * <p>The solution is a list of snakes connected to each other and forming the path from (0,0) to (N,M)
     *
     * @return the corresponding list of snakes
     * @throws IllegalStateException If no solution was found.
     */
    private List<Snake> computePath() {
      Vector vector = Vector.createGreedy(this.sizeA, this.sizeB);
      List<Vector> vectors = new ArrayList<>();

      // Maximum length for the path (N + M)
      final int max = this.sizeA + this.sizeB;

      // Find the endpoint of the furthest reaching D-path in diagonal k
      boolean found = false;
      for (int d = 0; d <= max; d++) {
        found = forward(vector, d);
        vectors.add(vector.snapshot(d));
        if (found) {
          break;
        }
      }

      if (!found)
        throw new IllegalStateException("Unable to find a solution!");

      // Compute the snakes from the vectors
      return solve(vectors);
    }

    /**
     * @return the last snake when a solution has been found.
     */
    private boolean forward(Vector vector, int d) {
      for (int k = -d; k <= d; k += 2) {
        // DOWN (insertion) or RIGHT (deletion)
        boolean down = (k == -d || (k != d && vector.getX(k - 1) < vector.getX(k + 1)));

        // To get to line k, we move DOWN (k+1) or RIGHT (k-1)
        int x = down ? vector.getX(k + 1) : vector.getX(k - 1) + 1;
        int y = x - k;

        // Follow diagonals
        while (x < this.sizeA && y < this.sizeB && this.a.get(x).equals(this.b.get(y))) {
          x++;
          y++;
        }

        // Save end points
        vector.setX(k, x);

        // Check if we've reached the end
        if (x >= this.sizeA && y >= this.sizeB) {
          return true;
        }
      }

      return false;
    }

    /**
     * @throws IllegalStateException If no solution could be found
     */
    private List<Snake> solve(List<Vector> vectors) {
      LinkedList<Snake> snakes = new LinkedList<>();
      Point target = new Point(this.sizeA, this.sizeB);

      // We go backwards following the vectors to get the snakes
      for (int d = vectors.size() - 1; target.x() > 0 || target.y() > 0; d--) {
        Vector vector = vectors.get(d);
        int k = target.x() - target.y();
        int xEnd = vector.getX(k);
        int yEnd = xEnd - k;

        if (target.isNotSame(xEnd, yEnd))
          throw new IllegalStateException("No solution for d:" + d + " k:" + k + " p:" + target + " V:( " + xEnd + ", " + yEnd + " )");

        boolean down = (k == -d || (k != d && vector.getX(k - 1) < vector.getX(k + 1)));
        int xStart = down ? vector.getX(k + 1) : vector.getX(k - 1);
        int yStart = xStart - (down ? k + 1 : k - 1);
        int matching = Math.min(xEnd - xStart, yEnd - yStart);

        // Only include non-empty snakes and the last one
        if (matching > 0 || snakes.isEmpty()) {
          Snake snake = new Snake(new Point(target.x() - matching, target.y() - matching), matching);
          snakes.addFirst(snake);
        }

        target = new Point(xStart, Math.max(yStart, 0));
      }
      return snakes;
    }

  }

}
