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

import org.pageseeder.diffx.sequence.EventSequence;
import org.pageseeder.diffx.sequence.Sequence;

/**
 * A base class for Diff-X algorithms.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
@Deprecated
public abstract class DiffXAlgorithmBase implements DiffXAlgorithm {

  /**
   * The first sequence of tokens to test.
   */
  protected final EventSequence sequence1;

  /**
   * The second sequence of tokens to test.
   */
  protected final EventSequence sequence2;

  /**
   * Length of the first sequence to compare.
   */
  protected final int length1;

  /**
   * Length of the second sequence to compare.
   */
  protected final int length2;

  /**
   * The length of the LCS.
   */
  protected int length = -1;

  /**
   * Creates a new DiffX algorithm base class.
   *
   * @param first  The first sequence to compare.
   * @param second The second sequence to compare.
   */
  public DiffXAlgorithmBase(EventSequence first, EventSequence second) {
    this.sequence1 = first;
    this.sequence2 = second;
    this.length1 = first.size();
    this.length2 = second.size();
  }

  @Override
  public final EventSequence getFirstSequence() {
    return this.sequence1;
  }

  @Override
  public final EventSequence getSecondSequence() {
    return this.sequence2;
  }

}
