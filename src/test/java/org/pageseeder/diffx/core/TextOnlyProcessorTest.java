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
package org.pageseeder.diffx.core;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.algorithm.BasicGeneralDiffTest;
import org.pageseeder.diffx.algorithm.BasicLinesDiffTest;
import org.pageseeder.diffx.algorithm.RandomGeneralDiffTest;
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.core.TextOnlyProcessor.Algorithm;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.impl.LineToken;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test case for text only algorithm.
 */
final class TextOnlyProcessorTest {

  private static <T> DiffAlgorithm<T> processor(Algorithm algorithm) {
    return new TextOnlyProcessor<>(algorithm);
  }

  @Test
  void testToString() {
    String result = new TextOnlyProcessor<>().toString();
    assertTrue(result.startsWith("TextOnlyProcessor{algo="));
    assertTrue(result.endsWith("}"));
  }

  @Nested
  public class KumarRanganDiff extends BasicGeneralDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return processor(Algorithm.KUMAR_RANGAN);
    }
  }

  @Nested
  public class RandomGeneralDiff extends RandomGeneralDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return processor(Algorithm.KUMAR_RANGAN);
    }
  }

  @Nested
  public class LinesDiff extends BasicLinesDiffTest {
    @Override
    public DiffAlgorithm<LineToken> getDiffAlgorithm() {
      return processor(Algorithm.KUMAR_RANGAN);
    }
  }

  @Nested
  public class HirschbergDiff extends BasicGeneralDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return processor(Algorithm.HIRSCHBERG);
    }
  }

  @Nested
  public class HistogramDiff extends BasicGeneralDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return processor(Algorithm.HISTOGRAM);
    }
  }

  @Nested
  public class MyerGreedyDiff extends BasicGeneralDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return processor(Algorithm.MYER_GREEDY);
    }
  }

  @Nested
  public class MyerGreedy2Diff extends BasicGeneralDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return processor(Algorithm.MYER_GREEDY2);
    }
  }

  @Nested
  public class MyerLinearDiff extends BasicGeneralDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return processor(Algorithm.MYER_LINEAR);
    }
  }

  @Nested
  public class PatienceDiff extends BasicGeneralDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return processor(Algorithm.PATIENCE);
    }
  }

  @Nested
  public class WagnerFischerDiff extends BasicGeneralDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return processor(Algorithm.WAGNER_FISCHER);
    }
  }

  @Nested
  public class WuDiff extends BasicGeneralDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return processor(Algorithm.WU);
    }
  }
}
