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
package org.pageseeder.diffx.handler;

import org.junit.Assert;
import org.junit.Test;
import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.Operations;
import org.pageseeder.diffx.event.TextEvent;
import org.pageseeder.diffx.event.impl.CharactersEvent;
import org.pageseeder.diffx.event.impl.SpaceEvent;
import org.pageseeder.diffx.event.impl.WordEvent;
import org.pageseeder.diffx.test.Events;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.pageseeder.diffx.test.TestOperations.toTextOperations;
import static org.pageseeder.diffx.test.TestOperations.toCharOperations;

public class CoalescingFilterTest {

  @Test
  public void testCoalesceEmpty() {
    TextEvent got = CoalescingFilter.coalesceText(Collections.emptyList());
    Assert.assertEquals("", got.getCharacters());
  }

  @Test
  public void testCoalesceSingle1() {
    TextEvent space = new SpaceEvent(" ");
    TextEvent got = CoalescingFilter.coalesceText(Collections.singletonList(space));
    Assert.assertSame(space, got);
  }

  @Test
  public void testCoalesceSingle2() {
    TextEvent text = new CharactersEvent("A big cat");
    TextEvent got = CoalescingFilter.coalesceText(Collections.singletonList(text));
    Assert.assertSame(text, got);
  }

  @Test
  public void testCoalesceSingle3() {
    TextEvent word = new WordEvent("cat");
    TextEvent got = CoalescingFilter.coalesceText(Collections.singletonList(word));
    Assert.assertSame(word, got);
  }

  @Test
  public void testCoalesceMultiple1() {
    List<TextEvent> events = Events.toTextEvents("A", " ", "big", " ", "cat!");
    TextEvent got = CoalescingFilter.coalesceText(events);
    Assert.assertEquals("A big cat!", got.getCharacters());
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
    List<Operation> got = toTextOperations("A", "- small", "+ big", "+ blue",  "- red", " castle");
    List<Operation> exp = toTextOperations("A", "- small red", "+ big blue", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  public void testFilter7() {
    List<Operation> got = toTextOperations("A", "- small", "+ big", "+ blue",  "- red", " castle");
    List<Operation> exp = toTextOperations("A", "- small red", "+ big blue", " castle");
    assertEquivalentOperations(exp, coalesceOperations(got));
  }

  @Test
  public void testFilter8() {
    List<Operation> got = toTextOperations("A", "- small", "+ big", "+ blue",  "- red", " castle");
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
    Assert.assertEquals(normalizeOperations(exp), normalizeOperations(got));
  }

  private static List<Operation> normalizeOperations(List<Operation> operations) {
    return operations.stream().map(CoalescingFilterTest::normalizeOperation).collect(Collectors.toList());
  }

  private static Operation normalizeOperation(Operation operation) {
    if (operation.event() instanceof TextEvent)
      return new Operation(operation.operator(), new CharactersEvent(((TextEvent)operation.event()).getCharacters()));
    return operation;
  }

}
