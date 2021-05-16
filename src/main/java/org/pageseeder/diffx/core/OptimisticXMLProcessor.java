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

import org.pageseeder.diffx.algorithm.DataLengthException;
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.algorithm.MatrixXMLAlgorithm;
import org.pageseeder.diffx.algorithm.MyersGreedyAlgorithm;
import org.pageseeder.diffx.handler.CoalescingFilter;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.handler.OperationsBuffer;
import org.pageseeder.diffx.handler.PostXMLFixer;
import org.pageseeder.diffx.token.XMLToken;

import java.util.List;

/**
 * A processor implementation which attempts to solve the diff using the most efficient algorithm,
 * and falls back to the default processor if unable to produce correct results.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class OptimisticXMLProcessor extends DiffProcessorBase implements XMLDiffProcessor {

  private static final boolean DEBUG = false;

  private int fallbackThreshold = MatrixXMLAlgorithm.DEFAULT_THRESHOLD;

  private boolean isDownscaleAllowed = true;

  public void setDownscaleAllowed(boolean allowed) {
    this.isDownscaleAllowed = allowed;
  }

  boolean isDownscaleAllowed() {
    return this.isDownscaleAllowed;
  }

  /**
   * Set the maximum amount of comparison in case the fast algorithm fails.
   */
  public void setFallbackThreshold(int fallbackThreshold) {
    this.fallbackThreshold = fallbackThreshold;
  }

  @Override
  public void diff(List<? extends XMLToken> from, List<? extends XMLToken> to, DiffHandler<XMLToken> handler) {
    // Try with fast diff
    OperationsBuffer<XMLToken> buffer = new OperationsBuffer<>();
    boolean successful = fastDiff(from, to, buffer);
    if (successful) {
      buffer.applyTo(getFilter(handler));
    } else {
      // Fallback on default diff
      if (DEBUG) System.err.println("Fast diff failed! Falling back on default diff");
      fallbackDiff(from, to, getFilter(handler), false);
    }
  }

  private DiffHandler<XMLToken> getFilter(DiffHandler<XMLToken> handler) {
    return this.coalesce ? new CoalescingFilter(handler) : handler;
  }

  /**
   * Run fast algorithm and try to fix any XML errors after the diff.
   */
  private boolean fastDiff(List<? extends XMLToken> from, List<? extends XMLToken> to, OperationsBuffer<XMLToken> buffer) {
    DiffAlgorithm<XMLToken> algorithm = new MyersGreedyAlgorithm<>();
    PostXMLFixer fixer = new PostXMLFixer(buffer);
    fixer.start();
    algorithm.diff(from, to, fixer);
    fixer.end();
    return !fixer.hasError();
  }

  /**
   * Fall back on slower matrix-based algorithm.
   */
  private void fallbackDiff(List<? extends XMLToken> from, List<? extends XMLToken> to, DiffHandler<XMLToken> handler, boolean coalesced) {
    MatrixXMLAlgorithm algorithm = new MatrixXMLAlgorithm();
    algorithm.setThreshold(this.fallbackThreshold);
    DiffHandler<XMLToken> actual = getFilter(handler);
    if (algorithm.isDiffComputable(from, to)) {
      actual.start();
      algorithm.diff(from, to, actual);
      actual.end();
    } else if (!coalesced && this.isDownscaleAllowed) {
      if (DEBUG) System.err.println("Coalescing content to");
      List<? extends XMLToken> a = CoalescingFilter.coalesce(from);
      List<? extends XMLToken> b = CoalescingFilter.coalesce(to);
      fallbackDiff(a, b, handler, true);
    } else {
      throw new DataLengthException(from.size() * to.size(), this.fallbackThreshold);
    }
  }

  @Override
  public String toString() {
    return "OptimisticXMLProcessor{" +
        "coalesce=" + coalesce +
        ", fallbackThreshold=" + fallbackThreshold +
        '}';
  }
}
