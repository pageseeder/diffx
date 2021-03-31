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

import org.junit.Test;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.TextEvent;
import org.pageseeder.diffx.event.impl.CharactersEvent;
import org.pageseeder.diffx.event.impl.SpaceEvent;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test case for the tokenizer.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TokenizerByCharTest {

  /**
   * Tests that a <code>NullPointerException</code> is thrown for a </code>null</code>
   * character sequence.
   */
  @Test
  public void testNull() {
    try {
      TokenizerByChar t = new TokenizerByChar();
      t.tokenize(null);
      assertTrue(false);
    } catch (NullPointerException ex) {
      assertTrue(true);
    }
  }

  /**
   * Tests that an empty array is returned for empty string.
   */
  @Test
  public void testEmpty() {
    TokenizerByChar t = new TokenizerByChar();
    List<TextEvent> e = t.tokenize("");
    assertEquals(0, e.size());
  }

  /**
   * Tests that the tokeniser counts the correct number of tokens.
   */
  @Test
  public void testCountToken1() {
    TokenizerByChar t = new TokenizerByChar();
    assertEquals(1, t.tokenize(" ").size());
    assertEquals(2, t.tokenize(" a").size());
    assertEquals(2, t.tokenize("a ").size());
    assertEquals(3, t.tokenize(" b ").size());
    assertEquals(3, t.tokenize("b b").size());
    assertEquals(4, t.tokenize("c c ").size());
    assertEquals(4, t.tokenize(" c c").size());
    assertEquals(5, t.tokenize(" d d ").size());
    assertEquals(5, t.tokenize("d d d").size());
  }

  /**
   * Tests that the tokeniser counts the correct number of tokens.
   */
  @Test
  public void testCountToken2() {
    TokenizerByChar t = new TokenizerByChar();
    assertEquals(1, t.tokenize(" ").size());
    assertEquals(3, t.tokenize("  a").size());
    assertEquals(3, t.tokenize("aa ").size());
    assertEquals(3, t.tokenize(" aa").size());
    assertEquals(3, t.tokenize("a  ").size());
    assertEquals(4, t.tokenize(" bb ").size());
    assertEquals(4, t.tokenize("b bb").size());
    assertEquals(6, t.tokenize("b   bb").size());
    assertEquals(8, t.tokenize("xx  yy  ").size());
  }

  /**
   * Tests that the tokeniser finds a space event as token.
   */
  @Test
  public void testSpace1() {
    TokenizerByChar t = new TokenizerByChar();
    List<TextEvent> e = t.tokenize(" ");
    assertEquals(1, e.size());
    DiffXEvent space = e.get(0);
    assertEquals(new SpaceEvent(" "), space);
    assertSame(SpaceEvent.SINGLE_WHITESPACE, space);
  }

  /**
   * Tests that the tokeniser finds a space event as token.
   */
  @Test
  public void testSpace3() {
    TokenizerByChar t = new TokenizerByChar();
    List<TextEvent> e = t.tokenize("\n");
    assertEquals(1, e.size());
    DiffXEvent space = e.get(0);
    assertEquals(new SpaceEvent("\n"), space);
    assertSame(SpaceEvent.NEW_LINE, space);
  }

  /**
   * Tests that the tokeniser finds a word event as token.
   */
  @Test
  public void testWord1() {
    TokenizerByChar t = new TokenizerByChar();
    List<TextEvent> e = t.tokenize("x");
    assertEquals(1, e.size());
    assertEquals(new CharactersEvent("x"), e.get(0));
  }

}

