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

import java.util.List;

public final class DiffResult<T> {

  private final List<Action<T>> actions;

  public DiffResult(List<Action<T>> actions) {
    this.actions = actions;
  }

  public List<Action<T>> actions() {
    return this.actions;
  }

  public DiffResult<T> reverse() {
    return new DiffResult<>(Actions.flip(this.actions));
  }

  public boolean isApplicableTo(List<T> to, List<T> from) {
    return Actions.isApplicable(to, from, this.actions);
  }

  public List<T> apply(List<T> tokens) {
    return Actions.apply(tokens, this.actions);
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
