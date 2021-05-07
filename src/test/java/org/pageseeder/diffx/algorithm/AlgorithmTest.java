/*
 * Copyright (c) 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.algorithm;

import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.Operator;

import java.util.List;

public abstract class AlgorithmTest {

  /**
   * Print the error details.
   */
  public static void printXMLErrorDetails(String xmlA, String xmlB, String[] exp, String got, List<Action> actions) {
    System.err.println("+------------------------------------------------");
    System.err.println("| Input A: \"" + xmlA + "\"");
    System.err.println("| Input B: \"" + xmlB + "\"");
    System.err.println("| Output:  \"" + got + "\"");
    if (exp != null && exp.length > 0) {
      System.err.println("| Expect:  \"" + exp[0] + "\" ");
      for (int i=1; i < exp.length; i++) {
        System.err.println("|     or:  \"" + exp[i] + "\" ");
      }
    }
    System.err.println();
    System.err.print("| Actions: ");
    for (Action action : actions) {
      System.err.print(action.operator() == Operator.DEL ? '-' : action.operator() == Operator.INS ? '+' : '=');
      System.err.print(action.tokens());
    }
    System.err.println();
  }

  public static String flip(String exp) {
    return exp.replace('+', '*').replace('-', '+').replace('*', '-');
  }

  public static String[] flip(String... exp) {
    String[] flipped = new String[exp.length];
    for (int i=0; i < exp.length; i++) {
      flipped[i] = flip(exp[i]);
    }
    return flipped;
  }

  /**
   * @return The algorithm instance to use for texting.
   */
  public abstract DiffAlgorithm getDiffAlgorithm();

}
