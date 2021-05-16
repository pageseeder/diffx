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

import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.token.XMLToken;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for operations.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class Operations {

  private Operations() {
  }

  /**
   * Generates the list of tokens from the list of operations.
   *
   * @param operations The list of operations.
   * @param forward    <code>true</code> for generating the new sequence (INS or MATCH);
   *                   <code>false</code> for generating the old sequence (DEL or MATCH).
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
   * Flip the operations by swapping the INS and DEL.
   */
  public static <T> List<Operation<T>> flip(List<Operation<T>> operations) {
    List<Operation<T>> reverse = new ArrayList<>(operations.size());
    for (Operation<T> operation : operations) {
      reverse.add(operation.flip());
    }
    return reverse;
  }

  /**
   * Apply the specified list of operations to the input sequence and return the corresponding output.
   */
  public static Sequence apply(Sequence input, List<Operation<XMLToken>> operations) {
    List<XMLToken> tokens = apply(input.tokens(), operations);
    Sequence out = new Sequence(tokens.size());
    out.addTokens(tokens);
    return out;
  }

  /**
   * Apply the specified list of action to the input sequence and return the corresponding output.
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
