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
package org.pageseeder.diffx.load;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.token.impl.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class XMLRecorder_SpaceWord_NS extends XMLRecorderTest {

  @Override
  public DiffXConfig getConfig() {
    DiffXConfig config = new DiffXConfig();
    config.setNamespaceAware(true);
    config.setGranularity(TextGranularity.SPACE_WORD);
    return config;
  }

  @Test
  @DisplayName("<a ")
  public final void testInvalidElement()  {
    assertThrows(LoadingException.class, () -> record("<bad-xml", getConfig()));
  }

  @Test
  @DisplayName("<a/>")
  public final void testEmptyElement() throws LoadingException {
    String xml = "<a/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("a"));
    exp.addToken(new EndElementTokenNSImpl("a"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<a>XX</a>")
  public final void testTextElement1() throws LoadingException {
    String xml = "<a>XX</a>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("a"));
    exp.addToken(new WordToken("XX"));
    exp.addToken(new EndElementTokenNSImpl("a"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<a>XX  YY</a>")
  public final void testTextElement2() throws LoadingException {
    String xml = "<a>XX  YY</a>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("a"));
    exp.addToken(new WordToken("XX"));
    exp.addToken(new SpaceToken(" "));
    exp.addToken(new WordToken(" YY"));
    exp.addToken(new EndElementTokenNSImpl("a"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<a>The black hat; a white cat!</a>")
  public final void testTextElement3() throws LoadingException {
    String xml = "<a>The black hat; a white cat!</a>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("a"));
    exp.addToken(new WordToken("The"));
    exp.addToken(new WordToken(" black"));
    exp.addToken(new WordToken(" hat"));
    exp.addToken(new WordToken(";"));
    exp.addToken(new WordToken(" a"));
    exp.addToken(new WordToken(" white"));
    exp.addToken(new WordToken(" cat"));
    exp.addToken(new WordToken("!"));
    exp.addToken(new EndElementTokenNSImpl("a"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<a><b>WWW</b></a>")
  public final void testElementsA() throws LoadingException {
    String xml = "<a><b>WWW</b></a>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("a"));
    exp.addToken(new StartElementTokenNSImpl("b"));
    exp.addToken(new WordToken("WWW"));
    exp.addToken(new EndElementTokenNSImpl("b"));
    exp.addToken(new EndElementTokenNSImpl("a"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<a><b>XX</b><c>YY</c></a>")
  public final void testElementsB() throws LoadingException {
    String xml = "<a><b>XX</b><c>YY</c></a>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("a"));
    exp.addToken(new StartElementTokenNSImpl("b"));
    exp.addToken(new WordToken("XX"));
    exp.addToken(new EndElementTokenNSImpl("b"));
    exp.addToken(new StartElementTokenNSImpl("c"));
    exp.addToken(new WordToken("YY"));
    exp.addToken(new EndElementTokenNSImpl("c"));
    exp.addToken(new EndElementTokenNSImpl("a"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<t>&lt;</t>")
  public final void testCharEntityLT() throws LoadingException {
    String xml = "<t>&lt;</t>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("t"));
    exp.addToken(new WordToken("<"));
    exp.addToken(new EndElementTokenNSImpl("t"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<t>&gt;</t>")
  public final void testCharEntityGT() throws LoadingException {
    String xml = "<t>&gt;</t>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("t"));
    exp.addToken(new WordToken(">"));
    exp.addToken(new EndElementTokenNSImpl("t"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<t>&amp;</t>")
  public final void testCharEntityAMP() throws LoadingException {
    String xml = "<t>&amp;</t>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("t"));
    exp.addToken(new WordToken("&"));
    exp.addToken(new EndElementTokenNSImpl("t"));
    assertEquivalent(exp, xml, getConfig());
  }

  /**
   * Tests parsing character &amp;#x8012;, it should become character <code>(char)0x8012</code>.
   */
  @Test
  @DisplayName("<t>&#x8012;</t>")
  public final void testCharEntityNumerical() throws LoadingException {
    String xml = "<t>&#x8012;</t>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("t"));
    exp.addToken(new WordToken("" + (char) 0x8012));
    exp.addToken(new EndElementTokenNSImpl("t"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<elt attr='value'/>")
  public final void testAttribute1() throws LoadingException {
    String xml = "<elt attr='value'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("elt"));
    exp.addToken(new AttributeTokenNSImpl("attr", "value"));
    exp.addToken(new EndElementTokenNSImpl("elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<elt b='second' a='first'/> (sort attributes)")
  public final void testSortAttributesA() throws LoadingException {
    String xml = "<elt b='second' a='first'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("elt"));
    exp.addToken(new AttributeTokenNSImpl("a", "first"));
    exp.addToken(new AttributeTokenNSImpl("b", "second"));
    exp.addToken(new EndElementTokenNSImpl("elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<elt b='second' c='third' a='first'/> (sort attributes)")
  public final void testSortAttributesB() throws LoadingException {
    String xml = "<elt b='second' c='third' a='first'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("elt"));
    exp.addToken(new AttributeTokenNSImpl("a", "first"));
    exp.addToken(new AttributeTokenNSImpl("b", "second"));
    exp.addToken(new AttributeTokenNSImpl("c", "third"));
    exp.addToken(new EndElementTokenNSImpl("elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<elt><?target data?></elt>")
  public final void testProcessingInstruction1() throws LoadingException {
    String xml = "<elt><?target data?></elt>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("elt"));
    exp.addToken(new ProcessingInstructionToken("target", "data"));
    exp.addToken(new EndElementTokenNSImpl("elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<elt><?wow?></elt>")
  public final void testProcessingInstruction2() throws LoadingException {
    String xml = "<elt><?wow?></elt>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("elt"));
    exp.addToken(new ProcessingInstructionToken("wow", ""));
    exp.addToken(new EndElementTokenNSImpl("elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<elt xmlns='https://example.org'/>")
  public final void testElementNamespaceA1() throws LoadingException {
    String xml = "<elt xmlns='https://example.org'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("https://example.org", "elt"));
    exp.addToken(new EndElementTokenNSImpl("https://example.org", "elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<x:elt xmlns:x='https://example.org'/>")
  public final void testElementNamespaceB1() throws LoadingException {
    String xml = "<x:elt xmlns:x='https://example.org'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("https://example.org", "elt"));
    exp.addToken(new EndElementTokenNSImpl("https://example.org", "elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<elt xmlns='https://example.org' a='1'/>")
  public final void testAttributeNamespaceA1() throws LoadingException {
    String xml = "<elt xmlns='https://example.org' a='1'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("https://example.org", "elt"));
    exp.addToken(new AttributeTokenNSImpl("", "a", "1"));
    exp.addToken(new EndElementTokenNSImpl("https://example.org", "elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<x:elt xmlns:x='http://example.org' x:a='1'/>")
  public final void testAttributeNamespaceB1() throws LoadingException {
    String xml = "<x:elt xmlns:x='http://example.org' x:a='1'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("http://example.org", "elt"));
    exp.addToken(new AttributeTokenNSImpl("http://example.org", "a", "1"));
    exp.addToken(new EndElementTokenNSImpl("http://example.org", "elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<elt xmlns='x://m.org' xmlns:x='x://m.org' a='1' x:a='2'/>")
  public final void testAttributeNamespaceC() throws LoadingException {
    String xml = "<elt xmlns='x://m.org' xmlns:x='x://m.org' a='1' x:a='2'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("x://m.org", "elt"));
    exp.addToken(new AttributeTokenNSImpl("", "a", "1"));
    exp.addToken(new AttributeTokenNSImpl("x://m.org", "a", "2"));
    exp.addToken(new EndElementTokenNSImpl("x://m.org", "elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<x:elt xmlns:x='http://m.org' xmlns:y='http://n.org' a='1' x:a='2' y:a='3'/>")
  public final void testAttributeNamespaceD() throws LoadingException {
    String xml = "<x:elt xmlns:x='http://m.org' xmlns:y='http://n.org' a='1' x:a='2' y:a='3'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("http://m.org", "elt"));
    exp.addToken(new AttributeTokenNSImpl("", "a", "1"));
    exp.addToken(new AttributeTokenNSImpl("http://m.org", "a", "2"));
    exp.addToken(new AttributeTokenNSImpl("http://n.org", "a", "3"));
    exp.addToken(new EndElementTokenNSImpl("http://m.org", "elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<elt xmlns:x='https://x.org' xmlns:y='https://y.org' xmlns:z='https://z.org' a='0' x:a='1' y:a='2' z:a='3'/>")
  public final void testSortAttributesNamespaceA() throws LoadingException {
    String xml = "<elt xmlns:x='https://x.org' xmlns:y='https://y.org' xmlns:z='https://z.org' a='0' x:a='1' y:a='2' z:a='3'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("", "elt"));
    exp.addToken(new AttributeTokenNSImpl("", "a", "0"));
    exp.addToken(new AttributeTokenNSImpl("https://x.org", "a", "1"));
    exp.addToken(new AttributeTokenNSImpl("https://y.org", "a", "2"));
    exp.addToken(new AttributeTokenNSImpl("https://z.org", "a", "3"));
    exp.addToken(new EndElementTokenNSImpl("", "elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<elt xmlns:x='https://x.org' xmlns:y='https://y.org' xmlns:z='https://z.org' a='0' z:a='3' y:a='2' x:a='1'/>")
  public final void testSortAttributesNamespaceB() throws LoadingException {
    String xml = "<elt xmlns:x='https://x.org' xmlns:y='https://y.org' xmlns:z='https://z.org' a='0' z:a='3' y:a='2' x:a='1'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("", "elt"));
    exp.addToken(new AttributeTokenNSImpl("", "a", "0"));
    exp.addToken(new AttributeTokenNSImpl("https://x.org", "a", "1"));
    exp.addToken(new AttributeTokenNSImpl("https://y.org", "a", "2"));
    exp.addToken(new AttributeTokenNSImpl("https://z.org", "a", "3"));
    exp.addToken(new EndElementTokenNSImpl("", "elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<e xmlns='https://example.org'><f xmlns='https://example.net'><g/></f></e>")
  public final void testOverrideDefaultNamespace1() throws LoadingException {
    String xml = "<e xmlns='https://example.org'><f xmlns='https://example.net'><g/></f></e>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("https://example.org", "e"));
    exp.addToken(new StartElementTokenNSImpl("https://example.net", "f"));
    exp.addToken(new StartElementTokenNSImpl("https://example.net", "g"));
    exp.addToken(new EndElementTokenNSImpl("https://example.net", "g"));
    exp.addToken(new EndElementTokenNSImpl("https://example.net", "f"));
    exp.addToken(new EndElementTokenNSImpl("https://example.org", "e"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<d><e xmlns='https://example.org'><f xmlns='https://example.net'><g/></f></e></d>")
  public final void testOverrideDefaultNamespace2() throws LoadingException {
    String xml = "<d><e xmlns='https://example.org'><f xmlns='https://example.net'><g/></f></e></d>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenNSImpl("", "d"));
    exp.addToken(new StartElementTokenNSImpl("https://example.org", "e"));
    exp.addToken(new StartElementTokenNSImpl("https://example.net", "f"));
    exp.addToken(new StartElementTokenNSImpl("https://example.net", "g"));
    exp.addToken(new EndElementTokenNSImpl("https://example.net", "g"));
    exp.addToken(new EndElementTokenNSImpl("https://example.net", "f"));
    exp.addToken(new EndElementTokenNSImpl("https://example.org", "e"));
    exp.addToken(new EndElementTokenNSImpl("", "d"));
    assertEquivalent(exp, xml, getConfig());
  }
}
