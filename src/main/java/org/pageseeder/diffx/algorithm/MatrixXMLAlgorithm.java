/*
 * Copyright (c) 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.algorithm;

import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Equality;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.sequence.TokenListSlicer;
import org.pageseeder.diffx.token.AttributeToken;
import org.pageseeder.diffx.token.XMLToken;

import java.util.List;

/**
 * An XML-aware algorithm based on the Wagner-Fisher algorithm.
 *
 * <p>This algorithm uses a matrix to compute the edit path and a stack to eliminate invalid paths.</p>
 *
 * @author Christophe Lauret
 *
 * @version 1.3.1
 * @since 0.9.0
 */
public final class MatrixXMLAlgorithm implements DiffAlgorithm<XMLToken> {

  /**
   * The default maximum number of comparisons allowed for this algorithm.
   */
  public static final int DEFAULT_THRESHOLD = 64_000_000;

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = false;

  /**
   * Set to <code>true</code> to allow sequence slicing.
   */
  private boolean slice = true;

  private int threshold = DEFAULT_THRESHOLD;

  /**
   * Determines the strategy to compare elements for equality within the diff algorithm.
   */
  private final Equality<XMLToken> eq;

  /**
   * Default constructor using token equality.
   */
  public MatrixXMLAlgorithm() {
    this.eq = XMLToken::equals;
  }

  /**
   * Constructor specifying the equality strategy.
   *
   * @param eq The strategy to compare elements for equality.
   */
  public MatrixXMLAlgorithm(Equality<XMLToken> eq) {
    this.eq = eq;
  }

  /**
   * Set whether common tokens at the beginning or the end of the sequences can be removed from the diff.
   *
   * @param slice true to slice start and end of sequences;
   *              false to force diffing on all tokens
   */
  public void setSlice(boolean slice) {
    this.slice = slice;
  }

  /**
   * Set the maximum number of tokens comparisons that can be performed.
   *
   * <p>If the number of tokens post-slicing is larger, it will throw an <code>IllegalArgumentException</code>.
   *
   * @param threshold Max number of token comparisons allowed
   */
  public void setThreshold(int threshold) {
    this.threshold = threshold;
  }

  /**
   * Determines whether the differences between two lists of {@link XMLToken} objects can be computed
   * based on the configured threshold and optional slicing.
   *
   * @param from The list of {@link XMLToken} objects representing the original sequence.
   * @param to The list of {@link XMLToken} objects representing the modified sequence.
   * @return {@code true} if the differences between the two lists can be computed;
   *         {@code false} otherwise.
   */
  public boolean isDiffComputable(List<? extends XMLToken> from, List<? extends XMLToken> to) {
    // Check without slicer from
    if (from.size() * to.size() <= this.threshold) return true;
    // Check if possible after slicing
    TokenListSlicer slicer = new TokenListSlicer(from, to);
    int commonCount = this.slice ? slicer.analyze() : 0;
    int matrixSize = (from.size() - commonCount) * (to.size() - commonCount);
    return matrixSize > this.threshold;
  }

  @Override
  public void diff(List<? extends XMLToken> from, List<? extends XMLToken> to, DiffHandler<XMLToken> handler) {
    final int lengthA = from.size();
    final int lengthB = to.size();

    // handle the case when one of the two sequences is empty
    if (lengthA == 0 || lengthB == 0) {
      // A is empty, insert all tokens from B
      for (XMLToken token : to) {
        handler.handle(Operator.INS, token);
      }
      // B is empty, delete all tokens from A
      for (XMLToken token : from) {
        handler.handle(Operator.DEL, token);
      }
      return;
    }

    // Initialize state
    ElementStackFilter estate = new ElementStackFilter(handler, this.eq);
    diff(from, to, estate);
  }

