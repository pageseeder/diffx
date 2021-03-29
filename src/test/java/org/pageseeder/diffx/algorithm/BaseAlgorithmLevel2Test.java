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

import java.io.IOException;

/**
 * Test case for Diff-X algorithm implementations.
 *
 * <p>This class extends the the level 1 tests, and expect algorithms to produce
 * results of better quality, that is which serialised form always produce
 * well-formed XML.
 *
 * @author Christophe Lauret
 * @version 15 April 2005
 */
public abstract class BaseAlgorithmLevel2Test extends BaseAlgorithmLevel1Test {

// constructors -------------------------------------------------------------------------

  /**
   * Default constructor.
   *
   * @param name Name of the test.
   */
  public BaseAlgorithmLevel2Test(String name) {
    super(name);
  }

// test ---------------------------------------------------------------------------------

  /**
   * Wraps the XML in the same element.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testSelfWrapALevel2() throws IOException, DiffXException {
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
  public final void testSelfWrapBLevel2() throws IOException, DiffXException {
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
  public final void testSplitMergeALevel2() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b> <b>Y</b></a>";
    String xml2 = "<a><b>X Y</b></a>";
    String exp1 = "<a><b>$w{X}-$s{ }-$w{Y}</b>+$s{ }+<b>+$w{Y}+</b></a>";
    String exp2 = "<a><b>$w{X}+$s{ }+$w{Y}</b>-$s{ }-<b>-$w{Y}-</b></a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  /**
   * Tests some moved branch.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testMovedBranch() throws IOException, DiffXException {
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
  public final void testSplitMergeBLevel2() throws IOException, DiffXException {
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
  public final void testBestPath() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b></a>";
    String xml2 = "<a><b/><b>X</b></a>";
    String exp1 = "<a>-<b>-</b><b>$w{X}</b></a>";
    String exp2 = "<a>+<b>+</b><b>$w{X}</b></a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  public final void testTemp() throws IOException, DiffXException {
    String xml1 = "<a xmlns:x='XXX' xmlns:y='YYY' xmlns='ns'><b>X</b></a>";
    String xml2 = "<a xmlns:x='XXX' xmlns:y='YYY' xmlns='ns'><x:b>X</x:b></a>";
    String exp1 = "<a>-<b>-</b><b>$w{X}</b></a>";
    String exp2 = "<a>+<b>+</b><b>$w{X}</b></a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

}
