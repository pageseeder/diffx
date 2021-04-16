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
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;
import java.util.List;

public final class DiffResult {

  private final List<Action> actions;

  public DiffResult(List<Action> actions) {
    this.actions = actions;
  }

  public List<Action> actions() {
    return this.actions;
  }

  public DiffResult reverse() {
    return new DiffResult(Actions.reverse(this.actions));
  }

  public void format(DiffXFormatter formatter) throws IOException {
    Actions.format(this.actions, formatter);
  }

  public boolean isApplicableTo(List<Token> to, List<Token> from) {
    return Actions.isApplicable(to, from, this.actions);
  }

  public List<Token> apply(List<Token> tokens) {
    return Actions.apply(tokens, this.actions);
  }

  public EventSequence apply(EventSequence sequence) {
    return Actions.apply(sequence, this.actions);
  }

  public boolean hasChanges() {
    return this.actions.stream().anyMatch(action -> action.operator() != Operator.MATCH);
  }

  public boolean isIdentical() {
    return this.actions.stream().noneMatch(action -> action.operator() != Operator.MATCH);
  }

  public int countEvents(Operator operator) {
    return this.actions.stream()
        .filter(action -> action.operator() == operator)
        .mapToInt(action -> action.tokens().size())
        .sum();
  }

}
