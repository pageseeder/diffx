package org.pageseeder.diffx.format;

import org.junit.Assert;
import org.junit.Test;
import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.OperationFormatter;
import org.pageseeder.diffx.action.Operations;
import org.pageseeder.diffx.event.TextEvent;
import org.pageseeder.diffx.event.impl.CharactersEvent;
import org.pageseeder.diffx.event.impl.SpaceEvent;
import org.pageseeder.diffx.event.impl.WordEvent;
import org.pageseeder.diffx.test.Events;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
  public void testFilter1() throws IOException {
    List<Operation> got = coalesceOperations("A", " ", "+big", " ", "castle");
    List<Operation> exp = asTextOperations("A ", "+big", " castle");
    assertEquivalentOperations(exp, got);
  }

  @Test
  public void testFilter2() throws IOException {
    List<Operation> got = coalesceOperations("The", "+ very", "+ big", "- large", "- medieval", " castle");
    List<Operation> exp = asTextOperations("The", "+ very big", "- large medieval", " castle");
    assertEquivalentOperations(exp, got);
  }

  @Test
  public void testFilter3() throws IOException {
    List<Operation> got = coalesceOperations("-A", "+The", "+ very", "+ big", " castle");
    List<Operation> exp = asTextOperations("-A", "+The very big", " castle");
    assertEquivalentOperations(exp, got);
  }

  // Private helpers
  // --------------------------------------------------------------------------

  private List<Operation> coalesceOperations(String... ops) throws IOException{
    List<Operation> operations = asTextOperations(ops);
    OperationFormatter target = new OperationFormatter();
    CoalescingFilter filter = new CoalescingFilter(target);
    Operations.format(operations, filter);
    filter.flushText();
    return target.getOperations();
  }

  private List<Operation> asTextOperations(String... ops) {
    OperationFormatter source = new OperationFormatter();
    for (String op : ops) {
      if (op.startsWith("+")) source.insert(Events.toTextEvent(op.substring(1)));
      else if (op.startsWith("-")) source.delete(Events.toTextEvent(op.substring(1)));
      else source.format(Events.toTextEvent(op));
    }
    return source.getOperations();
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
