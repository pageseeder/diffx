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

import org.jspecify.annotations.Nullable;
import org.pageseeder.diffx.api.Operator;

import java.util.Objects;

/**
 * An immutable atomic Diff operation on a single token.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class Operation<T> {

  private final Operator operator;

  private final T token;

  /**
   * Constructs a new {@code Operation} instance with the specified operator and token.
   *
   * @param operator The operator representing the type of operation (e.g., insertion, deletion, or match).
   *                 Must not be null.
   * @param token    The token associated with this operation. Must not be null.
   * @throws NullPointerException If {@code operator} or {@code token} is null.
   */
  public Operation(Operator operator, T token) {
    this.operator = Objects.requireNonNull(operator);
    this.token = Objects.requireNonNull(token);
  }

  public Operator operator() {
    return this.operator;
  }

  public T token() {
    return this.token;
  }

  @Override
  public int hashCode() {
    return this.operator.hashCode() + 31 * this.token.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Operation) return equals((Operation<?>) obj);
    return false;
  }

  public boolean equals(@Nullable Operation<?> operation) {
    if (operation == null)
      return false;
    if (operation == this)
      return true;
    return operation.operator == this.operator && operation.token.equals(this.token);
  }

  /**
   * Compares the specified operator and token with the operator and token of this {@code Operation} instance
   * for equality.
   *
   * @param operator The operator to be compared.
   * @param token    The token to be compared.
   * @return {@code true} if the specified operator and token match the operator and token of this instance;
   *         {@code false} otherwise.
   */
  public boolean equals(Operator operator, T token) {
    return operator == this.operator && token.equals(this.token);
  }

  @Override
  public String toString() {
    return this.operator.toString() + this.token;
  }

  /**
   * @return the reserve operation by swapping INS with DEL or the same operation if MATCH.
   */
  public Operation<T> flip() {
    return this.operator == Operator.MATCH ? this : new Operation<>(this.operator.flip(), this.token);
  }

}
