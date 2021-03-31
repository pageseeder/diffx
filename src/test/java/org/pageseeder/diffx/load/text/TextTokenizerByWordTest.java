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
import org.pageseeder.diffx.event.impl.IgnorableSpaceEvent;
import org.pageseeder.diffx.event.impl.WordEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Test case for the tokenizer.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TextTokenizerByWordTest {

  /**
   * Tests that a <code>NullPointerException</code> is thrown for a </code>null</code>
   * character sequence.
   */
  @Test(expected = NullPointerException.class)
  public void testNullConstructor() {
    new TokenizerByWord(null);
  }

  @Test(expected = NullPointerException.class)
  public void testNull() {
    TextTokenizer tokenizer = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    tokenizer.tokenize(null);
  }

  /**
   * Tests that an empty array is returned for empty string.
   */
  @Test
  public void testEmpty() {
    List<TextEvent> events = TokenizerByWord.tokenize("", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(0, events.size());
  }

  @Test
  public void testChar() {
    List<TextEvent> events = TokenizerByWord.tokenize("a", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents("a"), events);
  }

  @Test
  public void testCharWithLeadingSpace() {
    List<TextEvent> events = TokenizerByWord.tokenize(" a", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents(" a"), events);
  }

  @Test
  public void testCharWithTrailingSpace() {
    List<TextEvent> events = TokenizerByWord.tokenize("a ", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents("a", " "), events);
  }

  @Test
  public void testCharWithLeadingTrailingSpace() {
    List<TextEvent> events = TokenizerByWord.tokenize(" a ", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents(" a", " "), events);
  }

  @Test
  public void testWord() {
    List<TextEvent> events = TokenizerByWord.tokenize("story", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents("story"), events);
  }

  @Test
  public void testWordWithLeadingSpace() {
    List<TextEvent> events = TokenizerByWord.tokenize(" story", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents(" story"), events);
  }

  @Test
  public void testWordWithTrailingSpace() {
    List<TextEvent> events = TokenizerByWord.tokenize("story ", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents("story", " "), events);
  }

  @Test
  public void testWordWithLeadingTrailingSpace() {
    List<TextEvent> events = TokenizerByWord.tokenize(" story ", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents(" story", " "), events);
  }

  /**
   * Tests that an empty array is returned for empty string.
   */
  @Test
  public void testWords() {
    List<TextEvent> events = TokenizerByWord.tokenize("A great story", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents("A", " great", " story"), events);
  }

  /**
   * Tests that an empty array is returned for empty string.
   */
  @Test
  public void testWordsWithLeadingSpace() {
    List<TextEvent> events = TokenizerByWord.tokenize(" A great story", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents(" A", " great", " story"), events);
  }

  /**
   * Tests that an empty array is returned for empty string.
   */
  @Test
  public void testWordsWithTrailingSpace() {
    List<TextEvent> events = TokenizerByWord.tokenize("A great story ", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents("A", " great", " story", " "), events);
  }

  /**
   * Tests that an empty array is returned for empty string.
   */
  @Test
  public void testWordsWithLeadingTrailingSpace() {
    List<TextEvent> events = TokenizerByWord.tokenize(" A great story ", WhiteSpaceProcessing.PRESERVE);
    Assert.assertEquals(toTextEvents(" A", " great", " story", " "), events);
  }


//  /**
//   * Tests that the tokeniser finds a space event as token.
//   */
//  @Test public void testSpace1() {
//    TextTokeniser ct = TokenizerByWord.tokenize(" ");
//    Assert.assertEquals(1, ct.countTokens());
//    DiffXEvent space = ct.nextToken();
//    Assert.assertEquals(new SpaceEvent(" "), space);
//    assertSame(SpaceEvent.SINGLE_WHITESPACE, space);
//    assertNoMoreTokens(ct);
//  }
//
//  /**
//   * Tests that the tokeniser finds a space event as token.
//   */
//  @Test public void testSpace2() {
//    TextTokeniser ct = TokenizerByWord.tokenize("  ");
//    Assert.assertEquals(1, ct.countTokens());
//    DiffXEvent space = ct.nextToken();
//    Assert.assertEquals(new SpaceEvent("  "), space);
//    assertSame(SpaceEvent.DOUBLE_WHITESPACE, space);
//    assertNoMoreTokens(ct);
//  }
//
//  /**
//   * Tests that the tokeniser finds a space event as token.
//   */
//  @Test public void testSpace3() {
//    TextTokeniser ct = TokenizerByWord.tokenize("\n");
//    Assert.assertEquals(1, ct.countTokens());
//    DiffXEvent space = ct.nextToken();
//    Assert.assertEquals(new SpaceEvent("\n"), space);
//    assertSame(SpaceEvent.NEW_LINE, space);
//    assertNoMoreTokens(ct);
//  }
//
//  /**
//   * Tests that the tokeniser finds a word event as token.
//   */
//  @Test public void testWord1() {
//    TextTokeniser ct = TokenizerByWord.tokenize("x");
//    Assert.assertEquals(1, ct.countTokens());
//    Assert.assertEquals(new WordEvent("x"), ct.nextToken());
//    assertNoMoreTokens(ct);
//  }
//
//  /**
//   * Tests that the tokeniser finds the correct sequence of events.
//   */
//  @Test public void testSeq1() {
//    TextTokeniser ct = TokenizerByWord.tokenize("xx  ");
//    Assert.assertEquals(2, ct.countTokens());
//    Assert.assertEquals(new WordEvent("xx"), ct.nextToken());
//    Assert.assertEquals(new SpaceEvent("  "), ct.nextToken());
//    assertNoMoreTokens(ct);
//  }
//
//  /**
//   * Tests that the tokeniser finds the correct sequence of events.
//   */
//  @Test public void testSeq2() {
//    TextTokeniser ct = TokenizerByWord.tokenize("  xx");
//    Assert.assertEquals(2, ct.countTokens());
//    Assert.assertEquals(new SpaceEvent("  "), ct.nextToken());
//    Assert.assertEquals(new WordEvent("xx"), ct.nextToken());
//    assertNoMoreTokens(ct);
//  }
//
//  /**
//   * Tests that the tokeniser finds the correct sequence of events.
//   */
//  @Test public void testSeq3() {
//    TextTokeniser ct = TokenizerByWord.tokenize("  xx\n");
//    Assert.assertEquals(3, ct.countTokens());
//    Assert.assertEquals(new SpaceEvent("  "), ct.nextToken());
//    Assert.assertEquals(new WordEvent("xx"), ct.nextToken());
//    Assert.assertEquals(SpaceEvent.NEW_LINE, ct.nextToken());
//    assertNoMoreTokens(ct);
//  }
//
//  /**
//   * Tests that the tokeniser finds the correct sequence of events.
//   */
//  @Test public void testSeq4() {
//    TextTokeniser ct = TokenizerByWord.tokenize("  xx\n\n");
//    Assert.assertEquals(4, ct.countTokens());
//    Assert.assertEquals(new SpaceEvent("  "), ct.nextToken());
//    Assert.assertEquals(new WordEvent("xx"), ct.nextToken());
//    Assert.assertEquals(SpaceEvent.NEW_LINE, ct.nextToken());
//    Assert.assertEquals(SpaceEvent.NEW_LINE, ct.nextToken());
//    assertNoMoreTokens(ct);
//  }
//
//  /**
//   * Tests that the tokeniser finds the correct sequence of events.
//   */
//  @Test public void testSeq5() {
//    TextTokeniser ct = TokenizerByWord.tokenize("  \n\nxx");
//    Assert.assertEquals(4, ct.countTokens());
//    Assert.assertEquals(new SpaceEvent("  "), ct.nextToken());
//    Assert.assertEquals(SpaceEvent.NEW_LINE, ct.nextToken());
//    Assert.assertEquals(SpaceEvent.NEW_LINE, ct.nextToken());
//    Assert.assertEquals(new WordEvent("xx"), ct.nextToken());
//    assertNoMoreTokens(ct);
//  }
//
//  /**
//   * Asserts that this was the last token of the tokeniser.
//   *
//   * <p>Checks that the {@link TextTokeniserByWord#nextToken()} method throws a
//   * <code>NoSuchElementException</code>.
//   *
//   * @param ct The character tokeniser to check.
//   */
//  public void assertNoMoreTokens(TextTokeniser ct) {
//    try {
//      ct.nextToken();
//      assertTrue(false);
//    } catch (NoSuchElementException ex) {
//      assertTrue(true);
//    }
//  }

  private List<TextEvent> toTextEvents(String... words) {
    List<TextEvent> events = new ArrayList<>();
    for (String word : words) {
      if (word.matches("\\s+")) {
        events.add(new IgnorableSpaceEvent(word));
      } else {
        events.add(new WordEvent(word));
      }
    }
    return events;
  }

}
