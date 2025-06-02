package org.pageseeder.diffx.similarity;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * A generic interface for calculating the similarity score between two streams of arbitrary elements.
 * Implementations of this interface provide specific algorithms for measuring the similarity
 * between collections of elements, such as Jaccard, Cosine, or other similarity metrics.
 *
 * @param <T> the type of elements in the streams
 *
 * @author Christophe Lauret
 * @version 1.1.3
 * @since 1.1.3
 */
public interface StreamSimilarity<T> extends Similarity<Stream<T>> {

  /**
   * Calculates the similarity score between two streams of elements.
   * The implementation and the scoring algorithm may vary depending on the specific
   * similarity metric being used (e.g., Jaccard, Cosine, Edit-based).
   *
   * @param a the first input stream of elements
   * @param b the second input stream of elements
   * @return a float value representing the calculated similarity score between
   *         the two streams, where the range and interpretation of the value
   *         depend on the specific implementation.
   */
  float score(@NotNull Stream<T> a, @NotNull Stream<T> b);

}
