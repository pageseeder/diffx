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

import org.junit.jupiter.api.Nested;
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.impl.LineToken;

/**
 * Test case for Wagner-Fischer algorithm (text only).
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class WagnerFischerAlgorithmTest {

  private <T> DiffAlgorithm<T> newAlgorithm() {
    return new WagnerFischerAlgorithm<>();
  }

  @Nested
  public class GeneralDiff extends BasicGeneralDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return newAlgorithm();
    }
  }

  @Nested
  public class RandomGeneralDiff extends RandomGeneralDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return newAlgorithm();
    }
  }

  @Nested
  public class LinesDiff extends BasicLinesDiffTest {
    @Override
    public DiffAlgorithm<LineToken> getDiffAlgorithm() {
      return newAlgorithm();
    }
  }

  @Nested
  public class EqualityDiff extends BasicEqualityAlgorithmTest {

    EqualityDiff() {
      super(WagnerFischerAlgorithm::new);
    }

    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return newAlgorithm();
    }
  }
}
