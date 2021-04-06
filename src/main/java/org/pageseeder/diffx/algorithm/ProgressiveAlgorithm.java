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
package org.pageseeder.diffx.algorithm;

import org.pageseeder.diffx.format.CoalescingFormatter;
import org.pageseeder.diffx.format.CompareReplaceFormatter;
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;

/**
 * A matrix-based algorithm using weighted events which produces correct results, but may require
 * minor adjustments during formatting.
 *
 * <p>Implementation note: this algorithm effectively detects the correct changes in the
 * sequences, but will not necessarily return events that can be serialised as well-formed
 * XML as they stand.
 *
 * <p>Known problem in this implementation: elements that contain themselves tend to
 * generate events that are harder to serialise as XML.
 *
 * <p>This class is said 'fit' because it will adapt the matrix to the sequences that it
 * is being given in order to improve performance.
 *
 * <p>Note: The name of this class comes from a contracted version of the features of
 * this algorithm, as explained below:
 * <ul>
 *   <li><b>Weighted, each token is has a given weight;</li>
 *   <li><b>Symmetrical, when possible, the algorithm will try to choose a path
 *      that is symmetrical in regards to the arrangement of the tokens;</li>
 *   <li><b>Matrix, this class uses a matrix for its internal representation;</li>
 *   </li>
 * </ul>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class ProgressiveAlgorithm implements DiffXAlgorithm {

  /**
   * We use the guano algorithm as the main
   */
  private final GuanoAlgorithm main;

  /**
   * Creates a new DiffXAlgorithmBase.
   *
   * @param seq0 The first sequence to compare.
   * @param seq1 The second sequence to compare.
   */
  public ProgressiveAlgorithm(EventSequence seq0, EventSequence seq1) {
    this.main = new GuanoAlgorithm(seq0, seq1);
  }

  /**
   * Returns the length of the longest common sequence.
   *
   * @return the length of the longest common sequence.
   */
  @Override
  public int length() {
    // TODO The value is actually incorrect!
    return this.main.length();
  }

  /**
   * Writes the diff sequence using the specified formatter.
   *
   * @param formatter The formatter that will handle the output.
   *
   * @throws IOException If thrown by the formatter.
   */
  @Override
  public void process(DiffXFormatter formatter) throws IOException {
    CoalescingFormatter coalesce = new CoalescingFormatter(formatter);
    CompareReplaceFormatter compare = new CompareReplaceFormatter(coalesce);
    this.main.process(compare);
  }

  @Override
  public final EventSequence getFirstSequence() {
    return this.main.getFirstSequence();
  }

  @Override
  public final EventSequence getSecondSequence() {
    return this.main.getFirstSequence();
  }

}
