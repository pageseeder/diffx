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

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.token.Token;

import java.util.List;

/**
 * An implementation of the Wagner-Fisher algorithm with no optimisation.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class WagnerFischerAlgorithm implements DiffAlgorithm {

  @Override
  public void diff(List<? extends Token> first, List<? extends Token> second, DiffHandler handler) {
    // calculate the LCS length to fill the matrix
    MatrixProcessor builder = new MatrixProcessor();
    builder.setInverse(true);
    Matrix matrix = builder.process(first, second);
    final int length1 = first.size();
    final int length2 = second.size();
    int i = 0;
    int j = 0;
    Token t1;
    Token t2;

    // Backtrack start walking the matrix
    while (i < length1 && j < length2) {
      t1 = first.get(i);
      t2 = second.get(j);
      if (matrix.isGreaterX(i, j)) {
        handler.handle(Operator.INS, t1);
        i++;
      } else if (matrix.isGreaterY(i, j)) {
        handler.handle(Operator.DEL, t2);
        j++;
      } else if (matrix.isSameXY(i, j)) {
        if (t1.equals(t2)) {
          handler.handle(Operator.MATCH, t1);
          i++;
          j++;
        } else {
          handler.handle(Operator.INS, t1);
          i++;
        }
      }
    }

    // finish off the tokens from the first sequence
    for (; i < length1; i++) {
      handler.handle(Operator.INS, first.get(i));
    }
    // finish off the tokens from the second sequence
    for (; j < length2; j++) {
      handler.handle(Operator.DEL, second.get(j));
    }
  }
}
