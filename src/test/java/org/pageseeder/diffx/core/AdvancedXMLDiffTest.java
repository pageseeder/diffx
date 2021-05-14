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
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.xml.NamespaceSet;

import java.util.List;

import static org.pageseeder.diffx.config.TextGranularity.WORD;

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
public abstract class AdvancedXMLDiffTest extends AlgorithmTest<XMLToken> {

  /**
   * Compares two identical XML documents.
   *
   * <p>Compares
   * <pre>&lt;a&gt;X Y&lt;/a&gt;</pre>
   * with
   * <pre>&lt;a&gt;X Y&lt;/a&gt;</pre>
   */
  @Test
  public final void testAdvanced_Identical() throws LoadingException {
    String xmlA = "<a>X Y</a>";
    String xmlB = "<a>X Y</a>";
    String exp = "<a>X Y</a>";
    assertDiffXMLOK(xmlA, xmlB, WORD, exp);
  }

  /**
   * Wraps the XML in the same element.
   */
  @Test
  public final void testAdvanced_SelfWrapA() throws LoadingException {
    String xmlA = "<a></a>";
    String xmlB = "<a><a/></a>";
    String[] exp = new String[]{
        "<a>+<a>+</a></a>",
        "+<a><a></a>+</a>"
    };
    assertDiffXMLOK(xmlA, xmlB, WORD, exp);
    assertDiffXMLOK(xmlB, xmlA, WORD, flip(exp));
  }

  /**
   * Wraps the XML in the same element.
   */
  @Test
  public final void testAdvanced_SelfWrapB() throws LoadingException {
    String xmlA = "<a>x</a>";
    String xmlB = "<a><a>x</a></a>";
    String[] exp = new String[]{
        "<a>+<a>x+</a></a>",
        "+<a><a>x</a>+</a>"
    };
    assertDiffXMLOK(xmlA, xmlB, WORD, exp);
    assertDiffXMLOK(xmlB, xmlA, WORD, flip(exp));
  }

  /**
   * Splits / merge the text of the XML string in 2.
   */
  @Test
  public final void testAdvanced_SplitMergeA() throws LoadingException {
    String xmlA = "<a><b>X Y</b></a>";
    String xmlB = "<a><b>X</b> <b>Y</b></a>";
    String[] exp = new String[]{
        "<a><b>X- -Y</b>+ +<b>+Y+</b></a>",
        "<a>+<b>+X+</b>+ <b>-X- Y</b></a>"
    };
    assertDiffXMLOK(xmlA, xmlB, WORD, exp);
    assertDiffXMLOK(xmlB, xmlA, WORD, flip(exp));
  }

  /**
   * Tests some moved branch.
   */
  @Test
  public final void testAdvanced_MovedBranch1() throws LoadingException {
    String xmlA = "<a><b>M<a><b>A</b></a></b><b>N</b></a>";
    String xmlB = "<a><b>M</b><a><b>A</b></a><b>N</b></a>";
    String[] exp = new String[] {
        "<a><b>M-<a>-<b>-A-</b>-</a></b>+<a>+<b>+A+</b>+</a><b>N</b></a>",
        "<a><b>M-<a>-<b>-A-</b>-</a></b>+<a><b>-N+A</b>+</a>+<b>+N+</b></a>",
        "<a><b>M-<a>-<b>-A-</b>-</a></b>+<a><b>+A-N</b>+</a>+<b>+N+</b></a>"
    };
    assertDiffXMLOK(xmlA, xmlB, WORD, exp);
  }

  /**
   * Tests some moved branch.
   */
  @Test
  public final void testAdvanced_MovedBranch2() throws LoadingException {
    String xmlA = "<a><b>M</b><a><b>A</b></a><b>N</b></a>";
    String xmlB = "<a><b>M<a><b>A</b></a></b><b>N</b></a>";
    String[] exp = new String[] {
        "<a><b>M-<a>-<b>-A-</b>-</a></b>+<a>+<b>+A+</b>+</a><b>N</b></a>",
        "<a><b>M+<a>+<b>+A+</b>+</a></b>-<a>-<b>-A-</b>-</a><b>N</b></a>",
        "<a><b>M+<a>+<b>+A+</b>+</a></b>-<a><b>-A+N</b>-</a>-<b>-N-</b></a>",
        "<a><b>M+<a>+<b>+A+</b>+</a></b>-<a><b>+N-A</b>-</a>-<b>-N-</b></a>"
    };
    assertDiffXMLOK(xmlA, xmlB, WORD, exp);
  }

