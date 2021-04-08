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

import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;

/**
 * An implementation of dynamic programming algorithm for computing the LCS.
 *
 * It is designed for text only.
 *
 * <p>It is a variation on the Wagner-Fischer Algorithm which computes in O(mn) time
 * and O(mn) space.</p>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TextOnlyAlgorithm implements DiffXAlgorithm {

  /**
   * The first sequence of events to test.
   */
  private final EventSequence sequence1;

  /**
   * The second sequence of events to test.
   */
  private final EventSequence sequence2;

  /**
   * Matrix storing the paths.
   */
  private transient Matrix matrix;

  /**
   * The length of the LCS.
   */
  private transient int length = -1;

  /**
   * Creates a new instance.
   *
   * @param first The first sequence to compare (the "new" one).
   * @param second The second sequence to compare (the "old" one).
   */
  public TextOnlyAlgorithm(EventSequence first, EventSequence second) {
    this.sequence1 = first;
    this.sequence2 = second;
    this.matrix = null;
  }

  /**
   * Returns the length of the longest common sequence.
   *
   * @return the length of the longest common sequence.
   */
  @Override
  public int length() {
    if (this.length < 0) {
      MatrixProcessor builder = new MatrixProcessor();
      builder.setInverse(true);
      this.matrix = builder.process(this.sequence1, this.sequence2);
      this.length = this.matrix.getLCSLength();
    }
    return this.length;
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
    final int length1 = this.sequence1.size();
    final int length2 = this.sequence2.size();

    // handle the case when one of the two sequences is empty
    if (length1 == 0) {
      for (DiffXEvent event : this.sequence2.events()) formatter.delete(event);
      return;
    }
    // the second sequence is empty, events from the first sequence have been inserted
    if (length2 == 0) {
      for (DiffXEvent event : this.sequence1.events()) formatter.insert(event);
      return;
    }
    // calculate the LCS length to fill the matrix
    length();
    int i = 0;
    int j = 0;
    DiffXEvent e1;
    DiffXEvent e2;
    // start walking the matrix
    while (i < length1 && j < length2) {
      e1 = this.sequence1.getEvent(i);
      e2 = this.sequence2.getEvent(j);
      if (this.matrix.isGreaterX(i, j)) {
        formatter.insert(e1);
        i++;
      } else if (this.matrix.isGreaterY(i, j)) {
        formatter.delete(e2);
        j++;
      } else if (this.matrix.isSameXY(i, j)) {
        if (e1.equals(e2)) {
          formatter.format(e1);
          i++; j++;
        } else {
          formatter.insert(e1);
          i++;
        }
      }
    }

    // finish off the events from the first sequence
    for (; i < length1; i++) {
      formatter.insert(this.sequence1.getEvent(i));
    }
    // finish off the events from the second sequence
    for (; j < length2; j++) {
      formatter.delete(this.sequence2.getEvent(j));
    }
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
