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
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.token.impl.*;

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
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("http://x.org", "elt"));
    exp.addToken(new EndElementTokenNSImpl("http://x.org", "elt"));
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
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("elt"));
    exp.addToken(new AttributeTokenImpl("xmlns", "http://x.org"));
    exp.addToken(new EndElementTokenImpl("elt"));
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
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("http://x.org", "elt"));
    exp.addToken(new EndElementTokenNSImpl("http://x.org", "elt"));
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
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("x:elt"));
    exp.addToken(new AttributeTokenImpl("xmlns:x", "http://x.org"));
    exp.addToken(new EndElementTokenImpl("x:elt"));
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
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("http://ns.org", "elt"));
    exp.addToken(new AttributeTokenNSImpl("", "a", "1"));
    exp.addToken(new EndElementTokenNSImpl("http://ns.org", "elt"));
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
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("elt"));
    exp.addToken(new AttributeTokenImpl("a", "1"));
    exp.addToken(new AttributeTokenImpl("xmlns", "http://ns.org"));
    exp.addToken(new EndElementTokenImpl("elt"));
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
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("http://x.org", "elt"));
    exp.addToken(new AttributeTokenNSImpl("http://x.org", "a", "1"));
    exp.addToken(new EndElementTokenNSImpl("http://x.org", "elt"));
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
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("x:elt"));
    exp.addToken(new AttributeTokenImpl("x:a", "1"));
    exp.addToken(new AttributeTokenImpl("xmlns:x", "http://x.org"));
    exp.addToken(new EndElementTokenImpl("x:elt"));
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
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("x://m.org", "elt"));
    exp.addToken(new AttributeTokenNSImpl("", "a", "1"));
    exp.addToken(new AttributeTokenNSImpl("x://m.org", "a", "2"));
    exp.addToken(new EndElementTokenNSImpl("x://m.org", "elt"));
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
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("http://m.org", "elt"));
    exp.addToken(new AttributeTokenNSImpl("", "a", "1"));
    exp.addToken(new AttributeTokenNSImpl("http://m.org", "a", "2"));
    exp.addToken(new AttributeTokenNSImpl("http://n.org", "a", "3"));
    exp.addToken(new EndElementTokenNSImpl("http://m.org", "elt"));
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
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("", "elt"));
    exp.addToken(new AttributeTokenNSImpl("", "a", "0"));
    exp.addToken(new AttributeTokenNSImpl("http://x.org", "a", "1"));
    exp.addToken(new AttributeTokenNSImpl("http://y.org", "a", "2"));
    exp.addToken(new AttributeTokenNSImpl("http://z.org", "a", "3"));
    exp.addToken(new EndElementTokenNSImpl("", "elt"));
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
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("", "elt"));
    exp.addToken(new AttributeTokenNSImpl("", "a", "0"));
    exp.addToken(new AttributeTokenNSImpl("http://x.org", "a", "1"));
    exp.addToken(new AttributeTokenNSImpl("http://y.org", "a", "2"));
    exp.addToken(new AttributeTokenNSImpl("http://z.org", "a", "3"));
    exp.addToken(new EndElementTokenNSImpl("", "elt"));
    assertEquivalent(exp, xml);
  }

}
