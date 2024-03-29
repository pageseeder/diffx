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
import org.pageseeder.diffx.DiffException;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.load.SAXLoader;
import org.pageseeder.diffx.xml.Sequence;
import org.pageseeder.diffx.token.impl.XMLAttribute;
import org.pageseeder.diffx.token.impl.XMLEndElement;
import org.pageseeder.diffx.token.impl.XMLStartElement;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the strict formatter.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class StrictXMLDiffOutputTest {

  /**
   * The namespace declaration.
   */
  private static final String NS_DECL = "xmlns:diff=\"https://www.pageseeder.org/diffx\"";

  /**
   * The loader being tested.
   */
  private final SAXLoader recorder = new SAXLoader();

  /**
   * The formatter being tested.
   */
  private StrictXMLDiffOutput output = null;

  /**
   * The string writer.
   */
  private StringWriter w = null;

  @BeforeEach
  public void setUp() {
    this.w = new StringWriter();
    this.output = new StrictXMLDiffOutput(this.w);
    this.output.setWriteXMLDeclaration(false);
  }

//opening and closing elements ---------------------------------------------------------------

  /**
   * Test open and closing an element.
   *
   * @throws DiffException Should an error occur whilst parsing one of the XML files.
   */
  @Test
  public void testOpenAndClose0() throws DiffException {
    this.output.start();
    this.output.handle(Operator.MATCH, new XMLStartElement("a"));
    this.output.handle(Operator.MATCH, new XMLEndElement("a"));
    this.output.end();
    assertEquivalentToXML("<a/>");
    String xml = "<a " + NS_DECL + "></a>";
    assertEquals(xml, this.w.toString());
  }

// playing with attributes --------------------------------------------------------------------

  /**
   * Test formatting an attribute.
   *
   * @throws DiffException Should an error occur whilst parsing one of the XML files.
   */
  @Test
  public void testAttributes0() throws DiffException {
    this.output.start();
    this.output.handle(Operator.MATCH, new XMLStartElement("a"));
    this.output.handle(Operator.MATCH, new XMLAttribute("", "x", "y"));
    this.output.handle(Operator.MATCH, new XMLEndElement("a"));
    this.output.end();
    assertEquivalentToXML("<a x='y'/>");
    String xml = "<a " + NS_DECL + " x=\"y\"></a>";
    assertEquals(xml, this.w.toString());
  }

// helpers ------------------------------------------------------------------------------------

  /**
   * Tests whether the content generated by the formatter is equivalent to the specified XML.
   *
   * @param xml The first XML to test.
   *
   * @throws DiffException Should an error occur whilst parsing one of the XML files.
   */
  private void assertEquivalentToXML(String xml) throws DiffException {
    // process the XML to get the sequence
    Sequence exp = this.recorder.load(xml);
    // process the output of the formatter
    Sequence seq = this.recorder.load(this.w.toString());
    try {
      assertEquals(exp, seq);
    } catch (AssertionError ex) {
      PrintWriter pw = new PrintWriter(System.err);
      seq.export(pw);
      pw.flush();
      System.err.println(this.w.toString());
      throw ex;
    }
  }

}

