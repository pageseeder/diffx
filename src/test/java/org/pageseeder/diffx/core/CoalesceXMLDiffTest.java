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
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.Actions;
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.test.DiffAssertions;
import org.pageseeder.diffx.test.Events;
import org.pageseeder.diffx.test.TestActions;
import org.pageseeder.diffx.test.TestHandler;
import org.pageseeder.diffx.token.Token;

import java.io.IOException;
import java.util.List;

/**
 * Test case for progressive XML processor.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class CoalesceXMLDiffTest extends AlgorithmTest {

  @Test
  public final void testCoalesce_Identical() throws DiffXException {
    String xml1 = "<a>X Y</a>";
    String xml2 = "<a>X Y</a>";
    String exp = "<a>X Y</a>";
    assertDiffXMLCoalesceOK(xml1, xml2, exp);
  }

  @Test
  public final void testProg_SplitMergeA1() throws DiffXException {
    String xml1 = "<a><b>X</b> <b>Y</b></a>";
    String xml2 = "<a><b>X Y</b></a>";
    // split
    String[] exp1 = new String[]{
        "<a><b>X+</b> +<b>Y</b></a>",           // tags inserted
        "<a><b>X-( Y)</b>+ +<b>+Y+</b></a>",    // text has moved
        "<a>+<b>+X+</b>+ <b>+Y-(X Y)</b></a>",
        "<a>+<b>+X+</b>+ <b>-X-( Y)+Y</b></a>"
    };
    // merge
    String[] exp2 = new String[]{
        "<a><b>X-</b> -<b>Y</b></a>",         // tags removed
        "<a><b>X+( Y)</b>- -<b>-Y-</b></a>",  // text has moved
        "<a>-<b>-X-</b>- <b>+X-Y+( Y)</b></a>"
    };
    assertDiffXMLCoalesceOK(xml1, xml2, exp1);
    assertDiffXMLCoalesceOK(xml2, xml1, exp2);
  }

  @Test
  public final void testCoalesce_Sticky() throws DiffXException {
    String xml1 = "<a>a white cat</a>";
    String xml2 = "<a>a black hat</a>";
    String expA = "<a>a+( white cat)-( black hat)</a>";
    String expB = "<a>a-( black hat)+( white cat)</a>";
    assertDiffXMLCoalesceOK(xml1, xml2, expA, expB);
  }

  @Test
  public final void testProg_MovedBranch() throws IOException, DiffXException {
    String xml1 = "<a><b>M</b><a><b>A</b></a><b>N</b></a>";
    String xml2 = "<a><b>M<a><b>A</b></a></b><b>N</b></a>";
    String exp1 = "<a><b>M-<a>-<b>-A-</b>-</a></b>+<a>+<b>+A+</b>+</a><b>N</b></a>";
    String exp2 = "<a><b>M+<a>+<b>+A+</b>+</a></b>-<a>-<b>-A-</b>-</a><b>N</b></a>";
    assertDiffXMLCoalesceOK(xml1, xml2, exp1);
    assertDiffXMLCoalesceOK(xml2, xml1, exp2);
  }

  @Test
  public final void testProg_BestPath() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b></a>";
    String xml2 = "<a><b/><b>X</b></a>";
    String exp1 = "<a>-<b>-</b><b>X</b></a>";
    String exp2 = "<a>+<b>+</b><b>X</b></a>";
    assertDiffXMLCoalesceOK(xml1, xml2, exp1);
    assertDiffXMLCoalesceOK(xml2, xml1, exp2);
  }

  /**
   * Asserts that the Diff-X operation for XML meets expectations.
   *
   * @param xml1 The first XML to compare with diffx.
   * @param xml2 The first XML to compare with diffx.
   * @param exp  The expected result as formatted by the TestFormatter.
   *
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void assertDiffXMLCoalesceOK(String xml1, String xml2, String... exp) throws DiffXException {
    // Record XML
    DiffXConfig config = new DiffXConfig();
    config.setGranularity(TextGranularity.SPACE_WORD);
    List<? extends Token> seq1 = Events.loadTokens(xml1, config);
    List<? extends Token> seq2 = Events.loadTokens(xml2, config);

    // Process as list of actions
    List<Action> actions = TestActions.diffToActions(getDiffAlgorithm(), seq1, seq2);

    TestHandler th = new TestHandler();
    Actions.handle(actions, th);

    try {
      DiffAssertions.assertIsCorrect(seq1, seq2, actions);
      DiffAssertions.assertIsWellFormedXML(actions);
      DiffAssertions.assertMatchTestOutput(actions, exp);
    } catch (AssertionError ex) {
      printXMLErrorDetails(xml1, xml2, exp, TestActions.toXML(actions), actions);
      throw ex;
    }
  }

}
