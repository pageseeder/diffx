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
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;
import java.util.*;

/**
 * Utility class for actions.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public class Actions {

  /**
   * Generates the list of events from the list of actions.
   *
   * @param actions  The list of actions.
   * @param positive <code>true</code> for generating the new sequence;
   *                 <code>false</code> for generating the old sequence.
   */
  public static List<DiffXEvent> generate(List<Action> actions, boolean positive) {
    List<DiffXEvent> generated = new LinkedList<>();
    for (Action action : actions) {
      if (positive ? action.type() == Operator.INS : action.type() == Operator.DEL) {
        generated.addAll(action.events());
      } else if (action.type() == Operator.KEEP) {
        generated.addAll(action.events());
      }
    }
    return generated;
  }

  /**
   * Converts the list of actions into a list of atomic operations.
   *
   * @param actions  The list of actions.
   */
  public static List<Operation> toOperations(List<Action> actions) {
    List<Operation> operations = new LinkedList<>();
    for (Action action : actions) {
      Operator operator = action.type();
      for (DiffXEvent event : action.events()) {
        operations.add(new Operation(operator, event));
      }
    }
    return operations;
  }

  /**
   * Reverse the actions by swapping the INS and DEL.
   */
  public static List<Action> reverse(List<Action> actions) {
    List<Action> reverse = new ArrayList<>(actions.size());
    for (Action action : actions) {
      reverse.add(action.reverse());
    }
    return reverse;
  }

  /**
   * Apply the specified list of action to the input sequence and return the corresponding output.
   */
  public static EventSequence apply(EventSequence input, List<Action> actions) {
    List<? extends DiffXEvent> events = apply(input.events(), actions);
    EventSequence out = new EventSequence(events.size());
    events.forEach(out::addEvent);
    return out;
  }

  /**
   * Apply the specified list of action to the input sequence and return the corresponding output.
   */
  public static List<DiffXEvent> apply(List<? extends DiffXEvent> input, List<Action> actions) {
    List<DiffXEvent> out = new ArrayList<>(input.size());
    int i = 0;
    try {
      for (Action action : actions) {
        int count = action.events().size();
        switch (action.type()) {
          case KEEP:
            out.addAll(input.subList(i, i + count));
            i += count;
            break;
          case INS:
            out.addAll(action.events());
            break;
          case DEL:
            i += count;
            break;
        }
      }
    } catch (IndexOutOfBoundsException ex) {
      throw new IllegalArgumentException("Actions cannot be applied to specified input", ex);
    }
    if (i != input.size()) {
      throw new IllegalArgumentException("Actions do not match specified input");
    }
    return out;
  }

  public static boolean isApplicable(List<? extends DiffXEvent> a, List<? extends DiffXEvent> b, List<Action> actions) {
    int i = 0; // Index of A
    int j = 0; // Index of B
    for (Action action : actions) {
      if (action.type() == Operator.KEEP) {
        for (DiffXEvent e : action.events()) {
          if (i >= a.size() || !e.equals(a.get(i))) return false;
          if (j >= b.size() || !e.equals(b.get(j))) return false;
          i++;
          j++;
        }
      } else if (action.type() == Operator.INS) {
        for (DiffXEvent e : action.events()) {
          if (i >= a.size() || !e.equals(a.get(i))) return false;
          i++;
        }
      } else if (action.type() == Operator.DEL) {
        for (DiffXEvent e : action.events()) {
          if (j >= b.size() || !e.equals(b.get(j))) return false;
          j++;
        }
      }
    }
    return true;
  }

  public static void format(List<Action> actions, DiffXFormatter formatter) throws IOException {
    for (Action action : actions) {
      switch (action.type()) {
        case KEEP:
          for (DiffXEvent event : action.events()) {
            formatter.format(event);
          }
          break;
        case INS:
          for (DiffXEvent event : action.events()) {
            formatter.insert(event);
          }
          break;
        case DEL:
          for (DiffXEvent event : action.events()) {
            formatter.delete(event);
          }
          break;
        default:
      }
    }
  }

}
