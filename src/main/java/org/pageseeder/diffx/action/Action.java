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

import org.pageseeder.diffx.event.DiffXEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * An action associated to a sequence of DiffX events.
 * <p>
 * Wraps an event and binds it with an action type.
 * <p>
 * A type of action for the events:
 * <ul>
 *   <li>Add a diffx event to a sequence (+);</li>
 *   <li>Remove a diffx event to sequence (-);</li>
 *   <li>Preserve a diffx event.</li>
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
   * The list of events associated with this action.
   */
  private final List<DiffXEvent> events;

  /**
   * Creates a new action.
   *
   * @param operator The type of action.
   * @throws NullPointerException If the specified operator is <code>null</code>.
   */
  public Action(Operator operator) {
    this(operator, new ArrayList<>());
  }

  /**
   * Creates a new action from a list of events.
   *
   * @param operator The type of action.
   * @throws NullPointerException If the given type is <code>null</code>.
   */
  public Action(Operator operator, List<DiffXEvent> events) {
    if (operator == null) throw new NullPointerException("An action must have a type.");
    this.operator = operator;
    this.events = events;
  }

  /**
   * Add an event to the list for this action.
   *
   * @param event The event to add.
   */
  public void add(DiffXEvent event) {
    this.events.add(event);
  }

  /**
   * @return the list of DiffXEvents.
   */
  public List<DiffXEvent> events() {
    return this.events;
  }

  /**
   * @return The operator of this action.
   */
  public Operator operator() {
    return this.operator;
  }

  /**
   * @return A new action using the opposite operator by swapping INS with DEL;
   *         or the same actions if operator is MATCH.
   */
  public Action flip() {
    switch (this.operator) {
      case DEL:
        return new Action(Operator.INS, this.events);
      case INS:
        return new Action(Operator.DEL, this.events);
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
    return this.events.equals(action.events);
  }

  @Override
  public int hashCode() {
    int result = operator.hashCode();
    result = 31 * result + events.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Action{" +
        "operator=" + operator +
        ", events=" + events +
        '}';
  }
}
