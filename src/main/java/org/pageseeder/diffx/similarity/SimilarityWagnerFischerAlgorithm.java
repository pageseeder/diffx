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
package org.pageseeder.diffx.similarity;

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.token.Token;

import java.util.List;

/**
 * Implements the Wagner-Fischer algorithm for computing the differences between two
 * sequences of tokens, based on similarity measures. The algorithm computes the
 * optimal alignment by maximizing similarity, considering operations like match, deletion,
 * and insertion.
 *
 * @param <T> The type of token this algorithm operates on, which must extend the {@code Token} class.
 *
 * @author Christophe Lauret
 * @version 1.2.0
 * @since 1.1.2
 */
public final class SimilarityWagnerFischerAlgorithm<T extends Token> implements DiffAlgorithm<T> {

  // Define constants for operations (easily fits in a byte)
  private static final byte MATCH = 0;
  private static final byte DELETE = 1;
  private static final byte INSERT = 2;

  private final Similarity<T> similarity;
  private final float minThreshold;

  /**
   * Creates a new Wagner-Fischer algorithm for computing the differences between the two.
   *
   * @param similarity The similarity function to use to compute the similarity between two tokens.
   * @param minThreshold The minimum similarity threshold to consider a token as a match.
   */
  public SimilarityWagnerFischerAlgorithm(Similarity<T> similarity, float minThreshold) {
    this.similarity = similarity;
    this.minThreshold = minThreshold;
  }

  @Override
  public void diff(@NotNull List<? extends T> from, @NotNull List<? extends T> to, @NotNull DiffHandler<T> handler) {
    // Early termination for empty lists
    if (from.isEmpty()) {
      for (T t : to) {
        handler.handle(Operator.INS, t);
      }
      return;
    }
    if (to.isEmpty()) {
      for (T t : from) {
        handler.handle(Operator.DEL, t);
      }
      return;
    }

    // Special handling for single-element lists
    if (from.size() == 1 && to.size() == 1) {
      T fromToken = from.get(0);
      T toToken = to.get(0);
      float score = this.similarity.score(fromToken, toToken);
      if (score >= this.minThreshold) {
        handler.handle(Operator.MATCH, fromToken);
      } else {
        handler.handle(Operator.DEL, fromToken);
        handler.handle(Operator.INS, toToken);
      }
      return;
    }

    Instance<T> instance = new Instance<>(from, to);
    instance.process(handler, this.similarity, this.minThreshold);
  }

  /**
   * Represents a computational instance for processing differences
   * between two lists of elements of type {@code T} based on their similarity.
   *
   * @param <T> the type of token this algorithm operates on
   */
  private static class Instance<T> {

    private final List<? extends T> from;
    private final List<? extends T> to;

    Instance(List<? extends T> from, List<? extends T> to) {
      this.from = from;
      this.to = to;
    }

    public void process(DiffHandler<T> handler, Similarity<T> similarity, float minThreshold) {
      byte[][] decisions = computeDecisions(similarity, minThreshold);
      handle(decisions, handler);
    }

    /**
     * Calculates the decision matrix required for identifying optimal
     * operations (insert, delete, or match) between two sequences based
     * on similarity scores and a minimum similarity threshold.
     *
     * @param similarity A function to compute the similarity score
     *                           between elements of the two sequences.
     * @param minThreshold       A minimum similarity score required to
     *                           consider two elements as matching.
     *
     * @return A 2D byte array representing decisions for each pair of elements
     *         of the sequences. The values indicate the operations:
     *         MATCH, DELETE, or INSERT.
     */
    private byte[][] computeDecisions(Similarity<T> similarity, float minThreshold) {
      final int fromSize = from.size();
      final int toSize = to.size();

      // We only need two rows for the score matrix
      float[] prevRow = new float[toSize + 1];
      float[] currRow = new float[toSize + 1];

      // Create the decision matrix (we need the full matrix for backtracking)
      byte[][] decisions = new byte[fromSize + 1][toSize + 1];

      // Initialize the last row and column of decisions
      // These represent the cost of inserting/deleting all remaining elements
      for (int j = 0; j <= toSize; j++) {
        decisions[fromSize][j] = INSERT;
      }

      for (int i = 0; i <= fromSize; i++) {
        decisions[i][toSize] = DELETE;
      }

      // Fill the score matrix one row at a time, starting from the end
      for (int i = fromSize - 1; i >= 0; i--) {
        // Start from the end of the second sequence
        for (int j = toSize - 1; j >= 0; j--) {
          // Calculate score on demand
          float score = similarity.score(from.get(i), to.get(j));

          float matchScore = prevRow[j+1] + (score >= minThreshold ? score : 0);
          float deleteScore = prevRow[j];
          float insertScore = currRow[j+1];

          // Choose the action with the highest score
          if (matchScore >= deleteScore && matchScore >= insertScore && score >= minThreshold) {
            currRow[j] = matchScore;
            decisions[i][j] = MATCH;
          } else if (deleteScore >= insertScore) {
            currRow[j] = deleteScore;
            decisions[i][j] = DELETE;
          } else {
            currRow[j] = insertScore;
            decisions[i][j] = INSERT;
          }
        }

        // Swap rows for the next iteration
        float[] temp = prevRow;
        prevRow = currRow;
        currRow = temp;
      }

      return decisions;
    }

    /**
     * Processes the decision matrix to determine and execute operations (match, delete, or insert)
     * between elements in two sequences.
     *
     * @param decisions A 2D byte array representing decisions for each pair of elements in the sequences.
     *                  Each value indicates the operation to perform: MATCH, DELETE, or INSERT.
     * @param handler   The object responsible for handling the diff operations.
     */
    private void handle(byte[][] decisions, DiffHandler<T> handler) {
      int i = 0;
      int j = 0;

      // Start from the beginning and follow decisions
      while (i < from.size() && j < to.size()) {
        byte decision = decisions[i][j];
        if (decision == MATCH) {
          handler.handle(Operator.MATCH, from.get(i));
          i++;
          j++;
        } else if (decision == DELETE) {
          handler.handle(Operator.DEL, from.get(i));
          i++;
        } else { // INSERT
          handler.handle(Operator.INS, to.get(j));
          j++;
        }
      }

      // We've exhausted 'to', delete remaining from 'from'
      while (i < from.size()) {
        handler.handle(Operator.DEL, from.get(i));
        i++;
      }
      // We've exhausted 'from', insert remaining from 'to'
      while (j < to.size()) {
        handler.handle(Operator.INS, to.get(j));
        j++;
      }
    }
  }
}