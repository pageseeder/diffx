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
import org.pageseeder.diffx.algorithm.DiffAlgorithm;
import org.pageseeder.diffx.algorithm.HirschbergAlgorithm;
import org.pageseeder.diffx.algorithm.KumarRanganAlgorithm;
import org.pageseeder.diffx.algorithm.WagnerFischerAlgorithm;
import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.sequence.TokenListSlicer;
import org.pageseeder.diffx.token.Token;

import java.util.Iterator;
import java.util.List;

/**
 * An implementation of dynamic programming algorithm for computing the LCS.
 * <p>
 * It is designed for text only and designed for simple sequences of tokens.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TextOnlyProcessor extends DiffProcessorBase implements DiffProcessor {

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
    this(Algorithm.KUMAR_RANGAN);
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

      TokenListSlicer slicer = new TokenListSlicer(first, second);
      int common = slicer.analyze();

      // Slice the beginning
      int startCount = slicer.getStartCount();
      int endCount = slicer.getEndCount();

      // Copy the end
      if (startCount > 0) {
        for (int i = 0; i < startCount; i++) handler.handle(Operator.MATCH, first.get(i));
      }

      // Check the end
      if (startCount > 0 || endCount > 0) {
        List<? extends Token> firstSub = first.subList(startCount, first.size() - endCount);
        List<? extends Token> secondSub = second.subList(startCount, second.size() - endCount);
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
      if (endCount > 0) {
        for (int i = first.size() - endCount; i < first.size(); i++) handler.handle(Operator.MATCH, first.get(i));
      }
    }
    handler.end();
  }

  @Override
  public String toString() {
    return "TextOnlyProcessor{algo=" + getAlgorithm().getClass().getSimpleName() + "}";
  }

  private DiffAlgorithm getAlgorithm() {
    switch (this.algo) {
      case HIRSCHBERG:
        return new HirschbergAlgorithm();
      case WAGNER_FISCHER:
        return new WagnerFischerAlgorithm();
      case KUMAR_RANGAN:
        return new KumarRanganAlgorithm();
      default:
        throw new IllegalStateException("No algorithm defined");
    }
  }

}
