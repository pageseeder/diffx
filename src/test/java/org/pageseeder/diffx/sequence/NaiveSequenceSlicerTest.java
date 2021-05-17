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
package org.pageseeder.diffx.sequence;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.DiffException;
import org.pageseeder.diffx.load.SAXLoader;
import org.pageseeder.diffx.token.impl.WordToken;
import org.pageseeder.diffx.token.impl.XMLEndElement;
import org.pageseeder.diffx.token.impl.XMLStartElement;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test case for the sequence slicer.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class NaiveSequenceSlicerTest {

  /**
   * The loader used for the tests.
   */
  private final SAXLoader recorder = new SAXLoader();

  /**
   * The first sequence.
   */
  private XMLSequence seq1;

  /**
   * The second sequence.
   */
  private XMLSequence seq2;

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart0() throws IOException, DiffException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(3);
    exp.addToken(new XMLStartElement("a"));
    exp.addToken(new WordToken("XXX"));
    exp.addToken(new XMLEndElement("a"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart1() throws IOException, DiffException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>yyy</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(1);
    exp.addToken(new XMLStartElement("a"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart2() throws IOException, DiffException {
    String xml1 = "<a>XXX </a>";
    String xml2 = "<a>XXX</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(2);
    exp.addToken(new XMLStartElement("a"));
    exp.addToken(new WordToken("XXX"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart3() throws IOException, DiffException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX </a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(2);
    exp.addToken(new XMLStartElement("a"));
    exp.addToken(new WordToken("XXX"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart4() throws IOException, DiffException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX YYY</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(2);
    exp.addToken(new XMLStartElement("a"));
    exp.addToken(new WordToken("XXX"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart5() throws IOException, DiffException {
    String xml1 = "<a><b/></a>";
    String xml2 = "<a><c/></a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(1);
    exp.addToken(new XMLStartElement("a"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart6() throws IOException, DiffException {
    String xml1 = "<a/>";
    String xml2 = "<b/>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(0);
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart7() throws IOException, DiffException {
    String xml1 = "<a>X</a>";
    String xml2 = "<b>X</b>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(0);
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart8() throws IOException, DiffException {
    String xml1 = "<a><b>X</b></a>";
    String xml2 = "<b><a>X</a></b>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(0);
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd0() throws IOException, DiffException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(3);
    exp.addToken(new XMLStartElement("a"));
    exp.addToken(new WordToken("XXX"));
    exp.addToken(new XMLEndElement("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd1() throws IOException, DiffException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>yyy</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(1);
    exp.addToken(new XMLEndElement("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd2() throws IOException, DiffException {
    String xml1 = "<a>XXX </a>";
    String xml2 = "<a>XXX</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(1);
    exp.addToken(new XMLEndElement("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd3() throws IOException, DiffException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX </a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(1);
    exp.addToken(new XMLEndElement("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd4() throws IOException, DiffException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX YYY</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(1);
    exp.addToken(new XMLEndElement("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd5() throws IOException, DiffException {
    String xml1 = "<a><b/></a>";
    String xml2 = "<a><c/></a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(1);
    exp.addToken(new XMLEndElement("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd6() throws IOException, DiffException {
    String xml1 = "<a/>";
    String xml2 = "<b/>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(0);
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd7() throws IOException, DiffException {
    String xml1 = "<a>X</a>";
    String xml2 = "<b>X</b>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(0);
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd8() throws IOException, DiffException {
    String xml1 = "<a><b>X</b></a>";
    String xml2 = "<b><a>X</a></b>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence exp = new XMLSequence(0);
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStartEnd0() throws IOException, DiffException {
    String xml1 = "<a><b>WWW</b></a>";
    String xml2 = "<a><b>VVV</b></a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    XMLSequence start = new XMLSequence(2);
    start.addToken(new XMLStartElement("a"));
    start.addToken(new XMLStartElement("b"));
    XMLSequence end = new XMLSequence(2);
    end.addToken(new XMLEndElement("b"));
    end.addToken(new XMLEndElement("a"));
    assertStartOK(slicer, start);
    assertEndOK(slicer, end);
  }

// helpers ------------------------------------------------------------------------------------


  /**
   * Prepare the sequences and returns a sequence slicer on them.
   *
   * @param xml1 The first XML to test.
   * @param xml2 The second XML to test.
   *
   * @return The sequence slicer on the 2 sequences.
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  private NaiveSequenceSlicer init(String xml1, String xml2) throws IOException, DiffException {
    // process the strings
    Reader xmlr1 = new StringReader(xml1);
    Reader xmlr2 = new StringReader(xml2);
    this.seq1 = this.recorder.load(new InputSource(xmlr1));
    this.seq2 = this.recorder.load(new InputSource(xmlr2));
    return new NaiveSequenceSlicer(this.seq1, this.seq2);
  }

  /**
   * Asserts that the sliceStart operation is OK.
   *
   * @param slicer The slicer to test.
   * @param exp    The expected start sub sequence.
   */
  private void assertStartOK(NaiveSequenceSlicer slicer, XMLSequence exp) {
    int len1 = this.seq1.size() - exp.size();
    int len2 = this.seq2.size() - exp.size();
    // check the length are OK
    int slen = slicer.sliceStart();
    assertEquals(exp.size(), slen);
    assertEquals(len1, this.seq1.size());
    assertEquals(len2, this.seq2.size());
    // check the start sequence is as expected
    assertEquals(exp, slicer.getStart());
  }

  /**
   * Asserts that the sliceEnd operation is OK.
   *
   * @param slicer The slicer to test.
   * @param exp    The expected start sub sequence.
   */
  private void assertEndOK(NaiveSequenceSlicer slicer, XMLSequence exp) {
    int len1 = this.seq1.size() - exp.size();
    int len2 = this.seq2.size() - exp.size();
    // check the length are OK
    int slen = slicer.sliceEnd();
    assertEquals(exp.size(), slen);
    assertEquals(len1, this.seq1.size());
    assertEquals(len2, this.seq2.size());
    // check the start sequence is as expected
    assertEquals(exp, slicer.getEnd());
  }

}
