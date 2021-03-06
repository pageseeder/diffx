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
import org.pageseeder.diffx.xml.Sequence;
import org.pageseeder.diffx.test.DiffAssertions;
import org.pageseeder.diffx.test.TestActions;
import org.pageseeder.diffx.test.TestTokens;
import org.pageseeder.diffx.token.XMLToken;

import java.util.List;

/**
 * Test case to produce results of better "quality"
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class QualityXMLDiffTest extends ProcessorTest<XMLToken> {

  @Test
  public final void testQuality_Table1() throws DiffException {
    String xml1 = "<table><row><cell>A</cell><cell>A X</cell></row><row><cell>A</cell><cell>C X</cell></row><row><cell>B</cell><cell>C X</cell></row></table>";
    String xml2 = "<table><row><cell>A</cell><cell>A X</cell></row><row><cell>B</cell><cell>C X</cell></row></table>";
    String exp = "<table><row><cell>A</cell><cell>A X</cell></row>+<row>+<cell>+A+</cell>+<cell>+C+( X)+</cell>+</row><row><cell>B</cell><cell>C X</cell></row></table>";
    assertDiffXMLQualityOK(xml1, xml2, PRESERVE_SPACE_WORDS, exp);
  }

  @Test
  public final void testQuality_Table2() throws DiffException {
    String xml1 = "<fragment><table><row><cell>A</cell><cell>A X</cell></row><row><cell>A</cell><cell>C X</cell></row><row><cell>B</cell><cell>C X</cell></row></table></fragment>";
    String xml2 = "<fragment><table><row><cell>A</cell><cell>A X</cell></row><row><cell>B</cell><cell>C X</cell></row></table></fragment>";
    String exp = "<fragment><table><row><cell>A</cell><cell>A X</cell></row>+<row>+<cell>+A+</cell>+<cell>+C+( X)+</cell>+</row><row><cell>B</cell><cell>C X</cell></row></table></fragment>";
    assertDiffXMLQualityOK(xml1, xml2, PRESERVE_SPACE_WORDS, exp);
  }

  /**
   * Asserts that the Diff-X operation for XML meets expectations.
   *
   * @param xml1 The first XML to compare with diffx.
   * @param xml2 The first XML to compare with diffx.
   * @param exp  The expected result as formatted by the TestFormatter.
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  public final void assertDiffXMLQualityOK(String xml1, String xml2, DiffConfig config, String... exp) throws DiffException {
    Sequence seq1 = TestTokens.loadSequence(xml1, config);
    Sequence seq2 = TestTokens.loadSequence(xml2, config);

    // Process as list of actions
    List<Action<XMLToken>> actions = TestActions.diffToActions(getProcessor(), seq1.tokens(), seq2.tokens());
    try {
      DiffAssertions.assertIsCorrect(seq1, seq2, actions);
      DiffAssertions.assertIsWellFormedXML(actions);
      DiffAssertions.assertMatchTestOutput(actions, exp);
    } catch (AssertionError ex) {
      AlgorithmTest.printXMLErrorDetails(xml1, xml2, exp, TestActions.toXML(actions), actions);
      throw ex;
    }
  }

}
