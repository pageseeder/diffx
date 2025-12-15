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

import org.pageseeder.diffx.api.Equality;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.xml.Sequence;

import java.io.PrintStream;
import java.util.List;

/**
 * Build the matrix for the specified tokens using dynamic programming.
 *
 * @author Christophe Lauret
 *
 * @version 1.4.0
 * @since 0.9.0
 */
public final class MatrixProcessor<T> {

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
  public Matrix process(Sequence first, Sequence second) {
    return process(first, second, XMLToken::equals);
  }

  /**
   * @param first  The first sequence of tokens to test.
   * @param second The second sequence of tokens to test.
   * @param eq The strategy to compare elements for equality.
   *
   * @return the matrix using dynamic programming
   */
  public Matrix process(Sequence first, Sequence second, Equality<XMLToken> eq) {
    return this.inverse ? computeInverse(first.tokens(), second.tokens(), eq) : compute(first.tokens(), second.tokens(), eq);
  }

  /**
   * @param first  The first sequence of tokens to test.
   * @param second The second sequence of tokens to test.
   *
   * @return the matrix using dynamic programming
   */
  public Matrix process(List<? extends T> first, List<? extends T> second) {
    return process(first, second, T::equals);
  }

  /**
   * @param first  The first sequence of tokens to test.
   * @param second The second sequence of tokens to test.
   * @param eq The strategy to compare elements for equality.
   *
   * @return the matrix using dynamic programming
   */
  public Matrix process(List<? extends T> first, List<? extends T> second, Equality<T> eq) {
    return this.inverse ? computeInverse(first, second, eq) : compute(first, second, eq);
  }

  private static <T> Matrix compute(List<? extends T> first, List<? extends T> second, Equality<T> eq) {
    Matrix matrix = getMatrix(first, second, false);
    int length1 = first.size();
    int length2 = second.size();
    matrix.setup(length1 + 1, length2 + 1);
    // allocate storage for array L
    for (int i = 0; i < length1 + 1; i++) {
      for (int j = 0; j < length2 + 1; j++) {
        // we reach the end of the sequence (fill with 0)
        if (i == 0 || j == 0) {
          matrix.set(i, j, 0);
        } else {
          if (eq.equals(first.get(i - 1), second.get(j - 1))) {
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

  private static <T> Matrix computeInverse(List<? extends T> first, List<? extends T> second, Equality<T> eq) {
    Matrix matrix = getMatrix(first, second, true);
    int length1 = first.size();
    int length2 = second.size();
    matrix.setup(length1 + 1, length2 + 1);
    // allocate storage for array L
    for (int i = length1; i >= 0; i--) {
      for (int j = length2; j >= 0; j--) {
        // we reach the end of the sequence (fill with 0)
        if (i >= length1 || j >= length2) {
          matrix.set(i, j, 0);
        } else {
          if (eq.equals(first.get(i), second.get(j))) {
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

  @SuppressWarnings("unused")
  private static <T> void printDebug(List<? extends T> first, List<? extends T> second, Matrix matrix, PrintStream out) {
    out.print("A:");
    for (T token : first) {
      out.print(token + "\t");
    }
    out.println();
    out.print("B:");
    for (T token : second) {
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
  private static <T> Matrix getMatrix(List<? extends T> first, List<? extends T> second, boolean inverse) {
    if (first.size() + 1 > Short.MAX_VALUE || second.size() + 1 > Short.MAX_VALUE)
      return inverse ? new InvMatrixInt() : new MatrixInt();
    else
      return inverse ? new InvMatrixShort() : new MatrixShort();
  }

}
