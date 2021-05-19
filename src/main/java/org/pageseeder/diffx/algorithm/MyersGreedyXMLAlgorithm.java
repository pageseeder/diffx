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
import org.pageseeder.diffx.handler.PostXMLFixer;
import org.pageseeder.diffx.token.XMLToken;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of Myers' greedy algorithm adjusted for XML.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @see <a href="https://neil.fraser.name/writing/diff/myers.pdf">An O(ND) Difference Algorithm and its Variations</a>
 */
public final class MyersGreedyXMLAlgorithm extends MyersAlgorithm<XMLToken> implements DiffAlgorithm<XMLToken> {

  private final static boolean DEBUG = false;

  @Override
  public void diff(@NotNull List<? extends XMLToken> from, @NotNull List<? extends XMLToken> to, @NotNull DiffHandler<XMLToken> handler) {
    Instance instance = new Instance(from, to);
    List<EdgeSnake> snakes = instance.computePath();
    // Auto-correct (required until we can fix the attributes)
    PostXMLFixer correction = new PostXMLFixer(handler);
    correction.start();
    handleResults(from, to, correction, snakes);
    correction.end();
    // No autocorrect
//    handleResults(from, to, handler, snakes);
  }

  /**
   * An instance of this algorithm for the sequences being compared.
   */
  private static class Instance {

    private final List<? extends XMLToken> a;
    private final List<? extends XMLToken> b;
    private final int sizeA;
    private final int sizeB;

    Instance(List<? extends XMLToken> a, List<? extends XMLToken> b) {
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
    private List<EdgeSnake> computePath() {
      Vector vector = Vector.createGreedy(this.sizeA, this.sizeB);
      List<Vector> vectors = new ArrayList<>();
      XMLStackMap elements = new XMLStackMap();

      // Maximum length for the path (N + M)
      final int max = sizeA + sizeB;

      // Find the endpoint of the furthest reaching D-path in diagonal k
      boolean found = false;
      for (int d = 0; d <= max; d++) {
        found = forward(vector, elements, d);
        if (DEBUG) System.err.println("D" + d + ": " + elements + " | " + vector + "\n");
        vectors.add(vector.snapshot(d));
        // We've found a path
        if (found) break;
      }
      if (!found) throw new IllegalStateException("Unable to find a solution!");

      // Return the corresponding snakes
      return solve(vectors);
    }

    /**
     * @return the last snake when a solution has been found.
     */
    private boolean forward(Vector vector, XMLStackMap elements, int d) {
      elements.nextDiff();
      for (int k = -d; k <= d; k += 2) {
        int xLeft = k != -d ? vector.getX(k - 1) : 0;
        int xUp = k != d ? vector.getX(k + 1) : 0;
        // DOWN (insertion) or RIGHT (deletion)
        boolean down = k == -d || (k != d && xLeft < xUp);
        // TODO There may be a choice to reach k via k-1 (right) or k+1 (down) if xLeft+1 == xUp
        elements.initK(k, down);

        // Calculate end points
        int x = down ? xUp : xLeft + 1;
        int y = x - k;

        XMLToken editToken = getEditToken(down, x, y);
        if (DEBUG) System.err.print("D" + d + "? K" + k + " " + (down ? "DOWN" : "RIGHT") + " (" + x + "," + y + ")");

        if (editToken == null || elements.isAllowed(k, down ? Operator.INS : Operator.DEL, editToken)) {

          if (editToken != null) {
            Operator op = down ? Operator.INS : Operator.DEL;
            if (DEBUG) System.out.print(" " + op + editToken);
            elements.update(k, op, editToken);
          }

          // Follow diagonals
          while (x < sizeA && y < sizeB && a.get(x).equals(b.get(y))
              && elements.isAllowed(k, Operator.MATCH, a.get(x))) {
            if (DEBUG) System.out.print(" =" + a.get(x));
            elements.update(k, Operator.MATCH, a.get(x));
            x++;
            y++;
          }

        } else {
          if (DEBUG) System.out.print(" !" + (down ? Operator.INS : Operator.DEL) + editToken);
          x = down ? x : x - 1;
          y = down ? y - 1 : y;
        }

        if (DEBUG) System.out.println(" -> (" + x + "," + y + ")");

        // Save end points
        vector.setX(k, x);

        // Check if we've reached the end
        if (x >= sizeA && y >= sizeB) {
          return true;
        }
      }

      return false;
    }

    private XMLToken getEditToken(boolean down, int x, int y) {
      boolean hasEdit = down ? y > 0 && y <= sizeB : x > 0 && x <= sizeA;
      if (!hasEdit) return null;
      return down ? this.b.get(y - 1) : this.a.get(x - 1);
    }

    /**
     * @throws IllegalStateException If no solution could be found
     */
    private List<EdgeSnake> solve(List<Vector> vectors) {
      List<EdgeSnake> snakes = new ArrayList<>();
      Point p = new Point(this.sizeA, this.sizeB);

      for (int d = vectors.size() - 1; p.x() > 0 || p.y() > 0; d--) {
        Vector vector = vectors.get(d);
        int k = p.x() - p.y();
        int xEnd = vector.getX(k);
        int yEnd = xEnd - k;
        if (DEBUG) System.out.println("D=" + d + " k=" + k + " x=" + xEnd + " y=" + yEnd);

        if (!p.isSame(xEnd, yEnd))
          throw new IllegalStateException("No solution for d:" + d + " k:" + k + " p:" + p + " V:( " + xEnd + ", " + yEnd + " )");

        EdgeSnake solution = createToPoint(p, vector, k, d);

        if (!p.isSame(solution.getXEnd(), solution.getYEnd()))
          throw new IllegalStateException("Missed solution for d:" + d + " k:" + k + " p:" + p + " V:( " + xEnd + ", " + yEnd + " )");

        if (snakes.size() > 0) {
          EdgeSnake snake = snakes.get(0);
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

  private static <T> EdgeSnake createToPoint(Point point, Vector vector, int k, int d) {
    final int aEnd = point.x();
    final int bEnd = point.y();
    boolean down = (k == -d || (k != d && vector.getX(k - 1) < vector.getX(k + 1)));
    int xStart = down ? vector.getX(k + 1) : vector.getX(k - 1);
    int yStart = xStart - (down ? k + 1 : k - 1);
    int xEnd = down ? xStart : xStart + 1;
    int yEnd = xEnd - k;
    int matching = Math.min(aEnd - xEnd, bEnd - yEnd);

    // Create corresponding snake instance
    EdgeSnake.Direction direction = down ? EdgeSnake.Direction.DOWN : EdgeSnake.Direction.RIGHT;
    return EdgeSnake.create(0, aEnd, 0, bEnd, direction, xStart, yStart, 1, matching);
  }

  @Override
  public String toString() {
    return "MyersGreedyXMLAlgorithm";
  }
}
