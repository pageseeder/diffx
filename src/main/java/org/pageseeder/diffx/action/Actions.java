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

import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.token.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for actions.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public class Actions {

  /**
   * Generates the list of tokens from the list of actions.
   *
   * @param actions  The list of actions.
   * @param positive <code>true</code> for generating the new sequence;
   *                 <code>false</code> for generating the old sequence.
   */
  public static List<Token> generate(List<Action> actions, boolean positive) {
    List<Token> generated = new LinkedList<>();
    for (Action action : actions) {
      if (positive ? action.operator() == Operator.INS : action.operator() == Operator.DEL) {
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
  public static List<Operation> toOperations(List<Action> actions) {
    List<Operation> operations = new LinkedList<>();
    for (Action action : actions) {
      Operator operator = action.operator();
      for (Token token : action.tokens()) {
        operations.add(new Operation(operator, token));
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
      reverse.add(action.flip());
    }
    return reverse;
  }

  /**
   * Apply the specified list of action to the input sequence and return the corresponding output.
   */
  public static Sequence apply(Sequence input, List<Action> actions) {
    List<? extends Token> tokens = apply(input.tokens(), actions);
    Sequence out = new Sequence(tokens.size());
    tokens.forEach(out::addToken);
    return out;
  }

  /**
   * Apply the specified list of action to the input sequence and return the corresponding output.
   */
  public static List<Token> apply(List<? extends Token> input, List<Action> actions) {
    List<Token> out = new ArrayList<>(input.size());
    int i = 0;
    try {
      for (Action action : actions) {
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

  public static boolean isApplicable(List<? extends Token> a, List<? extends Token> b, List<Action> actions) {
    int i = 0; // Index of A
    int j = 0; // Index of B
    for (Action action : actions) {
      if (action.operator() == Operator.MATCH) {
        for (Token token : action.tokens()) {
          if (i >= a.size() || !token.equals(a.get(i))) return false;
          if (j >= b.size() || !token.equals(b.get(j))) return false;
          i++;
          j++;
        }
      } else if (action.operator() == Operator.INS) {
        for (Token token : action.tokens()) {
          if (i >= a.size() || !token.equals(a.get(i))) return false;
          i++;
        }
      } else if (action.operator() == Operator.DEL) {
        for (Token token : action.tokens()) {
          if (j >= b.size() || !token.equals(b.get(j))) return false;
          j++;
        }
      }
    }
    return true;
  }

  public static void format(List<Action> actions, DiffXFormatter formatter) throws IOException {
    for (Action action : actions) {
      switch (action.operator()) {
        case MATCH:
          for (Token token : action.tokens()) {
            formatter.format(token);
          }
          break;
        case INS:
          for (Token token : action.tokens()) {
            formatter.insert(token);
          }
          break;
        case DEL:
          for (Token token : action.tokens()) {
            formatter.delete(token);
          }
          break;
        default:
      }
    }
  }

  public static void handle(List<Action> actions, DiffHandler handler) {
    for (Action action : actions) {
      for (Token token : action.tokens()) {
        handler.handle(action.operator(), token);
      }
    }
  }

  public static void applyTo(List<Action> actions, DiffHandler handler) {
    handler.start();
    for (Action action : actions) {
      for (Token token : action.tokens()) {
        handler.handle(action.operator(), token);
      }
    }
    handler.end();
  }

}
