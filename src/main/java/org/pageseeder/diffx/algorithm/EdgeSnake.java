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

/**
 * An edge-snake is non-diagonal edge and then a possibly empty sequence of diagonal edges from the edit graph.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class EdgeSnake {

  /**
   * Which direction does the snake go.
   */
  public enum Direction {

    /**
     * Down the y-axis: insertions in forward comparisons.
     */
    DOWN(Operator.INS, true),

    /**
     * Right on the x-axis: deletions in forward comparisons.
     */
    RIGHT(Operator.DEL, true),

    /**
     * Up the y-axis: insertions in reverse comparisons.
     */
    UP(Operator.INS, false),

    /**
     * Left on the x-axis: deletions in reverse comparisons.
     */
    LEFT(Operator.DEL, false);

    final Operator operator;
    final boolean isForward;

    Direction(Operator operator, boolean isForward) {
      this.operator = operator;
      this.isForward = isForward;
    }
  }

  /**
   * The x-position of the starting point
   */
  public int x;

  /**
   * The y-position of the starting point
   */
  public int y;

  /**
   * Defines the edited characters are inserted or deleted.
   */
  public Direction direction;

  /**
   * The number of edited (inserted or deleted) elements.
   */
  public int edited;

  /**
   * The number of matching elements
   */
  public int matching;

  /**
   * The difference in length between the first and second sequence. This value is used as an offset between
   * the forward k lines to the reverse ones
   */
  public int delta;

  /**
   * A value of 0 or 1 indicate an edge, where 0 means both objects are equal while 1 means there is either one
   * insertion or one deletion. A value of greater than needs to be checked in both directions
   **/
  private int diff;

  private EdgeSnake(int x, int y, Direction direction, int edited, int matching, int delta, int diff) {
    this.x = x;
    this.y = y;
    this.direction = direction;
    this.edited = edited;
    this.matching = matching;
    this.delta = delta;
    this.diff = diff;
  }

  private EdgeSnake(int x, int y, Direction direction, int edited, int matching) {
    this(x, y, direction, edited, matching, 0, -1);
  }

  /**
   * Create a new EdgeSnake within the rectangle.
   */
  public static EdgeSnake create(int aStart, int aEnd, int bStart, int bEnd, Direction direction, int xStart, int yStart, int edited, int matching) {
    EdgeSnake snake = new EdgeSnake(xStart, yStart, direction, edited, matching);
    snake.removeStubs(aStart, aEnd, bStart, bEnd);
    return snake;
  }

  /**
   * @return true for forward comparison (right / bottom), false for reverse (left / up)
   */
  public boolean isForward() {
    return this.direction.isForward;
  }

  /**
   * @return The start point of this snake segment
   */
  public Point getStartPoint() {
    return new Point(this.x, this.y);
  }

  /**
   * @return The mid point of this snake segment
   */
  public Point getMidPoint() {
    return new Point(this.getXMid(), this.getYMid());
  }

  /**
   * @return The end point of this snake segment
   */
  public Point getEndPoint() {
    return new Point(this.getXEnd(), this.getYEnd());
  }

  public int deleted() {
    return this.direction.operator == Operator.DEL ? this.edited : 0;
  }

  public int inserted() {
    return this.direction.operator == Operator.INS ? this.edited : 0;
  }

  /**
   * @return The x-position of the mid point
   */
  public int getXMid() {
    if (this.direction.operator != Operator.DEL) return this.x;
    return this.direction.isForward ? this.x + this.edited : this.x - this.edited;
  }

  /**
   * @return The y-position of the mid point
   */
  public int getYMid() {
    if (this.direction.operator != Operator.INS) return this.y;
    return this.direction.isForward ? this.y + this.edited : this.y - this.edited;
  }

  /**
   * @return The x-position of the end point
   */
  public int getXEnd() {
    return this.direction.isForward ? getXMid() + this.matching : getXMid() - this.matching;
  }

  /**
   * @return The y-position of the end point
   */
  public int getYEnd() {
    return this.direction.isForward ? getYMid() + this.matching : getYMid() - this.matching;
  }

  /**
   * @return The number of differences
   */
  public int getDiff() {
    return this.diff;
  }

  /**
   * Sets the d contours for this segment which correspond to the number of differences in that trace, irrespective of
   * the number of equal elements.
   *
   * @param diff The number of differences in that trace
   */
  public void setDiff(int diff) {
    this.diff = diff;
  }

  @Override
  public String toString() {
    return "EdgeSnake " + direction + ": " + getStartPoint() + " + " +
        "(" + inserted() + ", " + deleted() + ")" +
        " + " + matching + " -> " + getEndPoint() +
        " k=" + (this.getXMid() - this.getYMid());
  }

  /**
   * Removes the effects of a single insertion (down or up movement in the graph) if the x-position of the starting
   * vertex equals <em>a0</em> and the y-position of the starting vertex equals the y-position of <em>b0</em> before
   * the insertion.
   *
   * @param aStart The starting position in the array of elements from the first object to compare
   * @param aEnd   The index of the last element from the first object to compare
   * @param bStart The starting position in the array of elements from the second object to compare
   * @param bEnd   The index of the last element from the second object to compare
   */
  private void removeStubs(int aStart, int aEnd, int bStart, int bEnd) {
    // TODO Refactor to use immutable snakes
    if (this.edited != 1) return;
    if (this.direction == Direction.DOWN && this.x == aStart && this.y == bStart - 1) {
      this.y++;
      this.edited = 0;
    }
    if (this.direction == Direction.UP && this.x == aStart + aEnd && this.y == bStart + bEnd + 1) {
      this.y--;
      this.edited = 0;
    }
  }

  /**
   * Append the path of the specified snake to this snake.
   *
   * @param snake The snake to append to the current snake
   *
   * @return true if the snake could be appended to this snake; false otherwise
   */
  boolean append(EdgeSnake snake) {
    if (this.direction != snake.direction) return false;
    // TODO We could also compute when diagonals match
    // TODO Refactor so that we can use immutable snakes
    if (this.edited > 0 && snake.edited > 0 && this.matching == 0 && snake.matching == 0) {
      this.edited += snake.edited;
      this.matching += snake.matching;
      if (this.direction.isForward) {
        this.x = Math.min(this.x, snake.x);
        this.y = Math.min(this.y, snake.y);
      } else {
        this.x = Math.max(this.x, snake.x);
        this.y = Math.max(this.y, snake.y);
      }
      return true;
    }
    return false;
  }
}