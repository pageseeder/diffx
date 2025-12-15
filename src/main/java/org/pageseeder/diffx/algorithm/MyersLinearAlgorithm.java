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

import org.jspecify.annotations.Nullable;
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Equality;

import java.util.ArrayList;
import java.util.List;

import static org.pageseeder.diffx.algorithm.EdgeSnake.Direction.*;

/**
 * An implementation of the linear algorithm as outlined in Eugene Myers' paper
 * "An O(ND) Difference Algorithm and its Variations".
 *
 * <p>Portions of this code are based on the C# implementation of Nicholas Butler at SimplyGenius.NET
 *
 * @param <T> The type of token being compared
 *
 * @author Christophe Lauret
 *
 * @version 1.4.0
 * @since 0.9.0
 *
 * @see <a href="https://neil.fraser.name/writing/diff/myers.pdf">An O(ND) Difference Algorithm and its Variations</a>
 * @see <a href="http://simplygenius.net/Article/DiffTutorial2">Myers' Diff Algorithm: The linear space refinement</a>
 */
public final class MyersLinearAlgorithm<T> extends MyersAlgorithm<T> implements DiffAlgorithm<T> {

  /**
   * Determines the strategy to compare elements for equality within the diff algorithm.
   */
  private final Equality<T> eq;

  /**
   * Default constructor using token equality.
   */
  public MyersLinearAlgorithm() {
    this.eq = T::equals;
  }

  /**
   * Constructor specifying the equality strategy.
   *
   * @param eq The strategy to compare elements for equality.
   */
  public MyersLinearAlgorithm(Equality<T> eq) {
    this.eq = eq;
  }

  @Override
  public void diff(List<? extends T> from, List<? extends T> to, DiffHandler<T> handler) {
    Instance<T> instance = new Instance<>(from, to, this.eq);
    List<EdgeSnake> snakes = instance.computePath();
    handleResults(from, to, handler, snakes);
  }

  static class Instance<T> {

    private final List<? extends T> a;
    private final List<? extends T> b;

    private final Equality<T> eq;

    Instance(List<? extends T> a, List<? extends T> b, Equality<T> eq) {
      this.a = a;
      this.b = b;
      this.eq = eq;
    }

    public List<EdgeSnake> computePath() {
      Vector vForward = Vector.createLinear(this.a.size(), this.b.size(), true);
      Vector vReverse = Vector.createLinear(this.a.size(), this.b.size(), false);
      List<EdgeSnake> snakes = new ArrayList<>();
      List<Vector> forwardVs = new ArrayList<>();
      List<Vector> reverseVs = new ArrayList<>();
      computePath(0, snakes, forwardVs, reverseVs, 0, this.a.size(), 0, this.b.size(), vForward, vReverse);
      return snakes;
    }

    private void computePath(int recursion, List<EdgeSnake> snakes,
                             @Nullable List<Vector> forwardVs, @Nullable List<Vector> reverseVs,
                             int startA, int sizeA,
                             int startB, int sizeB,
                             Vector vForward, Vector vReverse) {

      // Only deletions
      if (sizeB == 0 && sizeA > 0) {
        EdgeSnake right = EdgeSnake.create(startA, sizeA, startB, sizeB, RIGHT, startA, startB, sizeA, 0);
        if (snakes.isEmpty() || !snakes.get(snakes.size() - 1).append(right)) {
          snakes.add(right);
        }
      }

      // Only insertions
      if (sizeA == 0 && sizeB > 0) {
        EdgeSnake down = EdgeSnake.create(startA, sizeA, startB, sizeB, DOWN, startA, startB, sizeB, 0);
        if (snakes.isEmpty() || !snakes.get(snakes.size() - 1).append(down)) {
          snakes.add(down);
        }
      }

      // We're done here
      if (sizeA <= 0 || sizeB <= 0) {
        return;
      }

      // Calculate middle snake
      MiddleSnake middle = middleSnake(startA, sizeA, startB, sizeB, vForward, vReverse, forwardVs, reverseVs);

      if (middle.getDiff() > 1) {
        // Middle snake (D > 1)

        // Solve top left rectangle
        Point xy = middle.isForward() ? middle.snake().getStartPoint() : middle.snake().getEndPoint();
        computePath(recursion + 1, snakes, null, null, startA, xy.x() - startA, startB, xy.y() - startB, vForward, vReverse);

        // Add middle snake to results
        if (snakes.isEmpty() || !snakes.get(snakes.size() - 1).append(middle.snake())) {
          snakes.add(middle.snake());
        }

        // Solve bottom right rectangle
        Point uv = !middle.isForward() ? middle.snake().getStartPoint() : middle.snake().getEndPoint();
        computePath(recursion + 1, snakes, null, null, uv.x(), startA + sizeA - uv.x(), uv.y(), startB + sizeB - uv.y(),
            vForward, vReverse);

      } else {
        // Edge case D=0 (identical) or D=1 (1 insertion or deletion)

        if (middle.isForward()) {
          if (middle.snake().x > startA) {
            if (middle.snake().x - startA != middle.snake().y - startB)
              throw new IllegalStateException("Missed D0 forward");

            EdgeSnake snake = EdgeSnake.create(startA, sizeA, startB, sizeB, DOWN, startA, startB, 0, middle.snake().x - startA);
            if (snakes.isEmpty() || !snakes.get(snakes.size() - 1).append(snake)) {
              snakes.add(snake);
            }
          }

          // Add the middle snake to results
          if (snakes.isEmpty() || !snakes.get(snakes.size() - 1).append(middle.snake())) {
            snakes.add(middle.snake());
          }
        } else {
          // Add the middle snake to results
          if (snakes.isEmpty() || !snakes.get(snakes.size() - 1).append(middle.snake())) {
            snakes.add(middle.snake());
          }

          if (middle.snake().x < startA + sizeA) {
            if (startA + sizeA - middle.snake().x != startB + sizeB - middle.snake().y)
              throw new IllegalStateException("Missed D0 reverse");

            EdgeSnake snake = EdgeSnake.create(startA, sizeA, startB, sizeB, DOWN, middle.snake().x, middle.snake().y, 0, startA + sizeA - middle.snake().x);
            if (snakes.isEmpty() || !snakes.get(snakes.size() - 1).append(snake)) {
              snakes.add(snake);
            }
          }
        }
      }
    }

