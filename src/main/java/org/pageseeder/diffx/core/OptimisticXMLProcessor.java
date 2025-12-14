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

import org.pageseeder.diffx.action.OperationsBuffer;
import org.pageseeder.diffx.algorithm.DataLengthException;
import org.pageseeder.diffx.algorithm.MatrixXMLAlgorithm;
import org.pageseeder.diffx.algorithm.MyersGreedyAlgorithm;
import org.pageseeder.diffx.algorithm.MyersGreedyXMLAlgorithm;
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.handler.CoalescingFilter;
import org.pageseeder.diffx.handler.PostXMLFixer;
import org.pageseeder.diffx.token.XMLToken;

import java.util.List;

/**
 * The optimistic XML processor attempts to process XML using a non-XML algorithm and fall back on to
 * an XML-aware algorithm if it is unable to produce a well-formed diff solution.
 *
 * <p>In many cases, when changes occur within text, the generic LCS is the same as the XML-LCS. When
 * that is the case, an correct XML solution can simply be found be reordering the tokens in the results
 * using the {@link PostXMLFixer}. Since generic LCS solution are more efficient than their XML
 * counterpart, we get the solution more efficiently.</p>
 *
 * @author Christophe Lauret
 * @version 1.2.0
 * @since 0.9.0
 */
public final class OptimisticXMLProcessor extends DiffProcessorBase implements XMLDiffProcessor {

  private int fallbackThreshold = MatrixXMLAlgorithm.DEFAULT_THRESHOLD;

  private boolean isDownscaleAllowed = true;

  public void setDownscaleAllowed(boolean allowed) {
    this.isDownscaleAllowed = allowed;
  }

  boolean isDownscaleAllowed() {
    return this.isDownscaleAllowed;
  }

  /**
   * Sets the threshold value for falling back to a slower or alternative diff algorithm.
   *
   * <p>Effectively, set the maximum amount of comparison in case the fast algorithm fails.
   *
   * @param fallbackThreshold The threshold value indicating the point at which
   *                          a fallback algorithm should be used.
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
      try {
        fallbackDiffMyers(from, to, getFilter(handler));
      } catch (IllegalStateException ex) {
        // In some rare cases Myers XML fails, we fall back on the matrix
        fallbackDiffMatrix(from, to, getFilter(handler), false);
      }
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
  private void fallbackDiffMatrix(List<? extends XMLToken> from, List<? extends XMLToken> to, DiffHandler<XMLToken> handler, boolean coalesced) {
    MatrixXMLAlgorithm algorithm = new MatrixXMLAlgorithm();
    DiffHandler<XMLToken> actual = getFilter(handler);
    if (algorithm.isDiffComputable(from, to)) {
      actual.start();
      algorithm.diff(from, to, actual);
      actual.end();
    } else if (!coalesced && this.isDownscaleAllowed) {
      List<? extends XMLToken> a = CoalescingFilter.coalesce(from);
      List<? extends XMLToken> b = CoalescingFilter.coalesce(to);
      fallbackDiffMatrix(a, b, handler, true);
    } else {
      throw new DataLengthException(from.size() * to.size(), this.fallbackThreshold);
    }
  }

  /**
   * Fall back on XML algorithm
   */
  private void fallbackDiffMyers(List<? extends XMLToken> from, List<? extends XMLToken> to, DiffHandler<XMLToken> handler) {
    MyersGreedyXMLAlgorithm algorithm = new MyersGreedyXMLAlgorithm();
    DiffHandler<XMLToken> actual = getFilter(handler);
    actual.start();
    algorithm.diff(from, to, actual);
    actual.end();
  }


  @Override
  public String toString() {
    return "OptimisticXMLProcessor{" +
        "coalesce=" + coalesce +
        '}';
  }
}
