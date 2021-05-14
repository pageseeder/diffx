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

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.DiffException;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.algorithm.AlgorithmTest;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.test.DiffAssertions;
import org.pageseeder.diffx.test.TestActions;
import org.pageseeder.diffx.test.TestTokens;
import org.pageseeder.diffx.token.XMLToken;

import java.util.List;

/**
 * Test for the text coalescing option for processors.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class CoalesceXMLDiffTest extends ProcessorTest<XMLToken> {

  @Test
  public final void testCoalesce_Identical() throws DiffException {
    String xmlA = "<a>X Y</a>";
    String xmlB = "<a>X Y</a>";
    String exp = "<a>X Y</a>";
    assertDiffXMLCoalesceOK(xmlA, xmlB, COMPARE_SPACE_WORDS, exp);
  }

  @Test
  public final void testCoalesce_Split() throws DiffException {
    String xmlA = "<a><b>X Y</b></a>";
    String xmlB = "<a><b>X</b> <b>Y</b></a>";
    String[] exp = new String[]{
//        "<a><b>X+</b> +<b>Y</b></a>",           // tags inserted
        "<a><b>X-( Y)</b>+ +<b>+Y+</b></a>",    // text has moved
//        "<a>+<b>+X+</b>+ <b>+Y-(X Y)</b></a>",
//        "<a>+<b>+X+</b>+ <b>-X-( Y)+Y</b></a>",
    };
    assertDiffXMLCoalesceOK(xmlA, xmlB, COMPARE_SPACE_WORDS, exp);
  }

  @Test
  public final void testCoalesce_Merge() throws DiffException {
    String xmlA = "<a><b>X</b> <b>Y</b></a>";
    String xmlB = "<a><b>X Y</b></a>";
    String exp = "<a><b>X+( Y)</b>- -<b>-Y-</b></a>";
    assertDiffXMLCoalesceOK(xmlA, xmlB, COMPARE_SPACE_WORDS, exp);
  }


  @Test
  public final void testCoalesce_Sticky() throws DiffException {
    String xmlA = "<a>a black hat</a>";
    String xmlB = "<a>a white cat</a>";
    String expA = "<a>a+( white cat)-( black hat)</a>";
    String expB = "<a>a-( black hat)+( white cat)</a>";
    assertDiffXMLCoalesceOK(xmlA, xmlB, COMPARE_SPACE_WORDS, expA, expB);
  }

  /**
   * Asserts that the Diff-X operation for XML meets expectations.
   *
   * @param xmlA The first XML to compare with diffx.
   * @param xmlB The second XML to compare with diffx.
   * @param exp  The expected result as formatted by the TestFormatter.
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  public final void assertDiffXMLCoalesceOK(String xmlA, String xmlB, DiffConfig config, String... exp) throws DiffException {
    Sequence seqA = TestTokens.loadSequence(xmlA, config);
    Sequence seqB = TestTokens.loadSequence(xmlB, config);

    // Process as list of actions
    List<Action<XMLToken>> actions = TestActions.diffToActions(getProcessor(), seqA.tokens(), seqB.tokens());
    try {
      DiffAssertions.assertIsCorrect(seqA, seqB, actions);
      DiffAssertions.assertIsWellFormedXML(actions);
      DiffAssertions.assertMatchTestOutput(actions, exp);
    } catch (AssertionError ex) {
      AlgorithmTest.printXMLErrorDetails(xmlA, xmlB, exp, DiffAssertions.toTestOutput(actions), actions);
      throw ex;
    }
  }

}
