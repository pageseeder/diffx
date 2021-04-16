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

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.event.Token;

import java.io.UncheckedIOException;

/**
 * An interface for handling the output of diff processors.
 *
 * <p>Handlers may be used to filter or format the operations of diff processors.
 *
 * @author Christophe Lauret
 *
 * @version 0.9.0
 */
public interface DiffHandler {

  /**
   * Receives notification of the start of the processing.
   *
   * This method is called before any other method.
   */
  default void start() {}

  /**
   * Handles the specified operation reported by the diff processor.
   *
   * @param operator The operator
   * @param token    The token to handle
   *
   * @throws UncheckedIOException Should an I/O exception occur.
   * @throws IllegalStateException If the handler is not in a state to run this method.
   */
  void handle(Operator operator, Token token) throws UncheckedIOException, IllegalStateException;

  /**
   * Receives notification of the end of the processing.
   *
   * This method is called after any other method.
   */
  default void end() {}

}
