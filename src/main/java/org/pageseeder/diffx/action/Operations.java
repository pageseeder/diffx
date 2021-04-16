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
import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;
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

  private Operations() {}

  /**
   * Generates the list of tokens from the list of operations.
   *
   * @param operations  The list of operations.
   * @param positive <code>true</code> for generating the new sequence (INS or MATCH);
   *                 <code>false</code> for generating the old sequence (DEL or MATCH).
   */
  public static List<Token> generate(List<Operation> operations, boolean positive) {
    List<Token> generated = new LinkedList<>();
    for (Operation operation : operations) {
      if (positive ? operation.operator() == Operator.INS : operation.operator() == Operator.DEL) {
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
  public static List<Operation> flip(List<Operation> operations) {
    List<Operation> reverse = new ArrayList<>(operations.size());
    for (Operation operation : operations) {
      reverse.add(operation.flip());
    }
    return reverse;
  }

  /**
   * Apply the specified list of operations to the input sequence and return the corresponding output.
   */
  public static EventSequence apply(EventSequence input, List<Operation> operations) {
    List<Token> tokens = apply(input.tokens(), operations);
    EventSequence out = new EventSequence(tokens.size());
    out.addTokens(tokens);
    return out;
  }

  /**
   * Apply the specified list of action to the input sequence and return the corresponding output.
   */
  public static List<Token> apply(List<Token> input, List<Operation> operations) {
    List<Token> out = new ArrayList<>(input.size());
    int i = 0;
    try {
      for (Operation operation : operations) {
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

  public static void format(List<Operation> operations, DiffXFormatter formatter) throws IOException {
    for (Operation operation : operations) {
      switch (operation.operator()) {
        case MATCH:
          formatter.format(operation.token());
          break;
        case INS:
          formatter.insert(operation.token());
          break;
        case DEL:
          formatter.delete(operation.token());
          break;
        default:
      }
    }
  }

  public static void handle(List<Operation> operations, DiffHandler handler) {
    for (Operation operation : operations) {
      handler.handle(operation.operator(), operation.token());
    }
  }
}
