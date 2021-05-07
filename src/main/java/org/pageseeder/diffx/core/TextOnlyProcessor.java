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

import java.util.List;

/**
 * An implementation of dynamic programming algorithm for computing the LCS.
 * <p>
 * It is designed for text only and designed for simple sequences of tokens.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TextOnlyProcessor extends DiffProcessorBase implements DiffProcessor<Token> {

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
  public void diff(List<? extends Token> from, List<? extends Token> to, DiffHandler<Token> handler) {
    handler.start();
    // handle the case when one of the two sequences is empty
    if (from.isEmpty() || to.isEmpty()) {
      for (Token token : to) handler.handle(Operator.INS, token);
      for (Token token : from) handler.handle(Operator.DEL, token);
    } else {

      TokenListSlicer slicer = new TokenListSlicer(from, to);
      int common = slicer.analyze();

      // Slice the beginning
      int startCount = slicer.getStartCount();
      int endCount = slicer.getEndCount();

      // Copy the end
      if (startCount > 0) {
        for (int i = 0; i < startCount; i++) handler.handle(Operator.MATCH, from.get(i));
      }

      // Check the end
      if (startCount > 0 || endCount > 0) {
        List<? extends Token> subA = from.subList(startCount, from.size() - endCount);
        List<? extends Token> subB = to.subList(startCount, to.size() - endCount);
        if (subA.isEmpty() || subB.isEmpty()) {
          for (Token token : subB) handler.handle(Operator.INS, token);
          for (Token token : subA) handler.handle(Operator.DEL, token);
        } else {
          DiffAlgorithm<Token> algorithm = getAlgorithm();
          algorithm.diff(subA, subB, handler);
        }

      } else {
        DiffAlgorithm<Token> algorithm = getAlgorithm();
        algorithm.diff(from, to, handler);
      }

      // Copy the end
      if (endCount > 0) {
        for (int i = from.size() - endCount; i < from.size(); i++) handler.handle(Operator.MATCH, from.get(i));
      }
    }
    handler.end();
  }

  @Override
  public String toString() {
    return "TextOnlyProcessor{algo=" + getAlgorithm().getClass().getSimpleName() + "}";
  }

  private DiffAlgorithm<Token> getAlgorithm() {
    switch (this.algo) {
      case HIRSCHBERG:
        return new HirschbergAlgorithm<>();
      case WAGNER_FISCHER:
        return new WagnerFischerAlgorithm<>();
      case KUMAR_RANGAN:
        return new KumarRanganAlgorithm<>();
      default:
        throw new IllegalStateException("No algorithm defined");
    }
  }

}
