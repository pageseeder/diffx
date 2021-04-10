/*
 * Copyright 2010-2015 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.core;

import org.junit.Assert;
import org.junit.Test;
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.test.Events;
import org.pageseeder.diffx.test.TestHandler;

import java.io.IOException;
import java.util.List;

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
public abstract class BaseProcessorLevel1Test extends BaseProcessorLevel0Test {

  /**
   * Compares two identical XML documents.
   *
   * <p>Compares
   * <pre>&lt;a/&gt;</pre>
   * with
   * <pre>&lt;a/&gt;</pre>
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_IdenticalA() throws IOException, DiffXException {
    String xml1 = "<a/>";
    String xml2 = "<a/>";
    String exp = "<a></a>";
    assertDiffXMLOKTextOnly(xml1, xml2, exp);
  }

  /**
   * Compares two identical XML documents.
   *
   * <p>Compares
   * <pre>&lt;a&gt;X&lt;/a&gt;</pre>
   * with
   * <pre>&lt;a&gt;X&lt;/a&gt;</pre>
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_IdenticalB() throws IOException, DiffXException {
    String xml1 = "<a>X</a>";
    String xml2 = "<a>X</a>";
    String exp = "<a>X</a>";
    assertDiffXMLOKTextOnly(xml1, xml2, exp);
  }

  /**
   * Compares two identical XML documents.
   *
   * <p>Compares
   * <pre>&lt;a m='x'/&gt;</pre>
   * with
   * <pre>&lt;a m='x'/&gt;</pre>
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_IdenticalD() throws IOException, DiffXException {
    String xml1 = "<a m='x'/>";
    String xml2 = "<a m='x'/>";
    String exp = "<a>@(m=x)</a>";
    assertDiffXMLOKTextOnly(xml1, xml2, exp);
  }

  /**
   * Compares two identical XML documents.
   *
   * <p>Compares
   * <pre>&lt;a m='x'/&gt;</pre>
   * with
   * <pre>&lt;a m='x'/&gt;</pre>
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_IdenticalE() throws IOException, DiffXException {
    String xml1 = "<a m='x' n='y'/>";
    String xml2 = "<a n='y' m='x'/>";
    String[] exp = new String[]{
        "<a>@(m=x)@(n=y)</a>",
        "<a>@(n=y)@(m=x)</a>"
    };
    assertDiffXMLOKTextOnly(xml1, xml2, exp);
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
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_TotalDiffA() throws IOException, DiffXException {
    String xml1 = "<a/>";
    String xml2 = "<b/>";
    String[] exp1 = new String[]{
        "+<a>+</a>-<b>-</b>",
        "+<a>-<b>-</b>+</a>",
        "-<b>+<a>+</a>-</b>",
        "-<b>-</b>+<a>+</a>"};
    String[] exp2 = new String[]{
        "-<a>-</a>+<b>+</b>",
        "-<a>+<b>+</b>-</a>",
        "+<b>-<a>-</a>+</b>",
        "+<b>+</b>-<a>-</a>"};
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
  }

  /**
   * Compares two completely different XML documents.
   *
   * <p>Compares
   * <pre>&lt;a/&gt;</pre>
   * with
   * <pre><i>(nothing)</i></pre>
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_TotalDiffB() throws IOException, DiffXException {
    String xml1 = "<a/>";
    String xml2 = "";
    String exp1 = "+<a>+</a>";
    String exp2 = "-<a>-</a>";
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
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
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_ModifiedTextA() throws IOException, DiffXException {
    String xml1 = "<a>X</a>";
    String xml2 = "<a>Y</a>";
    String[] exp1 = new String[]{
        "<a>+(X)-(Y)</a>",
        "<a>-(Y)+(X)</a>"
    };
    String[] exp2 = new String[]{
        "<a>-(X)+(Y)</a>",
        "<a>+(Y)-(X)</a>"
    };
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
  }

  /**
   * Compares two XML documents with a simple difference in the text.
   *
   * <p>Compares
   * <pre>&lt;a&gt;x &lt;/a&gt;</pre>
   * with
   * <pre>&lt;a&gt;x&lt;/a&gt;</pre>
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_ModifiedTextB() throws IOException, DiffXException {
    String xml1 = "<a>X </a>";
    String xml2 = "<a>X</a>";
    String exp1 = "<a>X+( )</a>";
    String exp2 = "<a>X-( )</a>";
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
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
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_MoveA() throws IOException, DiffXException {
    String xml1 = "<a><b>x</b><c/></a>";
    String xml2 = "<a><b/><c>x</c></a>";
    String exp1 = "<a><b>+(x)</b><c>-(x)</c></a>";
    String exp2 = "<a><b>-(x)</b><c>+(x)</c></a>";
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
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
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_ElementA() throws IOException, DiffXException {
    String xml1 = "<a><b/></a>";
    String xml2 = "<a><c/></a>";
    String[] exp1 = new String[]{
        "<a>+<b>+</b>-<c>-</c></a>",
        "<a>+<b>-<c>-</c>+</b></a>",
        "<a>-<c>+<b>+</b>-</c></a>",
        "<a>-<c>-</c>+<b>+</b></a>"
    };
    String[] exp2 = new String[]{
        "<a>-<b>-</b>+<c>+</c></a>",
        "<a>-<b>+<c>+</c>-</b></a>",
        "<a>+<c>-<b>-</b>+</c></a>",
        "<a>+<c>+</c>-<b>-</b></a>"
    };
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
  }

  /**
   * Compares two XML documents with a simple difference in the element nodes.
   *
   * <p>Compares
   * <pre>&lt;a&gt;X&lt;/a&gt;</pre>
   * with
   * <pre>&lt;b&gt;X&lt;/b&gt;</pre>
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_ElementB() throws IOException, DiffXException {
    String xml1 = "<a>X</a>";
    String xml2 = "<b>X</b>";
    String[] exp1 = new String[]{
        "-<b>+<a>X+</a>-</b>",
        "+<a>-<b>X-</b>+</a>"
    };
    String[] exp2 = new String[]{
        "+<b>-<a>X-</a>+</b>",
        "-<a>+<b>X+</b>-</a>"
    };
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
  }

  /**
   * Compares two XML documents with a simple difference in the element nodes.
   *
   * <p>Compares
   * <pre>&lt;a&gt;&lt;b&gt;X&lt;/b&gt;&lt;/a&gt;</pre>
   * with
   * <pre>&lt;b&gt;&lt;a&gt;X&lt;/a&gt;&lt;/b&gt;</pre>
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_ElementC() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b></a>";
    String xml2 = "<b><a>X</a></b>";
    String exp1 = "+<a><b>-<a>X-</a></b>+</a>";
    String exp2 = "+<b><a>-<b>X-</b></a>+</b>";
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
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
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_TextElementA() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b><c>Y</c></a>";
    String xml2 = "<a><b>X</b></a>";
    String exp1 = "<a><b>X</b>+<c>+(Y)+</c></a>";
    String exp2 = "<a><b>X</b>-<c>-(Y)-</c></a>";
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
  }

  /**
   * Compares two XML documents with differences in both the text and the element nodes.
   *
   * <p>Compares
   * <pre>&lt;a&gt;&lt;b&gt;X&lt;/b&gt;&lt;c&gt;Y&lt;/c&gt;&lt;/a&gt;</pre>
   * with
   * <pre>&lt;a&gt;&lt;c&gt;Y&lt;/c&gt;&lt;/a&gt;</pre>
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_TextElementB() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b><c>Y</c></a>";
    String xml2 = "<a><c>Y</c></a>";
    String exp1 = "<a>+<b>+(X)+</b><c>Y</c></a>";
    String exp2 = "<a>-<b>-(X)-</b><c>Y</c></a>";
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
  }

// attributes tests ---------------------------------------------------------------------

  /**
   * Compares two XML documents where an attribute has been inserted / removed.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_AttributeA() throws IOException, DiffXException {
    String xml1 = "<a m='x'/>";
    String xml2 = "<a/>";
    String exp1 = "<a>+@(m=x)</a>";
    String exp2 = "<a>-@(m=x)</a>";
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
  }

  /**
   * Compares two XML documents where an attribute has been modified.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_AttributeB() throws IOException, DiffXException {
    String xml1 = "<a m='y'/>";
    String xml2 = "<a m='x'/>";
    String[] exp1 = new String[]{
        "<a>-@(m=x)+@(m=y)</a>",
        "<a>+@(m=y)-@(m=x)</a>",
    };
    String[] exp2 = new String[]{
        "<a>+@(m=x)-@(m=y)</a>",
        "<a>-@(m=y)+@(m=x)</a>",
    };
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
  }

  /**
   * Compares two XML documents where an attribute has been modified.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_AttributeC() throws IOException, DiffXException {
    String xml2 = "<a n='w' m='x'/>";
    String xml1 = "<a m='y' n='z'/>";
    String[] exp = new String[]{
        "<a>+@(m=y)+@(n=z)-@(m=x)-@(n=w)</a>",
        "<a>+@(m=y)-@(m=x)+@(n=z)-@(n=w)</a>",
        "<a>+@(m=y)-@(m=x)+@(n=z)-@(n=w)</a>",
    };
    assertDiffXMLOKTextOnly(xml1, xml2, exp);
  }

  /**
   * Compares two XML documents where an attribute has been inserted and the following text
   * has been changed.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_TextAttributeA() throws IOException, DiffXException {
    String xml1 = "<a m='x'>X</a>";
    String xml2 = "<a>Y</a>";
    String[] exp1 = new String[]{
        "<a>+@(m=x)+(X)-(Y)</a>",
        "<a>+@(m=x)-(Y)+(X)</a>"
    };
    String[] exp2 = new String[]{
        "<a>-@(m=x)-(X)+(Y)</a>",
        "<a>-@(m=x)+(Y)-(X)</a>"
    };
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
  }

  /**
   * Compares two XML documents where an attribute has been inserted and the following text
   * has been changed.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_TextAttributeB() throws IOException, DiffXException {
    String xml1 = "<a m='x'>X</a>";
    String xml2 = "<a m='y'>Y</a>";
    String[] exp1 = new String[]{
        "<a>+@(m=x)-@(m=y)+(X)-(Y)</a>",
        "<a>+@(m=x)-@(m=y)-(Y)+(X)</a>",
        "<a>-@(m=y)+@(m=x)+(X)-(Y)</a>",
        "<a>-@(m=y)+@(m=x)-(Y)+(X)</a>"
    };
    String[] exp2 = new String[]{
        "<a>-@(m=x)+@(m=y)-(X)+(Y)</a>",
        "<a>-@(m=x)+@(m=y)+(Y)-(X)</a>",
        "<a>+@(m=y)-@(m=x)-(X)+(Y)</a>",
        "<a>+@(m=y)-@(m=x)+(Y)-(X)</a>"
    };
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
  }

  /**
   * Compares two XML documents where two attributes are on a different namespace.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_AttributeNamespaces0() throws IOException, DiffXException {
    String xml1 = "<a e:m='y' xmlns:e='https://example.org'/>";
    String xml2 = "<a f:m='y' xmlns:f='https://example.net'/>";
    String[] exp = new String[]{};
    // FIXME: write test
    assertDiffXMLOKTextOnly(xml1, xml2, exp);
  }

// self wrap tests ----------------------------------------------------------------------

  /**
   * Wraps the XML in the same element.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_SelfWrapA() throws IOException, DiffXException {
    String xml1 = "<a><a/></a>";
    String xml2 = "<a></a>";
    String[] exp1 = new String[]{
        "<a>+<a>+</a></a>",
        "+<a><a></a>+</a>",
        "<a>+<a></a>+</a>",
        "+<a><a>+</a></a>"
    };
    String[] exp2 = new String[]{
        "<a>-<a>-</a></a>",
        "-<a><a></a>-</a>",
        "<a>-<a></a>-</a>",
        "-<a><a>-</a></a>"
    };
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
  }

  /**
   * Wraps the XML in the same element.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_SelfWrapB() throws IOException, DiffXException {
    String xml1 = "<a><a>x</a></a>";
    String xml2 = "<a>x</a>";
    String[] exp1 = new String[]{
        "<a>+<a>x+</a></a>",
        "+<a><a>x</a>+</a>",
        "<a>+<a>x</a>+</a>",
        "+<a><a>x+</a></a>",
    };
    String[] exp2 = new String[]{
        "<a>-<a>x-</a></a>",
        "-<a><a>x</a>-</a>",
        "<a>-<a>x</a>-</a>",
        "-<a><a>x-</a></a>",
    };
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
  }

// split and merge problems -------------------------------------------------------------

  /**
   * Splits the text of the XML string in 2.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_SplitMergeB() throws IOException, DiffXException {
    String xml1 = "<a><b><c/></b><b><d/></b></a>";
    String xml2 = "<a><b><c/><d/></b></a>";
    String[] exp1 = new String[]{
        "<a><b><c></c>+</b>+<b><d></d></b></a>",
        "<a><b><c></c>-<d>-</d></b>+<b>+<d>+</d>+</b></a>"
    };
    String[] exp2 = new String[]{
        "<a><b><c></c>-</b>-<b><d></d></b></a>",
        "<a><b><c></c>+<d>+</d></b>-<b>-<d>-</d>-</b></a>"
    };
    assertDiffXMLOKTextOnly(xml1, xml2, exp1);
    assertDiffXMLOKTextOnly(xml2, xml1, exp2);
  }

// line tests ---------------------------------------------------------------------------------

  /**
   * Tests that the line diff also work.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_SameLine0() throws IOException, DiffXException {
    String text1 = "line #1\n"
        + "line #2\n"
        + "line #3\n";
    String text2 = "line #1\n"
        + "line #2\n"
        + "line #3\n";
    String exp = "L1L2L3";
    String diff = processDiffLines(text1, text2);
    Assert.assertEquals(exp, diff);
  }

  /**
   * Tests that the line diff also work.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_LineChange0() throws IOException, DiffXException {
    String text1 = "line #1\n"
        + "line #2\n"
        + "line #3\n";
    String text2 = "line #1\n"
        + "line #X\n"
        + "line #3\n";
    String[] exp = new String[]{
        "L1+L2-L2L3",
        "L1-L2+L2L3"
    };
    assertDiffLinesOK(text1, text2, exp);
  }

  /**
   * Tests that the line diff also work.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_LineInsert0() throws IOException, DiffXException {
    String text1 = "line #1\n"
        + "line #2\n"
        + "line #3\n";
    String text2 = "line #1\n"
        + "line #3\n";
    String[] exp = new String[]{
        "L1+L2L2",
        "L1+L2L3"
    };
    assertDiffLinesOK(text1, text2, exp);
  }

  /**
   * Tests that the line diff also work.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel1_LineRemove0() throws IOException, DiffXException {
    String text1 = "line #1\n"
        + "line #3\n";
    String text2 = "line #1\n"
        + "line #2\n"
        + "line #3\n";
    String[] exp = new String[]{
        "L1-L2L2",
        "L1-L2L3"
    };
    assertDiffLinesOK(text1, text2, exp);
  }

  // helpers
  // --------------------------------------------------------------------------

  private void assertDiffXMLOKTextOnly(String xml1, String xml2, String exp)
      throws IOException, DiffXException {
    assertDiffXMLOKTextOnly(xml1, xml2, new String[]{exp});
  }

  private void assertDiffXMLOKTextOnly(String xml1, String xml2, String[] exp)
      throws IOException, DiffXException {
    // Record XML
    List<? extends DiffXEvent> seq1 = Events.recordXMLEvents(xml1, TextGranularity.TEXT);
    List<? extends DiffXEvent> seq2 = Events.recordXMLEvents(xml2, TextGranularity.TEXT);
    // Process as list of actions
    List<Action> actions = diffToActions(seq1, seq2);
    try {
      assertDiffIsCorrect(seq1, seq2, actions);
      assertDiffIsWellFormedXML(actions);
      assertMatchTestOutput(actions, exp);
    } catch (AssertionError ex) {
      printXMLErrorDetails(xml1, xml2, exp, toXML(actions), actions);
      throw ex;
    }
  }

  private void assertDiffLinesOK(String text1, String text2, String[] exp)
      throws IOException, DiffXException {
    // process the strings
    String diffout = processDiffLines(text1, text2);
    // check the possible values
    boolean ok = false;
    try {
      for (String s : exp) {
        ok = ok || s.equals(diffout);
      }
      if (!ok)
        Assert.assertEquals(exp[0], diffout);
    } catch (AssertionError ex) {
      //printErrorDetails(text1, text2, exp);
      throw ex;
    }
  }

  private String processDiffLines(String text1, String text2)
      throws IOException, DiffXException, IllegalStateException {
    // process the strings
    List<? extends DiffXEvent> seq1 = Events.recordLineEvents(text1);
    List<? extends DiffXEvent> seq2 = Events.recordLineEvents(text2);
    DiffAlgorithm processor = getDiffAlgorithm();
    TestHandler handler = new TestHandler();
    processor.diff(seq1, seq2, handler);
    return handler.getOutput();
  }

}
