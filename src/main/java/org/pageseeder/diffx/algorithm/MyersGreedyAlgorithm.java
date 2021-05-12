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
import org.pageseeder.diffx.handler.DiffHandler;

import java.util.ArrayList;
import java.util.List;

import static org.pageseeder.diffx.algorithm.Snake.Direction.DOWN;
import static org.pageseeder.diffx.algorithm.Snake.Direction.RIGHT;

/**
 * An implementation of the greedy algorithm as outlined in Eugene Myers' paper
 * "An O(ND) Difference Algorithm and its Variations".
 *
 * <p>Portions of this code are based on the C# implementation of Nicholas Butler at SimplyGenius.NET
 *
 * @param <T> The type of token being compared
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @see <a href="https://neil.fraser.name/writing/diff/myers.pdf">An O(ND) Difference Algorithm and its Variations</a>
 * @see <a href="http://simplygenius.net/Article/DiffTutorial1">Myers' Diff Algorithm: The basic greedy algorithm</a>
 */
public final class MyersGreedyAlgorithm<T> extends MyersAlgorithm<T> implements DiffAlgorithm<T> {

  @Override
  public void diff(@NotNull List<? extends T> from, @NotNull List<? extends T> to, @NotNull DiffHandler<T> handler) {
    Instance<T> instance = new Instance<>(from, to);
    List<Snake> snakes = instance.computePath();
    handleResults(from, to, handler, snakes);
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
      Snake last = null;

      // Maximum length for the path (N + M)
      final int max = sizeA + sizeB;

      // Find the endpoint of the furthest reaching D-path in diagonal k
      for (int d = 0; d <= max; d++) {
        last = forward(vector, d);
        vectors.add(vector.createCopy(d, true, 0));
        if (last != null) {
          break;
        }
      }

      if (last == null)
        throw new IllegalStateException("Unable to find a solution!");

      // Compute the snakes from the vectors
      return solve(vectors);
    }

    /**
     * @return the last snake when a solution has been found.
     */
    private Snake forward(Vector vector, int d) {
      for (int k = -d; k <= d; k += 2) {
        // DOWN (insertion) or RIGHT (deletion)
        boolean down = (k == -d || (k != d && vector.getX(k - 1) < vector.getX(k + 1)));

        // To get to line k, we move DOWN (k+1) or RIGHT (k-1)
        int xStart = down ? vector.getX(k + 1) : vector.getX(k - 1);
        int yStart = xStart - (down ? k + 1 : k - 1);

        // Calculate end points
        int xEnd = down ? xStart : xStart + 1;
        int yEnd = xEnd - k;

        // Follow diagonals
        int matching = 0;
        while (xEnd < sizeA && yEnd < sizeB && a.get(xEnd).equals(b.get(yEnd))) {
          xEnd++;
          yEnd++;
          matching++;
        }

        // Save end points
        vector.setX(k, xEnd);

        // Check if we've reached the end
        if (xEnd >= sizeA && yEnd >= sizeB) {
          return Snake.create(0, sizeA, 0, sizeB, down ? DOWN : RIGHT, xStart, yStart, 1, matching);
        }
      }

      return null;
    }

    /**
     * @throws IllegalStateException If no solution could be found
     */
    private List<Snake> solve(List<Vector> vectors) {
      List<Snake> snakes = new ArrayList<>();
      Point p = new Point(this.sizeA, this.sizeB);

      for (int d = vectors.size() - 1; p.x() > 0 || p.y() > 0; d--) {
        Vector vector = vectors.get(d);
        int k = p.x() - p.y();
        int xEnd = vector.getX(k);
        int yEnd = xEnd - k;

        if (!p.isSame(xEnd, yEnd))
          throw new IllegalStateException("No solution for d:" + d + " k:" + k + " p:" + p + " V:( " + xEnd + ", " + yEnd + " )");

        Snake solution = createToPoint(p, vector, k, d);

        if (!p.isSame(solution.getXEnd(), solution.getYEnd()))
          throw new IllegalStateException("Missed solution for d:" + d + " k:" + k + " p:" + p + " V:( " + xEnd + ", " + yEnd + " )");

        if (snakes.size() > 0) {
          Snake snake = snakes.get(0);
          // Combine snakes if possible
          if (!snake.append(solution)) {
            snakes.add(0, solution);
          }
        } else {
          snakes.add(0, solution);
        }

        p = solution.getStartPoint();
      }
      return snakes;
    }

  }

  private static <T> Snake createToPoint(Point point, Vector vector, int k, int d) {
    final int aEnd = point.x();
    final int bEnd = point.y();
    boolean down = (k == -d || (k != d && vector.getX(k - 1) < vector.getX(k + 1)));
    int xStart = down ? vector.getX(k + 1) : vector.getX(k - 1);
    int yStart = xStart - (down ? k + 1 : k - 1);
    int xEnd = down ? xStart : xStart + 1;
    int yEnd = xEnd - k;
    int matching = Math.min(aEnd - xEnd, bEnd - yEnd);

    // Create corresponding snake instance
    Snake.Direction direction = down ? Snake.Direction.DOWN : Snake.Direction.RIGHT;
    return Snake.create(0, aEnd, 0, bEnd, direction, xStart, yStart, 1, matching);
  }

}
