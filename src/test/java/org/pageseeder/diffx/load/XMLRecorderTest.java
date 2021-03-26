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

import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.event.impl.*;
import org.pageseeder.diffx.format.SmartXMLFormatter;
import org.pageseeder.diffx.sequence.EventSequence;
import org.pageseeder.diffx.sequence.PrefixMapping;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;

/**
 * Base test class for the XML recorders.
 *
 * @author Christophe Lauret
 * @version 27 April 2005
 */
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
public abstract class XMLRecorderTest extends TestCase {

  /**
   * The XML recorder to use.
   */
  private transient XMLRecorder recorder = null;

  /**
   * The XML recorder to use.
   */
  private static final DiffXConfig SIMPLE = new DiffXConfig();
  static {
    SIMPLE.setNamespaceAware(false);
  }

// constructor---------------------------------------------------------------------------

  /**
   * Default constructor.
   *
   * @param name The name of the loader.
   */
  public XMLRecorderTest(String name) {
    super(name);
  }

// method that test classes must implement ----------------------------------------------

  /**
   * Returns the Diff-X Algorithm instance from the specified sequences.
   *
   * @param config The configuration to use for the recorder.
   *
   * @return The Diff-X Algorithm instance.
   */
  public abstract XMLRecorder makeXMLRecorder(DiffXConfig config);

// tests on elements only -----------------------------------------------------------------

