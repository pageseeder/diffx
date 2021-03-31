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
package org.pageseeder.diffx.load.text;

import org.junit.Assert;
import org.junit.Test;
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.event.impl.LineEvent;
import org.pageseeder.diffx.load.TextRecorder;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Test class for the text recorder.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TextRecorderTest {

  /**
   * The recorder being tested.
   */
  TextRecorder recorder = new TextRecorder();

  /**
   * Tests a simple case.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while reading the text.
   */
  @Test
  public void testSimpleLine0() throws IOException, DiffXException {
    String text = "line 1\n"
        + "line2\n";
    EventSequence exp = new EventSequence();
    exp.addEvent(new LineEvent("line 1", 1));
    exp.addEvent(new LineEvent("line2", 2));
    assertEquals(exp, text);
  }

  /**
   * Tests a simple case.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while reading the text.
   */
  @Test
  public void testSimpleLine2() throws IOException, DiffXException {
    String text = "line #1\n"
        + "line #2\n"
        + "line #3\n"
        + "line #4";
    EventSequence exp = new EventSequence();
    exp.addEvent(new LineEvent("line #1", 1));
    exp.addEvent(new LineEvent("line #2", 2));
    exp.addEvent(new LineEvent("line #3", 3));
    exp.addEvent(new LineEvent("line #4", 4));
    assertEquals(exp, text);
  }

  /**
   * Tests a simple case.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while reading the text.
   */
  @Test
  public void testEmptyLine() throws IOException, DiffXException {
    String text = "line #1\n"
        + "\n"
        + "line #3\n"
        + "line #4";
    EventSequence exp = new EventSequence();
    exp.addEvent(new LineEvent("line #1", 1));
    exp.addEvent(new LineEvent("", 2));
    exp.addEvent(new LineEvent("line #3", 3));
    exp.addEvent(new LineEvent("line #4", 4));
    assertEquals(exp, text);
  }

  /**
   * Tests a simple case.
   *
   * <pre>
   *   &lt;a&gt;XX&lt;/a&gt;
   * </pre>
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while reading the text.
   */
  @Test
  public void testXMLLine0() throws IOException, DiffXException {
    String text = "<a>XX</a>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new LineEvent("<a>XX</a>", 1));
    assertEquals(exp, text);
  }

  /**
   * Tests parsing the &amp;lt;, it should remain the same.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while reading the text.
   */
  @Test
  public void testEncoding1() throws IOException, DiffXException {
    String text = "&lt;";
    EventSequence exp = new EventSequence();
    exp.addEvent(new LineEvent("&lt;", 1));
    assertEquals(exp, text);
  }

  /**
   * Tests parsing character &amp;#x8012;, it should remain the same.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while reading the text.
   */
  @Test
  public void testEncoding3() throws IOException, DiffXException {
    String xml = "&#x8012;";
    EventSequence exp = new EventSequence();
    exp.addEvent(new LineEvent("&#x8012;", 1));
    Assert.assertEquals(exp, xml);
  }

// helpers ------------------------------------------------------------------------------------

  /**
   * Checks that the given XML is equivalent to the given event sequence.
   *
   * @param exp The expected event sequence.
   * @param xml The XML to test.
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  private void assertEquals(EventSequence exp, String xml) throws IOException, DiffXException {
    // process the strings
    EventSequence seq = this.recorder.process(xml);
    try {
      Assert.assertEquals(exp.size(), seq.size());
      Assert.assertEquals(exp, seq);
    } catch (AssertionError ex) {
      PrintWriter pw = new PrintWriter(System.err);
      seq.export(pw);
      pw.flush();
      throw ex;
    }
  }

}
