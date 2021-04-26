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
package org.pageseeder.diffx.format;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.load.SAXLoader;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.token.impl.XMLAttribute;
import org.pageseeder.diffx.token.impl.XMLEndElement;
import org.pageseeder.diffx.token.impl.XMLStartElement;
import org.xml.sax.InputSource;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the smart XML Diff output.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class SmartXMLDiffOutputTest {

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
    this.formatter = new SmartXMLDiffOutput(this.w);
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
    this.formatter.format(new XMLStartElement("a"));
    this.formatter.format(new XMLEndElement("a"));
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
    this.formatter.format(new XMLStartElement("a"));
    this.formatter.format(new XMLEndElement("b"));
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
    this.formatter.format(new XMLStartElement("a"));
    this.formatter.insert(new XMLEndElement("b"));
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
    this.formatter.format(new XMLStartElement("a"));
    this.formatter.delete(new XMLEndElement("b"));
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
    this.formatter.format(new XMLStartElement("a"));
    this.formatter.format(new XMLAttribute("", "x", "y"));
    this.formatter.format(new XMLEndElement("a"));
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
    this.formatter.format(new XMLStartElement("a"));
    this.formatter.insert(new XMLAttribute("", "x", "y"));
    this.formatter.format(new XMLEndElement("a"));
    assertEquivalentToXML("<a x='y' ins:x='true' xmlns:ins='http://www.topologi.com/2005/Diff-X/Insert'/>");
  }

  /**
   * Test formatting an attribute.
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public void testAttributes2() throws DiffXException, IOException {
    this.formatter.format(new XMLStartElement("a"));
    this.formatter.delete(new XMLAttribute("", "x", "y"));
    this.formatter.format(new XMLEndElement("a"));
    assertEquivalentToXML("<a xmlns:del='http://www.topologi.com/2005/Diff-X/Delete' del:x='y'/>");
  }

// helpers ------------------------------------------------------------------------------------

  /**
   * Tests whether the content generated by the formatter is equivalent to the specified XML.
   *
   * @param xml The first XML to test.
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  private void assertEquivalentToXML(String xml) throws DiffXException, IOException {
    // process the XML to get the sequence
    Reader xmlr = new StringReader(xml);
    SAXLoader recorder = new SAXLoader();
    Sequence exp = recorder.load(new InputSource(xmlr));
    // process the output of the formatter
    Reader xmlr2 = new StringReader(this.w.toString());
    Sequence seq = recorder.load(new InputSource(xmlr2));
    try {
      assertEquals(exp.size(), seq.size());
      assertEquals(exp, seq);
    } catch (AssertionError ex) {
      PrintWriter pw = new PrintWriter(System.err);
      seq.export(pw);
      pw.flush();
      System.err.println(this.w.toString());
      throw ex;
    }
  }

  /**
   *
   */
  public XMLDiffOutput makeOutput() throws IOException {
    return new SmartXMLDiffOutput(new StringWriter());
  }

}
