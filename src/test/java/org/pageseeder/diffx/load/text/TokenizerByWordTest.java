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
import org.pageseeder.diffx.token.Token;
import org.pageseeder.diffx.token.impl.IgnorableSpaceToken;
import org.pageseeder.diffx.token.impl.WordToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test case for the tokenizer.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TokenizerByWordTest {

  /**
   * Tests that a <code>NullPointerException</code> is thrown for a </code>null</code>
   * character sequence.
   */
  @Test
  public void testNull() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.IGNORE);
    assertThrows(NullPointerException.class, () -> t.tokenize(null));
  }

  /**
   * Tests that an empty array is returned for empty string.
   */
  @Test
  public void testEmpty() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.IGNORE);
    List<TextToken> e = t.tokenize("");
    assertEquals(0, e.size());
  }

  /**
   * Tests that the tokeniser counts the correct number of tokens.
   */
  @Test
  public void testCountToken1() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.IGNORE);
    assertEquals(0, t.tokenize(" ").size());
    assertEquals(1, t.tokenize(" a").size());
    assertEquals(1, t.tokenize("a ").size());
    assertEquals(1, t.tokenize(" b ").size());
    assertEquals(2, t.tokenize("b b").size());
    assertEquals(2, t.tokenize("c c ").size());
    assertEquals(2, t.tokenize(" c c").size());
    assertEquals(2, t.tokenize(" d d ").size());
    assertEquals(3, t.tokenize("d d d").size());
  }

  /**
   * Tests that the tokeniser counts the correct number of tokens.
   */
  @Test
  public void testCountToken2() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.IGNORE);
    assertEquals(0, t.tokenize(" ").size());
    assertEquals(1, t.tokenize("  a").size());
    assertEquals(1, t.tokenize("aa ").size());
    assertEquals(1, t.tokenize(" aa").size());
    assertEquals(1, t.tokenize("a  ").size());
    assertEquals(1, t.tokenize(" bb ").size());
    assertEquals(2, t.tokenize("b bb").size());
    assertEquals(2, t.tokenize("b   bb").size());
    assertEquals(2, t.tokenize("xx  yy  ").size());
  }

  /**
   * Tests that the tokeniser counts the correct number of tokens.
   */
  @Test
  public void testCountToken3() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    assertEquals(1, t.tokenize(" ").size());
    assertEquals(2, t.tokenize("  \na").size());
    assertEquals(2, t.tokenize("aa \n").size());
    assertEquals(2, t.tokenize(" \naa").size());
    assertEquals(2, t.tokenize("a \n ").size());
    assertEquals(3, t.tokenize(" bb\n ").size());
    assertEquals(3, t.tokenize("b\n bb").size());
    assertEquals(3, t.tokenize("b \n  bb").size());
    assertEquals(4, t.tokenize("xx \n yy\n  ").size());
  }

  /**
   * Tests that the tokeniser counts the correct number of tokens.
   */
  @Test
  public void testCountToken4() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    assertEquals(1, t.tokenize("\n").size());
    assertEquals(1, t.tokenize("\n \n").size());
    assertEquals(1, t.tokenize(" \n\n").size());
    assertEquals(1, t.tokenize("\n\n\n").size());
  }

  /**
   * Tests that the tokeniser finds a space token as token.
   */
  @Test
  public void testSpace1() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> e = t.tokenize(" ");
    assertEquals(1, e.size());
    Token space = e.get(0);
    assertEquals(new IgnorableSpaceToken(" "), space);
  }

  /**
   * Tests that the tokeniser finds a space token as token.
   */
  @Test
  public void testSpace2() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> e = t.tokenize("  ");
    assertEquals(1, e.size());
    Token space = e.get(0);
    assertEquals(new IgnorableSpaceToken("  "), space);
  }

  /**
   * Tests that the tokeniser finds a space token as token.
   */
  @Test
  public void testSpace3() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> e = t.tokenize("\n");
    assertEquals(1, e.size());
    Token space = e.get(0);
    assertEquals(new IgnorableSpaceToken("\n"), space);
  }

  /**
   * Tests that the tokeniser finds a word token as token.
   */
  @Test
  public void testWord1() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> e = t.tokenize("x");
    assertEquals(1, e.size());
    assertEquals(new WordToken("x"), e.get(0));
  }

  /**
   * Tests that the tokeniser finds the correct sequence of tokens.
   */
  @Test
  public void testSeq1() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> e = t.tokenize("xx  ");
    assertEquals(2, e.size());
    assertEquals(new WordToken("xx"), e.get(0));
    assertEquals(new IgnorableSpaceToken("  "), e.get(1));
  }

  /**
   * Tests that the tokeniser finds the correct sequence of tokens.
   */
  @Test
  public void testSeq2() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> e = t.tokenize("  xx");
    assertEquals(2, e.size());
    assertEquals(new IgnorableSpaceToken("  "), e.get(0));
    assertEquals(new WordToken("xx"), e.get(1));
  }

  /**
   * Tests that the tokeniser finds the correct sequence of tokens.
   */
  @Test
  public void testSeq3() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> e = t.tokenize("  xx\n");
    assertEquals(3, e.size());
    assertEquals(new IgnorableSpaceToken("  "), e.get(0));
    assertEquals(new WordToken("xx"), e.get(1));
    assertEquals(new IgnorableSpaceToken("\n"), e.get(2));
  }

  /**
   * Tests that the tokeniser finds the correct sequence of tokens.
   */
  @Test
  public void testSeq4() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> e = t.tokenize("  xx\n\n");
    assertEquals(3, e.size());
    assertEquals(new IgnorableSpaceToken("  "), e.get(0));
    assertEquals(new WordToken("xx"), e.get(1));
    assertEquals(new IgnorableSpaceToken("\n\n"), e.get(2));
  }

  /**
   * Tests that the tokeniser finds the correct sequence of tokens.
   */
  @Test
  public void testSeq5() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> e = t.tokenize("  \n\nxx");
    assertEquals(2, e.size());
    assertEquals(new IgnorableSpaceToken("  \n\n"), e.get(0));
    assertEquals(new WordToken("xx"), e.get(1));
  }

}

