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

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.DiffException;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.xml.Sequence;

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
public interface XMLDiffProcessor extends DiffProcessor<XMLToken> {

  /**
   * Set whether consecutive text operations should be coalesced into a single operation.
   *
   * @param coalesce <code>true</code> to coalesce; <code>false</code> to leave a separate operations.
   */
  void setCoalesce(boolean coalesce);

  /**
   * Determines whether consecutive text operations are being coalesced into a single operation.
   *
   * @return true if coalescing is enabled, false otherwise.
   */
  boolean isCoalescing();

  /**
   * Performs the comparison and uses the specified handler.
   *
   * @param from    The original list of tokens to compare (deleted)
   * @param to      The target list of tokens to compare (inserted)
   * @param config  The diff config to use
   * @param handler The handler for the results of the comparison
   *
   * @throws DiffException Wrap any error occurring during processing.
   */
  void diff(Sequence from, Sequence to, DiffConfig config, DiffHandler<XMLToken> handler) throws DiffException;

  /**
   * Performs the comparison and uses the specified handler.
   *
   * @param from    The first list of tokens to compare (deleted)
   * @param to      The second list of tokens to compare (inserted)
   * @param handler The handler for the results of the comparison
   *
   * @throws UncheckedIOException     If thrown by the handler while writing output.
   * @throws IllegalStateException    If thrown by the algorithm or handler.
   * @throws IllegalArgumentException If the algorithm is unable to process to the list of tokens.
   */
  void diff(@NotNull List<? extends XMLToken> from, @NotNull List<? extends XMLToken> to, @NotNull DiffHandler<XMLToken> handler);

}
