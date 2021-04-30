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

import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.token.Token;

import java.io.UncheckedIOException;
import java.util.List;

/**
 * A diff processor performs comparisons on the specified resources.
 * <p>
 * It can be configurable and may use different algorithm depending on the task.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public interface DiffProcessor extends DiffAlgorithm {

  /**
   * Performs the comparison and uses the specified handler.
   *
   * @param first   The first list of tokens to compare (inserted)
   * @param second  The first list of tokens to compare (deleted)
   * @param handler The handler for the results of the comparison
   *
   * @throws UncheckedIOException     If thrown by the handler while writing output.
   * @throws IllegalStateException    If thrown by the algorithm or handler.
   * @throws IllegalArgumentException If the algorithm is unable to process to the list of tokens.
   */
  void diff(List<? extends Token> first, List<? extends Token> second, DiffHandler handler);

}
