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

import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.token.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates a list of actions from the output of the algorithms.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public class ActionFormatter implements DiffXFormatter {

  /**
   * The list of actions produced by this formatter.
   */
  private final List<Action> actions = new ArrayList<>();

  /**
   * The action used in the last operation.
   */
  private Action action = null;

  /**
   * Adds the token to an action of type 'DEL'.
   *
   * @param token The token to delete.
   * @see Operator#DEL
   */
  public void delete(Token token) {
    setupAction(Operator.DEL);
    this.action.add(token);
  }

  /**
   * Adds the token to an action of type 'KEEP'.
   *
   * @param token The token to format.
   * @see Operator#MATCH
   */
  public void format(Token token) {
    setupAction(Operator.MATCH);
    this.action.add(token);
  }

  /**
   * Adds the token to an action type 'INS'.
   *
   * @param token The token to insert.
   * @see Operator#INS
   */
  public void insert(Token token) {
    setupAction(Operator.INS);
    this.action.add(token);
  }

  /**
   * Does nothing - the configuration does not affect the action.
   */
  public void setConfig(DiffXConfig config) {
  }

  /**
   * @return the list of actions generated by this formatter.
   */
  public List<Action> getActions() {
    return this.actions;
  }

  /**
   * Sets up the action prior to handling the operation.
   * <p>
   * If the action does not exist or is of a different type, create a new one
   * and add to the list of actions.
   *
   * @param type The type of action.
   */
  private void setupAction(Operator type) {
    if (action == null || action.operator() != type) {
      this.action = new Action(type);
      this.actions.add(this.action);
    }
  }

}
