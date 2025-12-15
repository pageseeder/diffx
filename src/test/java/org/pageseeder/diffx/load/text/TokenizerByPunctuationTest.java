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
package org.pageseeder.diffx.load.text;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.token.TextToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.impl.CharactersToken;
import org.pageseeder.diffx.token.impl.IgnorableSpaceToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Christophe Lauret
 * @version 0.9.0
 */
final class TokenizerByPunctuationTest {

  /**
   * Tests that a <code>NullPointerException</code> is thrown for a </code>null</code>
   * character sequence.
   */
  @Test
  void testNull() {
    TextTokenizer t = new TokenizerByPunctuation(WhiteSpaceProcessing.IGNORE);
    assertThrows(NullPointerException.class, () -> t.tokenize(null));
  }

  @Test
  void testEmpty() {
    TextTokenizer t = new TokenizerByPunctuation(WhiteSpaceProcessing.IGNORE);
    List<TextToken> e = t.tokenize("");
    assertEquals(0, e.size());
  }

  @Test
  void testCountTokenIgnore() {
    TextTokenizer t = new TokenizerByPunctuation(WhiteSpaceProcessing.IGNORE);
    assertEquals(0, t.tokenize(" ").size());
    assertEquals(1, t.tokenize(" a").size());
    assertEquals(1, t.tokenize("a ").size());
    assertEquals(1, t.tokenize(" b ").size());
    assertEquals(1, t.tokenize("b b").size());
    assertEquals(1, t.tokenize("c c ").size());
    assertEquals(1, t.tokenize(" c c").size());
    assertEquals(1, t.tokenize(" d d ").size());
    assertEquals(1, t.tokenize("d d d").size());
  }

  @Test
  void testCountTokenPreserve() {
    TextTokenizer t = new TokenizerByPunctuation(WhiteSpaceProcessing.PRESERVE);
    assertEquals(1, t.tokenize(" ").size());
    assertEquals(1, t.tokenize("  \na").size());
    assertEquals(1, t.tokenize("aa \n").size());
    assertEquals(1, t.tokenize(" \naa").size());
    assertEquals(1, t.tokenize("a \n ").size());
    assertEquals(1, t.tokenize(" bb\n ").size());
    assertEquals(1, t.tokenize("b\n bb").size());
    assertEquals(1, t.tokenize("b \n  bb").size());
    assertEquals(1, t.tokenize("xx \n yy\n  ").size());
  }

  @Test
  void testSpace1() {
    TextTokenizer t = new TokenizerByPunctuation(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> e = t.tokenize(" ");
    assertEquals(1, e.size());
    XMLToken space = e.get(0);
    assertEquals(new IgnorableSpaceToken(" "), space);
  }

  @Test
  void testPunctuation1() {
    TextTokenizer t = new TokenizerByPunctuation(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> e = t.tokenize("A black cat");
    assertEquals(1, e.size());
    XMLToken text = e.get(0);
    assertEquals(new CharactersToken("A black cat"), text);
  }

  @Test
  void testPunctuation2() {
    TextTokenizer t = new TokenizerByPunctuation(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> e = t.tokenize("A black cat, a white hat!");
    assertEquals(2, e.size());
    assertEquals(new CharactersToken("A black cat,"), e.get(0));
    assertEquals(new CharactersToken(" a white hat!"), e.get(1));
  }

  @Test
  void testPunctuation3() {
    TextTokenizer t = new TokenizerByPunctuation(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> e = t.tokenize("A black cat, a white hat...");
    assertEquals(2, e.size());
    assertEquals(new CharactersToken("A black cat,"), e.get(0));
    assertEquals(new CharactersToken(" a white hat..."), e.get(1));
  }

  @Test
  void testPunctuation4() {
    TextTokenizer t = new TokenizerByPunctuation(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> e = t.tokenize("A black cat, a white hat");
    assertEquals(2, e.size());
    assertEquals(new CharactersToken("A black cat,"), e.get(0));
    assertEquals(new CharactersToken(" a white hat"), e.get(1));
  }

  @Test
  void testPunctuation5() {
    TextTokenizer t = new TokenizerByPunctuation(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> e = t.tokenize("A black cat; a white hat? a black rat! a white bat, and a black mat.");
    assertEquals(5, e.size());
    assertEquals(new CharactersToken("A black cat;"), e.get(0));
    assertEquals(new CharactersToken(" a white hat?"), e.get(1));
    assertEquals(new CharactersToken(" a black rat!"), e.get(2));
    assertEquals(new CharactersToken(" a white bat,"), e.get(3));
    assertEquals(new CharactersToken(" and a black mat."), e.get(4));
  }

}

