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

/**
 * Calculates similarity between two elements.
 *
 * <p>Returns a value between 0.0 (completely different) and
 * 1.0 (identical) indicating how similar the elements are.
 *
 * @param <T> the type of elements being compared
 *
 * @author Christophe Lauret
 * @version 1.1.3
 * @since 1.1.3
 */
@FunctionalInterface
public interface Similarity<T> {

  /**
   * Calculates the similarity score between two elements.
   *
   * <p>The score represents how similar the two elements are, ranging from 0.0 to 1.0,
   * where 0.0 indicates completely different and 1.0 indicates identical.
   *
   * @param a the first element to compare, must not be null
   * @param b the second element to compare, must not be null
   * @return a float value between 0.0 and 1.0 representing the similarity score
   */
  float score(@NotNull T a, @NotNull T b);

}
