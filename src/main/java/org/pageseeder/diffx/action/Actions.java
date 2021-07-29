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

import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.xml.Sequence;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for actions.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class Actions {

  /**
   * Generates the list of tokens from the list of actions.
   *
   * @param actions The list of actions.
   * @param forward <code>true</code> for generating the new sequence (A to B);
   *                <code>false</code> for generating the old sequence (B to A).
   */
  public static <T> List<T> generate(List<Action<T>> actions, boolean forward) {
    List<T> generated = new ArrayList<>();
    for (Action<T> action : actions) {
      if (forward ? action.operator() == Operator.INS : action.operator() == Operator.DEL) {
        generated.addAll(action.tokens());
      } else if (action.operator() == Operator.MATCH) {
        generated.addAll(action.tokens());
      }
    }
    return generated;
  }

  /**
   * Converts the list of actions into a list of atomic operations.
   *
   * @param actions The list of actions.
   */
  public static <T> List<Operation<T>> toOperations(List<Action<T>> actions) {
    List<Operation<T>> operations = new ArrayList<>();
    for (Action<T> action : actions) {
      Operator operator = action.operator();
      for (T token : action.tokens()) {
        operations.add(new Operation<>(operator, token));
      }
    }
    return operations;
  }

  /**
   * Flip operator on the actions by swapping the INS and DEL.
   *
   * @return A new list of actions.
   */
  public static <T> List<Action<T>> flip(List<Action<T>> actions) {
    List<Action<T>> reverse = new ArrayList<>(actions.size());
    for (Action<T> action : actions) {
      reverse.add(action.flip());
    }
    return reverse;
  }

  /**
   * Apply the specified list of actions to the input sequence and return the corresponding output.
   */
  public static Sequence apply(Sequence input, List<Action<XMLToken>> actions) {
    List<XMLToken> tokens = apply(input.tokens(), actions);
    Sequence out = new Sequence(tokens.size());
    tokens.forEach(out::addToken);
    return out;
  }

  /**
   * Apply the specified list of actions to the input and return the corresponding output.
   *
   * <p>This method can be used to generate A from B.</p>
   *
   * @return The corresponding output.
   * @throws IllegalArgumentException If the list of actions cannot be applied to the specified input
   */
  public static <T> List<T> apply(List<T> input, List<Action<T>> actions) {
    List<T> out = new ArrayList<>(input.size());
    int i = 0;
    try {
      for (Action<T> action : actions) {
        int count = action.tokens().size();
        switch (action.operator()) {
          case MATCH:
            out.addAll(input.subList(i, i + count));
            i += count;
            break;
          case INS:
            out.addAll(action.tokens());
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

  /**
   * Checks whether the specified list of actions is applicable to the two specified inputs.
   *
   * <p>Implementation note: this method iterates over the actions and ensure that the tokens
   * as specified in the correct order and match A or B or both depending on the whether the
   * operator is DEL, INS or MATCH respectively.</p>
   *
   * @param a       List of tokens that may be deleted or matched
   * @param b       List of tokens that may be deleted of matched
   * @param actions List of actions to check
   * @param <T>     The type of token
   */
  public static <T> boolean isApplicable(List<? extends T> a, List<? extends T> b, List<Action<T>> actions) {
    int i = 0; // Index of A
    int j = 0; // Index of B
    for (Action<T> action : actions) {
      if (action.operator() == Operator.MATCH) {
        for (T token : action.tokens()) {
          if (i >= a.size() || !token.equals(a.get(i))) return false;
          if (j >= b.size() || !token.equals(b.get(j))) return false;
          i++;
          j++;
        }
      } else if (action.operator() == Operator.DEL) {
        for (T token : action.tokens()) {
          if (i >= a.size() || !token.equals(a.get(i))) return false;
          i++;
        }
      } else if (action.operator() == Operator.INS) {
        for (T token : action.tokens()) {
          if (j >= b.size() || !token.equals(b.get(j))) return false;
          j++;
        }
      }
    }
    return true;
  }

  public static <T> void handle(List<Action<T>> actions, DiffHandler<T> handler) {
    for (Action<T> action : actions) {
      for (T token : action.tokens()) {
        handler.handle(action.operator(), token);
      }
    }
  }

  public static <T> void applyTo(List<Action<T>> actions, DiffHandler<T> handler) {
    handler.start();
    for (Action<T> action : actions) {
      for (T token : action.tokens()) {
        handler.handle(action.operator(), token);
      }
    }
    handler.end();
  }

}
