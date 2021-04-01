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
import org.pageseeder.diffx.format.ShortStringFormatter;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;

/**
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TextOnlyAlgorithm implements DiffXAlgorithm {

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = false;

  /**
   * The first sequence of events to test.
   */
  private final EventSequence sequence1;

  /**
   * The second sequence of events to test.
   */
  private final EventSequence sequence2;

  /**
   * Length of the first sequence to compare.
   */
  private final int length1;

  /**
   * Length of the second sequence to compare.
   */
  private final int length2;

  /**
   * Matrix storing the paths.
   */
  private transient Matrix matrix;

  /**
   * The length of the LCS.
   */
  private transient int length = -1;

  /**
   * Creates a new DiffXAlgorithmBase.
   *
   * @param seq0 The first sequence to compare.
   * @param seq1 The second sequence to compare.
   */
  public TextOnlyAlgorithm(EventSequence seq0, EventSequence seq1) {
    this.sequence1 = seq0;
    this.sequence2 = seq1;
    this.length1 = seq0.size();
    this.length2 = seq1.size();
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
    // handle the case when one of the two sequences is empty
    processEmpty(formatter);
    if (this.length1 == 0 || this.length2 == 0) return;
    // calculate the LCS length to fill the matrix
    length();
    int i = 0;
    int j = 0;
    DiffXEvent e1;
    DiffXEvent e2;
    // start walking the matrix
    while (i < this.length1 && j < this.length2) {
      e1 = this.sequence1.getEvent(i);
      e2 = this.sequence2.getEvent(j);
      if (this.matrix.isGreaterX(i, j)) {
        if (DEBUG) {
          System.err.print("["+i+","+j+"]->["+(i+1)+","+j+"] +"+ShortStringFormatter.toShortString(e1));
        }
        formatter.insert(e1);
        i++;
      } else if (this.matrix.isGreaterY(i, j)) {
        if (DEBUG) {
          System.err.print("["+i+","+j+"]->["+i+","+(j+1)+"] -"+ShortStringFormatter.toShortString(e2));
        }
        formatter.delete(e2);
        j++;
      } else if (this.matrix.isSameXY(i, j)) {
        if (DEBUG) {
          System.err.print("["+i+","+j+"]->["+(i+1)+","+(j+1)+"] ="+ShortStringFormatter.toShortString(e1));
        }
        if (e1.equals(e2)) {
          formatter.format(e1);
          i++; j++;
        } else {
          formatter.insert(e1);
          i++;
        }

      } else {
        System.err.print("["+i+","+j+"]->?");
      }
      if (DEBUG) System.err.println();
    }

    // finish off the events from the first sequence
    while (i < this.length1) {
      formatter.insert(this.sequence1.getEvent(i));
      i++;
    }
    // finish off the events from the second sequence
    while (j < this.length2) {
      formatter.delete(this.sequence2.getEvent(j));
      j++;
    }
    // free some resources
    // matrix.release();
  }

  @Override
  public final EventSequence getFirstSequence() {
    return this.sequence1;
  }

  @Override
  public final EventSequence getSecondSequence() {
    return this.sequence2;
  }

  /**
   * Writes the diff sequence using the specified formatter when one of
   * the sequences is empty.
   *
   * <p>The result becomes either only insertions (when the second sequence is
   * empty) or deletions (when the first sequence is empty).
   *
   * @param formatter The formatter that will handle the output.
   *
   * @throws IOException If thrown by the formatter.
   */
  private void processEmpty(DiffXFormatter formatter) throws IOException {
    // the first sequence is empty, events from the second sequence have been deleted
    if (this.length1 == 0) {
      for (int i = 0; i < this.length2; i++) {
        formatter.delete(this.sequence2.getEvent(i));
      }
    }
    // the second sequence is empty, events from the first sequence have been inserted
    if (this.length2 == 0) {
      for (int i = 0; i < this.length1; i++) {
        formatter.insert(this.sequence1.getEvent(i));
      }
    }
  }

}
