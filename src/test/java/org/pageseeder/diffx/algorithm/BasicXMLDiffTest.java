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
import org.pageseeder.diffx.DiffException;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.load.LoadingException;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.test.DiffAssertions;
import org.pageseeder.diffx.test.TestActions;
import org.pageseeder.diffx.test.TestTokens;
import org.pageseeder.diffx.token.Token;
import org.pageseeder.diffx.xml.NamespaceSet;

import java.util.List;

import static org.pageseeder.diffx.config.TextGranularity.TEXT;

/**
 * Test case for Diff-X algorithm implementations.
 *
 * <p>To pass this test an algorithm must find the correct differences and produce
 * results that are well-formed in so far as insertion and deletion are considered
 * as separate dimensions.
 *
 * <p>Level 1 algorithms must produce results that are good enough to be formatted
 * and show the correct differences as XML.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class BasicXMLDiffTest extends AlgorithmTest<Token> {

  /**
   * Compares two identical XML documents.
   *
   * <p>Compares
   * <pre>&lt;a/&gt;</pre>
   * with
   * <pre>&lt;a/&gt;</pre>
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_IdenticalA() throws DiffException {
    String xmlA = "<a/>";
    String xmlB = "<a/>";
    String exp = "<a></a>";
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
  }

  /**
   * Compares two identical XML documents.
   *
   * <p>Compares
   * <pre>&lt;a&gt;X&lt;/a&gt;</pre>
   * with
   * <pre>&lt;a&gt;X&lt;/a&gt;</pre>
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_IdenticalB() throws DiffException {
    String xmlA = "<a>X</a>";
    String xmlB = "<a>X</a>";
    String exp = "<a>X</a>";
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
  }

  /**
   * Compares two identical XML documents.
   *
   * <p>Compares
   * <pre>&lt;a m='x'/&gt;</pre>
   * with
   * <pre>&lt;a m='x'/&gt;</pre>
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_IdenticalD() throws DiffException {
    String xmlA = "<a m='x'/>";
    String xmlB = "<a m='x'/>";
    String exp = "<a>@(m=x)</a>";
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
  }

  /**
   * Compares two identical XML documents.
   *
   * <p>Compares
   * <pre>&lt;a m='x'/&gt;</pre>
   * with
   * <pre>&lt;a m='x'/&gt;</pre>
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_IdenticalE() throws DiffException {
    String xmlA = "<a n='y' m='x'/>";
    String xmlB = "<a m='x' n='y'/>";
    String[] exp = new String[]{
        "<a>@(m=x)@(n=y)</a>",
        "<a>@(n=y)@(m=x)</a>"
    };
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
  }

// total difference tests ---------------------------------------------------------------

  /**
   * Compares two completely different XML documents.
   *
   * <p>Compares
   * <pre>&lt;a/&gt;</pre>
   * with
   * <pre>&lt;b/&gt;</pre>
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_TotalDiffA() throws DiffException {
    String xmlA = "<b/>";
    String xmlB = "<a/>";
    String[] exp = new String[]{
        "+<a>+</a>-<b>-</b>",
        "+<a>-<b>-</b>+</a>",
        "-<b>+<a>+</a>-</b>",
        "-<b>-</b>+<a>+</a>"};
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

  /**
   * Compares two completely different XML documents.
   *
   * <p>Compares
   * <pre>&lt;a/&gt;</pre>
   * with
   * <pre><i>(nothing)</i></pre>
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_TotalDiffB() throws DiffException {
    String xmlA = "";
    String xmlB = "<a/>";
    String exp = "+<a>+</a>";
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

// modified text test -------------------------------------------------------------------

  /**
   * Compares two XML documents with a simple difference in the text.
   *
   * <p>Compares
   * <pre>&lt;a&gt;x&lt;/a&gt;</pre>
   * with
   * <pre>&lt;a&gt;y&lt;/a&gt;</pre>
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_ModifiedTextA() throws DiffException {
    String xmlA = "<a>Y</a>";
    String xmlB = "<a>X</a>";
    String[] exp = new String[]{
        "<a>+X-Y</a>",
        "<a>-Y+X</a>"
    };
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

  /**
   * Compares two XML documents with a simple difference in the text.
   *
   * <p>Compares
   * <pre>&lt;a&gt;x &lt;/a&gt;</pre>
   * with
   * <pre>&lt;a&gt;x&lt;/a&gt;</pre>
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_ModifiedTextB() throws DiffException {
    String xmlA = "<a>X</a>";
    String xmlB = "<a>X </a>";
    String exp = "<a>X+ </a>";
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

//moved text tests ---------------------------------------------------------------------

  /**
   * Compares two XML documents where the text of one element moves to
   * another element.
   *
   * <p>Compares
   * <pre>&lt;a&gt;&lt;b&gt;x&lt;/b&gt;&lt;c/&gt;&lt;/a&gt;</pre>
   * with
   * <pre>&lt;a&gt;&lt;b/&gt;&lt;c&gt;x&lt;/c&gt;&lt;/a&gt;</pre>
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_MoveA() throws DiffException {
    String xmlA = "<a><b/><c>x</c></a>";
    String xmlB = "<a><b>x</b><c/></a>";
    String exp = "<a><b>+x</b><c>-x</c></a>";
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

// element tests ------------------------------------------------------------------------

  /**
   * Compares two XML documents with a simple difference in the element nodes.
   *
   * <p>Compares
   * <pre>&lt;a&gt;&lt;b/&gt;&lt;/a&gt;</pre>
   * with
   * <pre>&lt;a&gt;&lt;c/&gt;&lt;/a&gt;</pre>
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_ElementA() throws DiffException {
    String xmlA = "<a><c/></a>";
    String xmlB = "<a><b/></a>";
    String[] exp = new String[]{
        "<a>+<b>+</b>-<c>-</c></a>",
        "<a>+<b>-<c>-</c>+</b></a>",
        "<a>-<c>+<b>+</b>-</c></a>",
        "<a>-<c>-</c>+<b>+</b></a>"
    };
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

  /**
   * Compares two XML documents with a simple difference in the element nodes.
   *
   * <p>Compares
   * <pre>&lt;a&gt;X&lt;/a&gt;</pre>
   * with
   * <pre>&lt;b&gt;X&lt;/b&gt;</pre>
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_ElementB() throws DiffException {
    String xmlA = "<b>X</b>";
    String xmlB = "<a>X</a>";
    String[] exp = new String[]{
        "-<b>+<a>X+</a>-</b>",
        "+<a>-<b>X-</b>+</a>"
    };
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

  /**
   * Compares two XML documents with a simple difference in the element nodes.
   *
   * <p>Compares
   * <pre>&lt;a&gt;&lt;b&gt;X&lt;/b&gt;&lt;/a&gt;</pre>
   * with
   * <pre>&lt;b&gt;&lt;a&gt;X&lt;/a&gt;&lt;/b&gt;</pre>
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_ElementC() throws DiffException {
    String xmlA = "<b><a>X</a></b>";
    String xmlB = "<a><b>X</b></a>";
    String[] exp = new String[]{
        "+<a><b>-<a>X-</a></b>+</a>",
        "-<b><a>+<b>X+</b></a>-</b>"
    };
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

// text and elements --------------------------------------------------------------------

  /**
   * Compares two XML documents with differences in both the text and the element nodes.
   *
   * <p>Compares
   * <pre>&lt;a&gt;&lt;b&gt;X&lt;/b&gt;&lt;c&gt;Y&lt;/c&gt;&lt;/a&gt;</pre>
   * with
   * <pre>&lt;a&gt;&lt;b&gt;X&lt;/b&gt;&lt;/a&gt;</pre>
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_TextElementA() throws DiffException {
    String xmlA = "<a><b>X</b></a>";
    String xmlB = "<a><b>X</b><c>Y</c></a>";
    String exp = "<a><b>X</b>+<c>+Y+</c></a>";
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

  /**
   * Compares two XML documents with differences in both the text and the element nodes.
   *
   * <p>Compares
   * <pre>&lt;a&gt;&lt;b&gt;X&lt;/b&gt;&lt;c&gt;Y&lt;/c&gt;&lt;/a&gt;</pre>
   * with
   * <pre>&lt;a&gt;&lt;c&gt;Y&lt;/c&gt;&lt;/a&gt;</pre>
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_TextElementB() throws DiffException {
    String xmlA = "<a><c>Y</c></a>";
    String xmlB = "<a><b>X</b><c>Y</c></a>";
    String exp = "<a>+<b>+X+</b><c>Y</c></a>";
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

// attributes tests ---------------------------------------------------------------------

  /**
   * Compares two XML documents where an attribute has been inserted / removed.
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_AttributeA() throws DiffException {
    String xmlA = "<a/>";
    String xmlB = "<a m='x'/>";
    String exp = "<a>+@(m=x)</a>";
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

  /**
   * Compares two XML documents where an attribute has been modified.
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_AttributeB() throws DiffException {
    String xmlA = "<a m='x'/>";
    String xmlB = "<a m='y'/>";
    String[] exp = new String[]{
        "<a>-@(m=x)+@(m=y)</a>",
        "<a>+@(m=y)-@(m=x)</a>",
    };
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

  /**
   * Compares two XML documents where an attribute has been modified.
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_AttributeC() throws DiffException {
    String xmlA = "<a n='w' m='x'/>";
    String xmlB = "<a m='y' n='z'/>";
    String[] exp = new String[]{
        "<a>+@(m=y)+@(n=z)-@(m=x)-@(n=w)</a>",
        "<a>+@(m=y)-@(m=x)+@(n=z)-@(n=w)</a>",
        "<a>+@(m=y)-@(m=x)-@(n=w)+@(n=z)</a>",
        "<a>-@(m=x)+@(m=y)+@(n=z)-@(n=w)</a>",
        "<a>-@(m=x)+@(m=y)-@(n=w)+@(n=z)</a>",
        "<a>-@(m=x)-@(n=w)+@(m=y)+@(n=z)</a>"
    };
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
  }

  /**
   * Compares two XML documents where an attribute has been inserted and the following text
   * has been changed.
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_TextAttributeA() throws DiffException {
    String xmlA = "<a>Y</a>";
    String xmlB = "<a m='x'>X</a>";
    String[] exp = new String[]{
        "<a>+@(m=x)+X-Y</a>",
        "<a>+@(m=x)-Y+X</a>"
    };
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

  /**
   * Compares two XML documents where an attribute has been inserted and the following text
   * has been changed.
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_TextAttributeB() throws DiffException {
    String xmlA = "<a m='y'>Y</a>";
    String xmlB = "<a m='x'>X</a>";
    String[] exp = new String[]{
        "<a>+@(m=x)-@(m=y)+X-Y</a>",
        "<a>+@(m=x)-@(m=y)-Y+X</a>",
        "<a>-@(m=y)+@(m=x)+X-Y</a>",
        "<a>-@(m=y)+@(m=x)-Y+X</a>"
    };
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

  /**
   * Compares two XML documents where two attributes are on a different namespace.
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_AttributeNamespaces0() throws DiffException {
    String xmlA = "<a f:m='y' xmlns:f='https://example.net'/>";
    String xmlB = "<a e:m='y' xmlns:e='https://example.org'/>";
    String[] exp = new String[]{
        "<a>+@(m=y)-@(m=y)</a>",
        "<a>-@(m=y)+@(m=y)</a>"
    };
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
  }

  /**
   * Compares two XML documents where two attributes are on a different namespace.
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_AttributeNamespaces1() throws DiffException {
    String xmlA = "<a f:m='y' xmlns:f='https://example.org'/>";
    String xmlB = "<a e:m='y' xmlns:e='https://example.org'/>";
    String[] exp = new String[]{
        "<a>@(m=y)</a>"
    };
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
  }

// self wrap tests ----------------------------------------------------------------------

  /**
   * Wraps the XML in the same element.
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_SelfWrapA() throws DiffException {
    String xmlA = "<a></a>";
    String xmlB = "<a><a/></a>";
    String[] exp = new String[]{
        "<a>+<a>+</a></a>",
        "+<a><a></a>+</a>",
        "<a>+<a></a>+</a>",
        "+<a><a>+</a></a>"
    };
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

  /**
   * Wraps the XML in the same element.
   *
   * @throws DiffException Should an error occur while parsing XML.
   */
  @Test
  public final void testBasic_SelfWrapB() throws DiffException {
    String xmlA = "<a>x</a>";
    String xmlB = "<a><a>x</a></a>";
    String[] exp = new String[]{
        "<a>+<a>x+</a></a>",
        "+<a><a>x</a>+</a>",
        "<a>+<a>x</a>+</a>",
        "+<a><a>x+</a></a>",
    };
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

  /**
   * Tests some moved branch.
   */
  @Test
  public final void testBasic_MovedBranch() throws LoadingException {
    String xmlA = "<a><b>M<a><b>A</b></a></b><b>N</b></a>";
    String xmlB = "<a><b>M</b><a><b>A</b></a><b>N</b></a>";
    String exp = "<a><b>M-<a>-<b>-A-</b>-</a></b>+<a>+<b>+A+</b>+</a><b>N</b></a>";
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

  @Test
  public final void testBasic_BestPath() throws LoadingException {
    String xmlA = "<a><b/><b>X</b></a>";
    String xmlB = "<a><b>X</b></a>";
    String exp = "<a>-<b>-</b><b>X</b></a>";
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }


// split and merge problems -------------------------------------------------------------

  @Test
  public final void testBasic_SplitMergeB() throws LoadingException {
    String xmlA = "<a><b><c/><d/></b></a>";
    String xmlB = "<a><b><c/></b><b><d/></b></a>";
    String[] exp = new String[]{
        "<a><b><c></c>-<d>-</d></b>+<b>+<d>+</d>+</b></a>"
    };
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

  @Test
  public final void testBasic_NSTemp() throws LoadingException {
    String xmlA = "<a xmlns:x='https://x.example.com' xmlns:y='https://y.example.com' xmlns='https://example.org'><x:b>X</x:b></a>";
    String xmlB = "<a xmlns:x='https://x.example.com' xmlns:y='https://y.example.com' xmlns='https://example.org'><b>X</b></a>";
    String[] exp = new String[]{
        "<a>-<x:b>-X-</x:b>+<b>+X+</b></a>",
        "<a>+<b>-<x:b>X-</x:b>+</b></a>",
        "<a>-<x:b>+<b>X+</b>-</x:b></a>"
    };
    assertDiffXMLOK(xmlA, xmlB, TEXT, exp);
    assertDiffXMLOK(xmlB, xmlA, TEXT, flip(exp));
  }

  // helpers
  // --------------------------------------------------------------------------

  private void assertDiffXMLOK(String xmlA, String xmlB, TextGranularity granularity, String... exp) throws LoadingException {
    // Load XML
    Sequence seqA = TestTokens.loadSequence(xmlA, granularity);
    Sequence seqB = TestTokens.loadSequence(xmlB, granularity);
    NamespaceSet namespaces = NamespaceSet.merge(seqA.getNamespaces(), seqB.getNamespaces());

    // Process as list of actions
    List<Action<Token>> actions = TestActions.diffToActions(getDiffAlgorithm(), seqA.tokens(), seqB.tokens());
    try {
      DiffAssertions.assertIsCorrect(seqA, seqB, actions);
      DiffAssertions.assertIsWellFormedXML(actions, namespaces);
      DiffAssertions.assertMatchTestOutput(actions, exp, namespaces);

    } catch (AssertionError ex) {
      printXMLErrorDetails(xmlA, xmlB, exp, DiffAssertions.toTestOutput(actions, namespaces), actions);
      throw ex;
    }
  }

}