  /**
   * Tests the simplest case: an empty element.
   *
   * <pre>
   *   &lt;a/&gt;
   * </pre>
   *
   * @throws IOException Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  public final void testEmptyElementA1() throws IOException, DiffXException {
    String xml = "<a/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("a"));
    exp.addEvent(new CloseElementEventNSImpl("a"));
    assertEquivalent(exp, xml);
  }

  /**
   * Tests the simplest case: an empty element.
   *
   * <pre>
   *   &lt;a/&gt;
   * </pre>
   *
   * @throws IOException Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  public final void testEmptyElementA2() throws IOException, DiffXException {
    String xml = "<a/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventImpl("a"));
    exp.addEvent(new CloseElementEventImpl("a"));
    assertEquivalent(exp, xml, SIMPLE);
  }

// tests on elements and text only --------------------------------------------------------

  /**
   * Tests a simple case.
   *
   * <pre>
   *   &lt;a&gt;XX&lt;/a&gt;
   * </pre>
   *
   * @throws IOException Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  public final void testTextElementA() throws IOException, DiffXException {
    String xml = "<a>XX</a>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("a"));
    exp.addEvent(new WordEvent("XX"));
    exp.addEvent(new CloseElementEventNSImpl("a"));
    assertEquivalent(exp, xml);
  }

  /**
   * Tests a simple case.
   *
   * <pre>
   *   &lt;a&gt;XX YY&lt;/a&gt;
   * </pre>
   *
   * @throws IOException Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  public final void testTextElementB() throws IOException, DiffXException {
    String xml = "<a>XX  YY</a>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("a"));
    exp.addEvent(new WordEvent("XX"));
    exp.addEvent(new SpaceEvent("  "));
    exp.addEvent(new WordEvent("YY"));
    exp.addEvent(new CloseElementEventNSImpl("a"));
    assertEquivalent(exp, xml);
  }

  /**
   * Tests a simple case.
   *
   * <pre>
   *   &lt;a&gt;&lt;b&gt;WWW&lt;/b&gt;&lt;/a&gt;
   * </pre>
   *
   * @throws IOException Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  public final void testElementsA() throws IOException, DiffXException {
    String xml = "<a><b>WWW</b></a>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("a"));
    exp.addEvent(new OpenElementEventNSImpl("b"));
    exp.addEvent(new WordEvent("WWW"));
    exp.addEvent(new CloseElementEventNSImpl("b"));
    exp.addEvent(new CloseElementEventNSImpl("a"));
    assertEquivalent(exp, xml);
  }

  /**
   * Tests a simple case.
   *
   * <pre>
   *   &lt;a&gt;&lt;b&gt;XX&lt;/b&gt;&lt;c&gt;YY&lt;/c&gt;&lt;/a&gt;
   * </pre>
   *
   * @throws IOException Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  public final void testElementsB() throws IOException, DiffXException {
    String xml = "<a><b>XX</b><c>YY</c></a>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("a"));
    exp.addEvent(new OpenElementEventNSImpl("b"));
    exp.addEvent(new WordEvent("XX"));
    exp.addEvent(new CloseElementEventNSImpl("b"));
    exp.addEvent(new OpenElementEventNSImpl("c"));
    exp.addEvent(new WordEvent("YY"));
    exp.addEvent(new CloseElementEventNSImpl("c"));
    exp.addEvent(new CloseElementEventNSImpl("a"));
    assertEquivalent(exp, xml);
  }

// tests on character entities ------------------------------------------------------------

  /**
   * Tests parsing the &amp;lt;, it should become character '&lt;'.
   *
   * @throws IOException Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  public final void testCharEntityLT() throws IOException, DiffXException {
    String xml = "<t>&lt;</t>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("t"));
    exp.addEvent(new WordEvent("<"));
    exp.addEvent(new CloseElementEventNSImpl("t"));
    assertEquivalent(exp, xml);
  }

  /**
   * Tests parsing the &amp;gt;, it should become character '&gt;'.
   *
   * @throws IOException Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  public final void testCharEntityGT() throws IOException, DiffXException {
    String xml = "<t>&gt;</t>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("t"));
    exp.addEvent(new WordEvent(">"));
    exp.addEvent(new CloseElementEventNSImpl("t"));
    assertEquivalent(exp, xml);
  }

  /**
   * Tests parsing the &amp;amp;, it should become character '&amp;'.
   *
   * @throws IOException Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  public final void testCharEntityAMP() throws IOException, DiffXException {
    String xml = "<t>&amp;</t>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("t"));
    exp.addEvent(new WordEvent("&"));
    exp.addEvent(new CloseElementEventNSImpl("t"));
    assertEquivalent(exp, xml);
  }

  /**
   * Tests parsing character &amp;#x8012;, it should become character <code>(char)0x8012</code>.
   *
   * @throws IOException Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  public final void testCharEntityNumerical() throws IOException, DiffXException {
    String xml = "<t>&#x8012;</t>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("t"));
    exp.addEvent(new WordEvent(""+(char)0x8012));
    exp.addEvent(new CloseElementEventNSImpl("t"));
    assertEquivalent(exp, xml);
  }

// tests on attributes --------------------------------------------------------------------

  /**
   * Tests that the attributes are read and sorted properly.
   *
   * @throws IOException Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  public final void testAttributeA1() throws IOException, DiffXException {
    String xml = "<elt attr='value'/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("elt"));
    exp.addEvent(new AttributeEventNSImpl("attr", "value"));
    exp.addEvent(new CloseElementEventNSImpl("elt"));
    assertEquivalent(exp, xml);
  }

  /**
   * Tests that the attribute is read properly.
   *
   * @throws IOException Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  public final void testAttributeA2() throws IOException, DiffXException {
    String xml = "<elt attr='value'/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventImpl("elt"));
    exp.addEvent(new AttributeEventImpl("attr", "value"));
    exp.addEvent(new CloseElementEventImpl("elt"));
    assertEquivalent(exp, xml, SIMPLE);
  }

  /**
   * Tests that the attributes are read and sorted properly.
   *
   * @throws IOException Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  public final void testSortAttributesA() throws IOException, DiffXException {
    String xml = "<elt b='second' a='first'/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("elt"));
    exp.addEvent(new AttributeEventNSImpl("a", "first"));
    exp.addEvent(new AttributeEventNSImpl("b", "second"));
    exp.addEvent(new CloseElementEventNSImpl("elt"));
    assertEquivalent(exp, xml);
  }

  /**
   * Tests that the attributes are read and sorted properly.
   *
   * @throws IOException Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  public final void testSortAttributesB() throws IOException, DiffXException {
    String xml = "<elt b='second' c='third' a='first'/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("elt"));
    exp.addEvent(new AttributeEventNSImpl("a", "first"));
    exp.addEvent(new AttributeEventNSImpl("b", "second"));
    exp.addEvent(new AttributeEventNSImpl("c", "third"));
    exp.addEvent(new CloseElementEventNSImpl("elt"));
    assertEquivalent(exp, xml);
  }

// tests on processing instructions -------------------------------------------------------

  /**
   * Tests that the attributes are read and sorted properly.
   *
   * @throws IOException Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  public final void testProcessingInstructionA1() throws IOException, DiffXException {
    String xml = "<elt><?target data?></elt>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("elt"));
    exp.addEvent(new ProcessingInstructionEvent("target", "data"));
    exp.addEvent(new CloseElementEventNSImpl("elt"));
    assertEquivalent(exp, xml);
  }

  /**
   * Tests that the attributes are read and sorted properly.
   *
   * @throws IOException Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  public final void testProcessingInstructionA2() throws IOException, DiffXException {
    String xml = "<elt><?target data?></elt>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventImpl("elt"));
    exp.addEvent(new ProcessingInstructionEvent("target", "data"));
    exp.addEvent(new CloseElementEventImpl("elt"));
    assertEquivalent(exp, xml, SIMPLE);
  }

// helpers ------------------------------------------------------------------------------------

  /**
   * Checks that the given XML is equivalent to the given event sequence using the
   * default Diff-X configuration.
   *
   * @param exp    The expected event sequence.
   * @param xml    The XML to test.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void assertEquivalent(EventSequence exp, String xml) throws IOException, DiffXException {
    assertEquivalent(exp, xml, new DiffXConfig());
  }

  /**
   * Checks that the given XML is equivalent to the given event sequence.
   *
   * @param exp    The expected event sequence.
   * @param xml    The XML to test.
   * @param config The configuration to use for the XML
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void assertEquivalent(EventSequence exp, String xml, DiffXConfig config)
      throws IOException, DiffXException {
    // process the strings
    Reader xmlr = new StringReader(xml);
    this.recorder = makeXMLRecorder(config);
    EventSequence seq = this.recorder.process(new InputSource(xmlr));
    try {
      assertEquals(exp.size(), seq.size());
      assertEquals(exp, seq);
    } catch (AssertionFailedError ex) {
      System.err.println("_____________");
      System.err.println("* Expected:");
      PrintWriter pw1 = new PrintWriter(System.err);
      exp.export(pw1);
      pw1.flush();
      System.err.println("* But got:");
      PrintWriter pw2 = new PrintWriter(System.err);
      seq.export(pw2);
      pw2.flush();
      System.err.println("* Prefix Mapping:");
      PrefixMapping mapping = seq.getPrefixMapping();
      for (Enumeration e = mapping.getURIs(); e.hasMoreElements();) {
        String uri = (String)e.nextElement();
        System.err.println(uri+" -> "+mapping.getPrefix(uri));
      }
      throw ex;
    }
  }

  /**
   * Prints an XML file as a sequence using the <code>SmartXMLFormatter</code>.
   *
   * @param xml The XML to test.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  private void printAsSequence(String xml) throws IOException, DiffXException {
    Reader xmlr = new StringReader(xml);
    EventSequence seq = this.recorder.process(new InputSource(xmlr));
    PrintWriter pw = new PrintWriter(System.err);
    seq.export(pw);
    pw.flush();
    SmartXMLFormatter formatter = new SmartXMLFormatter();
    for (int i = 0; i < seq.size(); i++) {
      formatter.format(seq.getEvent(i));
    }
  }
}
