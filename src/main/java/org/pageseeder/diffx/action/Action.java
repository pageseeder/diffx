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

import org.pageseeder.diffx.token.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * An action associated to a sequence of DiffX tokens.
 * <p>
 * Wraps an token and binds it with an action type.
 * <p>
 * A type of action for the tokens:
 * <ul>
 *   <li>Add a diffx token to a sequence (+);</li>
 *   <li>Remove a diffx token to sequence (-);</li>
 *   <li>Preserve a diffx token.</li>
 * </ul>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class Action {

  /**
   * The type of action.
   */
  private final Operator operator;

  /**
   * The list of tokens associated with this action.
   */
  private final List<Token> tokens;

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
   * @throws NullPointerException If the given type is <code>null</code>.
   */
  public Action(Operator operator, List<Token> tokens) {
    if (operator == null) throw new NullPointerException("An action must have a type.");
    this.operator = operator;
    this.tokens = tokens;
  }

  /**
   * Add an token to the list for this action.
   *
   * @param token The token to add.
   */
  public void add(Token token) {
    this.tokens.add(token);
  }

  /**
   * @return the list of Tokens.
   */
  public List<Token> tokens() {
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
   * or the same actions if operator is MATCH.
   */
  public Action flip() {
    switch (this.operator) {
      case DEL:
        return new Action(Operator.INS, this.tokens);
      case INS:
        return new Action(Operator.DEL, this.tokens);
      default:
        return this;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Action action = (Action) o;
    if (this.operator != action.operator) return false;
    return this.tokens.equals(action.tokens);
  }

  @Override
  public int hashCode() {
    int result = operator.hashCode();
    result = 31 * result + tokens.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Action{" + operator + "," + tokens + '}';
  }
}
