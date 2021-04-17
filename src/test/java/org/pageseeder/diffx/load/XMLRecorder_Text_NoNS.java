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

public abstract class XMLRecorder_Text_NoNS extends XMLRecorderTest {

  @Override
  public DiffXConfig getConfig() {
    DiffXConfig config = new DiffXConfig();
    config.setNamespaceAware(false);
    config.setGranularity(TextGranularity.TEXT);
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
    exp.addToken(new StartElementTokenImpl("a"));
    exp.addToken(new EndElementTokenImpl("a"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<a>XX</a>")
  public final void testTextElement1() throws LoadingException {
    String xml = "<a>XX</a>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("a"));
    exp.addToken(new CharactersToken("XX"));
    exp.addToken(new EndElementTokenImpl("a"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<a>XX  YY</a>")
  public final void testTextElement2() throws LoadingException {
    String xml = "<a>XX  YY</a>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("a"));
    exp.addToken(new CharactersToken("XX  YY"));
    exp.addToken(new EndElementTokenImpl("a"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<a>The black hat; A white cat!</a>")
  public final void testTextElement3() throws LoadingException {
    String xml = "<a>The black hat; A white cat!</a>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("a"));
    exp.addToken(new CharactersToken("The black hat; A white cat!"));
    exp.addToken(new EndElementTokenImpl("a"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<a><b>WWW</b></a>")
  public final void testElements1() throws LoadingException {
    String xml = "<a><b>WWW</b></a>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("a"));
    exp.addToken(new StartElementTokenImpl("b"));
    exp.addToken(new CharactersToken("WWW"));
    exp.addToken(new EndElementTokenImpl("b"));
    exp.addToken(new EndElementTokenImpl("a"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<a><b>XX</b><c>YY</c></a>")
  public final void testElements2() throws LoadingException {
    String xml = "<a><b>XX</b><c>YY</c></a>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("a"));
    exp.addToken(new StartElementTokenImpl("b"));
    exp.addToken(new CharactersToken("XX"));
    exp.addToken(new EndElementTokenImpl("b"));
    exp.addToken(new StartElementTokenImpl("c"));
    exp.addToken(new CharactersToken("YY"));
    exp.addToken(new EndElementTokenImpl("c"));
    exp.addToken(new EndElementTokenImpl("a"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<t>&lt;</t>")
  public final void testCharEntityLT() throws LoadingException {
    String xml = "<t>&lt;</t>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("t"));
    exp.addToken(new CharactersToken("<"));
    exp.addToken(new EndElementTokenImpl("t"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<t>&gt;</t>")
  public final void testCharEntityGT() throws LoadingException {
    String xml = "<t>&gt;</t>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("t"));
    exp.addToken(new CharactersToken(">"));
    exp.addToken(new EndElementTokenImpl("t"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<t>&amp;</t>")
  public final void testCharEntityAMP() throws LoadingException {
    String xml = "<t>&amp;</t>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("t"));
    exp.addToken(new CharactersToken("&"));
    exp.addToken(new EndElementTokenImpl("t"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<t>&#x8012;</t>")
  public final void testCharEntityNumerical() throws LoadingException {
    String xml = "<t>&#x8012;</t>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("t"));
    exp.addToken(new CharactersToken("" + (char) 0x8012));
    exp.addToken(new EndElementTokenImpl("t"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<elt attr='value'/>")
  public final void testAttribute1() throws LoadingException {
    String xml = "<elt attr='value'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("elt"));
    exp.addToken(new AttributeTokenImpl("attr", "value"));
    exp.addToken(new EndElementTokenImpl("elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  /**
   * Tests that the attributes are read and sorted properly.
   */
  @Test
  @DisplayName("<elt b='second' c='third' a='first'/>")
  public final void testSortAttributes1() throws LoadingException {
    String xml = "<elt b='second' c='third' a='first'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("elt"));
    exp.addToken(new AttributeTokenImpl("a", "first"));
    exp.addToken(new AttributeTokenImpl("b", "second"));
    exp.addToken(new AttributeTokenImpl("c", "third"));
    exp.addToken(new EndElementTokenImpl("elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<elt><?target data?></elt>")
  public final void testProcessingInstruction1() throws LoadingException {
    String xml = "<elt><?target data?></elt>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("elt"));
    exp.addToken(new ProcessingInstructionToken("target", "data"));
    exp.addToken(new EndElementTokenImpl("elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<elt><?target-only?></elt>")
  public final void testProcessingInstruction2() throws LoadingException {
    String xml = "<elt><?target-only?></elt>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("elt"));
    exp.addToken(new ProcessingInstructionToken("target-only", ""));
    exp.addToken(new EndElementTokenImpl("elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<elt xmlns='https://example.org'/>")
  public final void testElementNamespace1() throws LoadingException {
    String xml = "<elt xmlns='https://example.org'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("elt"));
    exp.addToken(new AttributeTokenImpl("xmlns", "https://example.org"));
    exp.addToken(new EndElementTokenImpl("elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<x:elt xmlns:x='https://example.org'/>")
  public final void testElementNamespace2() throws LoadingException {
    String xml = "<x:elt xmlns:x='https://example.org'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("x:elt"));
    exp.addToken(new AttributeTokenImpl("xmlns:x", "https://example.org"));
    exp.addToken(new EndElementTokenImpl("x:elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("https://example.org")
  public final void testAttributeNamespace1() throws LoadingException {
    String xml = "<elt xmlns='https://example.org' a='1'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("elt"));
    exp.addToken(new AttributeTokenImpl("a", "1"));
    exp.addToken(new AttributeTokenImpl("xmlns", "https://example.org"));
    exp.addToken(new EndElementTokenImpl("elt"));
    assertEquivalent(exp, xml, getConfig());
  }

  @Test
  @DisplayName("<x:elt xmlns:x='https://example.org' x:a='1'/>")
  public final void testAttributeNamespaceB2() throws LoadingException {
    String xml = "<x:elt xmlns:x='https://example.org' x:a='1'/>";
    Sequence exp = new Sequence();
    exp.addToken(new StartElementTokenImpl("x:elt"));
    exp.addToken(new AttributeTokenImpl("x:a", "1"));
    exp.addToken(new AttributeTokenImpl("xmlns:x", "https://example.org"));
    exp.addToken(new EndElementTokenImpl("x:elt"));
    assertEquivalent(exp, xml, getConfig());
  }

}
