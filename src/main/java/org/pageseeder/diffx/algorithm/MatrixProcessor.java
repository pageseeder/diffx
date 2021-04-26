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
import org.pageseeder.diffx.token.Token;

import java.io.PrintStream;
import java.util.List;

/**
 * Build the matrix for the specified tokens using dynamic programming.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class MatrixProcessor {

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = false;

  private boolean inverse = false;

  public void setInverse(boolean inverse) {
    this.inverse = inverse;
  }

  /**
   * @param first  The first sequence of tokens to test.
   * @param second The second sequence of tokens to test.
   *
   * @return the matrix using dynamic programming
   */
  @Deprecated
  public Matrix process(EventSequence first, EventSequence second) {
    return process(first.getSequence(), second.getSequence());
  }

  /**
   * @param first  The first sequence of tokens to test.
   * @param second The second sequence of tokens to test.
   *
   * @return the matrix using dynamic programming
   */
  public Matrix process(Sequence first, Sequence second) {
    Matrix matrix = this.inverse ? computeInverse(first.tokens(), second.tokens()) : compute(first.tokens(), second.tokens());
    if (DEBUG) {
      printDebug(first.tokens(), second.tokens(), matrix, System.err);
    }
    return matrix;
  }

  /**
   * @param first  The first sequence of tokens to test.
   * @param second The second sequence of tokens to test.
   *
   * @return the matrix using dynamic programming
   */
  public Matrix process(List<? extends Token> first, List<? extends Token> second) {
    Matrix matrix = this.inverse ? computeInverse(first, second) : compute(first, second);
    if (DEBUG) {
      printDebug(first, second, matrix, System.err);
    }
    return matrix;
  }


  private static Matrix compute(List<? extends Token> first, List<? extends Token> second) {
    Matrix matrix = getMatrix(first, second, false);
    int length1 = first.size();
    int length2 = second.size();
    matrix.setup(length1 + 1, length2 + 1);
    // allocate storage for array L;
    for (int i = 0; i < length1 + 1; i++) {
      for (int j = 0; j < length2 + 1; j++) {
        // we reach the end of the sequence (fill with 0)
        if (i == 0 || j == 0) {
          matrix.set(i, j, 0);
        } else {
          if (first.get(i - 1).equals(second.get(j - 1))) {
            // the tokens are the same
            matrix.incrementPath(i, j);
          } else {
            // different tokens
            matrix.incrementByMaxPath(i, j);
          }
        }
      }
    }
    return matrix;
  }

  private static Matrix computeInverse(List<? extends Token> first, List<? extends Token> second) {
    Matrix matrix = getMatrix(first, second, true);
    int length1 = first.size();
    int length2 = second.size();
    matrix.setup(length1 + 1, length2 + 1);
    // allocate storage for array L;
    for (int i = length1; i >= 0; i--) {
      for (int j = length2; j >= 0; j--) {
        // we reach the end of the sequence (fill with 0)
        if (i >= length1 || j >= length2) {
          matrix.set(i, j, 0);
        } else {
          if (first.get(i).equals(second.get(j))) {
            // the tokens are the same
            matrix.incrementPath(i, j);
          } else {
            // different tokens
            matrix.incrementByMaxPath(i, j);
          }
        }
      }
    }
    return matrix;
  }

  private static void printDebug(List<? extends Token> first, List<? extends Token> second, Matrix matrix, PrintStream out) {
    out.print("A:");
    for (Token token : first) {
      out.print(token + "\t");
    }
    out.println();
    out.print("B:");
    for (Token token : second) {
      out.print(token + "\t");
    }
    out.println();
    out.println(matrix);
  }

  /**
   * Determines the most appropriate matrix to use based on the length of the sequences.
   *
   * @param first  The first sequence.
   * @param second The second sequence.
   *
   * @return The most appropriate matrix.
   */
  private static Matrix getMatrix(List<? extends Token> first, List<? extends Token> second, boolean inverse) {
    if (first.size() + 1 > Short.MAX_VALUE || second.size() + 1 > Short.MAX_VALUE)
      return inverse ? new InvMatrixInt() : new MatrixInt();
    else
      return inverse ? new InvMatrixShort() : new MatrixShort();
  }

}
