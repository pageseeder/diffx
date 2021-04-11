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
package org.pageseeder.diffx.core;

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.algorithm.Matrix;
import org.pageseeder.diffx.algorithm.MatrixProcessor;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.handler.DiffHandler;

import java.util.List;

/**
 * An implementation of the Wagner-Fisher algorithm with no optimisation.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class WagnerFischerAlgorithm implements DiffAlgorithm {

  @Override
  public void diff(List<? extends DiffXEvent> first, List<? extends DiffXEvent> second, DiffHandler handler) {
    // calculate the LCS length to fill the matrix
    MatrixProcessor builder = new MatrixProcessor();
    builder.setInverse(true);
    Matrix matrix = builder.process(first, second);
    final int length1 = first.size();
    final int length2 = second.size();
    int i = 0;
    int j = 0;
    DiffXEvent e1;
    DiffXEvent e2;

    // Backtrack start walking the matrix
    while (i < length1 && j < length2) {
      e1 = first.get(i);
      e2 = second.get(j);
      if (matrix.isGreaterX(i, j)) {
        handler.handle(Operator.INS, e1);
        i++;
      } else if (matrix.isGreaterY(i, j)) {
        handler.handle(Operator.DEL, e2);
        j++;
      } else if (matrix.isSameXY(i, j)) {
        if (e1.equals(e2)) {
          handler.handle(Operator.MATCH, e1);
          i++;
          j++;
        } else {
          handler.handle(Operator.INS, e1);
          i++;
        }
      }
    }

    // finish off the events from the first sequence
    for (; i < length1; i++) {
      handler.handle(Operator.INS, first.get(i));
    }
    // finish off the events from the second sequence
    for (; j < length2; j++) {
      handler.handle(Operator.DEL, second.get(j));
    }
  }
}
