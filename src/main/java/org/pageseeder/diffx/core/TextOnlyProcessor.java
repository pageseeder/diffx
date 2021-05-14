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
import org.pageseeder.diffx.algorithm.*;
import org.pageseeder.diffx.handler.DiffHandler;

import java.util.Iterator;
import java.util.List;

/**
 * A processors for the text only tokens.
 * <p>
 * It is designed for text only and designed for simple sequences of tokens.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TextOnlyProcessor<T> implements DiffProcessor<T> {

  /**
   * The main algorithms to choose from.
   */
  public enum Algorithm {
    HIRSCHBERG,
    WAGNER_FISCHER,
    KUMAR_RANGAN,
    MYER_GREEDY,
    MYER_LINEAR,
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
  public void diff(List<? extends T> from, List<? extends T> to, DiffHandler<T> handler) {
    handler.start();
    // handle the case when one of the two sequences is empty
    if (from.isEmpty() || to.isEmpty()) {
      for (T token : to) handler.handle(Operator.INS, token);
      for (T token : from) handler.handle(Operator.DEL, token);
    } else {

      Slicer<T> slicer = new Slicer<>(from, to);
      slicer.analyze();

      // Slice the beginning
      int startCount = slicer.getStartCount();
      int endCount = slicer.getEndCount();

      // Copy the end
      if (startCount > 0) {
        for (int i = 0; i < startCount; i++) handler.handle(Operator.MATCH, from.get(i));
      }

      // Check the end
      if (startCount > 0 || endCount > 0) {
        List<? extends T> subA = from.subList(startCount, from.size() - endCount);
        List<? extends T> subB = to.subList(startCount, to.size() - endCount);
        if (subA.isEmpty() || subB.isEmpty()) {
          for (T token : subB) handler.handle(Operator.INS, token);
          for (T token : subA) handler.handle(Operator.DEL, token);
        } else {
          DiffAlgorithm<T> algorithm = getAlgorithm();
          algorithm.diff(subA, subB, handler);
        }

      } else {
        DiffAlgorithm<T> algorithm = getAlgorithm();
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

  private DiffAlgorithm<T> getAlgorithm() {
    switch (this.algo) {
      case HIRSCHBERG:
        return new HirschbergAlgorithm<>();
      case WAGNER_FISCHER:
        return new WagnerFischerAlgorithm<>();
      case KUMAR_RANGAN:
        return new KumarRanganAlgorithm<>();
      case MYER_GREEDY:
        return new MyersGreedyAlgorithm<>();
      case MYER_LINEAR:
        return new MyersLinearAlgorithm<>();
      default:
        throw new IllegalStateException("No algorithm defined");
    }
  }

  /**
   * Identify common sequences at beginning and end of specified sequences.
   *
   * @author Christophe Lauret
   * @version 0.9.0
   */
  private static final class Slicer<T> {

    final List<? extends T> a;
    final List<? extends T> b;

    /**
     * The common start between the two sequences.
     */
    int startCount = -1;

    /**
     * The common end between the two sequences.
     */
    int endCount = -1;

    /**
     * Creates a new sequence slicer.
     *
     * @param a The first sequence to slice.
     * @param b The second sequence to slice.
     */
    public Slicer(List<? extends T> a, List<? extends T> b) {
      this.a = a;
      this.b = b;
    }

    /**
     * Analyse the sequences to know whether they can be sliced.
     *
     * @return the number of common tokens
     */
    public int analyze() throws IllegalStateException {
      this.startCount = computeStart();
      this.endCount = sliceEnd(this.startCount);
      return this.startCount + this.endCount;
    }

    int computeStart() {
      int counter = 0;
      Iterator<? extends T> i = this.a.iterator();
      Iterator<? extends T> j = this.b.iterator();
      // calculate the max possible index for slicing.
      while (i.hasNext() && j.hasNext()) {
        if (j.next().equals(i.next())) {
          counter++;
        } else {
          break;
        }
      }
      return counter;
    }

    public int sliceEnd(int start) {
      int counter = 0;     // number of tokens evaluated
      int pos1 = this.a.size() - 1;  // current position of the first sequence
      int pos2 = this.b.size() - 1;  // current position of the second sequence
      while (pos1 >= start && pos2 >= start) {
        T token = this.a.get(pos1);
        if (token.equals(this.b.get(pos2))) {
          counter++;
          pos1--;
          pos2--;
        } else {
          break;
        }
      }
      return counter;
    }

    /**
     * @return The number of common tokens at the start of the sequence.
     */
    public int getStartCount() {
      return this.startCount;
    }

    /**
     * @return The number of common tokens at the end of the sequence.
     */
    public int getEndCount() {
      return this.endCount;
    }

  }

}
