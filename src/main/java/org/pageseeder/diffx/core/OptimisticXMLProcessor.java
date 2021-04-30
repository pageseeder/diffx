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
    // Try with fast diff
    OperationsBuffer buffer = new OperationsBuffer();
    boolean successful = fastDiff(first, second, buffer);
    if (successful) {
      buffer.applyTo(getFilter(handler));
    } else {
      if (DEBUG) {
        System.err.println("Optimistic has error:");
        System.err.println(buffer.getOperations());
      }
      fallbackDiff(first, second, getFilter(handler));
    }

  }

  private DiffHandler getFilter(DiffHandler handler) {
    return this.coalesce ? new CoalescingFilter(handler) : handler;
  }

  /**
   * Run fast algorithm and try to fix any XML errors after the diff.
   */
  private boolean fastDiff(List<? extends Token> first, List<? extends Token> second, OperationsBuffer buffer) {
    DiffAlgorithm algorithm = new KumarRanganAlgorithm();
    PostXMLFixer fixer = new PostXMLFixer(buffer);
    fixer.start();
    algorithm.diff(first, second, fixer);
    fixer.end();
    return !fixer.hasError();
  }

  /**
   * Fall back on slower matrix-based algorithm.
   */
  private void fallbackDiff(List<? extends Token> first, List<? extends Token> second, DiffHandler handler) {
    MatrixXMLAlgorithm algorithm = new MatrixXMLAlgorithm();
    algorithm.setThreshold(this.fallbackThreshold);
    DiffHandler actual = getFilter(handler);
    actual.start();
    algorithm.diff(first, second, actual);
    actual.end();
  }

  @Override
  public String toString() {
    return "OptimisticXMLProcessor{" +
        "coalesce=" + coalesce +
        ", fallbackThreshold=" + fallbackThreshold +
        '}';
  }
}
