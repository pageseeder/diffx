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

import org.pageseeder.diffx.action.Operations;
import org.pageseeder.diffx.handler.*;
import org.pageseeder.diffx.token.Token;
import java.util.List;

/**
 * A processor implementation which attempts to solve the diff using the most efficient algorithm,
 * and falls back to the default processor if unable to produce correct results.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class OptimisticXMLProcessor implements DiffProcessor {

  private static final boolean DEBUG = false;

  private boolean coalesce = false;

  private int fallbackThreshold = MatrixXMLAlgorithm.DEFAULT_THRESHOLD;

  /**
   * Set whether to consecutive text operations should be coalesced into a single operation.
   *
   * @param coalesce <code>true</code> to coalesce; <code>false</code> to leave a separate operations.
   */
  public void setCoalesce(boolean coalesce) {
    this.coalesce = coalesce;
  }

  /**
   * Set the maximum amount of comparison in case the fast algorithm fails.
   */
  public void setFallbackThreshold(int fallbackThreshold) {
    this.fallbackThreshold = fallbackThreshold;
  }

  @Override
  public void diff(List<? extends Token> first, List<? extends Token> second, DiffHandler handler) {
    // Try...
    OperationHandler holding = new OperationHandler();
    DiffAlgorithm algorithm = new KumarRanganAlgorithm();
    PostXMLFixer fixer = new PostXMLFixer(holding);
    fixer.start();
    algorithm.diff(first, second, fixer);
    fixer.end();

    if (DEBUG && fixer.hasError()) {
      System.err.println("Optimistic has error:");
      System.err.println(holding.getOperations());
    }

    if (fixer.hasError()) {
      algorithm = new MatrixXMLAlgorithm();
      DiffHandler actual = getFilter(handler);
      handler.start();
      algorithm.diff(first, second, actual);
      handler.end();
    } else {
      DiffHandler out = getFilter(handler);
      out.start();
      Operations.handle(holding.getOperations(), out);
      out.end();
    }

  }

  private DiffHandler getFilter(DiffHandler handler) {
    return this.coalesce ? new CoalescingFilter(handler) : handler;
  }

  @Override
  public String toString() {
    return "OptimisticXMLProcessor{" +
        "coalesce=" + coalesce +
        '}';
  }

}
