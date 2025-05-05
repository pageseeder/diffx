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

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;

import java.util.List;

/**
 * An implementation of the Wagner-Fisher algorithm with no optimisation.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class WagnerFischerAlgorithm<T> implements DiffAlgorithm<T> {

  @Override
  public void diff(@NotNull List<? extends T> from, @NotNull List<? extends T> to, @NotNull DiffHandler<T> handler) {
    // calculate the LCS length to fill the matrix
    MatrixProcessor<T> builder = new MatrixProcessor<>();
    builder.setInverse(true);
    Matrix matrix = builder.process(from, to);
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
        if (t1.equals(t2)) {
          handler.handle(Operator.MATCH, t1);
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
