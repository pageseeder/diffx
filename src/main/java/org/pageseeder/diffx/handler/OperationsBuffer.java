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
package org.pageseeder.diffx.handler;

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.Operator;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates a list of operations from the output of the algorithms.
 * <p>
 * This handler is useful to capture the operations resulting from a diff.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @see OperationsBuffer
 */
public class OperationsBuffer<T> implements DiffHandler<T> {

  /**
   * The list of operations produced by this handler.
   */
  private final List<Operation<T>> operations = new ArrayList<>();

  /**
   * Create a new operation for the operator and token.
   */
  @Override
  public void handle(@NotNull Operator operator, T token) {
    this.operations.add(new Operation<>(operator, token));
  }

  /**
   * @return the list of operations generated by this formatter.
   */
  public List<Operation<T>> getOperations() {
    return this.operations;
  }

  @Override
  public String toString() {
    return "OperationsBuffer";
  }

  /**
   * Apply the operations captured by this handler to the specified handler.
   * <p>
   * This method invokes both the start and end methods on the handler.
   *
   * @param handler receives start, handler and end events.
   */
  public void applyTo(DiffHandler<T> handler) {
    handler.start();
    for (Operation<T> operation : this.operations) {
      handler.handle(operation.operator(), operation.token());
    }
    handler.end();
  }
}
