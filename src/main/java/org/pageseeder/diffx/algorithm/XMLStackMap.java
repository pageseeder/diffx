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
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.XMLToken;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * This class keeps track of the XML state so that we know whether to continue processing during the greedy phase
 * of Myers greedy algorithm.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
final class XMLStackMap {

  private Map<Integer, Deque<Operation<StartElementToken>>> previous = new HashMap<>();

  private Map<Integer, Deque<Operation<StartElementToken>>> stacks = new HashMap<>();

  XMLStackMap() {
  }

  void nextDiff() {
    this.previous = this.stacks;
    this.stacks = new HashMap<>(this.previous.size() + 1);
  }

  void initK(int k, boolean down) {
    Deque<Operation<StartElementToken>> stack = new ArrayDeque<>();
    Deque<Operation<StartElementToken>> prev = this.previous.get(down ? k + 1 : k - 1);
    if (prev != null) stack.addAll(prev);
    this.stacks.put(k, stack);
  }

  Deque<Operation<StartElementToken>> getStack(int k) {
    return this.stacks.get(k);
  }

  void update(int k, Operator operator, XMLToken token) {
    Deque<Operation<StartElementToken>> stack = getStack(k);
    if (token instanceof StartElementToken) stack.push(new Operation<>(operator, (StartElementToken) token));
    if (token instanceof EndElementToken) stack.pop();
  }

  boolean isAllowed(int k, Operator operator, XMLToken token) {
    if (token instanceof EndElementToken) {
      // Ensure that the end element matches the start element
      Deque<Operation<StartElementToken>> stack = getStack(k);
      Operation<StartElementToken> last = stack.peek();
      return last != null && last.operator() == operator && ((EndElementToken) token).match(last.token());
    }
    return true;
  }

  @Override
  public String toString() {
    return "S=" + stacks;
  }
}
