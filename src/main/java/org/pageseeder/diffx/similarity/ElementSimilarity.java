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

import org.pageseeder.diffx.token.ElementToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.XMLTokenType;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * A concrete implementation of the {@link Similarity} interface specifically designed
 * for XML tokens. This class uses a provided {@link StreamSimilarity} implementation to compute
 * the similarity between child elements based on their text content and applies an optional
 * length-based boosting factor to refine the similarity score.
 *
 * <p>The class supports computing the similarity score between two XML tokens, focusing
 * on XML elements. For XML elements, the similarity is determined by their tag names and
 * the similarity of their children, while other token types are compared for equality.
 *
 * @author Christophe Lauret
 * @version 1.2.0
 * @since 1.2.0
 */
public class ElementSimilarity implements Similarity<XMLToken> {

  /**
   * A `StreamSimilarity` instance used to determine the similarity between
   * two streams of text derived from the child elements of XML tokens.
   */
  private final StreamSimilarity<String> similarity;

  /**
   * A length-boosting factor used to adjust the similarity score based on
   * the length of the XML elements being compared.
   */
  private final double k;

  /**
   * Constructs an instance of ElementSimilarity, which calculates similarity scores
   * for XML elements using a specified stream-based similarity metric.
   *
   * @param similarity A stream similarity implementation used to compute the similarity score
   *                   between streams of textual tokens derived from XML elements.
   * @param k          A boosting factor used to adjust the similarity score based on
   *                   additional criteria, such as element length or other contextual factors.
   */
  public ElementSimilarity(StreamSimilarity<String> similarity, double k) {
    this.similarity = Objects.requireNonNull(similarity);
    this.k = k;
  }

  @Override
  public final float score(XMLToken a, XMLToken b) {
    if (a.getType() == XMLTokenType.ELEMENT && b.getType() == XMLTokenType.ELEMENT) {
      return scoreForElement((ElementToken) a, (ElementToken) b);
    }
    return a.equals(b) ? 1.0f : 0;
  }

  private float scoreForElement(ElementToken a, ElementToken b) {
    boolean sameElementName = a.getStart().equals(b.getStart());
    // Don't bother if the first token is different
    if (!sameElementName) return 0;

    // Empty it's a match
    if (a.getContent().isEmpty() && b.getContent().isEmpty())
      return 1;

    float score = this.similarity.score(toTextStream(a), toTextStream(b));
    if (this.k <= 0) return score;

    int length = Math.min(a.getContent().size(), b.getContent().size());
    return lengthBoostedSimilarity(score, length);
  }

  /**
   * Adjusts a given similarity score by applying a length-based boosting factor.
   * The method modifies the input score to account for the length of the elements
   * being compared, using the length-boosting factor defined in the instance.
   *
   * @param score The initial similarity score between 0 and 1, to be adjusted.
   * @param length The length of the XML element used to compute the boosting adjustment.
   * @return The length-boosted similarity score as a float, where larger lengths
   *         lead to a potentially smaller adjustment depending on the boosting factor.
   */
  protected float lengthBoostedSimilarity(float score, int length) {
    return (float) Math.pow(score, 1.0 / Math.pow(length, k));
  }

  private Stream<String> toTextStream(ElementToken element) {
    return element.getContent().stream()
        .filter(t -> t.getType() == XMLTokenType.TEXT)
        .map(t -> t.getValue().trim());
  }

}
