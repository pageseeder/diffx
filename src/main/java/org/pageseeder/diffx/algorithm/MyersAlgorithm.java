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

import java.util.List;

/**
 * A base class for algorithms based on Eugene W. Myers' paper 'An O(ND) Difference Algorithm and its Variations'.
 *
 * <p>These algorithms served as the basis for a new implementation of the UNIX diff program. They are much faster
 * in practice than other LCS algorithms especially when the number of differences between the two sequences to
 * compare is small.
 *
 * @param <T> The type of token being compared
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @see <a href="https://neil.fraser.name/writing/diff/myers.pdf">An O(ND) Difference Algorithm and its Variations</a>
 */
abstract class MyersAlgorithm<T> implements DiffAlgorithm<T> {

  private final static boolean DEBUG = false;

  /**
   * Handles the results of the diff.
   */
  protected void handleResults(List<? extends T> a, List<? extends T> b, DiffHandler<T> handler, List<EdgeSnake> snakes) {
    for (EdgeSnake snake : snakes) {
      if (DEBUG) System.err.println(snake);
      if (snake.isForward()) {
        handleForward(a, b, handler, snake);
      } else {
        handleReverse(a, b, handler, snake);
      }
    }
  }

  /**
   * Handles the results of the diff for a single snake in forward direction.
   */
  private void handleForward(List<? extends T> a, List<? extends T> b, DiffHandler<T> handler, @NotNull EdgeSnake snake) {
    Point start = snake.getStartPoint();
    Point mid = snake.getMidPoint();
    Point end = snake.getEndPoint();
    handleEdited(a, b, handler, snake, start, mid);
    if (snake.matching > 0) {
      for (int i = mid.x(); i < end.x(); i++) {
        handler.handle(Operator.MATCH, a.get(i));
      }
    }
  }

  /**
   * Handles the results of the diff for a single snake in reverse direction.
   */
  private void handleReverse(List<? extends T> a, List<? extends T> b, DiffHandler<T> handler, @NotNull EdgeSnake snake) {
    Point start = snake.getEndPoint();
    Point mid = snake.getMidPoint();
    Point end = snake.getStartPoint();
    if (snake.matching > 0) {
      for (int i = start.x(); i < mid.x(); i++) {
        handler.handle(Operator.MATCH, a.get(i));
      }
    }
    handleEdited(a, b, handler, snake, mid, end);
  }

  private void handleEdited(List<? extends T> a, List<? extends T> b, DiffHandler<T> handler, @NotNull EdgeSnake snake, Point start, Point mid) {
    if (snake.deleted() > 0) {
      for (int i = start.x(); i < mid.x(); i++) {
        handler.handle(Operator.DEL, a.get(i));
      }
    } else if (snake.inserted() > 0) {
      for (int j = start.y(); j < mid.y(); j++) {
        handler.handle(Operator.INS, b.get(j));
      }
    }
  }

}
