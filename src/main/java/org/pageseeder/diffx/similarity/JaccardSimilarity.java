package org.pageseeder.diffx.similarity;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A concrete implementation of the {@link StreamSimilarity} interface that calculates
 * the similarity between two streams based on the Jaccard Similarity metric.
 *
 * <p>Jaccard Similarity measures the similarity between finite sets by comparing their
 * intersection and union. The similarity score is computed as the ratio of the size of
 * the intersection of the two sets to the size of their union.
 *
 * <p>A score of 1.0 indicates that the two streams are identical, while a score of 0.0
 * indicates no overlap between the two streams.
 *
 * <p>This implementation ensures efficiency by iterating over the smaller set when computing
 * the intersection size.
 *
 * @param <T> the type of elements in the streams
 *
 * @author Christophe Lauret
 * @version 1.1.3
 * @since 1.1.3
 */
public final class JaccardSimilarity<T> implements StreamSimilarity<T> {

  @Override
  public float score(@NotNull Stream<T> a, @NotNull Stream<T> b) {
    Set<T> setA = a.collect(Collectors.toSet());
    Set<T> setB = b.collect(Collectors.toSet());
    // Use the smaller set for iteration to maximize efficiency
    Set<T> smaller = setA.size() < setB.size() ? setA : setB;
    Set<T> larger = setA.size() < setB.size() ? setB : setA;
    int intersectionCount = 0;
    for (T s : smaller) {
      if (larger.contains(s)) intersectionCount++;
    }
    int unionCount = setA.size() + setB.size() - intersectionCount;
    return (float) intersectionCount / unionCount;
  }

}
