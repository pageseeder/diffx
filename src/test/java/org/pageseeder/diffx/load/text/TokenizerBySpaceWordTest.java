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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.pageseeder.diffx.test.TestTokens.toTextTokens;

/**
 * Test case for the tokenizer.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TokenizerBySpaceWordTest {

  /**
   * Tests that a <code>NullPointerException</code> is thrown for a </code>null</code>
   * character sequence.
   */
  @Test
  public void testNullConstructor() {
    assertThrows(NullPointerException.class, () -> new TokenizerBySpaceWord(null));
  }

  @Test
  public void testNull() {
    TextTokenizer tokenizer = new TokenizerBySpaceWord(WhiteSpaceProcessing.PRESERVE);
    assertThrows(NullPointerException.class, () -> tokenizer.tokenize(null));
  }

  /**
   * Tests that an empty array is returned for empty string.
   */
  @Test
  public void testEmpty() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("", WhiteSpaceProcessing.PRESERVE);
    assertEquals(0, tokens.size());
  }

  @Test
  public void testChar() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("a", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("a"), tokens);
  }

  @Test
  public void testCharWithLeadingSpace() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize(" a", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens(" a"), tokens);
  }

  @Test
  public void testCharWithTrailingSpace() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("a ", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("a", " "), tokens);
  }

  @Test
  public void testCharWithLeadingTrailingSpace() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize(" a ", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens(" a", " "), tokens);
  }

  @Test
  public void testWord() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("story", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("story"), tokens);
  }

  @Test
  public void testWordWithLeadingSpace() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize(" story", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens(" story"), tokens);
  }

  @Test
  public void testWordWithTrailingSpace() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("story ", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("story", " "), tokens);
  }

  @Test
  public void testWordWithLeadingTrailingSpace() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize(" story ", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens(" story", " "), tokens);
  }

  @Test
  public void testWords() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("A great story", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("A", " great", " story"), tokens);
  }

  @Test
  public void testWordsWithLeadingSpace() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize(" A great story", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens(" A", " great", " story"), tokens);
  }

  @Test
  public void testWordsWithTrailingSpace() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("A great story ", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("A", " great", " story", " "), tokens);
  }

  @Test
  public void testWordsWithLeadingTrailingSpace() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize(" A great story ", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens(" A", " great", " story", " "), tokens);
  }

  @Test
  public void testWordsWithPunctuation1() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize(" A great story!", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens(" A", " great", " story", "!"), tokens);
  }

  @Test
  public void testWordsWithPunctuation2() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("Blue, white, and red.", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("Blue", ",", " white", ",", " and", " red", "."), tokens);
  }

  @Test
  public void testNumbers1() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("1", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("1"), tokens);
  }

  @Test
  public void testNumbers2() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("1.0", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("1.0"), tokens);
  }

  @Test
  public void testNumbersUnit1() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("10mg", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("10mg"), tokens);
  }

  @Test
  public void testNumbersUnit2() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("1.25g", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("1.25g"), tokens);
  }

  @Test
  public void testNumbersPrice() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("$1.25", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("$1.25"), tokens);
  }

  @Test
  public void testNumbersPercent() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("33.33%", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("33.33%"), tokens);
  }

  @Test
  public void testEmail() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("john.obrien@example.com", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("john.obrien@example.com"), tokens);
  }

  @Test
  public void testMix1() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("My email is bill@example.com. 10% of 24 is 2.4!", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("My", " email", " is", " bill@example.com", ".", " 10%", " of", " 24", " is", " 2.4", "!"), tokens);
  }

  @Test
  public void testMix2() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("I'm Joe (joe@example.org)", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("I'm", " Joe", " (joe@example.org)"), tokens);
  }

  @Test
  public void testQuoted1() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("This is a \"test\"", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("This", " is", " a", " \"test\""), tokens);
  }

  @Test
  public void testQuoted2() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("This is a \"test", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("This", " is", " a", " ", "\"", "test"), tokens);
  }

  @Test
  public void testQuoted3() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("This is a test\"", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("This", " is", " a", " test", "\""), tokens);
  }

  @Test
  public void testBracket1() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("A (test)", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("A", " (test)"), tokens);
  }

  @Test
  public void testBracket2() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("A (test", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("A", " ", "(", "test"), tokens);
  }

  @Test
  public void testBracket3() {
    List<TextToken> tokens = TokenizerBySpaceWord.tokenize("A test)", WhiteSpaceProcessing.PRESERVE);
    assertEquals(toTextTokens("A", " test", ")"), tokens);
  }

}
