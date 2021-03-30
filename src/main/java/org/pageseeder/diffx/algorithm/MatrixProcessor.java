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

import org.pageseeder.diffx.format.ShortStringFormatter;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.PrintStream;

/**
 * Build the matrix for the specified events using dynamic programming.
 *
 * @author Christophe Lauret
 *
 * @version 0.9.0
 */
public final class MatrixProcessor {

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = true;

  private boolean inverse = false;

  public void setInverse(boolean inverse) {
    this.inverse = inverse;
  }

  /**
   *
   * @param first  The first sequence of events to test.
   * @param second The second sequence of events to test.
   *
   * @return the matrix using dynamic programming
   */
  public Matrix processor(EventSequence first, EventSequence second) {
    Matrix matrix = this.inverse ? computeInverse(first, second) : compute(first, second);
    if (DEBUG) {
      printDebug(first, second, matrix, System.err);
    }
    return matrix;
  }

  private static Matrix compute(EventSequence first, EventSequence second) {
    Matrix matrix = getMatrix(first, second, false);
    int length1 = first.size();
    int length2 = second.size();
    matrix.setup(length1+1, length2+1);
    // allocate storage for array L;
    for (int i = 0; i < length1+1; i++) {
      for (int j = 0; j < length2+1; j++) {
        // we reach the end of the sequence (fill with 0)
        if (i == 0 || j == 0) {
          matrix.set(i, j, 0);
        } else {
          if (first.getEvent(i-1).equals(second.getEvent(j-1))) {
            // the events are the same
            matrix.incrementPath(i, j);
          } else {
            // different events
            matrix.incrementByMaxPath(i, j);
          }
        }
      }
    }
    return matrix;
  }

  private static Matrix computeInverse(EventSequence first, EventSequence second) {
    Matrix matrix = getMatrix(first, second, true);
    int length1 = first.size();
    int length2 = second.size();
    matrix.setup(length1+1, length2+1);
    // allocate storage for array L;
    for (int i = length1; i >= 0; i--) {
      for (int j = length2; j >= 0; j--) {
        // we reach the end of the sequence (fill with 0)
        if (i >= length1 || j >= length2) {
          matrix.set(i, j, 0);
        } else {
          if (first.getEvent(i).equals(second.getEvent(j))) {
            // the events are the same
            matrix.incrementPath(i, j);
          } else {
            // different events
            matrix.incrementByMaxPath(i, j);
          }
        }
      }
    }
    return matrix;
  }

  private static void printDebug(EventSequence first, EventSequence second, Matrix matrix, PrintStream out) {
    out.print("A:");
    for (int i = 0; i < first.size(); i++) {
      out.print(ShortStringFormatter.toShortString(first.getEvent(i))+"\t");
    }
    out.println();
    out.print("B:");
    for (int i = 0; i < second.size(); i++) {
      out.print(ShortStringFormatter.toShortString(second.getEvent(i))+"\t");
    }
    out.println();
    out.println(matrix);
  }


  /**
   * Determines the most appropriate matrix to use based on the length of the sequences.
   *
   * @param first The first sequence.
   * @param second The second sequence.
   *
   * @return The most appropriate matrix.
   */
  private static Matrix getMatrix(EventSequence first, EventSequence second, boolean inverse) {
    if (first.size()+1 > Short.MAX_VALUE || second.size()+1 > Short.MAX_VALUE)
      return inverse ? new InvMatrixInt() : new MatrixInt();
    else
      return inverse ? new InvMatrixShort() : new MatrixShort();
  }

}
