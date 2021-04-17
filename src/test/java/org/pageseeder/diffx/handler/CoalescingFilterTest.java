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
import org.pageseeder.diffx.test.Events;
import org.pageseeder.diffx.test.TestHandler;
import org.pageseeder.diffx.token.TextToken;
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

public class CoalescingFilterTest {

  @Test
  public void testCoalesceEmpty() {
    TextToken got = CoalescingFilter.coalesceText(Collections.emptyList());
    assertEquals("", got.getCharacters());
  }

  @Test
  public void testCoalesceSingle1() {
    TextToken space = new SpaceToken(" ");
    TextToken got = CoalescingFilter.coalesceText(Collections.singletonList(space));
    assertSame(space, got);
  }

  @Test
  public void testCoalesceSingle2() {
    TextToken text = new CharactersToken("A big cat");
    TextToken got = CoalescingFilter.coalesceText(Collections.singletonList(text));
    assertSame(text, got);
  }

  @Test
  public void testCoalesceSingle3() {
    TextToken word = new WordToken("cat");
    TextToken got = CoalescingFilter.coalesceText(Collections.singletonList(word));
    assertSame(word, got);
  }

  @Test
  public void testCoalesceMultiple1() {
    List<TextToken> tokens = Events.toTextTokens("A", " ", "big", " ", "cat!");
    TextToken got = CoalescingFilter.coalesceText(tokens);
    assertEquals("A big cat!", got.getCharacters());
  }

  @Test
  public void testFilter1() {
    List<Operation> got = toTextOperations("A", " ", "+big", " ", "castle");
    List<Operation> exp = toTextOperations("A ", "+big", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  public void testFilter2() {
    List<Operation> got = toTextOperations("The", "+ very", "+ big", "- large", "- medieval", " castle");
    List<Operation> exp = toTextOperations("The", "+ very big", "- large medieval", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  public void testFilter3() {
    List<Operation> got = toTextOperations("-A", "+The", "+ very", "+ big", " castle");
    List<Operation> exp = toTextOperations("-A", "+The very big", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  public void testFilter4() {
    List<Operation> got = toTextOperations("A", "+ big", "- small", "+ blue", "- red", " castle");
    List<Operation> exp = toTextOperations("A", "+ big blue", "- small red", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  public void testFilter5() {
    List<Operation> got = toTextOperations("A", "+ big", "+ blue", "- small", "- red", " castle");
    List<Operation> exp = toTextOperations("A", "+ big blue", "- small red", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  public void testFilter6() {
    List<Operation> got = toTextOperations("A", "- small", "+ big", "+ blue", "- red", " castle");
    List<Operation> exp = toTextOperations("A", "- small red", "+ big blue", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  public void testFilter7() {
    List<Operation> got = toTextOperations("A", "- small", "+ big", "+ blue", "- red", " castle");
    List<Operation> exp = toTextOperations("A", "- small red", "+ big blue", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  public void testFilter8() {
    List<Operation> got = toTextOperations("A", "- small", "+ big", "+ blue", "- red", " castle");
    List<Operation> exp = toTextOperations("A", "- small red", "+ big blue", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  public void testFilter9() {
    List<Operation> got = toCharOperations("+AM+A+A-R");
    List<Operation> exp = toTextOperations("+A", "M", "+AA", "-R");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  public void testFilter10() {
    List<Operation> got = toCharOperations("+A+AMMM+A+A-D+A-D+A");
    List<Operation> exp = toTextOperations("+AA", "MMM", "+AAAA", "-DD");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  public void testFilter11() {
    List<Operation> got = toCharOperations("+A-D+AMMM-D+A+A-D+A-D+A");
    List<Operation> exp = toTextOperations("+AA", "-D", "MMM", "-DDD", "+AAAA");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  public void testMixFilter1() {
    List<Operation> src = TestHandler.parse("<a>A+BC</a>");
    List<Operation> exp = TestHandler.parse("<a>A+BC</a>");
    assertEquivalentOperations(exp, coalesceOperations(src));
  }

  @Test
  public void testMixFilter2() {
    List<Operation> src = TestHandler.parse("<a>+A-B+C</a>");
    List<Operation> exp = TestHandler.parse("<a>+(AC)-B</a>");
    assertEquivalentOperations(exp, coalesceOperations(src));
  }

  @Test
  public void testMixFilter3() {
    List<Operation> src = TestHandler.parse("<a>A+BC-D</a>");
    List<Operation> exp = TestHandler.parse("<a>A+BC-D</a>");
    assertEquivalentOperations(exp, coalesceOperations(src));
  }

  @Test
  public void testMixFilter4() {
    List<Operation> src = TestHandler.parse("<a><b>X- -Y</b> +<b>+Y+</b></a>");
    System.out.println(src);
    List<Operation> exp = TestHandler.parse("<a><b>X-( Y)</b> +<b>+Y+</b></a>");
    assertEquivalentOperations(exp, coalesceOperations(src));
  }

  @Test
  public void testMixFilter5() {
    List<Operation> src = TestHandler.parse("<a><b>-Y</b>+ +<b>+Y+</b></a>");
    List<Operation> exp = TestHandler.parse("<a><b>-Y</b>+ +<b>+Y+</b></a>");
    assertEquivalentOperations(exp, coalesceOperations(src));
  }


  // Private helpers
  // --------------------------------------------------------------------------

  private List<Operation> coalesceOperations(List<Operation> operations) {
    OperationHandler target = new OperationHandler();
    CoalescingFilter filter = new CoalescingFilter(target);
    filter.start();
    Operations.handle(operations, filter);
    filter.end();
    return target.getOperations();
  }

  private void assertEquivalentOperations(List<Operation> exp, List<Operation> got) {
    assertEquals(normalizeOperations(exp), normalizeOperations(got));
  }

  private static List<Operation> normalizeOperations(List<Operation> operations) {
    return operations.stream().map(CoalescingFilterTest::normalizeOperation).collect(Collectors.toList());
  }

  private static Operation normalizeOperation(Operation operation) {
    if (operation.token() instanceof TextToken)
      return new Operation(operation.operator(), new CharactersToken(((TextToken) operation.token()).getCharacters()));
    return operation;
  }

}
