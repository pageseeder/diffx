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

  private Actions() {}


  /**
   * Generates a list of tokens by processing a list of actions based on their operators and the specified direction.
   *
   * @param <T>     The type of tokens in the actions.
   * @param actions The list of actions to process. Each action contains an operator and a list of tokens.
   * @param forward A boolean specifying the processing direction. If true, actions with the operator INS will be included.
   *                If false, actions with the operator DEL will be included instead.
   * @return A list of tokens extracted from the specified actions based on their operators.
   */
  public static <T> List<T> generate(List<Action<T>> actions, boolean forward) {
    List<T> generated = new ArrayList<>();
    for (Action<T> action : actions) {
      Operator op = action.operator();
      if (op == Operator.MATCH || (forward ? op == Operator.INS : op == Operator.DEL)) {
        generated.addAll(action.tokens());
      }
    }
    return generated;
  }

  /**
   * Converts a list of actions into a list of operations. For each action, all associated tokens
   * are transformed into operations using the action's operator.
   *
   * @param <T>     The type of tokens in the actions.
   * @param actions The list of actions to convert. Each action contains an operator and a list of tokens.
   * @return A list of operations where each operation corresponds to a token from the actions paired
   *         with its associated operator.
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
   * Reverses the operators of a list of actions by invoking the {@code flip} method
   * on each action, returning a new list of flipped actions.
   *
   * @param <T>     The type of tokens in the actions.
   * @param actions The list of actions to be flipped.
   * @return A new list of actions where each action has its operator reversed.
   */
  public static <T> List<Action<T>> flip(List<Action<T>> actions) {
    List<Action<T>> reverse = new ArrayList<>(actions.size());
    for (Action<T> action : actions) {
      reverse.add(action.flip());
    }
    return reverse;
  }

  /**
   * Applies a list of actions to the input sequence and returns a new sequence based on
   * the processed tokens.
   *
   * <p>This method processes the tokens in the input sequence using the provided actions
   * and generates a new sequence reflecting the result of applying these actions.
   *
   * @param input   The input sequence to which the actions will be applied.
   * @param actions A list of actions defining the operations to be performed on the tokens
   *                of the input sequence.
   * @return A new sequence resulting from applying the specified actions to the input sequence.
   */
  public static Sequence apply(Sequence input, List<Action<XMLToken>> actions) {
    List<XMLToken> tokens = apply(input.tokens(), actions);
    Sequence out = new Sequence(tokens.size());
    tokens.forEach(out::addToken);
    return out;
  }

  /**
   * Applies a list of actions to the given input list and produces a new list of tokens
   * based on the specified operations defined in the actions.
   *
   * @param <T>     The type of elements in the input list and tokens in the actions.
   * @param input   The input list of tokens to process.
   * @param actions The list of actions defining modifications to apply to the input.
   *
   * @return A new list of tokens resulting from applying the actions to the input list.
   *
   * @throws IllegalArgumentException If the actions cannot be fully applied or
   *                                  if they do not align with the input list.
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
   *
   * @param <T>     The type of token
   *
   * @return true if applicable; false otherwise.
   */
  public static <T> boolean isApplicable(List<? extends T> a, List<? extends T> b, List<Action<T>> actions) {
    int i = 0; // Index of A
    int j = 0; // Index of B
    final int aSize = a.size();
    final int bSize = b.size();
    for (Action<T> action : actions) {
      final Operator op = action.operator();
      if (op == Operator.MATCH) {
        for (T token : action.tokens()) {
          if (i >= aSize || !token.equals(a.get(i))) return false;
          if (j >= bSize || !token.equals(b.get(j))) return false;
          i++;
          j++;
        }
      } else if (op == Operator.DEL) {
        for (T token : action.tokens()) {
          if (i >= aSize || !token.equals(a.get(i))) return false;
          i++;
        }
      } else if (op == Operator.INS) {
        for (T token : action.tokens()) {
          if (j >= bSize || !token.equals(b.get(j))) return false;
          j++;
        }
      }
    }
    return true;
  }

  /**
   * Processes a list of actions by invoking a given diff handler for each token in each action.
   *
   * @param actions The list of actions to process.
   * @param handler The diff handler to apply to each token and operator.
   * @param <T>     The type of tokens in the actions.
   */
  public static <T> void handle(List<Action<T>> actions, DiffHandler<T> handler) {
    for (Action<T> action : actions) {
      for (T token : action.tokens()) {
        handler.handle(action.operator(), token);
      }
    }
  }

  /**
   * Processes a list of actions and applies the specified diff handler to each token in each action.
   * The handler is notified at the start and end of processing, and for each operator-token pair in the actions.
   *
   * @param <T>     The type of tokens in the actions.
   * @param actions The list of actions to process.
   * @param handler The diff handler to apply to each token and operator.
   */
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
