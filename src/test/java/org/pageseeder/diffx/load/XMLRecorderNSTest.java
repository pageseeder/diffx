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

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.event.impl.*;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;

/**
 * Extended test class for the XML recorders for namespaces.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class XMLRecorderNSTest extends XMLRecorderTest {

  /**
   * The XML recorder to use.
   */
  private static final DiffXConfig SIMPLE = new DiffXConfig();

  static {
    SIMPLE.setNamespaceAware(false);
  }

// elements under a different namespace -------------------------------------------------

  /**
   * Tests an empty element under a different namespace.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public final void testElementNamespaceA1() throws IOException, DiffXException {
    String xml = "<elt xmlns='http://x.org'/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("http://x.org", "elt"));
    exp.addEvent(new CloseElementEventNSImpl("http://x.org", "elt"));
    assertEquivalent(exp, xml);
  }

  /**
   * Tests an empty element under a different namespace.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public final void testElementNamespaceA2() throws IOException, DiffXException {
    String xml = "<elt xmlns='http://x.org'/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventImpl("elt"));
    exp.addEvent(new AttributeEventImpl("xmlns", "http://x.org"));
    exp.addEvent(new CloseElementEventImpl("elt"));
    assertEquivalent(exp, xml, SIMPLE);
  }

  /**
   * Tests an empty element under a different namespace and prefix.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public final void testElementNamespaceB1() throws IOException, DiffXException {
    String xml = "<x:elt xmlns:x='http://x.org'/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("http://x.org", "elt"));
    exp.addEvent(new CloseElementEventNSImpl("http://x.org", "elt"));
    assertEquivalent(exp, xml);
  }

  /**
   * Tests an empty element under a different namespace and prefix.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public final void testElementNamespaceB2() throws IOException, DiffXException {
    String xml = "<x:elt xmlns:x='http://x.org'/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventImpl("x:elt"));
    exp.addEvent(new AttributeEventImpl("xmlns:x", "http://x.org"));
    exp.addEvent(new CloseElementEventImpl("x:elt"));
    assertEquivalent(exp, xml, SIMPLE);
  }

// attributes under a different namespace -----------------------------------------------

  /**
   * Tests that the attributes are read and sorted properly.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public final void testAttributeNamespaceA1() throws IOException, DiffXException {
    String xml = "<elt xmlns='http://ns.org' a='1'/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("http://ns.org", "elt"));
    exp.addEvent(new AttributeEventNSImpl("", "a", "1"));
    exp.addEvent(new CloseElementEventNSImpl("http://ns.org", "elt"));
    assertEquivalent(exp, xml);
  }

  /**
   * Tests that the attributes are read and sorted properly.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public final void testAttributeNamespaceA2() throws IOException, DiffXException {
    String xml = "<elt xmlns='http://ns.org' a='1'/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventImpl("elt"));
    exp.addEvent(new AttributeEventImpl("a", "1"));
    exp.addEvent(new AttributeEventImpl("xmlns", "http://ns.org"));
    exp.addEvent(new CloseElementEventImpl("elt"));
    assertEquivalent(exp, xml, SIMPLE);
  }

  /**
   * Tests that the attributes are read and sorted properly.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public final void testAttributeNamespaceB1() throws IOException, DiffXException {
    String xml = "<x:elt xmlns:x='http://x.org' x:a='1'/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("http://x.org", "elt"));
    exp.addEvent(new AttributeEventNSImpl("http://x.org", "a", "1"));
    exp.addEvent(new CloseElementEventNSImpl("http://x.org", "elt"));
    assertEquivalent(exp, xml);
  }

  /**
   * Tests that the attributes are read and sorted properly.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public final void testAttributeNamespaceB2() throws IOException, DiffXException {
    String xml = "<x:elt xmlns:x='http://x.org' x:a='1'/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventImpl("x:elt"));
    exp.addEvent(new AttributeEventImpl("x:a", "1"));
    exp.addEvent(new AttributeEventImpl("xmlns:x", "http://x.org"));
    exp.addEvent(new CloseElementEventImpl("x:elt"));
    assertEquivalent(exp, xml, SIMPLE);
  }

  /**
   * Tests that the attributes are read and sorted properly.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public final void testAttributeNamespaceC() throws IOException, DiffXException {
    String xml = "<elt xmlns='x://m.org' xmlns:x='x://m.org' a='1' x:a='2'/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("x://m.org", "elt"));
    exp.addEvent(new AttributeEventNSImpl("", "a", "1"));
    exp.addEvent(new AttributeEventNSImpl("x://m.org", "a", "2"));
    exp.addEvent(new CloseElementEventNSImpl("x://m.org", "elt"));
    assertEquivalent(exp, xml);
  }

  /**
   * Tests that the attributes are read and sorted properly.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public final void testAttributeNamespaceD() throws IOException, DiffXException {
    String xml = "<x:elt xmlns:x='http://m.org' xmlns:y='http://n.org' a='1' x:a='2' y:a='3'/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("http://m.org", "elt"));
    exp.addEvent(new AttributeEventNSImpl("", "a", "1"));
    exp.addEvent(new AttributeEventNSImpl("http://m.org", "a", "2"));
    exp.addEvent(new AttributeEventNSImpl("http://n.org", "a", "3"));
    exp.addEvent(new CloseElementEventNSImpl("http://m.org", "elt"));
    assertEquivalent(exp, xml);
  }

// tests on the sorting of attributes -----------------------------------------------------

  /**
   * Tests that the attributes are read and sorted properly.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public final void testSortAttributesNamespaceA() throws IOException, DiffXException {
    String xml = "<elt xmlns:x='http://x.org'" +
        " xmlns:y='http://y.org'" +
        " xmlns:z='http://z.org'" +
        " a='0' x:a='1' y:a='2' z:a='3'/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("", "elt"));
    exp.addEvent(new AttributeEventNSImpl("", "a", "0"));
    exp.addEvent(new AttributeEventNSImpl("http://x.org", "a", "1"));
    exp.addEvent(new AttributeEventNSImpl("http://y.org", "a", "2"));
    exp.addEvent(new AttributeEventNSImpl("http://z.org", "a", "3"));
    exp.addEvent(new CloseElementEventNSImpl("", "elt"));
    assertEquivalent(exp, xml);
  }

  /**
   * Tests that the attributes are read and sorted properly.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML with SAX.
   */
  @Test
  public final void testSortAttributesNamespaceB() throws IOException, DiffXException {
    String xml = "<elt xmlns:x='http://x.org'" +
        " xmlns:y='http://y.org'" +
        " xmlns:z='http://z.org'" +
        " a='0' z:a='3' y:a='2' x:a='1'/>";
    EventSequence exp = new EventSequence();
    exp.addEvent(new OpenElementEventNSImpl("", "elt"));
    exp.addEvent(new AttributeEventNSImpl("", "a", "0"));
    exp.addEvent(new AttributeEventNSImpl("http://x.org", "a", "1"));
    exp.addEvent(new AttributeEventNSImpl("http://y.org", "a", "2"));
    exp.addEvent(new AttributeEventNSImpl("http://z.org", "a", "3"));
    exp.addEvent(new CloseElementEventNSImpl("", "elt"));
    assertEquivalent(exp, xml);
  }

}