  /**
   * Splits the text of the XML string in 2.
   */
  @Test
  public final void testAdvanced_SplitMergeB() throws LoadingException {
    String xmlA = "<a><b><c/><d/></b></a>";
    String xmlB = "<a><b><c/></b><b><d/></b></a>";
    String exp = "<a><b><c></c>-<d>-</d></b>+<b>+<d>+</d>+</b></a>";
    assertDiffXMLOK(xmlA, xmlB, WORD, exp);
    assertDiffXMLOK(xmlB, xmlA, WORD, flip(exp));
  }

  @Test
  public final void testAdvanced_BestPath() throws LoadingException {
    String xmlA = "<a><b/><b>X</b></a>";
    String xmlB = "<a><b>X</b></a>";
    String[] exp = new String[] {
        "<a>-<b>-</b><b>X</b></a>",
        "<a><b>+X</b>-<b>-X-</b></a>"
    };
    assertDiffXMLOK(xmlA, xmlB, WORD, exp);
    assertDiffXMLOK(xmlB, xmlA, WORD, flip(exp));
  }

  @Test
  public final void testAdvanced_MoveB() throws LoadingException {
    String xmlA = "<a><b/><c>x y</c></a>";
    String xmlB = "<a><b>x y</b><c/></a>";
    String exp = "<a><b>+x+ +y</b><c>-x- -y</c></a>";
    assertDiffXMLOK(xmlA, xmlB, WORD, exp);
    assertDiffXMLOK(xmlB, xmlA, WORD, flip(exp));
  }

  @Test
  public final void testAdvanced_ModifiedTextC() throws LoadingException {
    String xmlA = "<a>X</a>";
    String xmlB = "<a>X Y</a>";
    String exp = "<a>X+ +Y</a>";
    assertDiffXMLOK(xmlA, xmlB, WORD, exp);
    assertDiffXMLOK(xmlB, xmlA, WORD, flip(exp));
  }

  @Test
  public final void testAdvanced_TextElementC() throws LoadingException {
    String xmlA = "<a><b>W X</b></a>";
    String xmlB = "<a><b>W X</b><c>Y Z</c></a>";
    String exp = "<a><b>W X</b>+<c>+Y+ +Z+</c></a>";
    assertDiffXMLOK(xmlA, xmlB, WORD, exp);
    assertDiffXMLOK(xmlB, xmlA, WORD, flip(exp));
  }

  @Test
  public final void testAdvanced_List() throws LoadingException {
    String xmlB = "<ul><li>blue</li><li>red</li><li>green</li></ul>";
    String xmlA = "<ul><li>black</li><li>red</li><li>green</li></ul>";
    String[] exp = new String[]{
        "<ul><li>+(blue)-(black)</li><li>red</li><li>green</li></ul>",
        "<ul><li>-(black)+(blue)</li><li>red</li><li>green</li></ul>"
    };
    assertDiffXMLOK(xmlA, xmlB, WORD, exp);
    assertDiffXMLOK(xmlB, xmlA, WORD, flip(exp));
  }

  @Test
  public void testAdvanced_ChangeDefaultNamespace() throws LoadingException {
    String xmlB = "<body><svg xmlns='http://www.w3.org/2000/svg' version='1.1'><rect width='100%' height='100%' fill='red' /></svg></body>";
    String xmlA = "<body><svg xmlns='http://www.w3.org/2000/svg' width='300' height='200'><rect width='100%' height='100%' fill='blue' /></svg></body>";
    assertDiffXMLOK(xmlA, xmlB, WORD);
  }

  // helpers
  // --------------------------------------------------------------------------

  private void assertDiffXMLOK(String xmlA, String xmlB, TextGranularity granularity) throws LoadingException {
    assertDiffXMLOK(xmlA, xmlB, granularity, new String[0]);
  }

  private void assertDiffXMLOK(String xmlA, String xmlB, TextGranularity granularity, String... exp) throws LoadingException {
    // Record XML
    Sequence seqA = TestTokens.loadSequence(xmlA, granularity);
    Sequence seqB = TestTokens.loadSequence(xmlB, granularity);
    NamespaceSet namespaces = NamespaceSet.merge(seqB.getNamespaces(), seqA.getNamespaces());
    // Process as list of actions
    List<Action<XMLToken>> actions = TestActions.diffToActions(getDiffAlgorithm(), seqA.tokens(), seqB.tokens());
    try {
      DiffAssertions.assertIsCorrect(seqA, seqB, actions);
      DiffAssertions.assertIsWellFormedXML(actions, namespaces);
      if (exp.length > 0) {
        DiffAssertions.assertMatchTestOutput(actions, exp, namespaces);
      }
    } catch (AssertionError ex) {
      printXMLErrorDetails(xmlA, xmlB, exp, DiffAssertions.toTestOutput(actions, namespaces), actions);
      throw ex;
    }
  }

}
