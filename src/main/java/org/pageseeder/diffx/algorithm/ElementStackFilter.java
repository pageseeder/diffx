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
package org.pageseeder.diffx.algorithm;

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.handler.DiffFilter;
import org.pageseeder.diffx.token.AttributeToken;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.XMLToken;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Maintains the state of open and closed elements during the processing the Diff-X
 * algorithm.
 *
 * <p>This class has two purposes, firstly to provide an object that is more specialised
 * than the generic lists and stack for use by the DiffX algorithms. Second, to delegate
 * some of the complexity of algorithm.
 *
 * <p>This class has several methods that are similar to <code>List</code> interface
 * but does not implement it.
 *
 * <p>This class is not synchronised and is not meant to be serializable.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.7.0
 */
public final class ElementStackFilter extends DiffFilter<XMLToken> {

  /**
   * The stack of open elements.
   */
  private final Deque<Operation<StartElementToken>> elements;

  /**
   * Constructs a new filter.
   */
  public ElementStackFilter(DiffHandler<XMLToken> target) {
    super(target);
    this.elements = new ArrayDeque<>(16);
  }

  /**
   * @return the depth of the elements
   */
  public int depth() {
    return this.elements.size();
  }

  /**
   * Tests if this list has no elements.
   *
   * @return <code>true</code> if this list has no elements;
   * <code>false</code> otherwise.
   */
  public boolean isEmpty() {
    return this.elements.isEmpty();
  }

  /**
   * Returns the current open element.
   *
   * @return The current open element; or <code>null</code> if none.
   */
  public Operation<StartElementToken> current() {
    return this.elements.peek();
  }

  /**
   * Updates the state from the specified token.
   *
   * <p>If the token is a START_ELEMENT token, it is pushed into the stack along with the corresponding operation.</p>
   *
   * <p>If the token is an END_ELEMENT token, it is popped from the stack.</p>
   *
   * @param token    The deleted token.
   * @param operator The corresponding operator
   */
  @Override
  public void handle(@NotNull Operator operator, @NotNull XMLToken token) {
    this.target.handle(operator, token);
    if (token instanceof StartElementToken) {
      this.elements.push(new Operation<>(operator, (StartElementToken) token));
    } else if (token instanceof EndElementToken) {
      this.elements.pop();
    }
  }

  /**
   * Indicates whether the specified operation is allowed.
   *
   * <p>It is allowed if:</p>
   * <ul>
   *   <li>The token is not an END_ELEMENT token type</li>
   *   <li>OR the token is an END_ELEMENT token that matches the last START_ELEMENT token and operator</li>
   * </ul>
   *
   * @param token The token to check.
   *
   * @return <code>true</code> if it matches the current element;
   * <code>false</code> otherwise.
   */
  public boolean isAllowed(Operator operator, XMLToken token) {
    // Only check for end element tokens
    if (!(token instanceof EndElementToken)) return true;
    // Check that it matches the
    StartElementToken start = ((EndElementToken) token).getOpenElement();
    return matchCurrent(operator, start);
  }

  /**
   * Indicates whether the specified token is a close element that
   * matches the name and URI of the current open element.
   *
   * @param operator The token to check.
   * @param start    The token to check.
   *
   * @return <code>true</code> if it matches the current element;
   * <code>false</code> otherwise.
   */
  public boolean matchCurrent(Operator operator, StartElementToken start) {
    Operation<StartElementToken> current = this.current();
    if (current == null) return false;
    return operator == current.operator() && start.equals(current.token());
  }

  /**
   * Indicates whether the first specified token has priority over the second element.
   * <p>
   * It only seem to be the case when the algorithm has the choice between an attribute and another
   * element.
   *
   * @param token1 The token assumed to have priority.
   * @param token2 The other token.
   *
   * @return <code>true</code> if first specified token has priority over the second element;
   * <code>false</code> otherwise.
   */
  public boolean hasPriorityOver(XMLToken token1, XMLToken token2) {
    return token1 instanceof AttributeToken
        && !(token2 instanceof AttributeToken)
        && !isEmpty();
  }

  /**
   * Removes all of the elements from this list.  The list will
   * be empty after this call returns.
   */
  public void clear() {
    this.elements.clear();
  }

}
