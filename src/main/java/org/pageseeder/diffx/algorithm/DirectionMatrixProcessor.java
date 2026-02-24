/*
 * Copyright 2010-2021 Allette Systems (Australia)
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

import java.util.List;

/**
 * Builds a direction-only matrix for LCS-based backtracking.
 *
 * @author Christophe Lauret
 *
 * @version 1.3.3
 * @since 1.3.3
 */
public final class DirectionMatrixProcessor<T> {

  private boolean inverse = false;

  public void setInverse(boolean inverse) {
    this.inverse = inverse;
  }

  public DirectionMatrix process(Sequence first, Sequence second) {
    return process(first, second, XMLToken::equals);
  }

  public DirectionMatrix process(Sequence first, Sequence second, Equality<XMLToken> eq) {
    return this.inverse ? computeInverse(first.tokens(), second.tokens(), eq)
        : throwUnsupported();
  }

  public DirectionMatrix process(List<? extends T> first, List<? extends T> second) {
    return process(first, second, T::equals);
  }

  public DirectionMatrix process(List<? extends T> first, List<? extends T> second, Equality<T> eq) {
    return this.inverse ? computeInverse(first, second, eq) : throwUnsupported();
  }

  private static DirectionMatrix throwUnsupported() {
    throw new IllegalStateException("DirectionMatrixProcessor currently supports inverse matrices only");
  }

  private static <T> DirectionMatrix computeInverse(List<? extends T> first, List<? extends T> second, Equality<T> eq) {
    int length1 = first.size();
    int length2 = second.size();
    DirectionMatrix matrix = new DirectionMatrix();
    matrix.setup(length1 + 1, length2 + 1);

    int[] nextRow = new int[length2 + 1];
    int[] currRow = new int[length2 + 1];

    for (int i = length1; i >= 0; i--) {
      currRow[length2] = 0;
      for (int j = length2 - 1; j >= 0; j--) {
        if (i == length1) {
          currRow[j] = 0;
          matrix.setDirection(i, j, DirectionMatrix.compare(0, 0));
        } else {
          if (eq.equals(first.get(i), second.get(j))) {
            currRow[j] = nextRow[j + 1] + 1;
          } else {
            currRow[j] = Math.max(currRow[j + 1], nextRow[j]);
          }
          matrix.setDirection(i, j, DirectionMatrix.compare(nextRow[j], currRow[j + 1]));
        }
      }
      int[] tmp = nextRow;
      nextRow = currRow;
      currRow = tmp;
    }

    return matrix;
  }
}
