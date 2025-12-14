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
package org.pageseeder.diffx.action;

import org.pageseeder.diffx.api.Operator;

import java.util.List;
import java.util.Objects;

/**
 * A class that models the result of a diff operation, representing the sequence of actions
 * required to transform one list of tokens into another.
 *
 * @param <T> The type of token used in the actions.
 *
 * @version 1.2.0
 * @since 0.9.0
 */
public final class DiffResult<T> {

  private final List<Action<T>> actions;

  public DiffResult(List<Action<T>> actions) {
    this.actions = Objects.requireNonNull(actions);
  }

  /**
   * Retrieves the list of actions associated with this diff result.
   * Each action represents an operation (e.g., addition, deletion, match)
   * and is associated with tokens of type {@code T}.
   *
   * @return A list of actions representing the transformations in this diff result.
   */
  public List<Action<T>> actions() {
    return this.actions;
  }

  /**
   * Reverses the sequence of actions in this diff result.
   * The reverse operation essentially flips the direction of each action,
   * transforming additions into deletions, deletions into additions, and maintaining matches as-is.
   *
   * @return A new {@code DiffResult<T>} instance with reversed actions.
   */
  public DiffResult<T> reverse() {
    return new DiffResult<>(Actions.flip(this.actions));
  }

  /**
   * Determines if the current sequence of actions can be applied to transform
   * the {@code from} list into the {@code to} list.
   *
   * @param to   The target list of tokens after applying the actions.
   * @param from The source list of tokens before applying the actions.
   * @return {@code true} if the sequence of actions is valid for transforming
   *         {@code from} into {@code to}; {@code false} otherwise.
   */
  public boolean isApplicableTo(List<T> to, List<T> from) {
    return Actions.isApplicable(to, from, this.actions);
  }

  /**
   * Applies a sequence of transformation actions to the given list of tokens.
   * The actions define modifications such as additions, deletions, or matching elements.
   *
   * @param tokens The original list of tokens to which the transformation actions should be applied.
   * @return A new list of tokens resulting from applying the sequence of actions to the input list.
   */
  public List<T> apply(List<T> tokens) {
    return Actions.apply(tokens, this.actions);
  }

  /**
   * Checks if there are any changes in the sequence of actions.
   * A change is defined as any action with an operator other than {@code MATCH}.
   *
   * @return {@code true} if there is at least one action with an operator
   *         that is not {@code MATCH}; {@code false} otherwise.
   */
  public boolean hasChanges() {
    return this.actions.stream().anyMatch(action -> action.operator() != Operator.MATCH);
  }

  /**
   * Determines if all actions in the current sequence are of type {@code Operator.MATCH}.
   * This method checks if there are no actions with an operator that differs from {@code MATCH},
   * implying that the sequence has no insertions or deletions.
   *
   * @return {@code true} if all actions have the {@code Operator.MATCH};
   *         {@code false} otherwise.
   */
  public boolean isIdentical() {
    return this.actions.stream().noneMatch(action -> action.operator() != Operator.MATCH);
  }

  /**
   * Counts the cumulative number of tokens associated with actions
   * that match a specified operator.
   *
   * @param operator The operator to filter actions by. Only actions with this operator
   *                 will be considered for counting their associated tokens.
   * @return The total number of tokens in all actions that use the specified operator.
   */
  public int countEvents(Operator operator) {
    return this.actions.stream()
        .filter(action -> action.operator() == operator)
        .mapToInt(action -> action.tokens().size())
        .sum();
  }

}
