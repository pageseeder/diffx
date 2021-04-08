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
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.test.Events;

import java.io.IOException;
import java.util.List;

/**
 * Test case for progressive XML processor.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class ProgressiveXMLProcessorTest extends BaseProcessorLevel1Test {

  @Override
  public DiffAlgorithm getDiffAlgorithm() {
    return new ProgressiveXMLProcessor();
  }

  @Test
  public final void testProg_Identical() throws IOException, DiffXException {
    String xml1 = "<a>X Y</a>";
    String xml2 = "<a>X Y</a>";
    String exp = "<a>X Y</a>";
    assertDiffXMLProgOK(xml1, xml2, exp);
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
    assertDiffXMLProgOK(xml1, xml2, exp1);
    assertDiffXMLProgOK(xml2, xml1, exp2);
  }

  @Test
  public final void testProg_SelfWrapB() throws IOException, DiffXException {
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
    assertDiffXMLProgOK(xml1, xml2, exp1);
    assertDiffXMLProgOK(xml2, xml1, exp2);
  }

  /**
   * Splits / merge the text of the XML string in 2.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testProg_SplitMergeA() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b> <b>Y</b></a>";
    String xml2 = "<a><b>X Y</b></a>";
    String exp1 = "<a><b>X-( Y)</b>+( )+<b>+(Y)+</b></a>";
    String exp2 = "<a><b>X+( Y)</b>-( )-<b>-(Y)-</b></a>";
    assertDiffXMLProgOK(xml1, xml2, exp1);
    assertDiffXMLProgOK(xml2, xml1, exp2);
  }

  /**
   * Splits / merge the text of the XML string.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testProg_SplitMergeA1() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b> <b>Y</b></a>";
    String xml2 = "<a><b>X Y</b></a>";
    // split
    String[] exp1 = new String[]{
        "<a><b>X+</b> +<b>Y</b></a>",               // tags inserted
        "<a><b>X-( Y)</b>+( )+<b>+(Y)+</b></a>",    // text has moved
        "<a>+<b>+(X)+</b>+( )<b>+(Y)-(X Y)</b></a>"
    };
    // merge
    String[] exp2 = new String[]{
        "<a><b>X-</b> -<b>Y</b></a>",             // tags removed
        "<a><b>X+( Y)</b>-( )-<b>-(Y)-</b></a>"  // text has moved
    };
    assertDiffXMLProgOK(xml1, xml2, exp1);
    assertDiffXMLProgOK(xml2, xml1, exp2);
  }

  @Test
  public final void testProg_MovedBranch() throws IOException, DiffXException {
    String xml1 = "<a><b>M</b><a><b>A</b></a><b>N</b></a>";
    String xml2 = "<a><b>M<a><b>A</b></a></b><b>N</b></a>";
    String exp1 = "<a><b>M-<a>-<b>-(A)-</b>-</a></b>+<a>+<b>+(A)+</b>+</a><b>N</b></a>";
    String exp2 = "<a><b>M+<a>+<b>+(A)+</b>+</a></b>-<a>-<b>-(A)-</b>-</a><b>N</b></a>";
    assertDiffXMLProgOK(xml1, xml2, exp1);
    assertDiffXMLProgOK(xml2, xml1, exp2);
  }

  @Test
  public final void testProg_SplitMergeB() throws IOException, DiffXException {
    String xml1 = "<a><b><c/></b><b><d/></b></a>";
    String xml2 = "<a><b><c/><d/></b></a>";
    String exp1 = "<a><b><c></c>-<d>-</d></b>+<b>+<d>+</d>+</b></a>";
    String exp2 = "<a><b><c></c>+<d>+</d></b>-<b>-<d>-</d>-</b></a>";
    assertDiffXMLProgOK(xml1, xml2, exp1);
    assertDiffXMLProgOK(xml2, xml1, exp2);
  }

  @Test
  public final void testProg_BestPath() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b></a>";
    String xml2 = "<a><b/><b>X</b></a>";
    String exp1 = "<a>-<b>-</b><b>X</b></a>";
    String exp2 = "<a>+<b>+</b><b>X</b></a>";
    assertDiffXMLProgOK(xml1, xml2, exp1);
    assertDiffXMLProgOK(xml2, xml1, exp2);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testSticky() throws IOException, DiffXException {
    String xml1 = "<a>a white cat</a>";
    String xml2 = "<a>a black hat</a>";
    String expA = "<a>a+( white cat)-( black hat)</a>";
    String expB = "<a>a-( black hat)+( white cat)</a>";
    assertDiffXMLProgOK(xml1, xml2, expA, expB);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  @Test
  public final void testList() throws IOException, DiffXException {
    String xml1 = "<ul><li>blue</li><li>red</li><li>green</li></ul>";
    String xml2 = "<ul><li>black</li><li>red</li><li>green</li></ul>";
    String expA = "<ul><li>+(blue)-(black)</li><li>red</li><li>green</li></ul>";
    String expB = "<ul><li>-(black)+(blue)</li><li>red</li><li>green</li></ul>";
    assertDiffXMLProgOK(xml1, xml2, expA, expB);
  }

  /**
   * Asserts that the Diff-X operation for XML meets expectations.
   *
   * @param xml1 The first XML to compare with diffx.
   * @param xml2 The first XML to compare with diffx.
   * @param exp  The expected result as formatted by the TestFormatter.
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void assertDiffXMLProgOK(String xml1, String xml2, String ...exp)
      throws IOException, DiffXException {
    // Record XML
    DiffXConfig config = new DiffXConfig();
    config.setGranularity(TextGranularity.TEXT);
    List<? extends DiffXEvent> seq1 = Events.recordXMLEvents(xml1, config);
    List<? extends DiffXEvent> seq2 = Events.recordXMLEvents(xml2, config);

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

}
