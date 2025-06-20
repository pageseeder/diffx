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

/**
 * A Snake as defined by Myers is a continuous sequence of diagonal edges.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @see <a href="https://neil.fraser.name/writing/diff/myers.pdf">An O(ND) Difference Algorithm and its Variations</a>
 */
public final class Snake {

  private final Point start;

  private final int length;

  public Snake(@NotNull Point start, int length) {
    this.start = start;
    this.length = length;
  }

  /**
   * @return The starting point of the snake.
   */
  public Point getStart() {
    return this.start;
  }

  /**
   * Return the length of the snake in forward direction (down-right).
   *
   * <p>A negative length implies a reverse direction (up-left).
   *
   * @return the length of the snake.
   */
  public int length() {
    return length;
  }

  /**
   * <p><b>Implementation note:</b> The new end point is computed as the starting point plus the length.
   *
   * @return The end point of the snake
   */
  public Point getEnd() {
    return this.start.plus(length, length);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Snake snake = (Snake) o;
    if (length != snake.length) return false;
    return start.equals(snake.start);
  }

  @Override
  public int hashCode() {
    int result = start.hashCode();
    result = 31 * result + length;
    return result;
  }

  /**
   * @return A new snake in the opposite direction.
   */
  public Snake flip() {
    return new Snake(getEnd(), -length);
  }

  @Override
  public String toString() {
    return "Snake{" +
        "start=" + start +
        ", length=" + length +
        '}';
  }
}
