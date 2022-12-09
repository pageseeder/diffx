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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An action associated with a list of tokens.
 * <p>
 * Wraps a token and binds it with an operator.
 * <p>
 * A type of action for the tokens:
 * <ul>
 *   <li>Add a diffx token to a sequence (+);</li>
 *   <li>Remove a diffx token to sequence (-);</li>
 *   <li>Preserve a diffx token.</li>
 * </ul>
 *
 * @param <T> The type of token.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class Action<T> {

  /**
   * The operator.
   */
  private final Operator operator;

  /**
   * The list of tokens associated with this action.
   */
  private final List<T> tokens;

  /**
   * Creates a new action.
   *
   * @param operator The type of action.
   *
   * @throws NullPointerException If the specified operator is <code>null</code>.
   */
  public Action(Operator operator) {
    this(operator, new ArrayList<>());
  }

  /**
   * Creates a new action from a list of tokens.
   *
   * @param operator The type of action.
   *
   * @throws NullPointerException If either parameter is null.
   */
  public Action(Operator operator, List<T> tokens) {
    this.operator = Objects.requireNonNull(operator);
    this.tokens = Objects.requireNonNull(tokens);
  }

  /**
   * Add a token to the list for this action.
   *
   * @param token The token to add.
   */
  public void add(T token) {
    this.tokens.add(token);
  }

  /**
   * @return the list of tokens.
   */
  public List<T> tokens() {
    return this.tokens;
  }

  /**
   * @return The operator of this action.
   */
  public Operator operator() {
    return this.operator;
  }

  /**
   * @return A new action using the opposite operator by swapping INS with DEL;
   * or the same action if operator is MATCH.
   */
  public Action<T> flip() {
    return this.operator == Operator.MATCH ? this : new Action<>(this.operator.flip(), this.tokens);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Action<?> action = (Action<?>) o;
    if (this.operator != action.operator) return false;
    return this.tokens.equals(action.tokens);
  }

  @Override
  public int hashCode() {
    int result = this.operator.hashCode();
    result = 31 * result + this.tokens.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Action{" + this.operator + "," + this.tokens + '}';
  }
}
