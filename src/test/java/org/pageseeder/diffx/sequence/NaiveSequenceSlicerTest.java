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
package org.pageseeder.diffx.sequence;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.event.impl.CloseElementEventNSImpl;
import org.pageseeder.diffx.event.impl.OpenElementEventNSImpl;
import org.pageseeder.diffx.event.impl.WordEvent;
import org.pageseeder.diffx.load.SAXRecorder;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

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
  private SAXRecorder recorder = new SAXRecorder();

  /**
   * The first sequence.
   */
  private EventSequence seq1;

  /**
   * The second sequence.
   */
  private EventSequence seq2;

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public void testStart0() throws IOException, DiffXException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX</a>";
    NaiveSequenceSlicer slicer = init(xml1, xml2);
    EventSequence exp = new EventSequence(3);
    exp.addEvent(new OpenElementEventNSImpl("a"));
    exp.addEvent(new WordEvent("XXX"));
    exp.addEvent(new CloseElementEventNSImpl("a"));
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
    EventSequence exp = new EventSequence(1);
    exp.addEvent(new OpenElementEventNSImpl("a"));
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
    EventSequence exp = new EventSequence(2);
    exp.addEvent(new OpenElementEventNSImpl("a"));
    exp.addEvent(new WordEvent("XXX"));
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
    EventSequence exp = new EventSequence(2);
    exp.addEvent(new OpenElementEventNSImpl("a"));
    exp.addEvent(new WordEvent("XXX"));
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
    EventSequence exp = new EventSequence(2);
    exp.addEvent(new OpenElementEventNSImpl("a"));
    exp.addEvent(new WordEvent("XXX"));
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
    EventSequence exp = new EventSequence(1);
    exp.addEvent(new OpenElementEventNSImpl("a"));
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
    EventSequence exp = new EventSequence(0);
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
    EventSequence exp = new EventSequence(0);
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
    EventSequence exp = new EventSequence(0);
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
    EventSequence exp = new EventSequence(3);
    exp.addEvent(new OpenElementEventNSImpl("a"));
    exp.addEvent(new WordEvent("XXX"));
    exp.addEvent(new CloseElementEventNSImpl("a"));
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
    EventSequence exp = new EventSequence(1);
    exp.addEvent(new CloseElementEventNSImpl("a"));
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
    EventSequence exp = new EventSequence(1);
    exp.addEvent(new CloseElementEventNSImpl("a"));
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
    EventSequence exp = new EventSequence(1);
    exp.addEvent(new CloseElementEventNSImpl("a"));
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
    EventSequence exp = new EventSequence(1);
    exp.addEvent(new CloseElementEventNSImpl("a"));
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
    EventSequence exp = new EventSequence(1);
    exp.addEvent(new CloseElementEventNSImpl("a"));
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
    EventSequence exp = new EventSequence(0);
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
    EventSequence exp = new EventSequence(0);
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
    EventSequence exp = new EventSequence(0);
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
    EventSequence start = new EventSequence(2);
    start.addEvent(new OpenElementEventNSImpl("a"));
    start.addEvent(new OpenElementEventNSImpl("b"));
    EventSequence end = new EventSequence(2);
    end.addEvent(new CloseElementEventNSImpl("b"));
    end.addEvent(new CloseElementEventNSImpl("a"));
    assertStartOK(slicer, start);
    assertEndOK(slicer, end);
  }

// helpers ------------------------------------------------------------------------------------


  /**
   * Prepare the sequences and returns a sequence slicer on them.
   *
   * @param xml1 The first XML to test.
   * @param xml2 The second XML to test.
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
  private void assertStartOK(NaiveSequenceSlicer slicer, EventSequence exp) {
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
  private void assertEndOK(NaiveSequenceSlicer slicer, EventSequence exp) {
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
