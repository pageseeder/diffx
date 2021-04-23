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
package org.pageseeder.diffx.algorithm;

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.core.DiffAlgorithm;
import org.pageseeder.diffx.core.KumarRanganAlgorithm;
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.handler.FormattingAdapter;
import org.pageseeder.diffx.sequence.Sequence;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Performs the diff comparison using an optimized version of the linear space algorithm
 * of S.Kiran Kumar and C.Pandu Rangan.
 *
 * <p>Implementation note: this algorithm effectively detects the correct changes in the
 * sequences, but cannot be used on XML sequences as it cannot always produce well-formed XML.
 *
 * <p>For S. Kiran Kumar and C. Pandu Rangan. <i>A linear space algorithm for the LCS problem</i>,
 * Acta Informatica. Volume 24 ,  Issue 3  (June 1987); Copyright Springer-Verlag 1987
 *
 * <p>This class reuses portions of code originally written by Mikko Koivisto and Tuomo Saarni.
 *
 * <p><a href="http://dblp.uni-trier.de/rec/bibtex/journals/acta/KumarR87">
 * http://dblp.uni-trier.de/rec/bibtex/journals/acta/KumarR87</a>
 *
 * <p><a href="http://users.utu.fi/~tuiisa/Java/">http://users.utu.fi/~tuiisa/Java/</a>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.6.0
 */
public final class DiffXKumarRangan extends DiffXAlgorithmBase {

  /**
   * The length of the LCS.
   */
  private int length = -1;

  /**
   * Creates a new DiffXAlgorithmBase.
   *
   * @param first  The first sequence to compare.
   * @param second The second sequence to compare.
   */
  public DiffXKumarRangan(Sequence first, Sequence second) {
    super(first, second);
  }

  /**
   * Calculates the length of LCS and returns it.
   *
   * <p>If the length is calculated already it'll not be calculated repeatedly.
   *
   * <p>This algorithm starts from the length of the first sequence as the maximum possible
   * LCS and reduces the length for every difference with the second sequence.
   *
   * <p>The time complexity is O(n(m-p)) and the space complexity is O(n+m).
   *
   * @return The length of LCS
   */
  @Override
  public int length() {
    if (this.length < 0) {
      DiffAlgorithm algo = new KumarRanganAlgorithm();
      AtomicInteger length = new AtomicInteger();
      algo.diff(this.sequence1.tokens(), this.sequence2.tokens(), (operator, token) -> {
        if (operator == Operator.MATCH) length.getAndIncrement();
      });
      this.length = length.get();
    }
    return this.length;
  }

  /**
   * Writes the diff sequence using the specified formatter.
   *
   * @param formatter The formatter that will handle the output.
   *
   * @throws IOException If thrown by the formatter.
   */
  @Override
  public void process(DiffXFormatter formatter) throws IOException {
    DiffAlgorithm algo = new KumarRanganAlgorithm();
    try {
      FormattingAdapter adapter = new FormattingAdapter(formatter);
      algo.diff(this.sequence1.tokens(), this.sequence2.tokens(), adapter);
    } catch (UncheckedIOException ex) {
      // Unwrap
      throw ex.getCause();
    }
  }

}
