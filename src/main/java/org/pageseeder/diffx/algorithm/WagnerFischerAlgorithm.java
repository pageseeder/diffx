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
import org.pageseeder.diffx.api.MatchPreferenceConfigurable;
import org.pageseeder.diffx.api.Operator;

import java.util.List;

/**
 * An implementation of the Wagner-Fisher algorithm with no optimization.
 *
 * @author Christophe Lauret
 *
 * @version 1.3.2
 * @since 0.9.0
 */
public final class WagnerFischerAlgorithm<T> implements DiffAlgorithm<T>, MatchPreferenceConfigurable {

  /**
   * Determines the strategy to compare elements for equality within the diff algorithm.
   */
  private final Equality<T> eq;

  /**
   * Determines which side's element to emit when elements match.
   */
  private boolean preferFrom = false;

  /**
   * Default constructor using token equality.
   */
  public WagnerFischerAlgorithm() {
    this.eq = T::equals;
  }

  /**
   * Constructor specifying the equality strategy.
   *
   * @param eq The strategy to compare elements for equality.
   */
  public WagnerFischerAlgorithm(Equality<T> eq) {
    this.eq = eq;
  }

  /**
   * Whether to keep matching elements from the from list (true) or to list (false).
   *
   * @return <code>true</code> if matching elements should be kept from the "from" list,
   *         <code>false</code> otherwise.
   */
  @Override
  public boolean isPreferFrom() {
    return this.preferFrom;
  }

  /**
   * Whether to keep matching elements from the from list (true) or to list (false).
   *
   * @param preferFrom True to keep matching elements from the from list, false to keep from the to list.
   */
  @Override
  public void setPreferFrom(boolean preferFrom) {
    this.preferFrom = preferFrom;
  }

  @Override
  public void diff(List<? extends T> from, List<? extends T> to, DiffHandler<T> handler) {
    // calculate the LCS length to fill the matrix
    MatrixProcessor<T> builder = new MatrixProcessor<>();
    builder.setInverse(true);
    Matrix matrix = builder.process(from, to, this.eq);
    final int length1 = from.size();
    final int length2 = to.size();
    int i = 0;
    int j = 0;
    T t1;
    T t2;

    // Backtrack start walking the matrix
    while (i < length1 && j < length2) {
      t1 = from.get(i);
      t2 = to.get(j);
      if (matrix.isGreaterX(i, j)) {
        handler.handle(Operator.DEL, t1);
        i++;
      } else if (matrix.isGreaterY(i, j)) {
        handler.handle(Operator.INS, t2);
        j++;
      } else if (matrix.isSameXY(i, j)) {
        if (this.eq.equals(t1, t2)) {
          handler.handle(Operator.MATCH, this.preferFrom ? t1 : t2);
          i++;
          j++;
        } else {
          handler.handle(Operator.DEL, t1);
          i++;
        }
      }
    }

    // finish off the tokens from A
    for (; i < length1; i++) {
      handler.handle(Operator.DEL, from.get(i));
    }
    // finish off the tokens from B
    for (; j < length2; j++) {
      handler.handle(Operator.INS, to.get(j));
    }
  }
}