  private void diff(List<? extends XMLToken> A, List<? extends XMLToken> B, ElementStackFilter handler) {
    TokenListSlicer slicer = new TokenListSlicer(A, B);
    int common = this.slice ? slicer.analyze() : 0;

    // Check the end
    if (common > 0) {
      slicer.handleStart(handler);
      List<? extends XMLToken> subA = slicer.getSubSequence1();
      List<? extends XMLToken> subB = slicer.getSubSequence2();
      if (subA.isEmpty() || subB.isEmpty()) {
        for (XMLToken token : subB) handler.handle(Operator.INS, token);
        for (XMLToken token : subA) handler.handle(Operator.DEL, token);
      } else {
        processDiff(subA, subB, handler);
      }
      slicer.handleEnd(handler);
    } else {
      processDiff(A, B, handler);
    }
  }

  @SuppressWarnings("java:S106")
  private void processDiff(List<? extends XMLToken> A, List<? extends XMLToken> B, ElementStackFilter handler) {
    final int lengthA = A.size();
    final int lengthB = B.size();

    // Throws error if we can't process
    if (lengthA * lengthB > this.threshold)
      throw new DataLengthException(lengthA * lengthB, this.threshold);

    // calculate the LCS length to fill the matrix
    MatrixProcessor<XMLToken> builder = new MatrixProcessor<>();
    builder.setInverse(true);
    Matrix matrix = builder.process(A, B, this.eq);

    int i = 0;
    int j = 0;
    XMLToken tokenA;
    XMLToken tokenB;
    // start walking the matrix
    while (i < lengthA && j < lengthB) {
      tokenA = A.get(i);
      tokenB = B.get(j);
      // we can only insert or delete, priority to delete
      if (matrix.isGreaterX(i, j)) {
        // follow the natural path
        if (handler.isAllowed(Operator.DEL, tokenA) && !handler.hasPriorityOver(tokenB, tokenA)) {
          if (DEBUG) {
            System.err.print("[" + i + "," + j + "]->[" + (i + 1) + "," + j + "] >i +" + tokenA);
          }
          handler.handle(Operator.DEL, tokenA);
          i++;

          // if we can format checking at the stack, let's do it
        } else if (this.eq.equals(tokenA, tokenB) && handler.isAllowed(Operator.MATCH, tokenA)) {
          if (DEBUG) {
            System.err.print("[" + i + "," + j + "]->[" + (i + 1) + "," + (j + 1) + "] >f " + tokenA);
          }
          handler.handle(Operator.MATCH, tokenA);
          i++;
          j++;

          // go counter current and delete
        } else if (handler.isAllowed(Operator.INS, tokenB)) {
          if (DEBUG) {
            System.err.print("[" + i + "," + j + "]->[" + i + "," + (j + 1) + "] >d -" + tokenB);
          }
          handler.handle(Operator.INS, tokenB);
          j++;

        } else {
          if (DEBUG) {
            System.err.print("\n(i) case greater X");
            printLost(i, j, matrix, handler, A, B);
          }
          break;
        }

        // we can only insert or delete, priority to insert
      } else if (matrix.isGreaterY(i, j)) {
        // follow the natural and delete
        if (handler.isAllowed(Operator.INS, tokenB) && !handler.hasPriorityOver(tokenA, tokenB)) {
          if (DEBUG) {
            System.err.print("[" + i + "," + j + "]->[" + i + "," + (j + 1) + "] <d -" + tokenB);
          }
          handler.handle(Operator.INS, tokenB);
          j++;

          // if we can format checking at the stack, let's do it
        } else if (this.eq.equals(tokenA, tokenB) && handler.isAllowed(Operator.MATCH, tokenA)) {
          if (DEBUG) {
            System.err.print("[" + i + "," + j + "]->[" + (i + 1) + "," + (j + 1) + "] <f " + tokenA);
          }
          handler.handle(Operator.MATCH, tokenA);
          i++;
          j++;

          // insert (counter-current)
        } else if (handler.isAllowed(Operator.DEL, tokenA)) {
          if (DEBUG) {
            System.err.print("[" + i + "," + j + "]->[" + (i + 1) + "," + j + "] <i +" + tokenA);
          }
          handler.handle(Operator.DEL, tokenA);
          i++;

        } else {
          if (DEBUG) {
            System.err.println("\n(i) case greater Y");
            printLost(i, j, matrix, handler, A, B);
          }
          break;
        }

        // elements from i deleted and j inserted
        // we have to make a choice for where we are going
      } else if (matrix.isSameXY(i, j)) {
        // if we can format checking at the stack, let's do it
        if (this.eq.equals(tokenA, tokenB) && handler.isAllowed(Operator.MATCH, tokenA)) {
          if (DEBUG) {
            System.err.print("[" + i + "," + j + "]->[" + (i + 1) + "," + (j + 1) + "] =f " + tokenA);
          }
          handler.handle(Operator.MATCH, tokenA);
          i++;
          j++;

          // we can insert the closing tag
        } else if (handler.isAllowed(Operator.DEL, tokenA)
            && !(tokenB instanceof AttributeToken && !(tokenA instanceof AttributeToken))) {
          if (DEBUG) {
            System.err.print("[" + i + "," + j + "]->[" + (i + 1) + "," + j + "] =i +" + tokenA);
          }
          handler.handle(Operator.DEL, tokenA);
          i++;

          // we can delete the closing tag
        } else if (handler.isAllowed(Operator.INS, tokenB)
            && !(tokenA instanceof AttributeToken && !(tokenB instanceof AttributeToken))) {
          if (DEBUG) {
            System.err.print("[" + i + "," + j + "]->[" + i + "," + (j + 1) + "] =d -" + tokenB);
          }
          handler.handle(Operator.INS, tokenB);
          j++;

        } else {
          if (DEBUG) {
            System.err.println("\n(i) case same");
            printLost(i, j, matrix, handler, A, B);
          }
          break;
        }
      } else {
        if (DEBUG) {
          System.err.println("\n(i) case ???");
          printLost(i, j, matrix, handler, A, B);
        }
        break;
      }
      if (DEBUG) {
        System.err.println("    stack:" + handler.current());
      }
    }

    // finish off: delete remaining tokens from A
    while (i < lengthA) {
      if (DEBUG) {
        System.err.println("[" + i + "," + j + "]->[" + (i + 1) + "," + j + "] _i -" + A.get(i));
      }
      handler.handle(Operator.DEL, A.get(i));
      i++;
    }
    // finish off: insert remaining tokens from B
    while (j < lengthB) {
      if (DEBUG) {
        System.err.println("[" + i + "," + j + "]->[" + i + "," + (j + 1) + "] _d -" + B.get(j));
      }
      handler.handle(Operator.INS, B.get(j));
      j++;
    }
  }

