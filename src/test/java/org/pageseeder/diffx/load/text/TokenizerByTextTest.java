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
import org.pageseeder.diffx.token.impl.SpaceToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test case for {@link TokenizerByText}.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
final class TokenizerByTextTest {

  // --- Constructor ---

  @Test
  void constructor_nullWhiteSpace_throwsNPE() {
    assertThrows(NullPointerException.class, () -> new TokenizerByText(null));
  }

  // --- Null and empty input ---

  @Test
  void tokenize_null_throwsNPE() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.IGNORE);
    assertThrows(NullPointerException.class, () -> t.tokenize(null));
  }

  @Test
  void tokenize_empty_returnsEmptyList() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.IGNORE);
    List<TextToken> tokens = t.tokenize("");
    assertTrue(tokens.isEmpty());
  }

  // --- No leading or trailing whitespace (all modes behave the same) ---

  @Test
  void tokenize_noWhitespace_returnsSingleCharactersToken() {
    for (WhiteSpaceProcessing ws : WhiteSpaceProcessing.values()) {
      TextTokenizer t = new TokenizerByText(ws);
      List<TextToken> tokens = t.tokenize("hello");
      assertEquals(1, tokens.size(), "mode=" + ws);
      assertEquals(new CharactersToken("hello"), tokens.get(0), "mode=" + ws);
    }
  }

  @Test
  void tokenize_noWhitespace_internalSpaces_returnsSingleCharactersToken() {
    for (WhiteSpaceProcessing ws : WhiteSpaceProcessing.values()) {
      TextTokenizer t = new TokenizerByText(ws);
      List<TextToken> tokens = t.tokenize("a b c");
      assertEquals(1, tokens.size(), "mode=" + ws);
      assertEquals(new CharactersToken("a b c"), tokens.get(0), "mode=" + ws);
    }
  }

  // --- All whitespace ---

  @Test
  void tokenize_allWhitespace_compare_returnsSpaceToken() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.COMPARE);
    List<TextToken> tokens = t.tokenize(" ");
    assertEquals(1, tokens.size());
    assertSame(SpaceToken.SINGLE_WHITESPACE, tokens.get(0));
  }

  @Test
  void tokenize_allWhitespace_compare_doubleSpace() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.COMPARE);
    List<TextToken> tokens = t.tokenize("  ");
    assertEquals(1, tokens.size());
    assertSame(SpaceToken.DOUBLE_WHITESPACE, tokens.get(0));
  }

  @Test
  void tokenize_allWhitespace_compare_newline() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.COMPARE);
    List<TextToken> tokens = t.tokenize("\n");
    assertEquals(1, tokens.size());
    assertSame(SpaceToken.NEW_LINE, tokens.get(0));
  }

  @Test
  void tokenize_allWhitespace_compare_multipleNewlines() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.COMPARE);
    List<TextToken> tokens = t.tokenize("\n\n");
    assertEquals(1, tokens.size());
    assertEquals(new SpaceToken("\n\n"), tokens.get(0));
  }

  @Test
  void tokenize_allWhitespace_preserve_returnsIgnorableSpaceToken() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> tokens = t.tokenize(" ");
    assertEquals(1, tokens.size());
    assertInstanceOf(IgnorableSpaceToken.class, tokens.get(0));
    assertEquals(new IgnorableSpaceToken(" "), tokens.get(0));
  }

  @Test
  void tokenize_allWhitespace_preserve_multipleNewlines() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> tokens = t.tokenize("\n\n\n");
    assertEquals(1, tokens.size());
    assertInstanceOf(IgnorableSpaceToken.class, tokens.get(0));
  }

  @Test
  void tokenize_allWhitespace_ignore_returnsEmptyList() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.IGNORE);
    assertTrue(t.tokenize(" ").isEmpty());
    assertTrue(t.tokenize("  ").isEmpty());
    assertTrue(t.tokenize("\n").isEmpty());
    assertTrue(t.tokenize("\n \n").isEmpty());
  }

  // --- Mixed: COMPARE mode ---

  @Test
  void tokenize_leadingSpace_compare() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.COMPARE);
    List<TextToken> tokens = t.tokenize("  xx");
    assertEquals(2, tokens.size());
    assertEquals(new SpaceToken("  "), tokens.get(0));
    assertEquals(new CharactersToken("xx"), tokens.get(1));
  }

  @Test
  void tokenize_trailingSpace_compare() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.COMPARE);
    List<TextToken> tokens = t.tokenize("xx\n");
    assertEquals(2, tokens.size());
    assertEquals(new CharactersToken("xx"), tokens.get(0));
    assertEquals(SpaceToken.NEW_LINE, tokens.get(1));
  }

  @Test
  void tokenize_leadingAndTrailingSpace_compare() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.COMPARE);
    List<TextToken> tokens = t.tokenize("  xx\n");
    assertEquals(3, tokens.size());
    assertEquals(new SpaceToken("  "), tokens.get(0));
    assertEquals(new CharactersToken("xx"), tokens.get(1));
    assertEquals(SpaceToken.NEW_LINE, tokens.get(2));
  }

  @Test
  void tokenize_leadingAndTrailingMultiple_compare() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.COMPARE);
    List<TextToken> tokens = t.tokenize("  xx\n\n");
    assertEquals(3, tokens.size());
    assertEquals(new SpaceToken("  "), tokens.get(0));
    assertEquals(new CharactersToken("xx"), tokens.get(1));
    assertEquals(new SpaceToken("\n\n"), tokens.get(2));
  }

  // --- Mixed: PRESERVE mode ---

  @Test
  void tokenize_leadingSpace_preserve() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> tokens = t.tokenize("  \n\nxx");
    assertEquals(2, tokens.size());
    assertInstanceOf(IgnorableSpaceToken.class, tokens.get(0));
    assertEquals(new IgnorableSpaceToken("  "), tokens.get(0));
    assertEquals(new CharactersToken("xx"), tokens.get(1));
  }

  @Test
  void tokenize_trailingSpace_preserve() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> tokens = t.tokenize("xx  ");
    assertEquals(2, tokens.size());
    assertEquals(new CharactersToken("xx"), tokens.get(0));
    assertInstanceOf(IgnorableSpaceToken.class, tokens.get(1));
    assertEquals(new IgnorableSpaceToken("  "), tokens.get(1));
  }

  @Test
  void tokenize_leadingAndTrailingSpace_preserve() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.PRESERVE);
    List<TextToken> tokens = t.tokenize(" bb\n ");
    assertEquals(3, tokens.size());
    assertInstanceOf(IgnorableSpaceToken.class, tokens.get(0));
    assertEquals(new CharactersToken("bb"), tokens.get(1));
    assertInstanceOf(IgnorableSpaceToken.class, tokens.get(2));
  }

  // --- Mixed: IGNORE mode ---

  @Test
  void tokenize_leadingSpace_ignore_stripsWhitespace() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.IGNORE);
    List<TextToken> tokens = t.tokenize(" hello");
    assertEquals(1, tokens.size());
    assertEquals(new CharactersToken("hello"), tokens.get(0));
  }

  @Test
  void tokenize_trailingSpace_ignore_stripsWhitespace() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.IGNORE);
    List<TextToken> tokens = t.tokenize("hello ");
    assertEquals(1, tokens.size());
    assertEquals(new CharactersToken("hello"), tokens.get(0));
  }

  @Test
  void tokenize_leadingAndTrailingSpace_ignore_stripsWhitespace() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.IGNORE);
    List<TextToken> tokens = t.tokenize("  hello  ");
    assertEquals(1, tokens.size());
    assertEquals(new CharactersToken("hello"), tokens.get(0));
  }

  // --- Token count regressions (from original test suite) ---

  @Test
  void tokenize_countTokens_ignore() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.IGNORE);
    assertEquals(0, t.tokenize(" ").size());
    assertEquals(1, t.tokenize(" a").size());
    assertEquals(1, t.tokenize("a ").size());
    assertEquals(1, t.tokenize(" b ").size());
    assertEquals(1, t.tokenize("b b").size());
    assertEquals(1, t.tokenize("c c ").size());
    assertEquals(1, t.tokenize(" c c").size());
    assertEquals(1, t.tokenize(" d d ").size());
    assertEquals(1, t.tokenize("d d d").size());
    assertEquals(1, t.tokenize("  a").size());
    assertEquals(1, t.tokenize("aa ").size());
    assertEquals(1, t.tokenize("a  ").size());
    assertEquals(1, t.tokenize(" bb ").size());
    assertEquals(1, t.tokenize("b bb").size());
    assertEquals(1, t.tokenize("b   bb").size());
    assertEquals(1, t.tokenize("xx  yy  ").size());
  }

  @Test
  void tokenize_countTokens_preserve() {
    TextTokenizer t = new TokenizerByText(WhiteSpaceProcessing.PRESERVE);
    assertEquals(1, t.tokenize(" ").size());
    assertEquals(2, t.tokenize("  \na").size());
    assertEquals(2, t.tokenize("aa \n").size());
    assertEquals(2, t.tokenize(" \naa").size());
    assertEquals(2, t.tokenize("a \n ").size());
    assertEquals(3, t.tokenize(" bb\n ").size());
    assertEquals(1, t.tokenize("b\n bb").size());
    assertEquals(1, t.tokenize("b \n  bb").size());
    assertEquals(2, t.tokenize("xx \n yy\n  ").size());
    assertEquals(1, t.tokenize("\n").size());
    assertEquals(1, t.tokenize("\n \n").size());
    assertEquals(1, t.tokenize(" \n\n").size());
    assertEquals(1, t.tokenize("\n\n\n").size());
  }

}

