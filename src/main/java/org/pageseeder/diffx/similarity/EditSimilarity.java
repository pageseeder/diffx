/*
 * Copyright 2010-2025 Allette Systems (Australia)
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
import org.pageseeder.diffx.algorithm.MyersGreedyAlgorithm;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A concrete implementation of the {@link StreamSimilarity} interface that calculates
 * the similarity between two streams of elements based on the edit distance.
 *
 * <p>This implementation leverages the Myers Greedy Algorithm to compute the number of
 * edits (insertions, deletions, or substitutions) required to transform one stream into another.
 * The similarity score is then computed based on the proportion of edit operations relative
 * to the total number of tokens in both streams.
 *
 * <p>A score of 1.0 indicates that both streams are identical, while a score closer to 0.0
 * indicates lower similarity.
 *
 * @param <T> the type of elements in the streams
 *
 * @author Christophe Lauret
 * @version 1.1.3
 * @since 1.1.3
 */
public final class EditSimilarity<T> implements StreamSimilarity<T> {

  @Override
  public float score(@NotNull Stream<T> a, @NotNull Stream<T> b) {
    MyersGreedyAlgorithm<T> alg = new MyersGreedyAlgorithm<>();
    EditCounter<T> counter = new EditCounter<>();
    alg.diff(a.collect(Collectors.toList()), b.collect(Collectors.toList()), counter);
    return counter.score();
  }

  private static class EditCounter<T> implements DiffHandler<T> {

    int edits = 0;
    int tokens = 0;

    @Override
    public void handle(Operator operator, T token) {
      if (operator == Operator.MATCH) {
        tokens += 2;
      } else {
        edits += 1;
        tokens += 1;
      }
    }

    float score() {
      if (tokens == 0) return .5f;
      if (edits == 0) return 1;
      return 1 - (edits / (float)tokens);
    }

  }
}
