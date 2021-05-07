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

import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.token.Token;

import java.io.UncheckedIOException;
import java.util.List;

/**
 * A diff algorithm reports the differences between two lists of tokens as the shortest edit script (SES) to
 * the specified handler.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public interface DiffAlgorithm<T> {

  /**
   * Performs the comparison and report changes to the specified handler.
   *
   * @param from    The original list of tokens to compare (deleted)
   * @param to      The target list of tokens to compare (inserted)
   * @param handler The handler for the results of the comparison
   *
   * @throws UncheckedIOException If an IO error occurred while handler the diff
   * @throws IllegalStateException If the algorithm is in a state where it is unable to process the tokens.
   */
  void diff(List<? extends T> from, List<? extends T> to, DiffHandler<T> handler);

}
