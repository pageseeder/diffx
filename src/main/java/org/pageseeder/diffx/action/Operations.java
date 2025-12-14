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
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for operations.
 *
 * @author Christophe Lauret
 *
 * @version 1.3.0
 * @since 0.9.0
 */
public final class Operations {

  private Operations() {
  }

  /**
   * Generates the list of tokens from the list of operations.
   *
   * @param <T> The type of token associated with the operations.
   * @param operations The list of operations.
   * @param forward    <code>true</code> for generating the new sequence (INS or MATCH);
   *                   <code>false</code> for generating the old sequence (DEL or MATCH).
   *
   * @return The resulting list of operations
   */
  public static <T> List<T> generate(List<Operation<T>> operations, boolean forward) {
    List<T> generated = new LinkedList<>();
    for (Operation<T> operation : operations) {
      if (forward ? operation.operator() == Operator.INS : operation.operator() == Operator.DEL) {
        generated.add(operation.token());
      } else if (operation.operator() == Operator.MATCH) {
        generated.add(operation.token());
      }
    }
    return generated;
  }

  /**
   * Reverses a list of operations by flipping each operation. Flipping
   * transforms the operation's operator (e.g., INS to DEL or vice versa),
   * or leaves it unchanged if the operator is MATCH.
   *
   * @param <T> The type of token associated with the operations.
   * @param operations The list of operations to be flipped.
   *                   Must not be null, and all operations must provide a valid flip implementation.
   * @return A new list of operations where each operation is the flipped version
   *         of the corresponding operation in the provided list.
   */
  public static <T> List<Operation<T>> flip(List<Operation<T>> operations) {
    List<Operation<T>> reverse = new ArrayList<>(operations.size());
    for (Operation<T> operation : operations) {
      reverse.add(operation.flip());
    }
    return reverse;
  }

  /**
   * Applies a series of operations to a given sequence of tokens and generates a new sequence.
   * Each operation transforms the tokens of the input sequence, and the resulting tokens
   * are used to construct the output sequence.
   *
   * @param input The input sequence to which the operations will be applied. Must not be null.
   * @param operations A list of operations detailing how to transform the input sequence.
   *                   Each operation must have a valid operator and token. Must not be null.
   * @return A new sequence of tokens resulting from applying the operations to the input sequence.
   * @throws IllegalArgumentException If the operations cannot be successfully applied
   *                                  to the input sequence.
   */
  public static Sequence apply(Sequence input, List<Operation<XMLToken>> operations) {
    List<XMLToken> tokens = apply(input.tokens(), operations);
    Sequence out = new Sequence(tokens.size());
    out.addTokens(tokens);
    return out;
  }

  /**
   * Applies a list of operations to the input list and generates the corresponding output list.
   * This method processes each operation in the specified order, modifying the input list
   * according to the operation's type (MATCH, INS, DEL).
   *
   * @param <T> The type of elements in the input list and operations.
   * @param input The input list of elements to which the operations will be applied.
   *              Cannot be null.
   * @param operations The list of operations describing how to transform the input list.
   *                   Each operation must have a valid operator and token. Cannot be null.
   * @return A new list of elements resulting from applying the operations to the input list.
   * @throws IllegalArgumentException If the operations cannot be applied to the input list
   *                                  (e.g., due to mismatched lengths or invalid operations).
   */
  public static <T> List<T> apply(List<T> input, List<Operation<T>> operations) {
    List<T> out = new ArrayList<>(input.size());
    int i = 0;
    try {
      for (Operation<T> operation : operations) {
        switch (operation.operator()) {
          case MATCH:
            out.add(input.get(i));
            i++;
            break;
          case INS:
            out.add(operation.token());
            break;
          case DEL:
            i++;
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

  public static <T> void handle(List<Operation<T>> operations, DiffHandler<T> handler) {
    for (Operation<T> operation : operations) {
      handler.handle(operation.operator(), operation.token());
    }
  }
}
