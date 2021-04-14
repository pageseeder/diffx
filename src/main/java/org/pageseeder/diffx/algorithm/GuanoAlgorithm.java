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

import java.io.IOException;

import org.pageseeder.diffx.event.AttributeEvent;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.format.ShortStringFormatter;
import org.pageseeder.diffx.sequence.EventSequence;

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
 * @version 0.8.0
 */
public final class GuanoAlgorithm implements DiffXAlgorithm {

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
   * The state of the elements.
   */
  private final transient ElementState estate = new ElementState();

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
  public GuanoAlgorithm(EventSequence seq0, EventSequence seq1) {
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
      // we can only insert or delete, priority to insert
      if (this.matrix.isGreaterX(i, j)) {
        // follow the natural path and insert
        if (this.estate.okInsert(e1) && !this.estate.hasPriorityOver(e2, e1)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+j+"] >i +"+ShortStringFormatter.toShortString(e1));
          }
          formatter.insert(e1);
          this.estate.insert(e1);
          i++;

          // if we can format checking at the stack, let's do it
        } else if (e1.equals(e2) && this.estate.okFormat(e1)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+(j+1)+"] >f "+ShortStringFormatter.toShortString(e1));
          }
          formatter.format(e1);
          this.estate.format(e1);
          i++; j++;

          // go counter current and delete
        } else if (this.estate.okDelete(e2)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+i+","+(j+1)+"] >d -"+ShortStringFormatter.toShortString(e2));
          }
          formatter.delete(e2);
          this.estate.delete(e2);
          j++;

        } else {
          if (DEBUG) {
            System.err.print("\n(i) case greater X");
          }
          if (DEBUG) {
            printLost(i, j);
          }
          break;
        }

        // we can only insert or delete, priority to delete
      } else if (this.matrix.isGreaterY(i, j)) {
        // follow the natural and delete
        if (this.estate.okDelete(e2) && !this.estate.hasPriorityOver(e1, e2)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+i+","+(j+1)+"] <d -"+ShortStringFormatter.toShortString(e2));
          }
          formatter.delete(e2);
          this.estate.delete(e2);
          j++;

          // if we can format checking at the stack, let's do it
        } else if (e1.equals(e2) && this.estate.okFormat(e1)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+(j+1)+"] <f "+ShortStringFormatter.toShortString(e1));
          }
          formatter.format(e1);
          this.estate.format(e1);
          i++; j++;

          // insert (counter-current)
        } else if (this.estate.okInsert(e1)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+j+"] <i +"+ShortStringFormatter.toShortString(e1));
          }
          formatter.insert(e1);
          this.estate.insert(e1);
          i++;

        } else {
          if (DEBUG) {
            System.err.println("\n(i) case greater Y");
          }
          if (DEBUG) {
            printLost(i, j);
          }
          break;
        }

        // elements from i inserted and j deleted
        // we have to make a choice for where we are going
      } else if (this.matrix.isSameXY(i, j)) {
        // if we can format checking at the stack, let's do it
        if (e1.equals(e2) && this.estate.okFormat(e1)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+(j+1)+"] =f "+ShortStringFormatter.toShortString(e1));
          }
          formatter.format(e1);
          this.estate.format(e1);
          i++; j++;

          // we can insert the closing tag
        } else if (this.estate.okInsert(e1)
            && !(e2 instanceof AttributeEvent && !(e1 instanceof AttributeEvent))) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+j+"] =i +"+ShortStringFormatter.toShortString(e1));
          }
          this.estate.insert(e1);
          formatter.insert(e1);
          i++;

          // we can delete the closing tag
        } else if (this.estate.okDelete(e2)
            && !(e1 instanceof AttributeEvent && !(e2 instanceof AttributeEvent))) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+i+","+(j+1)+"] =d -"+ShortStringFormatter.toShortString(e2));
          }
          formatter.delete(e2);
          this.estate.delete(e2);
          j++;

        } else {
          if (DEBUG) {
            System.err.println("\n(i) case same");
          }
          if (DEBUG) {
            printLost(i, j);
          }
          break;
        }
      } else {
        if (DEBUG) {
          System.err.println("\n(i) case ???");
        }
        if (DEBUG) {
          printLost(i, j);
        }
        break;
      }
      if (DEBUG) {
        System.err.println("    stack:"+this.estate.currentChange()+ShortStringFormatter.toShortString(this.estate.current()));
      }
    }

    // finish off the events from the first sequence
    while (i < this.length1) {
      if (DEBUG) {
        System.err.println("["+i+","+j+"]->["+(i+1)+","+j+"] _i -"+ShortStringFormatter.toShortString(this.sequence1.getEvent(i)));
      }
      this.estate.insert(this.sequence1.getEvent(i));
      formatter.insert(this.sequence1.getEvent(i));
      i++;
    }
    // finish off the events from the second sequence
    while (j < this.length2) {
      if (DEBUG) {
        System.err.println("["+i+","+j+"]->["+i+","+(j+1)+"] _d -"+ShortStringFormatter.toShortString(this.sequence2.getEvent(j)));
      }
      this.estate.delete(this.sequence2.getEvent(j));
      formatter.delete(this.sequence2.getEvent(j));
      j++;
    }
    // free some resources
    //    matrix.release();
  }


  /**
   * @see DiffXAlgorithm#getFirstSequence()
   */
  @Override
  public final EventSequence getFirstSequence() {
    return this.sequence1;
  }

  /**
   * @see DiffXAlgorithm#getSecondSequence()
   */
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

  /**
   * Print information when the algorithm gets lost in the matrix,
   * ie when it does not know which direction to follow.
   *
   * @param i The X position.
   * @param j The Y position.
   */
  private void printLost(int i, int j) {
    DiffXEvent e1 = this.sequence1.getEvent(i);
    DiffXEvent e2 = this.sequence2.getEvent(j);
    System.err.println("(!) Ambiguous choice in ("+i+","+j+")");
    System.err.println(" ? +"+ShortStringFormatter.toShortString(e1));
    System.err.println(" ? -"+ShortStringFormatter.toShortString(e2));
    System.err.println(" current="+ShortStringFormatter.toShortString(this.estate.current()));
    System.err.println(" value in X+1="+this.matrix.get(i+1, j));
    System.err.println(" value in Y+1="+this.matrix.get(i, j+1));
    System.err.println(" equals="+e1.equals(e2));
    System.err.println(" greaterX="+this.matrix.isGreaterX(i, j));
    System.err.println(" greaterY="+this.matrix.isGreaterY(i, j));
    System.err.println(" sameXY="+this.matrix.isSameXY(i, j));
    System.err.println(" okFormat1="+this.estate.okFormat(e1));
    System.err.println(" okFormat2="+this.estate.okFormat(e2));
    System.err.println(" okInsert="+this.estate.okInsert(e1));
    System.err.println(" okDelete="+this.estate.okDelete(e2));
  }

}
