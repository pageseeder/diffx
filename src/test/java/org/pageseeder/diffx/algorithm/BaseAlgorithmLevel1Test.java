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
package org.pageseeder.diffx.algorithm;

import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.ActionFormatter;
import org.pageseeder.diffx.action.ActionsUtils;
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.format.MultiplexFormatter;
import org.pageseeder.diffx.format.SmartXMLFormatter;
import org.pageseeder.diffx.load.SAXRecorder;
import org.pageseeder.diffx.load.TextRecorder;
import org.pageseeder.diffx.sequence.EventSequence;
import org.pageseeder.diffx.test.TestFormatter;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * Test case for Diff-X algorithm implementations.
 *
 * <p>To pass this test an algorithm must find the correct differences and produce
 * results that are well-formed in so far as insertion and deletion are considered
 * as separate dimensions.
 *
 * <p>Level 1 algorithms must produce results that are good enough to be formatted
 * and show the correct differences.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class BaseAlgorithmLevel1Test extends TestCase {

  /**
   * The Diff-X algorithm being tested.
   */
  private transient DiffXAlgorithm diffx = null;

  /**
   * The loader used for the tests.
   */
  private final SAXRecorder recorder = new SAXRecorder();

  /**
   * Default constructor.
   *
   * @param name Name of the test.
   */
  public BaseAlgorithmLevel1Test(String name) {
    super(name);
  }

// method that test classes must implement ----------------------------------------------

  /**
   * Returns the Diff-X Algorithm instance from the specified sequences.
   *
   * @param seq1 The first sequence.
   * @param seq2 The second sequence.
   *
   * @return The Diff-X Algorithm instance.
   */
  public abstract DiffXAlgorithm makeDiffX(EventSequence seq1, EventSequence seq2);

// identity test ------------------------------------------------------------------------

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
  public final void testIdenticalA() throws IOException, DiffXException {
    String xml1 = "<a/>";
    String xml2 = "<a/>";
    String exp = "<a></a>";
    assertDiffXMLOK(xml1, xml2, exp);
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
  public final void testIdenticalB() throws IOException, DiffXException {
    String xml1 = "<a>X</a>";
    String xml2 = "<a>X</a>";
    String exp = "<a>$w{X}</a>";
    assertDiffXMLOK(xml1, xml2, exp);
  }

  /**
   * Compares two identical XML documents.
   *
   * <p>Compares
   * <pre>&lt;a&gt;X Y&lt;/a&gt;</pre>
   * with
   * <pre>&lt;a&gt;X Y&lt;/a&gt;</pre>
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testIdenticalC() throws IOException, DiffXException {
    String xml1 = "<a>X Y</a>";
    String xml2 = "<a>X Y</a>";
    String exp = "<a>$w{X}$s{ }$w{Y}</a>";
    assertDiffXMLOK(xml1, xml2, exp);
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
  public final void testIdenticalD() throws IOException, DiffXException {
    String xml1 = "<a m='x'/>";
    String xml2 = "<a m='x'/>";
    String exp = "<a>@{m=x}</a>";
    assertDiffXMLOK(xml1, xml2, exp);
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
  public final void testIdenticalE() throws IOException, DiffXException {
    String xml1 = "<a m='x' n='y'/>";
    String xml2 = "<a n='y' m='x'/>";
    String[] exp = new String[] {
        "<a>@{m=x}@{n=y}</a>",
        "<a>@{n=y}@{m=x}</a>"
    };
    assertDiffXMLOK(xml1, xml2, exp);
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
  public final void testTotalDiffA() throws IOException, DiffXException {
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
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
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
  public final void testTotalDiffB() throws IOException, DiffXException {
    String xml1 = "<a/>";
    String xml2 = "";
    String exp1 = "+<a>+</a>";
    String exp2 = "-<a>-</a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
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
  public final void testModifiedTextA() throws IOException, DiffXException {
    String xml1 = "<a>X</a>";
    String xml2 = "<a>Y</a>";
    String[] exp1 = new String[]{
        "<a>+$w{X}-$w{Y}</a>",
        "<a>-$w{Y}+$w{X}</a>"
    };
    String[] exp2 = new String[]{
        "<a>-$w{X}+$w{Y}</a>",
        "<a>+$w{Y}-$w{X}</a>"
    };
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
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
  public final void testModifiedTextB() throws IOException, DiffXException {
    String xml1 = "<a>X </a>";
    String xml2 = "<a>X</a>";
    String exp1 = "<a>$w{X}+$s{ }</a>";
    String exp2 = "<a>$w{X}-$s{ }</a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  /**
   * Compares two XML documents with a simple difference in the text.
   *
   * <p>Compares
   * <pre>&lt;a&gt;X&lt;/a&gt;</pre>
   * with
   * <pre>&lt;a&gt;X Y&lt;/a&gt;</pre>
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testModifiedTextC() throws IOException, DiffXException {
    String xml1 = "<a>X Y</a>";
    String xml2 = "<a>X</a>";
    String exp1 = "<a>$w{X}+$s{ }+$w{Y}</a>";
    String exp2 = "<a>$w{X}-$s{ }-$w{Y}</a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
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
  public final void testMoveA() throws IOException, DiffXException {
    String xml1 = "<a><b>x</b><c/></a>";
    String xml2 = "<a><b/><c>x</c></a>";
    String exp1 = "<a><b>+$w{x}</b><c>-$w{x}</c></a>";
    String exp2 = "<a><b>-$w{x}</b><c>+$w{x}</c></a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  /**
   * Compares two XML documents where the text of one element moves to
   * another element.
   *
   * <p>Compares
   * <pre>&lt;a&gt;&lt;b&gt;x y&lt;/b&gt;&lt;c/&gt;&lt;/a&gt;</pre>
   * with
   * <pre>&lt;a&gt;&lt;b/&gt;&lt;c&gt;x y&lt;/c&gt;&lt;/a&gt;</pre>
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testMoveB() throws IOException, DiffXException {
    String xml1 = "<a><b>x y</b><c/></a>";
    String xml2 = "<a><b/><c>x y</c></a>";
    String exp1 = "<a><b>+$w{x}+$s{ }+$w{y}</b><c>-$w{x}-$s{ }-$w{y}</c></a>";
    String exp2 = "<a><b>-$w{x}-$s{ }-$w{y}</b><c>+$w{x}+$s{ }+$w{y}</c></a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
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
  public final void testElementA() throws IOException, DiffXException {
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
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
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
  public final void testElementB() throws IOException, DiffXException {
    String xml1 = "<a>X</a>";
    String xml2 = "<b>X</b>";
    String[] exp1 = new String[]{
        "-<b>+<a>$w{X}+</a>-</b>",
        "+<a>-<b>$w{X}-</b>+</a>"
    };
    String[] exp2 = new String[]{
        "+<b>-<a>$w{X}-</a>+</b>",
        "-<a>+<b>$w{X}+</b>-</a>"
    };
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
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
  public final void testElementC() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b></a>";
    String xml2 = "<b><a>X</a></b>";
    String exp1 = "+<a><b>-<a>$w{X}-</a></b>+</a>";
    String exp2 = "+<b><a>-<b>$w{X}-</b></a>+</b>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
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
  public final void testTextElementA() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b><c>Y</c></a>";
    String xml2 = "<a><b>X</b></a>";
    String exp1 = "<a><b>$w{X}</b>+<c>+$w{Y}+</c></a>";
    String exp2 = "<a><b>$w{X}</b>-<c>-$w{Y}-</c></a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
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
  public final void testTextElementB() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b><c>Y</c></a>";
    String xml2 = "<a><c>Y</c></a>";
    String exp1 = "<a>+<b>+$w{X}+</b><c>$w{Y}</c></a>";
    String exp2 = "<a>-<b>-$w{X}-</b><c>$w{Y}</c></a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  /**
   * Compares two XML documents with differences in both the text and the element nodes.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testTextElementC() throws IOException, DiffXException {
    String xml1 = "<a><b>W X</b><c>Y Z</c></a>";
    String xml2 = "<a><b>W X</b></a>";
    String exp1 = "<a><b>$w{W}$s{ }$w{X}</b>+<c>+$w{Y}+$s{ }+$w{Z}+</c></a>";
    String exp2 = "<a><b>$w{W}$s{ }$w{X}</b>-<c>-$w{Y}-$s{ }-$w{Z}-</c></a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

// attributes tests ---------------------------------------------------------------------

  /**
   * Compares two XML documents where an attribute has been inserted / removed.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testAttributeA() throws IOException, DiffXException {
    String xml1 = "<a m='x'/>";
    String xml2 = "<a/>";
    String exp1 = "<a>+@{m=x}</a>";
    String exp2 = "<a>-@{m=x}</a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  /**
   * Compares two XML documents where an attribute has been modified.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testAttributeB() throws IOException, DiffXException {
    String xml1 = "<a m='y'/>";
    String xml2 = "<a m='x'/>";
    String[] exp1 = new String[]{
        "<a>-@{m=x}+@{m=y}</a>",
        "<a>+@{m=y}-@{m=x}</a>",
    };
    String[] exp2 = new String[]{
        "<a>+@{m=x}-@{m=y}</a>",
        "<a>-@{m=y}+@{m=x}</a>",
    };
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  /**
   * Compares two XML documents where an attribute has been modified.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testAttributeC() throws IOException, DiffXException {
    String xml2 = "<a n='w' m='x'/>";
    String xml1 = "<a m='y' n='z'/>";
    String[] exp = new String[]{
        "<a>+@{m=y}+@{n=z}-@{m=x}-@{n=w}</a>",
        "<a>+@{m=y}-@{m=x}+@{n=z}-@{n=w}</a>",
        "<a>+@{m=y}-@{m=x}+@{n=z}-@{n=w}</a>",
    };
    assertDiffXMLOK(xml1, xml2, exp);
  }

  /**
   * Compares two XML documents where an attribute has been inserted and the following text
   * has been changed.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testTextAttributeA() throws IOException, DiffXException {
    String xml1 = "<a m='x'>X</a>";
    String xml2 = "<a>Y</a>";
    String[] exp1 = new String[]{
        "<a>+@{m=x}+$w{X}-$w{Y}</a>",
        "<a>+@{m=x}-$w{Y}+$w{X}</a>"
    };
    String[] exp2 = new String[]{
        "<a>-@{m=x}-$w{X}+$w{Y}</a>",
        "<a>-@{m=x}+$w{Y}-$w{X}</a>"
    };
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  /**
   * Compares two XML documents where an attribute has been inserted and the following text
   * has been changed.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testTextAttributeB() throws IOException, DiffXException {
    String xml1 = "<a m='x'>X</a>";
    String xml2 = "<a m='y'>Y</a>";
    String[] exp1 = new String[]{
        "<a>+@{m=x}-@{m=y}+$w{X}-$w{Y}</a>",
        "<a>+@{m=x}-@{m=y}-$w{Y}+$w{X}</a>",
        "<a>-@{m=y}+@{m=x}+$w{X}-$w{Y}</a>",
        "<a>-@{m=y}+@{m=x}-$w{Y}+$w{X}</a>"
    };
    String[] exp2 = new String[]{
        "<a>-@{m=x}+@{m=y}-$w{X}+$w{Y}</a>",
        "<a>-@{m=x}+@{m=y}+$w{Y}-$w{X}</a>",
        "<a>+@{m=y}-@{m=x}-$w{X}+$w{Y}</a>",
        "<a>+@{m=y}-@{m=x}+$w{Y}-$w{X}</a>"
    };
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  /**
   * Compares two XML documents where two attributes are on a different namespace.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testAttributeNamespaces0() throws IOException, DiffXException {
    String xml1 = "<a e:m='y' xmlns:e='h://e.org'/>";
    String xml2 = "<a f:m='y' xmlns:f='h://f.org'/>";
    String[] exp = new String[]{
    };
    // FIXME: write test
//    assertDiffXMLOK(xml1, xml2, exp);
  }

// self wrap tests ----------------------------------------------------------------------

  /**
   * Wraps the XML in the same element.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testSelfWrapA() throws IOException, DiffXException {
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
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  /**
   * Wraps the XML in the same element.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testSelfWrapB() throws IOException, DiffXException {
    String xml1 = "<a><a>x</a></a>";
    String xml2 = "<a>x</a>";
    String[] exp1 = new String[]{
        "<a>+<a>$w{x}+</a></a>",
        "+<a><a>$w{x}</a>+</a>",
        "<a>+<a>$w{x}</a>+</a>",
        "+<a><a>$w{x}+</a></a>",
    };
    String[] exp2 = new String[]{
        "<a>-<a>$w{x}-</a></a>",
        "-<a><a>$w{x}</a>-</a>",
        "<a>-<a>$w{x}</a>-</a>",
        "-<a><a>$w{x}-</a></a>",
    };
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

// split and merge problems -------------------------------------------------------------

  /**
   * Splits / merge the text of the XML string.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testSplitMergeA() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b> <b>Y</b></a>";
    String xml2 = "<a><b>X Y</b></a>";
    // split
    String[] exp1 = new String[]{
        "<a><b>$w{X}+</b>$s{ }+<b>$w{Y}</b></a>",               // tags inserted
        "<a><b>$w{X}-$s{ }-$w{Y}</b>+$s{ }+<b>+$w{Y}+</b></a>"  // text has moved
    };
    // merge
    String[] exp2 = new String[]{
        "<a><b>$w{X}-</b>$s{ }-<b>$w{Y}</b></a>",               // tags removed
        "<a><b>$w{X}+$s{ }+$w{Y}</b>-$s{ }-<b>-$w{Y}-</b></a>"  // text has moved
    };
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  /**
   * Splits the text of the XML string in 2.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testSplitMergeB() throws IOException, DiffXException {
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
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

// line tests ---------------------------------------------------------------------------------

  /**
   * Tests that the line diff also work.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testSameLine0() throws IOException, DiffXException {
    String text1 = "line #1\n"
                 + "line #2\n"
                 + "line #3\n";
    String text2 = "line #1\n"
                 + "line #2\n"
                 + "line #3\n";
    String exp = "$L1$L2$L3";
    String diff = processDiffText(text1, text2);
    assertEquals(exp, diff);
  }

  /**
   * Tests that the line diff also work.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testLineChange0() throws IOException, DiffXException {
    String text1 = "line #1\n"
                 + "line #2\n"
                 + "line #3\n";
    String text2 = "line #1\n"
                 + "line #X\n"
                 + "line #3\n";
    String[] exp = new String[]{
                      "$L1+$L2-$L2$L3",
                      "$L1-$L2+$L2$L3"
                   };
    assertDiffTextOK(text1, text2, exp);
  }

  /**
   * Tests that the line diff also work.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testLineInsert0() throws IOException, DiffXException {
    String text1 = "line #1\n"
                 + "line #2\n"
                 + "line #3\n";
    String text2 = "line #1\n"
                 + "line #3\n";
    String[] exp = new String[]{
                      "$L1+$L2$L2",
                      "$L1+$L2$L3"
                   };
    assertDiffTextOK(text1, text2, exp);
  }

  /**
   * Tests that the line diff also work.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testLineRemove0() throws IOException, DiffXException {
    String text1 = "line #1\n"
                 + "line #3\n";
    String text2 = "line #1\n"
                 + "line #2\n"
                 + "line #3\n";
    String[] exp = new String[]{
                      "$L1-$L2$L2",
                      "$L1-$L2$L3"
                   };
    assertDiffTextOK(text1, text2, exp);
  }

// helpers ------------------------------------------------------------------------------------

  /**
   * Asserts that the Diff-X operation for XML meets expectations.
   *
   * @param xml1 The first XML to compare with diffx.
   * @param xml2 The first XML to compare with diffx.
   * @param exp  The expected result as formatted by the TestFormatter.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void assertDiffXMLOK(String xml1, String xml2, String exp)
     throws IOException, DiffXException {
    assertDiffXMLOK(xml1, xml2, new String[]{exp});
  }

  /**
   * Asserts that the Diff-X operation for XML meets expectations.
   *
   * @param xml1 The first XML to compare with diffx.
   * @param xml2 The first XML to compare with diffx.
   * @param exp  The expected result as formatted by the TestFormatter.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void assertDiffXMLOK(String xml1, String xml2, String[] exp)
     throws IOException, DiffXException {
    // process the strings
    String diffout = processDiffXML(xml1, xml2);
    // check the possible values
    boolean ok = false;
    try {
      for (String s : exp) {
        ok = ok || s.equals(diffout);
      }
      if (!ok)
        assertEquals(exp[0], diffout);
    } catch (AssertionFailedError ex) {
      printErrorDetails(xml1, xml2, exp);
      throw ex;
    }
  }

  /**
   * Processes the diff and returns the result
   *
   * @param xml1 The first XML doc.
   * @param xml2 The second XML doc.
   *
   * @return The diff output.
   *
   * @throws IOException           Should an I/O exception occur.
   * @throws DiffXException        Should an error occur while parsing XML.
   * @throws IllegalStateException Should the factory fail to create DiffX algorithm.
   */
  private String processDiffXML(String xml1, String xml2)
     throws IOException, DiffXException, IllegalStateException {
    // process the strings
    EventSequence seq1 = "".equals(xml1)? new EventSequence(0) : this.recorder.process(xml1);
    EventSequence seq2 = "".equals(xml2)? new EventSequence(0) : this.recorder.process(xml2);
    this.diffx = makeDiffX(seq1, seq2);
    MultiplexFormatter mf = new MultiplexFormatter();
    ActionFormatter af = new ActionFormatter();
    TestFormatter tf = new TestFormatter();
    mf.add(af);
    mf.add(tf);
    this.diffx.process(tf);
    // check for validity
    List<Action> actions = af.getActions();
    assertTrue(ActionsUtils.isValid(seq1.events(), seq2.events(), actions));
    return tf.getOutput();
  }

  /**
   * Asserts that the diffx operation went right.
   *
   * @param text1 The first XML to compare with diffx.
   * @param text2 The first XML to compare with diffx.
   * @param exp  The expected result as formatted by the TestFormatter.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  private void assertDiffTextOK(String text1, String text2, String[] exp)
     throws IOException, DiffXException {
    // process the strings
    String diffout = processDiffText(text1, text2);
    // check the possible values
    boolean ok = false;
    try {
      for (String s : exp) {
        ok = ok || s.equals(diffout);
      }
      if (!ok)
        assertEquals(exp[0], diffout);
    } catch (AssertionFailedError ex) {
      printErrorDetails(text1, text2, exp);
      throw ex;
    }
  }

  /**
   * Processes the diff and returns the result
   *
   * @param text1 The first text.
   * @param text2 The second text.
   *
   * @return The diff output.
   *
   * @throws IOException           Should an I/O exception occur.
   * @throws DiffXException        Should an error occur while parsing XML.
   * @throws IllegalStateException Should the factory fail to create DiffX algorithm.
   */
  private String processDiffText(String text1, String text2)
     throws IOException, DiffXException, IllegalStateException {
    // process the strings
    TextRecorder textRecorder = new TextRecorder();
    EventSequence seq1 = textRecorder.process(text1);
    EventSequence seq2 = textRecorder.process(text2);
    this.diffx = makeDiffX(seq1, seq2);
    TestFormatter tf = new TestFormatter();
    this.diffx.process(tf);
    return tf.getOutput();
  }

  /**
   * Print the error details.
   *
   * @param xml1 The new XML.
   * @param xml2 The old XML.
   * @param exp  The expected output
   *
   * @throws IOException Should an error occur.
   */
  private void printErrorDetails(String xml1, String xml2, String[] exp) throws IOException {
    // print the XML on the console
    Writer sw = new StringWriter();
    DiffXFormatter sf = new SmartXMLFormatter(sw);
    this.diffx.process(sf);
    TestFormatter tf = new TestFormatter();
    this.diffx.process(tf);
    System.err.println("*------------------------------------------------");
    System.err.println("* New XML:");
    System.err.println(xml1);
    System.err.println("* Old XML:");
    System.err.println(xml2);
    System.err.println("* Diff-X XML Output:");
    System.err.println(sw.toString());
    System.err.println("* Normalised Diff-X output:");
    System.err.println(tf.getOutput());
    for (int i = 0; i < exp.length; i++) {
      System.err.println("* Expected output #"+i);
      System.err.println(exp[i]);
    }
  }

}