  /**
   * Print information when the algorithm gets lost in the matrix,
   * ie when it does not know which direction to follow.
   *
   * @param i The X position.
   * @param j The Y position.
   */
  @SuppressWarnings("java:S106")
  private void printLost(int i, int j, Matrix matrix, ElementStackFilter estate, List<? extends XMLToken> first, List<? extends XMLToken> second) {
    XMLToken tokenA = first.get(i);
    XMLToken tokenB = second.get(j);
    System.err.println("(!) Ambiguous choice in (" + i + "," + j + ")");
    System.err.println(" ? +" + tokenA);
    System.err.println(" ? -" + tokenB);
    System.err.println(" current=" + estate.current());
    System.err.println(" value in X+1=" + matrix.get(i + 1, j));
    System.err.println(" value in Y+1=" + matrix.get(i, j + 1));
    System.err.println(" equals=" + this.eq.equals(tokenA, tokenB));
    System.err.println(" greaterX=" + matrix.isGreaterX(i, j));
    System.err.println(" greaterY=" + matrix.isGreaterY(i, j));
    System.err.println(" sameXY=" + matrix.isSameXY(i, j));
    System.err.println(" okFormat1=" + estate.isAllowed(Operator.MATCH, tokenA));
    System.err.println(" okFormat2=" + estate.isAllowed(Operator.MATCH, tokenB));
    System.err.println(" okDelete=" + estate.isAllowed(Operator.DEL, tokenA));
    System.err.println(" okInsert=" + estate.isAllowed(Operator.INS, tokenB));
  }

  @Override
  public String toString() {
    return "MatrixXMLAlgorithm{" +
        "slice=" + slice +
        ", threshold=" + threshold +
        '}';
  }
}
