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
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.algorithm.AlgorithmTest;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.load.LoadingException;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.test.DiffAssertions;
import org.pageseeder.diffx.test.TestActions;
import org.pageseeder.diffx.test.TestTokens;
import org.pageseeder.diffx.xml.NamespaceSet;

import java.util.List;

/**
 * Test case for Diff-X algorithm implementations.
 *
 * <p>This class extends the the level 1 tests, and expect algorithms to produce
 * results of better quality, that is which serialised form always produce
 * well-formed XML.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class AdvancedXMLDiffTest extends AlgorithmTest {

  /**
   * Compares two identical XML documents.
   *
   * <p>Compares
   * <pre>&lt;a&gt;X Y&lt;/a&gt;</pre>
   * with
   * <pre>&lt;a&gt;X Y&lt;/a&gt;</pre>
   */
  @Test
  public final void testAdvanced_IdenticalC() throws LoadingException {
    String xml1 = "<a>X Y</a>";
    String xml2 = "<a>X Y</a>";
    String exp = "<a>X Y</a>";
    assertDiffXMLWordsOK(xml1, xml2, exp);
  }

  /**
   * Wraps the XML in the same element.
   */
  @Test
  public final void testAdvanced_SelfWrapA() throws LoadingException {
    String xml1 = "<a><a/></a>";
    String xml2 = "<a></a>";
    String[] exp1 = new String[]{
        "<a>+<a>+</a></a>",
        "+<a><a></a>+</a>"
    };
    String[] exp2 = new String[]{
        "<a>-<a>-</a></a>",
        "-<a><a></a>-</a>"
    };
    assertDiffXMLWordsOK(xml1, xml2, exp1);
    assertDiffXMLWordsOK(xml2, xml1, exp2);
  }

  /**
   * Wraps the XML in the same element.
   */
  @Test
  public final void testAdvanced_SelfWrapB() throws LoadingException {
    String xml1 = "<a><a>x</a></a>";
    String xml2 = "<a>x</a>";
    String[] exp1 = new String[]{
        "<a>+<a>x+</a></a>",
        "+<a><a>x</a>+</a>"
    };
    String[] exp2 = new String[]{
        "<a>-<a>x-</a></a>",
        "-<a><a>x</a>-</a>"
    };
    assertDiffXMLWordsOK(xml1, xml2, exp1);
    assertDiffXMLWordsOK(xml2, xml1, exp2);
  }

  /**
   * Splits / merge the text of the XML string in 2.
   */
  @Test
  public final void testAdvanced_SplitMergeA() throws LoadingException {
    String xml1 = "<a><b>X</b> <b>Y</b></a>";
    String xml2 = "<a><b>X Y</b></a>";
    String exp1 = "<a><b>X- -Y</b>+ +<b>+Y+</b></a>";
    String exp2 = "<a><b>X+ +Y</b>- -<b>-Y-</b></a>";
    assertDiffXMLWordsOK(xml1, xml2, exp1);
    assertDiffXMLWordsOK(xml2, xml1, exp2);
  }

  /**
   * Splits / merge the text of the XML string.
   */
  @Test
  public final void testAdvanced_SplitMergeA1() throws LoadingException {
    String xml1 = "<a><b>X</b> <b>Y</b></a>";
    String xml2 = "<a><b>X Y</b></a>";
    // split
    String[] exp1 = new String[]{
        "<a><b>X+</b> +<b>Y</b></a>",               // tags inserted
        "<a><b>X- -Y</b>+ +<b>+Y+</b></a>"  // text has moved
    };
    // merge
    String[] exp2 = new String[]{
        "<a><b>X-</b> -<b>Y</b></a>",               // tags removed
        "<a><b>X+ +Y</b>- -<b>-Y-</b></a>"  // text has moved
    };
    assertDiffXMLWordsOK(xml1, xml2, exp1);
    assertDiffXMLWordsOK(xml2, xml1, exp2);
  }

  /**
   * Tests some moved branch.
   */
  @Test
  public final void testAdvanced_MovedBranch() throws LoadingException {
    String xml1 = "<a><b>M</b><a><b>A</b></a><b>N</b></a>";
    String xml2 = "<a><b>M<a><b>A</b></a></b><b>N</b></a>";
    String exp1 = "<a><b>M-<a>-<b>-A-</b>-</a></b>+<a>+<b>+A+</b>+</a><b>N</b></a>";
    String exp2 = "<a><b>M+<a>+<b>+A+</b>+</a></b>-<a>-<b>-A-</b>-</a><b>N</b></a>";
    assertDiffXMLWordsOK(xml1, xml2, exp1);
    assertDiffXMLWordsOK(xml2, xml1, exp2);
  }

  /**
   * Splits the text of the XML string in 2.
   */
  @Test
  public final void testAdvanced_SplitMergeB() throws LoadingException {
    String xml1 = "<a><b><c/></b><b><d/></b></a>";
    String xml2 = "<a><b><c/><d/></b></a>";
    String exp1 = "<a><b><c></c>-<d>-</d></b>+<b>+<d>+</d>+</b></a>";
    String exp2 = "<a><b><c></c>+<d>+</d></b>-<b>-<d>-</d>-</b></a>";
    assertDiffXMLWordsOK(xml1, xml2, exp1);
    assertDiffXMLWordsOK(xml2, xml1, exp2);
  }

  @Test
  public final void testAdvanced_BestPath() throws LoadingException {
    String xml1 = "<a><b>X</b></a>";
    String xml2 = "<a><b/><b>X</b></a>";
    String exp1 = "<a>-<b>-</b><b>X</b></a>";
    String exp2 = "<a>+<b>+</b><b>X</b></a>";
    assertDiffXMLWordsOK(xml1, xml2, exp1);
    assertDiffXMLWordsOK(xml2, xml1, exp2);
  }

  @Test
  public final void testAdvanced_MoveB() throws LoadingException {
    String xml1 = "<a><b>x y</b><c/></a>";
    String xml2 = "<a><b/><c>x y</c></a>";
    String exp1 = "<a><b>+x+ +y</b><c>-x- -y</c></a>";
    String exp2 = "<a><b>-x- -y</b><c>+x+ +y</c></a>";
    assertDiffXMLWordsOK(xml1, xml2, exp1);
    assertDiffXMLWordsOK(xml2, xml1, exp2);
  }

  @Test
  public final void testAdvanced_ModifiedTextC() throws LoadingException {
    String xml1 = "<a>X Y</a>";
    String xml2 = "<a>X</a>";
    String exp1 = "<a>X+ +Y</a>";
    String exp2 = "<a>X- -Y</a>";
    assertDiffXMLWordsOK(xml1, xml2, exp1);
    assertDiffXMLWordsOK(xml2, xml1, exp2);
  }

  @Test
  public final void testAdvanced_TextElementC() throws LoadingException {
    String xml1 = "<a><b>W X</b><c>Y Z</c></a>";
    String xml2 = "<a><b>W X</b></a>";
    String exp1 = "<a><b>W X</b>+<c>+Y+ +Z+</c></a>";
    String exp2 = "<a><b>W X</b>-<c>-Y- -Z-</c></a>";
    assertDiffXMLWordsOK(xml1, xml2, exp1);
    assertDiffXMLWordsOK(xml2, xml1, exp2);
  }

  @Test
  public final void testAdvanced_List() throws LoadingException {
    String xml1 = "<ul><li>blue</li><li>red</li><li>green</li></ul>";
    String xml2 = "<ul><li>black</li><li>red</li><li>green</li></ul>";
    String[] exp = new String[]{
        "<ul><li>+(blue)-(black)</li><li>red</li><li>green</li></ul>",
        "<ul><li>-(black)+(blue)</li><li>red</li><li>green</li></ul>"
    };
    assertDiffXMLWordsOK(xml1, xml2, exp);
  }

  @Test
  public void testAdvanced_ChangeDefaultNamespace() throws LoadingException {
    String xml1 = "<body><svg xmlns='http://www.w3.org/2000/svg' version='1.1'><rect width='100%' height='100%' fill='red' /></svg></body>";
    String xml2 = "<body><svg xmlns='http://www.w3.org/2000/svg' width='300' height='200'><rect width='100%' height='100%' fill='blue' /></svg></body>";
    assertDiffXMLWordsOK(xml1, xml2);
  }

  // helpers
  // --------------------------------------------------------------------------

  private void assertDiffXMLWordsOK(String xml1, String xml2) throws LoadingException {
    assertDiffXMLWordsOK(xml1, xml2, new String[0]);
  }

  private void assertDiffXMLWordsOK(String xml1, String xml2, String exp) throws LoadingException {
    assertDiffXMLWordsOK(xml1, xml2, new String[]{exp});
  }

  private void assertDiffXMLWordsOK(String xml1, String xml2, String[] exp) throws LoadingException {
    // Record XML
    Sequence seq1 = TestTokens.loadSequence(xml1, TextGranularity.WORD);
    Sequence seq2 = TestTokens.loadSequence(xml2, TextGranularity.WORD);
    NamespaceSet namespaces = NamespaceSet.merge(seq1.getNamespaces(), seq2.getNamespaces());
    // Process as list of actions
    List<Action> actions = TestActions.diffToActions(getDiffAlgorithm(), seq1.tokens(), seq2.tokens());
    try {
      DiffAssertions.assertIsCorrect(seq1, seq2, actions);
      DiffAssertions.assertIsWellFormedXML(actions, namespaces);
      if (exp.length > 0) {
        DiffAssertions.assertMatchTestOutput(actions, exp, namespaces);
      }
    } catch (AssertionError ex) {
      printXMLErrorDetails(xml1, xml2, exp, TestActions.toXML(actions, namespaces), actions);
      throw ex;
    }
  }

}
