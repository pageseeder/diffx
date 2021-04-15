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
package org.pageseeder.diffx.core;

import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.Operator;

import java.util.List;

public abstract class AlgorithmTest {

  /**
   * @return The processor instance to use for texting.
   */
  public abstract DiffAlgorithm getDiffAlgorithm();

  /**
   * Print the error details.
   */
  protected void printXMLErrorDetails(String xml1, String xml2, String[] exp, String got, List<Action> actions) {
    System.err.println("+------------------------------------------------");
    System.err.println("| Input A: \"" + xml1 + "\"");
    System.err.println("| Input B: \"" + xml2 + "\"");
    System.err.println("| Output:  \"" + got + "\"");
    System.err.print("| Expect:  ");
    for (String s : exp) System.err.print("\"" + s + "\" ");
    System.err.println();
    System.err.print("| Actions: ");
    for (Action action : actions) {
      System.err.print(action.operator() == Operator.DEL ? '-' : action.operator() == Operator.INS ? '+' : '=');
      System.err.print(action.events());
    }
    System.err.println();
  }

}
