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
package org.pageseeder.diffx.format;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.load.SAXRecorder;
import org.pageseeder.diffx.sequence.EventSequence;
import org.xml.sax.InputSource;

import java.io.*;

/**
 * Test class common to all XML formatters.
 *
 * <p>XML formatters should be able to round trip some XML document, in order words, if they
 * are given a sequence of events generated from one XML, they should be able to generate an
 * XML document that is equivalent.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class BaseXMLFormatterTest {

  /**
   * The loader being tested.
   */
  private final transient SAXRecorder recorder = new SAXRecorder();

  /**
   * The formatter being tested.
   */
  private transient XMLDiffXFormatter formatter = null;

  /**
   * The string writer.
   */
  private transient StringWriter w = null;

  @BeforeEach
  protected final void setUp() throws Exception {
    this.w = new StringWriter();
    this.formatter = makeFormatter(this.w);
  }

  /**
   * Generates the formatter to be tested by this class.
   *
   * @param writer The writer this formatter should use.
   * @return The XML Diffx Formatter to use.
   * @throws IOException Should and error occur
   */
  public abstract XMLDiffXFormatter makeFormatter(Writer writer) throws IOException;

// formatting and round-tripping --------------------------------------------------------------

  /**
   * Tests opening and closing an element.
   *
   * <p>Test with the following XML:
   * <pre>
   *   &lt;a&gt;&lt;/a&gt;
   * </pre>
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public final void testOpenClose0() throws DiffXException, IOException {
    String xml = "<a></a>";
    assertRoundTripOK(xml);
  }

  /**
   * Tests with an empty element.
   *
   * <p>Test with the following XML:
   * <pre>
   *   &lt;a/&gt;
   * </pre>
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public final void testEmptyElement0() throws DiffXException, IOException {
    String xml = "<a/>";
    assertRoundTripOK(xml);
  }

  /**
   * Tests formatting text.
   *
   * <p>Test with the following XML:
   * <pre>
   *   &lt;a&gt;x&lt;/a&gt;
   * </pre>
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public final void testText0() throws DiffXException, IOException {
    String xml = "<a>x</a>";
    assertRoundTripOK(xml);
  }

  /**
   * Tests formatting text.
   *
   * <p>Test with the following XML:
   * <pre>
   *   &lt;a&gt;xx y zzz&lt;/a&gt;
   * </pre>
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public final void testText1() throws DiffXException, IOException {
    String xml = "<a>xx y zzz</a>";
    assertRoundTripOK(xml);
  }

  /**
   * Tests formatting an entity.
   *
   * <p>Test with the following XML:
   * <pre>
   *   &lt;a&gt;&amp;lt;&lt;/a&gt;
   * </pre>
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public final void testEntity0() throws DiffXException, IOException {
    String xml = "<a>&lt;</a>";
    assertRoundTripOK(xml);
  }

  /**
   * Tests formatting an entity.
   *
   * <p>Test with the following XML:
   * <pre>
   *   &lt;a&gt;&amp;#8012;&lt;/a&gt;
   * </pre>
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public final void testEntity1() throws DiffXException, IOException {
    String xml = "<a>&#8012;</a>";
    assertRoundTripOK(xml);
  }

  /**
   * Tests formatting spaces.
   *
   * <p>Test with the following XML:
   * <pre>
   *   &lt;a&gt; &lt;/a&gt;
   * </pre>
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public final void testSpace0() throws DiffXException, IOException {
    String xml = "<a> </a>";
    assertRoundTripOK(xml);
  }

  /**
   * Tests formatting spaces.
   *
   * <p>Test with the following XML:
   * <pre>
   *   &lt;a&gt;
   *   &lt;/a&gt;
   * </pre>
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public final void testSpace1() throws DiffXException, IOException {
    String xml = "<a>\n</a>";
    assertRoundTripOK(xml);
  }

  /**
   * Tests formatting spaces.
   *
   * <p>Test with the following XML:
   * <pre>
   *   &lt;a&gt;    &lt;/a&gt;
   * </pre>
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public final void testSpace2() throws DiffXException, IOException {
    String xml = "<a>    </a>";
    assertRoundTripOK(xml);
  }

  /**
   * Tests formatting an element with one child.
   *
   * <p>Test with the following XML:
   * <pre>
   *   &lt;a&gt;
   *     &lt;b/&gt;
   *   &lt;/a&gt;
   * </pre>
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public final void testChild0() throws DiffXException, IOException {
    String xml = "<a>\n  <b/>\n</a>";
    assertRoundTripOK(xml);
  }

  /**
   * Tests formatting an element with one child.
   *
   * <p>Test with the following XML:
   * <pre>
   *   &lt;a&gt;
   *     &lt;b&gt;xx&lt;/b&gt;
   *   &lt;/a&gt;
   * </pre>
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public final void testChild1() throws DiffXException, IOException {
    String xml = "<a>\n  <b>xx</b>\n</a>";
    assertRoundTripOK(xml);
  }

  /**
   * Tests formatting an element with atributes.
   *
   * <p>Test with the following XML:
   * <pre>
   *   &lt;a x='y'/&gt;
   * </pre>
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public final void testAttributes0() throws DiffXException, IOException {
    String xml = "<a x='y'/>";
    assertRoundTripOK(xml);
  }

  /**
   * Tests formatting an element with atributes.
   *
   * <p>Test with the following XML:
   * <pre>
   *   &lt;a x='y' w='z' d='e'/&gt;
   * </pre>
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public final void testAttributes1() throws DiffXException, IOException {
    String xml = "<a x='y' w='z' d='e'/>";
    assertRoundTripOK(xml);
  }

  /**
   * Tests round-tripping of an XML document.
   *
   * <p>Test with the following XML:
   * <pre>
   *   &lt;a&gt;
   *     &lt;b&gt;xx&lt;/b&gt;
   *     &lt;c&gt;yy&lt;/c&gt;
   *   &lt;/a&gt;
   * </pre>
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public final void testChildren0() throws DiffXException, IOException {
    String xml = "<a>\n  <b>xx</b>\n  <c>yy</c>\n</a>";
    assertRoundTripOK(xml);
  }

  /**
   * Tests round-tripping of an XML document.
   *
   * <p>Test with the following XML:
   * <pre>
   *   &lt;a&gt;xx &lt;b&gt;yy&lt;/b&gt; zz&lt;/a&gt;
   * </pre>
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public final void testMixedContent0() throws DiffXException, IOException {
    String xml = "<a>xx <b>yy</b> zz</a>";
    assertRoundTripOK(xml);
  }

//helpers ------------------------------------------------------------------------------------

  /**
   * Prepare the sequences and returns a sequence slicer on them.
   *
   * @param xml The first XML to test.
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  private void assertRoundTripOK(String xml) throws DiffXException, IOException {
    // process the XML to get the sequence
    Reader xmlr = new StringReader(xml);
    EventSequence exp = this.recorder.process(new InputSource(xmlr));
    // format the sequence
    for (int i = 0; i < exp.size(); i++) {
      this.formatter.format(exp.getEvent(i));
    }
    // process the output of the formatter
    Reader xmlr2 = new StringReader(this.w.toString());
    EventSequence seq = this.recorder.process(new InputSource(xmlr2));
    try {
      assertEquals(exp.size(), seq.size());
      assertEquals(exp, seq);
    } catch (AssertionError ex) {
      PrintWriter pw = new PrintWriter(System.err);
      seq.export(pw);
      pw.flush();
      throw ex;
    }
    System.err.println(this.w.toString());
  }

}

