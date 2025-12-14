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

import org.pageseeder.diffx.algorithm.MatrixXMLAlgorithm;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.handler.CoalescingFilter;
import org.pageseeder.diffx.token.XMLToken;

import java.util.List;

public class DefaultXMLProcessor extends DiffProcessorBase implements DiffProcessor<XMLToken> {

  private int threshold = MatrixXMLAlgorithm.DEFAULT_THRESHOLD;

  /**
   * Set the maximum number of token comparisons that can be performed.
   *
   * <p>If the number of tokens post-slicing is larger, it will throw an <code>IllegalArgumentException</code>.
   *
   * @param threshold Max number of token comparisons allowed
   */
  public void setThreshold(int threshold) {
    this.threshold = threshold;
  }

  /**
   * Set whether consecutive text operations should be coalesced into a single operation.
   *
   * @param coalesce <code>true</code> to coalesce; <code>false</code> to leave a separate operations.
   */
  @Override
  public void setCoalesce(boolean coalesce) {
    this.coalesce = coalesce;
  }

  public boolean isDiffComputable(List<XMLToken> from, List<XMLToken> to) {
    MatrixXMLAlgorithm algorithm = new MatrixXMLAlgorithm();
    algorithm.setThreshold(this.threshold);
    return algorithm.isDiffComputable(from, to);
  }

  @Override
  public void diff(List<? extends XMLToken> from, List<? extends XMLToken> to, DiffHandler<XMLToken> handler) {
    MatrixXMLAlgorithm algorithm = new MatrixXMLAlgorithm();
    algorithm.setThreshold(this.threshold);
    DiffHandler<XMLToken> actual = getFilter(handler);
    handler.start();
    algorithm.diff(from, to, actual);
    handler.end();
  }

  private DiffHandler<XMLToken> getFilter(DiffHandler<XMLToken> handler) {
    return this.coalesce ? new CoalescingFilter(handler) : handler;
  }

  @Override
  public String toString() {
    return "DefaultXMLProcessor{" +
        "coalesce=" + coalesce +
        '}';
  }
}
