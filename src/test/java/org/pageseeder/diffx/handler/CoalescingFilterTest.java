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
package org.pageseeder.diffx.handler;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.Operations;
import org.pageseeder.diffx.action.OperationsBuffer;
import org.pageseeder.diffx.test.TestHandler;
import org.pageseeder.diffx.test.TestTokens;
import org.pageseeder.diffx.token.TextToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.impl.CharactersToken;
import org.pageseeder.diffx.token.impl.SpaceToken;
import org.pageseeder.diffx.token.impl.WordToken;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.pageseeder.diffx.test.TestOperations.toCharOperations;
import static org.pageseeder.diffx.test.TestOperations.toTextOperations;

class CoalescingFilterTest {

  private static List<Operation<XMLToken>> normalizeOperations(List<Operation<XMLToken>> operations) {
    return operations.stream().map(CoalescingFilterTest::normalizeOperation).collect(Collectors.toList());
  }

  private static Operation<XMLToken> normalizeOperation(Operation<XMLToken> operation) {
    if (operation.token() instanceof TextToken)
      return new Operation<>(operation.operator(), new CharactersToken(((TextToken) operation.token()).getCharacters()));
    return operation;
  }

  @Test
  void testCoalesceEmpty() {
    TextToken got = CoalescingFilter.coalesceText(Collections.emptyList());
    assertEquals("", got.getCharacters());
  }

  @Test
  void testCoalesceSingle1() {
    TextToken space = new SpaceToken(" ");
    TextToken got = CoalescingFilter.coalesceText(Collections.singletonList(space));
    assertSame(space, got);
  }

  @Test
  void testCoalesceSingle2() {
    TextToken text = new CharactersToken("A big cat");
    TextToken got = CoalescingFilter.coalesceText(Collections.singletonList(text));
    assertSame(text, got);
  }

  @Test
  void testCoalesceSingle3() {
    TextToken word = new WordToken("cat");
    TextToken got = CoalescingFilter.coalesceText(Collections.singletonList(word));
    assertSame(word, got);
  }

  @Test
  void testCoalesceMultiple1() {
    List<TextToken> tokens = TestTokens.toTextTokens("A", " ", "big", " ", "cat!");
    TextToken got = CoalescingFilter.coalesceText(tokens);
    assertEquals("A big cat!", got.getCharacters());
  }

  @Test
  void testFilter1() {
    List<Operation<XMLToken>> got = toTextOperations("A", " ", "+big", " ", "castle");
    List<Operation<XMLToken>> exp = toTextOperations("A ", "+big", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  void testFilter2() {
    List<Operation<XMLToken>> got = toTextOperations("The", "+ very", "+ big", "- large", "- medieval", " castle");
    List<Operation<XMLToken>> exp = toTextOperations("The", "+ very big", "- large medieval", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  void testFilter3() {
    List<Operation<XMLToken>> got = toTextOperations("-A", "+The", "+ very", "+ big", " castle");
    List<Operation<XMLToken>> exp = toTextOperations("-A", "+The very big", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  void testFilter4() {
    List<Operation<XMLToken>> got = toTextOperations("A", "+ big", "- small", "+ blue", "- red", " castle");
    List<Operation<XMLToken>> exp = toTextOperations("A", "+ big blue", "- small red", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  void testFilter5() {
    List<Operation<XMLToken>> got = toTextOperations("A", "+ big", "+ blue", "- small", "- red", " castle");
    List<Operation<XMLToken>> exp = toTextOperations("A", "+ big blue", "- small red", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  void testFilter6() {
    List<Operation<XMLToken>> got = toTextOperations("A", "- small", "+ big", "+ blue", "- red", " castle");
    List<Operation<XMLToken>> exp = toTextOperations("A", "- small red", "+ big blue", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  void testFilter7() {
    List<Operation<XMLToken>> got = toTextOperations("A", "- small", "+ big", "+ blue", "- red", " castle");
    List<Operation<XMLToken>> exp = toTextOperations("A", "- small red", "+ big blue", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  void testFilter8() {
    List<Operation<XMLToken>> got = toTextOperations("A", "- small", "+ big", "+ blue", "- red", " castle");
    List<Operation<XMLToken>> exp = toTextOperations("A", "- small red", "+ big blue", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  void testFilter9() {
    List<Operation<XMLToken>> got = toCharOperations("+AM+A+A-R");
    List<Operation<XMLToken>> exp = toTextOperations("+A", "M", "+AA", "-R");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  void testFilter10() {
    List<Operation<XMLToken>> got = toCharOperations("+A+AMMM+A+A-D+A-D+A");
    List<Operation<XMLToken>> exp = toTextOperations("+AA", "MMM", "+AAAA", "-DD");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  void testFilter11() {
    List<Operation<XMLToken>> got = toCharOperations("+A-D+AMMM-D+A+A-D+A-D+A");
    List<Operation<XMLToken>> exp = toTextOperations("+AA", "-D", "MMM", "-DDD", "+AAAA");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  void testMixFilter1() {
    List<Operation<XMLToken>> src = TestHandler.parse("<a>A+BC</a>");
    List<Operation<XMLToken>> exp = TestHandler.parse("<a>A+BC</a>");
    assertEquivalentOperations(exp, coalesceOperations(src));
  }

  @Test
  void testMixFilter2() {
    List<Operation<XMLToken>> src = TestHandler.parse("<a>+A-B+C</a>");
    List<Operation<XMLToken>> exp = TestHandler.parse("<a>+(AC)-B</a>");
    assertEquivalentOperations(exp, coalesceOperations(src));
  }

  @Test
  void testMixFilter3() {
    List<Operation<XMLToken>> src = TestHandler.parse("<a>A+BC-D</a>");
    List<Operation<XMLToken>> exp = TestHandler.parse("<a>A+BC-D</a>");
    assertEquivalentOperations(exp, coalesceOperations(src));
  }


  // Private helpers
  // --------------------------------------------------------------------------

  @Test
  void testMixFilter4() {
    List<Operation<XMLToken>> src = TestHandler.parse("<a><b>X- -Y</b> +<b>+Y+</b></a>");
    List<Operation<XMLToken>> exp = TestHandler.parse("<a><b>X-( Y)</b> +<b>+Y+</b></a>");
    assertEquivalentOperations(exp, coalesceOperations(src));
  }

  @Test
  void testMixFilter5() {
    List<Operation<XMLToken>> src = TestHandler.parse("<a><b>-Y</b>+ +<b>+Y+</b></a>");
    List<Operation<XMLToken>> exp = TestHandler.parse("<a><b>-Y</b>+ +<b>+Y+</b></a>");
    assertEquivalentOperations(exp, coalesceOperations(src));
  }

  private List<Operation<XMLToken>> coalesceOperations(List<Operation<XMLToken>> operations) {
    OperationsBuffer<XMLToken> target = new OperationsBuffer<>();
    CoalescingFilter filter = new CoalescingFilter(target);
    filter.start();
    Operations.handle(operations, filter);
    filter.end();
    return target.getOperations();
  }

  private void assertEquivalentOperations(List<Operation<XMLToken>> exp, List<Operation<XMLToken>> got) {
    assertEquals(normalizeOperations(exp), normalizeOperations(got));
  }

}