    /**
     * Calculate the middle snake
     */
    private MiddleSnake middleSnake(int startA, int sizeA, int startB, int sizeB,
                                    Vector vForward, Vector vReverse,
                                    @Nullable List<Vector> forwardVs, @Nullable List<Vector> reverseVs) {
      final int max = (sizeA + sizeB + 1) / 2;
      final int delta = sizeA - sizeB;

      vForward.init(sizeA, sizeB);
      vReverse.init(sizeA, sizeB);

      final boolean deltaIsEven = (delta % 2) == 0;

      for (int d = 0; d <= max; d++) {
        // For k in D to D in steps of 2 Do
        for (int k = -d; k <= d; k += 2) {

          // Find the end of the furthest reaching forward D-path in diagonal k
          boolean down = (k == -d || (k != d && vForward.getX(k - 1) < vForward.getX(k + 1)));
          int xStart = down ? vForward.getX(k + 1) : vForward.getX(k - 1);
          int yStart = xStart - (down ? k + 1 : k - 1);
          int xEnd = down ? xStart : xStart + 1;
          int yEnd = xEnd - k;
          int matching = 0;
          while (xEnd < sizeA && yEnd < sizeB &&
              eq.equals(a.get(xEnd + startA), b.get(yEnd + startB))) {
            xEnd++;
            yEnd++;
            matching++;
          }
          vForward.setX(k, xEnd);

          // If delta is odd and k within [ delta - ( D - 1 ), delta + ( D - 1 ) ]
          if (deltaIsEven || k < delta - (d - 1) || k > delta + (d - 1)) {
            continue;
          }

          // If the path overlaps the furthest reaching reverse (D- 1)-path in diagonal k
          if (vForward.getX(k) < vReverse.getX(k)) {
            continue;
          }

          // Length of an SES is 2D-1, the last snake of the forward path is the middle snake.
          EdgeSnake forward = EdgeSnake.create(startA, sizeA, startB, sizeB, down ? DOWN : RIGHT, xStart + startA, yStart + startB, 1, matching);
          forward.setDiff(d);
          return new MiddleSnake((2 * d) - 1, forward);
        }
        if (forwardVs != null) {
          forwardVs.add(vForward.snapshot(d, true, 0));
        }

        // For k in -D to D in steps of 2 Do
        for (int k = -d + delta; k <= d + delta; k += 2) {

          // Find the end of the furthest reaching reverse D-path in diagonal k + delta
          boolean up = (k == d + delta || (k != -d + delta && vReverse.getX(k - 1) < vReverse.getX(k + 1)));
          int xStart = up ? vReverse.getX(k - 1) : vReverse.getX(k + 1);
          int yStart = xStart - (up ? k - 1 : k + 1);
          int xEnd = up ? xStart : xStart - 1;
          int yEnd = xEnd - k;
          int matching = 0;
          while (xEnd > 0 && yEnd > 0 && a.get(xEnd + startA - 1).equals(b.get(yEnd + startB - 1))) {
            xEnd--;
            yEnd--;
            matching++;
          }
          vReverse.setX(k, xEnd);

          // If delta is even and k + delta within [ -D, D ]
          if (!deltaIsEven || k < -d || k > d) {
            continue;
          }

          // If the path overlaps the furthest reaching forward D-path in diagonal k+D
          if (vReverse.getX(k) > vForward.getX(k)) {
            continue;
          }

          // Length of an SES is 2D. The last snake of the reverse path is the middle snake
          EdgeSnake reverse = EdgeSnake.create(startA, sizeA, startB, sizeB, up ? UP : LEFT, xStart + startA, yStart + startB, 1, matching);
          reverse.setDiff(d);
          return new MiddleSnake(2 * d, reverse);
        }
        if (reverseVs != null) {
          reverseVs.add(vReverse.snapshot(d, false, delta));
        }
      }

      throw new IllegalStateException("Unable to find a middle snake");
    }
  }

  /**
   * Temporary utility class
   */
  private static class MiddleSnake {

    private final int diff;

    private final EdgeSnake snake;

    public MiddleSnake(int diff, EdgeSnake snake) {
      this.diff = diff;
      this.snake = snake;
    }

    /**
     * Returns the number of differences for both calculation directions.
     *
     * <p>A value of 0 indicates that compared elements from the first and the second object are equal.
     * A value of 1 indicates either an insertion from the second object or a deletion from the first object.
     *
     * <p>Moreover, a value of 0 must be a reverse segment, while a value of 1 results from a forward segment.
     *
     * @return The number of differences for both calculation directions
     */
    public int getDiff() {
      return this.diff;
    }

    public boolean isForward() {
      return this.snake.isForward();
    }

    public EdgeSnake snake() {
      return this.snake;
    }

  }
}
