/*
 * Copyright 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
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

import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.Token;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * A Myers algorithm vector with XML awareness.
 */
public final class XMLVector {

  private final Map<Integer, Deque<Operation<StartElementToken>>> stacks = new HashMap<>();

  public final Vector vector;

  XMLVector(Vector vector) {
    this.vector = vector;
  }

  /**
   * Create a new V array for the greedy algorithm.
   *
   * @param m The length of the first sequence
   * @param n The length of the second sequence
   */
  public static XMLVector createGreedy(int m, int n) {
    Vector vector = Vector.createGreedy(m, n);
    return new XMLVector(vector);
  }

  public XMLVector createCopy(int d) {
    Vector vector = this.vector.createCopy(d);
    XMLVector copy = new XMLVector(vector);
    copy.stacks.putAll(this.stacks);
    return copy;
  }

  public void setX(int k, int x) {
    this.vector.setX(k, x);
  }

  public int getX(int k) {
    return this.vector.getX(k);
  }

  private Deque<Operation<StartElementToken>> getStack(int k) {
    Deque<Operation<StartElementToken>> stack = stacks.get(k);
    if (stack == null) {
      stack = new ArrayDeque<>();
      Deque<Operation<StartElementToken>> prev = stacks.get(k < 0? k + 1 : k-1);
      if (prev != null) stack.addAll(prev);
      stacks.put(k, stack);
    }
    return stack;
  }

  public void update(int k, Operator operator, Token token) {
    Deque<Operation<StartElementToken>> stack = getStack(k);
    if (token instanceof StartElementToken) stack.add(new Operation<>(operator, (StartElementToken)token));
    if (token instanceof EndElementToken) stack.pop();
  }

  public boolean isAllowed(int k, Operator operator, Token token) {
    Deque<Operation<StartElementToken>> stack = getStack(k);
    if (token instanceof EndElementToken) {
      Operation<StartElementToken> last = stack.peek();
      return last != null && last.operator() == operator && ((EndElementToken)token).match(last.token());
    }
    return true;
  }

  @Override
  public String toString() {
    return "S=" + stacks +", " + vector;
  }
}
