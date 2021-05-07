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

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.handler.ActionsBuffer;
import org.pageseeder.diffx.handler.MuxHandler;
import org.pageseeder.diffx.test.DiffAssertions;
import org.pageseeder.diffx.test.GeneralToken;
import org.pageseeder.diffx.test.RandomStringFactory;
import org.pageseeder.diffx.test.TestHandler;
import org.pageseeder.diffx.token.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Unit tests on random strings.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class RandomGeneralDiffTest extends AlgorithmTest<Token> {

  @Test
  public final void testGeneral_RandomVariations1() {
    RandomStringFactory factory = new RandomStringFactory();
    for (int n = 3; n < 20; n++) {
      for (int i = 0; i < 100; i++) {
        String a = factory.getRandomString(10, false);
        String b = factory.vary(a, .1);
        assertGeneralDiffOK(a, b);
      }
    }
  }

  @Test
  public final void testGeneral_RandomVariations2() {
    RandomStringFactory factory = new RandomStringFactory();
    for (int n = 3; n < 20; n++) {
      for (int i = 0; i < 100; i++) {
        String a = factory.getRandomString(100, false);
        String b = factory.vary(a, .1);
        assertGeneralDiffOK(a, b);
      }
    }
  }

  @Test
  public final void testGeneral_RandomVariations3() {
    RandomStringFactory factory = new RandomStringFactory();
    for (int n = 3; n < 20; n++) {
      for (int i = 0; i < 100; i++) {
        String a = factory.getRandomString(100, false);
        String b = factory.vary(a, .2);
        assertGeneralDiffOK(a, b);
      }
    }
  }

  private void assertGeneralDiffOK(String a, String b) {
    DiffAlgorithm<Token> algorithm = getDiffAlgorithm();
    BasicGeneralDiffTest.assertGeneralDiffOK(a, b, algorithm, new String[0]);
  }

}
