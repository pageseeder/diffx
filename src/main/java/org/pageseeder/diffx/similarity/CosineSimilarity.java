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

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A concrete implementation of the {@link StreamSimilarity} interface that calculates
 * the similarity between two streams using the Cosine Similarity metric.
 *
 * <p>Cosine Similarity measures the similarity between two non-zero vectors in a multidimensional
 * space by calculating the cosine of the angle between them. It is often used to compare
 * frequency distributions or tokenized data streams.
 *
 * <p>This implementation constructs frequency vectors from the input streams and computes
 * the cosine similarity score. A score close to 1.0 represents high similarity, and a
 * score closer to 0.0 indicates low similarity.
 *
 * @param <T> the type of elements in the streams
 *
 * @author Christophe Lauret
 * @version 1.1.3
 * @since 1.1.3
 */
public final class CosineSimilarity<T> implements StreamSimilarity<T> {

  @Override
  public float score(Stream<T> a, Stream<T> b) {
    Map<T, Integer> freqA = toFreqMap(a);
    Map<T, Integer> freqB = toFreqMap(b);

    // Calculate the dot product and magnitudes
    double dotProduct = 0.0;
    double magA = 0.0;
    double magB = 0.0;

    for (Map.Entry<T, Integer> entry : freqA.entrySet()) {
      T token = entry.getKey();
      int countA = entry.getValue();
      int countB = freqB.getOrDefault(token, 0);
      dotProduct += countA * countB;
      magA += countA * countA;
    }
    // Complete magnitude for B
    for (int countB : freqB.values()) {
      magB += countB * countB;
    }

    // Handle the zero vector case (no children)
    if (magA == 0.0 || magB == 0.0) return 1.0f;

    return (float) (dotProduct / (Math.sqrt(magA) * Math.sqrt(magB)));
  }

  private Map<T, Integer> toFreqMap(Stream<T> children) {
    return children.collect(Collectors.toMap( item -> item, item -> 1, Integer::sum));
  }
}
