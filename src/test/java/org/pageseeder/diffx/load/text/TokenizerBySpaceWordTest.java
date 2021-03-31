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
package org.pageseeder.diffx.load.text;

import org.junit.Assert;
import org.junit.Test;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.event.TextEvent;

import java.util.List;

import static org.pageseeder.diffx.test.Events.toTextEvents;

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
  @Test(expected = NullPointerException.class)
  public void testNullConstructor() {
    new TokenizerBySpaceWord(null);
  }

  @Test(expected = NullPointerException.class)
  public void testNull() {
    TextTokenizer tokenizer = new TokenizerBySpaceWord(WhiteSpaceProcessing.PRESERVE);
    tokenizer.tokenize(null);
  }

  /**
   * Tests that an empty array is returned for empty string.
   */
  @Test
  public void testEmpty() {
    List<TextEvent> events = TokenizerBySpaceWord.tokenize("", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(0, events.size());
  }

  @Test
  public void testChar() {
    List<TextEvent> events = TokenizerBySpaceWord.tokenize("a", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents("a"), events);
  }

  @Test
  public void testCharWithLeadingSpace() {
    List<TextEvent> events = TokenizerBySpaceWord.tokenize(" a", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents(" a"), events);
  }

  @Test
  public void testCharWithTrailingSpace() {
    List<TextEvent> events = TokenizerBySpaceWord.tokenize("a ", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents("a", " "), events);
  }

  @Test
  public void testCharWithLeadingTrailingSpace() {
    List<TextEvent> events = TokenizerBySpaceWord.tokenize(" a ", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents(" a", " "), events);
  }

  @Test
  public void testWord() {
    List<TextEvent> events = TokenizerBySpaceWord.tokenize("story", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents("story"), events);
  }

  @Test
  public void testWordWithLeadingSpace() {
    List<TextEvent> events = TokenizerBySpaceWord.tokenize(" story", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents(" story"), events);
  }

  @Test
  public void testWordWithTrailingSpace() {
    List<TextEvent> events = TokenizerBySpaceWord.tokenize("story ", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents("story", " "), events);
  }

  @Test
  public void testWordWithLeadingTrailingSpace() {
    List<TextEvent> events = TokenizerBySpaceWord.tokenize(" story ", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents(" story", " "), events);
  }

  @Test
  public void testWords() {
    List<TextEvent> events = TokenizerBySpaceWord.tokenize("A great story", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents("A", " great", " story"), events);
  }

  @Test
  public void testWordsWithLeadingSpace() {
    List<TextEvent> events = TokenizerBySpaceWord.tokenize(" A great story", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents(" A", " great", " story"), events);
  }

  @Test
  public void testWordsWithTrailingSpace() {
    List<TextEvent> events = TokenizerBySpaceWord.tokenize("A great story ", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents("A", " great", " story", " "), events);
  }

  @Test
  public void testWordsWithLeadingTrailingSpace() {
    List<TextEvent> events = TokenizerBySpaceWord.tokenize(" A great story ", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents(" A", " great", " story", " "), events);
  }

  @Test
  public void testWordsWithPunctuation1() {
    List<TextEvent> events = TokenizerBySpaceWord.tokenize(" A great story!", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents(" A", " great", " story", "!"), events);
  }

  /**
   * Tests that an empty array is returned for empty string.
   */
  @Test
  public void testWordsWithPunctuation2() {
    List<TextEvent> events = TokenizerBySpaceWord.tokenize("Blue, white, and red.", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents("Blue", ",", " white", ",", " and", " red", "."), events);
  }

}
