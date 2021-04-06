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

import org.junit.Test;
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.algorithm.BaseAlgorithmLevel1Test;

import java.io.IOException;

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
public abstract class BaseProcessorLevel2Test extends BaseProcessorLevel1Test {

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
  @Test
  public final void testLevel2_IdenticalC() throws IOException, DiffXException {
    String xml1 = "<a>X Y</a>";
    String xml2 = "<a>X Y</a>";
    String exp = "<a>$w{X}$s{ }$w{Y}</a>";
    assertDiffXMLOK2(xml1, xml2, exp);
  }

  /**
   * Wraps the XML in the same element.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel2_SelfWrapA() throws IOException, DiffXException {
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
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  /**
   * Wraps the XML in the same element.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel2_SelfWrapB() throws IOException, DiffXException {
    String xml1 = "<a><a>x</a></a>";
    String xml2 = "<a>x</a>";
    String[] exp1 = new String[]{
        "<a>+<a>$w{x}+</a></a>",
        "+<a><a>$w{x}</a>+</a>"
    };
    String[] exp2 = new String[]{
        "<a>-<a>$w{x}-</a></a>",
        "-<a><a>$w{x}</a>-</a>"
    };
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  /**
   * Splits / merge the text of the XML string in 2.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel2_SplitMergeA() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b> <b>Y</b></a>";
    String xml2 = "<a><b>X Y</b></a>";
    String exp1 = "<a><b>$w{X}-$s{ }-$w{Y}</b>+$s{ }+<b>+$w{Y}+</b></a>";
    String exp2 = "<a><b>$w{X}+$s{ }+$w{Y}</b>-$s{ }-<b>-$w{Y}-</b></a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  /**
   * Splits / merge the text of the XML string.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel2_SplitMergeA1() throws IOException, DiffXException {
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
    assertDiffXMLOK2(xml1, xml2, exp1);
    assertDiffXMLOK2(xml2, xml1, exp2);
  }

  /**
   * Tests some moved branch.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel2_MovedBranch() throws IOException, DiffXException {
    String xml1 = "<a><b>M</b><a><b>A</b></a><b>N</b></a>";
    String xml2 = "<a><b>M<a><b>A</b></a></b><b>N</b></a>";
    String exp1 = "<a><b>$w{M}-<a>-<b>-$w{A}-</b>-</a></b>+<a>+<b>+$w{A}+</b>+</a><b>$w{N}</b></a>";
    String exp2 = "<a><b>$w{M}+<a>+<b>+$w{A}+</b>+</a></b>-<a>-<b>-$w{A}-</b>-</a><b>$w{N}</b></a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  /**
   * Splits the text of the XML string in 2.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel2_SplitMergeB() throws IOException, DiffXException {
    String xml1 = "<a><b><c/></b><b><d/></b></a>";
    String xml2 = "<a><b><c/><d/></b></a>";
    String exp1 = "<a><b><c></c>-<d>-</d></b>+<b>+<d>+</d>+</b></a>";
    String exp2 = "<a><b><c></c>+<d>+</d></b>-<b>-<d>-</d>-</b></a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  /**
   * Compares two completely different XML documents.
   *
   * <p>Compares
   * <pre>&lt;a&gt;&lt;b&gt;x y&lt;/b&gt;&lt;c/&gt;&lt;/a&gt;</pre>
   * with
   * <pre>&lt;a&gt;&lt;b/&gt;&lt;c&gt;x y&lt;/c&gt;&lt;/a&gt;</pre>
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel2_BestPath() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b></a>";
    String xml2 = "<a><b/><b>X</b></a>";
    String exp1 = "<a>-<b>-</b><b>$w{X}</b></a>";
    String exp2 = "<a>+<b>+</b><b>$w{X}</b></a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  @Test
  public final void testLevel2_Temp() throws IOException, DiffXException {
    String xml1 = "<a xmlns:x='XXX' xmlns:y='YYY' xmlns='ns'><b>X</b></a>";
    String xml2 = "<a xmlns:x='XXX' xmlns:y='YYY' xmlns='ns'><x:b>X</x:b></a>";
    String exp1 = "<a>-<b>-</b><b>$w{X}</b></a>";
    String exp2 = "<a>+<b>+</b><b>$w{X}</b></a>";
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
  @Test
  public final void testLevel2_MoveB() throws IOException, DiffXException {
    String xml1 = "<a><b>x y</b><c/></a>";
    String xml2 = "<a><b/><c>x y</c></a>";
    String exp1 = "<a><b>+$w{x}+$s{ }+$w{y}</b><c>-$w{x}-$s{ }-$w{y}</c></a>";
    String exp2 = "<a><b>-$w{x}-$s{ }-$w{y}</b><c>+$w{x}+$s{ }+$w{y}</c></a>";
    assertDiffXMLOK2(xml1, xml2, exp1);
    assertDiffXMLOK2(xml2, xml1, exp2);
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
  @Test
  public final void testLevel2_ModifiedTextC() throws IOException, DiffXException {
    String xml1 = "<a>X Y</a>";
    String xml2 = "<a>X</a>";
    String exp1 = "<a>$w{X}+$s{ }+$w{Y}</a>";
    String exp2 = "<a>$w{X}-$s{ }-$w{Y}</a>";
    assertDiffXMLOK2(xml1, xml2, exp1);
    assertDiffXMLOK2(xml2, xml1, exp2);
  }

  /**
   * Compares two XML documents with differences in both the text and the element nodes.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testLevel2_TextElementC() throws IOException, DiffXException {
    String xml1 = "<a><b>W X</b><c>Y Z</c></a>";
    String xml2 = "<a><b>W X</b></a>";
    String exp1 = "<a><b>$w{W}$s{ }$w{X}</b>+<c>+$w{Y}+$s{ }+$w{Z}+</c></a>";
    String exp2 = "<a><b>$w{W}$s{ }$w{X}</b>-<c>-$w{Y}-$s{ }-$w{Z}-</c></a>";
    assertDiffXMLOK2(xml1, xml2, exp1);
    assertDiffXMLOK2(xml2, xml1, exp2);
  }

}
