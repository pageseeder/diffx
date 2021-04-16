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

/**
 * An atomic Diff operation associated with a single token.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class Operation {

  private final Operator operator;

  private final Token token;

  public Operation(Operator operator, Token token) {
    this.operator = operator;
    this.token = token;
  }

  public Operator operator() {
    return operator;
  }

  public Token token() {
    return token;
  }

  @Override
  public int hashCode() {
    return this.operator.hashCode()+ 31*this.token.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Operation) return equals((Operation)obj);
    return false;
  }

  public boolean equals(Operation operation) {
    if (operation == null)
      return false;
    if (operation == this)
      return true;
    return operation.operator == this.operator && operation.token.equals(this.token);
  }

  @Override
  public String toString() {
    return this.operator.toString()+this.token;
  }

  /**
   * @return the reserve operation by swapping INS with DEL.
   */
  public Operation flip() {
    return this.operator == Operator.MATCH ? this : new Operation(this.operator.flip(), this.token);
  }

}
