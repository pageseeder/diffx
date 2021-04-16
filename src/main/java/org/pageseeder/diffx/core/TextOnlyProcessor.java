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
import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.token.Token;

import java.util.Iterator;
import java.util.List;

/**
 * An implementation of dynamic programming algorithm for computing the LCS.
 *
 * It is designed for text only and designed for simple sequences of tokens.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TextOnlyProcessor implements DiffProcessor {

  /**
   * The main algorithms to choose from.
   */
  public enum Algorithm {
    HIRSCHBERG,
    WAGNER_FISCHER,
    KUMAR_RANGAN
  }

  private final Algorithm algo;

  /**
   * Create a text only processor using Kumar-Rangan's algorithm.
   */
  public TextOnlyProcessor() {
    this(Algorithm.HIRSCHBERG);
  }

  public TextOnlyProcessor(Algorithm algorithm) {
    this.algo = algorithm;
  }

  @Override
  public void diff(List<? extends Token> first, List<? extends Token> second, DiffHandler handler) {
    handler.start();
    // handle the case when one of the two sequences is empty
    if (first.isEmpty() || second.isEmpty()) {
      for (Token token : second) handler.handle(Operator.DEL, token);
      for (Token token : first) handler.handle(Operator.INS, token);
    } else {

      // Slice the beginning
      int start = sliceStart(first, second);
      int end = sliceEnd(first, second, start);

      // Copy the end
      if (start > 0) {
        for (int i=0; i < start; i++) handler.handle(Operator.MATCH, first.get(i));
      }

      // Check the end
      if (start > 0 || end > 0) {
        List<? extends Token> firstSub = first.subList(start, first.size()-end);
        List<? extends Token> secondSub = second.subList(start, second.size()-end);
        if (firstSub.isEmpty() || secondSub.isEmpty()) {
          for (Token token : secondSub) handler.handle(Operator.DEL, token);
          for (Token token : firstSub) handler.handle(Operator.INS, token);
        } else {
          DiffAlgorithm algorithm = getAlgorithm();
          algorithm.diff(firstSub, secondSub, handler);
        }

      } else {
        DiffAlgorithm algorithm = getAlgorithm();
        algorithm.diff(first, second, handler);
      }

      // Copy the end
      if (end > 0) {
        for (int i=first.size()-end; i < first.size(); i++) handler.handle(Operator.MATCH, first.get(i));
      }
    }
    handler.end();
  }

  @Override
  public String toString() {
    return "TextOnlyProcessor{algo="+getAlgorithm().getClass().getSimpleName()+"}";
  }

  private DiffAlgorithm getAlgorithm() {
    switch (this.algo) {
      case HIRSCHBERG: return new HirschbergAlgorithm();
      case WAGNER_FISCHER: return new WagnerFischerAlgorithm();
      case KUMAR_RANGAN: return new KumarRanganAlgorithm();
      default: throw new IllegalStateException("No algorithm defined");
    }
  }

  /**
   * Slices the start of both sequences.
   *
   * @return The number of common elements at the start of the sequences.
   */
  public static int sliceStart(List<? extends Token> first, List<? extends Token> second) {
    int count = 0;
    Iterator<? extends Token> i = first.iterator();
    Iterator<? extends Token> j = second.iterator();
    while (i.hasNext() && j.hasNext()) {
      Token token = i.next();
      if (j.next().equals(token)) {
        count++;
      } else return count;
    }
    return count;
  }

  /**
   * Slices the end of both sequences.
   *
   * @return The number of common elements at the end of the sequences.
   *
   * @throws IllegalStateException If the end buffer is not empty.
   */
  public int sliceEnd(List<? extends Token> first, List<? extends Token> second, int start) {
    int count = 0;
    int i = first.size() - 1, j = second.size() - 1;
    for (; i >= start && j >= start; i--, j--) {
      Token token = first.get(i);
      if (token.equals(second.get(j))) {
        count++;
      } else return count;
    }
    return count;
  }

}
