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
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.load.SAXRecorder;
import org.pageseeder.diffx.token.impl.EndElementTokenNSImpl;
import org.pageseeder.diffx.token.impl.StartElementTokenNSImpl;
import org.pageseeder.diffx.token.impl.WordToken;
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
  private final SAXRecorder recorder = new SAXRecorder();

  /**
   * The first sequence.
   */
  private Sequence seq1;

  /**
   * The second sequence.
   */
  private Sequence seq2;

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart0() throws IOException, DiffXException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(3);
    exp.addToken(new StartElementTokenNSImpl("a"));
    exp.addToken(new WordToken("XXX"));
    exp.addToken(new EndElementTokenNSImpl("a"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart1() throws IOException, DiffXException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>yyy</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(1);
    exp.addToken(new StartElementTokenNSImpl("a"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart2() throws IOException, DiffXException {
    String xml1 = "<a>XXX </a>";
    String xml2 = "<a>XXX</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(2);
    exp.addToken(new StartElementTokenNSImpl("a"));
    exp.addToken(new WordToken("XXX"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart3() throws IOException, DiffXException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX </a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(2);
    exp.addToken(new StartElementTokenNSImpl("a"));
    exp.addToken(new WordToken("XXX"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart4() throws IOException, DiffXException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX YYY</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(2);
    exp.addToken(new StartElementTokenNSImpl("a"));
    exp.addToken(new WordToken("XXX"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart5() throws IOException, DiffXException {
    String xml1 = "<a><b/></a>";
    String xml2 = "<a><c/></a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(1);
    exp.addToken(new StartElementTokenNSImpl("a"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart6() throws IOException, DiffXException {
    String xml1 = "<a/>";
    String xml2 = "<b/>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(0);
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart7() throws IOException, DiffXException {
    String xml1 = "<a>X</a>";
    String xml2 = "<b>X</b>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(0);
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart8() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b></a>";
    String xml2 = "<b><a>X</a></b>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(0);
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd0() throws IOException, DiffXException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(3);
    exp.addToken(new StartElementTokenNSImpl("a"));
    exp.addToken(new WordToken("XXX"));
    exp.addToken(new EndElementTokenNSImpl("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd1() throws IOException, DiffXException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>yyy</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(1);
    exp.addToken(new EndElementTokenNSImpl("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd2() throws IOException, DiffXException {
    String xml1 = "<a>XXX </a>";
    String xml2 = "<a>XXX</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(1);
    exp.addToken(new EndElementTokenNSImpl("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd3() throws IOException, DiffXException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX </a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(1);
    exp.addToken(new EndElementTokenNSImpl("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd4() throws IOException, DiffXException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX YYY</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(1);
    exp.addToken(new EndElementTokenNSImpl("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd5() throws IOException, DiffXException {
    String xml1 = "<a><b/></a>";
    String xml2 = "<a><c/></a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(1);
    exp.addToken(new EndElementTokenNSImpl("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd6() throws IOException, DiffXException {
    String xml1 = "<a/>";
    String xml2 = "<b/>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(0);
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd7() throws IOException, DiffXException {
    String xml1 = "<a>X</a>";
    String xml2 = "<b>X</b>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(0);
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testEnd8() throws IOException, DiffXException {
    String xml1 = "<a><b>X</b></a>";
    String xml2 = "<b><a>X</a></b>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(0);
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStartEnd0() throws IOException, DiffXException {
    String xml1 = "<a><b>WWW</b></a>";
    String xml2 = "<a><b>VVV</b></a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    Sequence start = new Sequence(2);
    start.addToken(new StartElementTokenNSImpl("a"));
    start.addToken(new StartElementTokenNSImpl("b"));
    Sequence end = new Sequence(2);
    end.addToken(new EndElementTokenNSImpl("b"));
    end.addToken(new EndElementTokenNSImpl("a"));
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
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  private NaiveSequenceSlicer init(String xml1, String xml2) throws IOException, DiffXException {
    // process the strings
    Reader xmlr1 = new StringReader(xml1);
    Reader xmlr2 = new StringReader(xml2);
    this.seq1 = this.recorder.process(new InputSource(xmlr1));
    this.seq2 = this.recorder.process(new InputSource(xmlr2));
    return new NaiveSequenceSlicer(this.seq1, this.seq2);
  }

  /**
   * Asserts that the sliceStart operation is OK.
   *
   * @param slicer The slicer to test.
   * @param exp    The expected start sub sequence.
   */
  private void assertStartOK(NaiveSequenceSlicer slicer, Sequence exp) {
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
  private void assertEndOK(NaiveSequenceSlicer slicer, Sequence exp) {
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
