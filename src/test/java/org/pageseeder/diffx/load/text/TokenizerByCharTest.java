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
import org.pageseeder.diffx.token.TextToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.impl.CharactersToken;
import org.pageseeder.diffx.token.impl.SpaceToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    TokenizerByChar t = new TokenizerByChar();
    assertThrows(NullPointerException.class, () -> t.tokenize(null));
  }

  @Test
  public void testEmpty() {
    TokenizerByChar t = new TokenizerByChar();
    List<TextToken> e = t.tokenize("");
    assertEquals(0, e.size());
  }

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

  @Test
  public void testSpace1() {
    TokenizerByChar t = new TokenizerByChar();
    List<TextToken> e = t.tokenize(" ");
    assertEquals(1, e.size());
    XMLToken space = e.get(0);
    assertEquals(new SpaceToken(" "), space);
    assertSame(SpaceToken.SINGLE_WHITESPACE, space);
  }

  @Test
  public void testSpace3() {
    TokenizerByChar t = new TokenizerByChar();
    List<TextToken> e = t.tokenize("\n");
    assertEquals(1, e.size());
    XMLToken space = e.get(0);
    assertEquals(new SpaceToken("\n"), space);
    assertSame(SpaceToken.NEW_LINE, space);
  }

  @Test
  public void testWord1() {
    TokenizerByChar t = new TokenizerByChar();
    List<TextToken> e = t.tokenize("x");
    assertEquals(1, e.size());
    assertEquals(new CharactersToken("x"), e.get(0));
  }

}

