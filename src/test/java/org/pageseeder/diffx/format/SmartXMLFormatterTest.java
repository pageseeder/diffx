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
import org.pageseeder.diffx.event.impl.AttributeEventNSImpl;
import org.pageseeder.diffx.event.impl.CloseElementEventNSImpl;
import org.pageseeder.diffx.event.impl.OpenElementEventNSImpl;
import org.pageseeder.diffx.load.SAXRecorder;
import org.pageseeder.diffx.sequence.EventSequence;
import org.xml.sax.InputSource;

import java.io.*;

/**
 * Test class for the smart XML formatter.
 *
 * <p>The {@link org.pageseeder.diffx.format.SmartXMLFormatter} must also pass the
 * {@link org.pageseeder.diffx.format.BaseXMLFormatterTest}, therefore this class
 * should only contain tests that specific to the <code>SmartXMLFormatter</code>.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class SmartXMLFormatterTest {

  /**
   * The loader being tested.
   */
  SAXRecorder recorder = new SAXRecorder();

  /**
   * The formatter being tested.
   */
  DiffXFormatter formatter = null;

  /**
   * The string writer.
   */
  StringWriter w = null;

  @BeforeEach
  public void setUp() throws Exception {
    this.w = new StringWriter();
    this.formatter = new SmartXMLFormatter(this.w);
  }

// opening and closing elements ---------------------------------------------------------------

  /**
   * Test open and closing an element.
   *
   * @throws DiffXException Should an error occur while parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public void testOpenAndClose0() throws DiffXException, IOException {
    this.formatter.format(new OpenElementEventNSImpl("a"));
    this.formatter.format(new CloseElementEventNSImpl("a"));
    assertEquivalentToXML("<a/>");
  }

  /**
   * Test open and closing mismatching elements.
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public void testOpenAndClose1() throws DiffXException, IOException {
    this.formatter.format(new OpenElementEventNSImpl("a"));
    this.formatter.format(new CloseElementEventNSImpl("b"));
    assertEquivalentToXML("<a/>");
  }

  /**
   * Test open and closing mismatching elements.
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public void testOpenAndClose2() throws DiffXException, IOException {
    this.formatter.format(new OpenElementEventNSImpl("a"));
    this.formatter.insert(new CloseElementEventNSImpl("b"));
    assertEquivalentToXML("<a/>");
  }

  /**
   * Test open and closing mismatching elements.
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public void testOpenAndClose3() throws DiffXException, IOException {
    this.formatter.format(new OpenElementEventNSImpl("a"));
    this.formatter.delete(new CloseElementEventNSImpl("b"));
    assertEquivalentToXML("<a/>");
  }

// playing with attributes --------------------------------------------------------------------

  /**
   * Test formatting an attribute.
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public void testAttributes0() throws DiffXException, IOException {
    this.formatter.format(new OpenElementEventNSImpl("a"));
    this.formatter.format(new AttributeEventNSImpl("", "x", "y"));
    this.formatter.format(new CloseElementEventNSImpl("a"));
    assertEquivalentToXML("<a x='y'/>");
  }

  /**
   * Test formatting an attribute.
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public void testAttributes1() throws DiffXException, IOException {
    this.formatter.format(new OpenElementEventNSImpl("a"));
    this.formatter.insert(new AttributeEventNSImpl("", "x", "y"));
    this.formatter.format(new CloseElementEventNSImpl("a"));
    assertEquivalentToXML("<a x='y'/>");
  }

  /**
   * Test formatting an attribute.
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public void testAttributes2() throws DiffXException, IOException {
    this.formatter.format(new OpenElementEventNSImpl("a"));
    this.formatter.delete(new AttributeEventNSImpl("http://www.topologi.org/2004/Diff-X/Delete", "x", "y"));
    this.formatter.format(new CloseElementEventNSImpl("a"));
    assertEquivalentToXML("<a xmlns:del='http://www.topologi.org/2004/Diff-X/Delete' del:x='y'/>");
  }

// helpers ------------------------------------------------------------------------------------

  /**
   * Tests whether the content generated by the formatter is equivalent to the specified XML.
   *
   * @param xml The first XML to test.
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  private void assertEquivalentToXML(String xml) throws DiffXException, IOException {
    // process the XML to get the sequence
    Reader xmlr = new StringReader(xml);
    EventSequence exp = this.recorder.process(new InputSource(xmlr));
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

  /**
   *
   */
  public XMLDiffXFormatter makeFormatter() throws IOException {
    return new SmartXMLFormatter(new StringWriter());
  }

}
