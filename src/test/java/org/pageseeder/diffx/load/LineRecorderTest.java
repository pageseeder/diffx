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
package org.pageseeder.diffx.load;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.token.impl.LineToken;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Test class for the text recorder.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class LineRecorderTest {

  /**
   * The recorder being tested.
   */
  final LineRecorder recorder = new LineRecorder();

  /**
   * Tests a simple case.
   *
   * @throws IOException    Should an I/O exception occur.
   */
  @Test
  public void testSimpleLine0() throws IOException {
    String text = "line 1\n"
        + "line2\n";
    EventSequence exp = new EventSequence();
    exp.addToken(new LineToken("line 1", 1));
    exp.addToken(new LineToken("line2", 2));
    assertEqualsText(exp, text);
  }

  /**
   * Tests a simple case.
   *
   * @throws IOException    Should an I/O exception occur.
   */
  @Test
  public void testSimpleLine2() throws IOException {
    String text = "line #1\n"
        + "line #2\n"
        + "line #3\n"
        + "line #4";
    EventSequence exp = new EventSequence();
    exp.addToken(new LineToken("line #1", 1));
    exp.addToken(new LineToken("line #2", 2));
    exp.addToken(new LineToken("line #3", 3));
    exp.addToken(new LineToken("line #4", 4));
    assertEqualsText(exp, text);
  }

  /**
   * Tests a simple case.
   *
   * @throws IOException    Should an I/O exception occur.
   */
  @Test
  public void testEmptyLine() throws IOException {
    String text = "line #1\n"
        + "\n"
        + "line #3\n"
        + "line #4";
    EventSequence exp = new EventSequence();
    exp.addToken(new LineToken("line #1", 1));
    exp.addToken(new LineToken("", 2));
    exp.addToken(new LineToken("line #3", 3));
    exp.addToken(new LineToken("line #4", 4));
    assertEqualsText(exp, text);
  }

  /**
   * Tests a simple case.
   *
   * <pre>
   *   &lt;a&gt;XX&lt;/a&gt;
   * </pre>
   *
   * @throws IOException    Should an I/O exception occur.
   */
  @Test
  public void testXMLLine0() throws IOException {
    String text = "<a>XX</a>";
    EventSequence exp = new EventSequence();
    exp.addToken(new LineToken("<a>XX</a>", 1));
    assertEqualsText(exp, text);
  }

  /**
   * Tests parsing the &amp;lt;, it should remain the same.
   *
   * @throws IOException    Should an I/O exception occur.
   */
  @Test
  public void testEncoding1() throws IOException {
    String text = "&lt;";
    EventSequence exp = new EventSequence();
    exp.addToken(new LineToken("&lt;", 1));
    assertEqualsText(exp, text);
  }

  /**
   * Tests parsing character &amp;#x8012;, it should remain the same.
   *
   * @throws IOException    Should an I/O exception occur.
   */
  @Test
  public void testEncoding3() throws IOException {
    String xml = "&#x8012;";
    EventSequence exp = new EventSequence();
    exp.addToken(new LineToken("&#x8012;", 1));
    assertEqualsText(exp, xml);
  }

// helpers ------------------------------------------------------------------------------------

  /**
   * Checks that the given XML is equivalent to the given event sequence.
   *
   * @param exp The expected event sequence.
   * @param text The text to parse
   * @throws IOException    Should an I/O exception occur.
   */
  private void assertEqualsText(EventSequence exp, String text) {
    EventSequence seq = this.recorder.process(text);
    try {
      assertEquals(exp.size(), seq.size());
      assertEquals(exp, seq);
    } catch (AssertionError ex) {
      PrintWriter pw = new PrintWriter(System.err);
      seq.export(pw);
      pw.flush();
      throw ex;
    }
  }

}
