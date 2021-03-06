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

/**
 * Test case for the Myers' linear algorithm.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class MyersLinearAlgorithmTest {

  private MyersLinearAlgorithm<XMLToken> newAlgorithm() {
    return new MyersLinearAlgorithm<XMLToken>();
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
    public DiffAlgorithm getDiffAlgorithm() {
      return newAlgorithm();
    }
  }

}
