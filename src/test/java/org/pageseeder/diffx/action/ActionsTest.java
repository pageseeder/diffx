package org.pageseeder.diffx.action;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ActionsTest {

  @Test
  void testApply_WithValidActions() {
    List<String> input = List.of("match1", "match2", "del1");
    List<Action<String>> actions = new ArrayList<>();
    actions.add(new Action<>(Operator.MATCH, List.of("match1")));
    actions.add(new Action<>(Operator.MATCH, List.of("match2")));
    actions.add(new Action<>(Operator.INS, List.of("ins1", "ins2")));
    actions.add(new Action<>(Operator.DEL, List.of("del1")));

    List<String> result = Actions.apply(input, actions);

    List<String> expected = List.of("match1", "match2", "ins1", "ins2");
    assertEquals(expected, result, "The apply method should correctly process MATCH, INS, and skip DEL actions.");
  }

  @Test
  void testApply_WithMismatchedActionsAndInput() {
    List<String> input = List.of("match1", "match2");
    List<Action<String>> actions = new ArrayList<>();
    actions.add(new Action<>(Operator.MATCH, List.of("match1")));
    actions.add(new Action<>(Operator.MATCH, List.of("match3"))); // Mismatched token here
    actions.add(new Action<>(Operator.DEL, List.of("del1")));
    assertThrows(IllegalArgumentException.class, () -> Actions.apply(input, actions));
  }

  @Test
  void testApply_WithEmptyInputAndActions() {
    List<String> input = Collections.emptyList();
    List<Action<String>> actions = Collections.emptyList();

    List<String> result = Actions.apply(input, actions);

    assertTrue(result.isEmpty(), "The apply method should return an empty list when both input and actions are empty.");
  }

  @Test
  void testFlip_WithMixedOperators() {
    List<Action<String>> actions = new ArrayList<>();
    actions.add(new Action<>(Operator.MATCH, List.of("match1")));
    actions.add(new Action<>(Operator.INS, List.of("ins1", "ins2")));
    actions.add(new Action<>(Operator.DEL, List.of("del1", "del2")));

    List<Action<String>> flipped = Actions.flip(actions);

    List<Action<String>> expected = List.of(
        new Action<>(Operator.MATCH, List.of("match1")),
        new Action<>(Operator.DEL, List.of("ins1", "ins2")),
        new Action<>(Operator.INS, List.of("del1", "del2"))
    );

    assertEquals(expected, flipped, "Flipped actions list should correctly reverse INS and DEL operators.");
  }

  @Test
  void testFlip_WithEmptyActions() {
    List<Action<String>> actions = Collections.emptyList();

    List<Action<String>> flipped = Actions.flip(actions);

    assertTrue(flipped.isEmpty(), "Flipped actions list should be empty when the input list is empty.");
  }

  @Test
  void testFlip_WithSingleOperator() {
    List<Action<String>> actions = new ArrayList<>();
    actions.add(new Action<>(Operator.INS, List.of("ins1", "ins2")));
    actions.add(new Action<>(Operator.INS, List.of("ins3")));

    List<Action<String>> flipped = Actions.flip(actions);

    List<Action<String>> expected = List.of(
        new Action<>(Operator.DEL, List.of("ins1", "ins2")),
        new Action<>(Operator.DEL, List.of("ins3"))
    );

    assertEquals(expected, flipped, "Flipped actions list should correctly reverse all operators of type INS to DEL.");
  }

  @Test
  void testToOperations_WithMixedOperators() {
    List<Action<String>> actions = new ArrayList<>();
    actions.add(new Action<>(Operator.MATCH, List.of("match1")));
    actions.add(new Action<>(Operator.INS, List.of("ins1", "ins2")));
    actions.add(new Action<>(Operator.DEL, List.of("del1", "del2")));

    List<Operation<String>> operations = Actions.toOperations(actions);

    List<Operation<String>> expected = List.of(
        new Operation<>(Operator.MATCH, "match1"),
        new Operation<>(Operator.INS, "ins1"),
        new Operation<>(Operator.INS, "ins2"),
        new Operation<>(Operator.DEL, "del1"),
        new Operation<>(Operator.DEL, "del2")
    );

    assertEquals(expected, operations, "Operations list should match the expected operations.");
  }

  @Test
  void testToOperations_WithEmptyActions() {
    List<Action<String>> actions = Collections.emptyList();

    List<Operation<String>> operations = Actions.toOperations(actions);

    assertTrue(operations.isEmpty(), "Operations list should be empty when actions list is empty.");
  }

  @Test
  void testToOperations_WithSingleOperator() {
    List<Action<String>> actions = new ArrayList<>();
    actions.add(new Action<>(Operator.MATCH, List.of("a", "b")));
    actions.add(new Action<>(Operator.MATCH, List.of("c", "d")));

    List<Operation<String>> operations = Actions.toOperations(actions);

    List<Operation<String>> expected = List.of(
        new Operation<>(Operator.MATCH, "a"),
        new Operation<>(Operator.MATCH, "b"),
        new Operation<>(Operator.MATCH, "c"),
        new Operation<>(Operator.MATCH, "d")
    );

    assertEquals(expected, operations, "All tokens should be correctly converted into operations with the MATCH operator.");
  }

  /**
   * Tests for {@link Actions#generate(List, boolean)}.
   * <p>
   * The method processes a list of actions and returns tokens based on the operator
   * (MATCH, INS, DEL). When `forward` is true, only MATCH or INS tokens are returned.
   * When `forward` is false, only MATCH or DEL tokens are returned.
   */

  @Test
  void testGenerate_WithForwardTrue() {
    List<Action<String>> actions = new ArrayList<>();
    Action<String> matchAction = new Action<>(Operator.MATCH, List.of("a", "b"));
    Action<String> insertAction = new Action<>(Operator.INS, List.of("c"));
    Action<String> deleteAction = new Action<>(Operator.DEL, List.of("d"));
    actions.add(matchAction);
    actions.add(insertAction);
    actions.add(deleteAction);

    List<String> result = Actions.generate(actions, true);

    assertEquals(List.of("a", "b", "c"), result, "Generated list should include MATCH and INS tokens when forward is true.");
  }

  @Test
  void testGenerate_WithForwardFalse() {
    List<Action<String>> actions = new ArrayList<>();
    Action<String> matchAction = new Action<>(Operator.MATCH, List.of("x", "y", "z"));
    Action<String> insertAction = new Action<>(Operator.INS, List.of("m", "n"));
    Action<String> deleteAction = new Action<>(Operator.DEL, List.of("p", "q"));
    actions.add(matchAction);
    actions.add(insertAction);
    actions.add(deleteAction);

    List<String> result = Actions.generate(actions, false);

    assertEquals(List.of("x", "y", "z", "p", "q"), result, "Generated list should include MATCH and DEL tokens when forward is false.");
  }

  @Test
  void testGenerate_WithEmptyActions() {
    List<Action<String>> actions = Collections.emptyList();

    List<String> resultForward = Actions.generate(actions, true);
    List<String> resultBackward = Actions.generate(actions, false);

    assertTrue(resultForward.isEmpty(), "Generated list should be empty when actions list is empty and forward is true.");
    assertTrue(resultBackward.isEmpty(), "Generated list should be empty when actions list is empty and forward is false.");
  }

  @Test
  void testGenerate_WithOnlyMatch() {
    List<Action<String>> actions = new ArrayList<>();
    Action<String> matchAction = new Action<>(Operator.MATCH, List.of("match1", "match2"));
    actions.add(matchAction);

    List<String> resultForward = Actions.generate(actions, true);
    List<String> resultBackward = Actions.generate(actions, false);

    assertEquals(List.of("match1", "match2"), resultForward, "Generated list should include all MATCH tokens when forward is true.");
    assertEquals(List.of("match1", "match2"), resultBackward, "Generated list should include all MATCH tokens when forward is false.");
  }

  @Test
  void testGenerate_WithOnlyInsert() {
    List<Action<String>> actions = new ArrayList<>();
    Action<String> insertAction = new Action<>(Operator.INS, List.of("ins1", "ins2"));
    actions.add(insertAction);

    List<String> resultForward = Actions.generate(actions, true);
    List<String> resultBackward = Actions.generate(actions, false);

    assertEquals(List.of("ins1", "ins2"), resultForward, "Generated list should include all INS tokens when forward is true.");
    assertTrue(resultBackward.isEmpty(), "Generated list should be empty for INS tokens when forward is false.");
  }

  @Test
  void testGenerate_WithOnlyDelete() {
    List<Action<String>> actions = new ArrayList<>();
    Action<String> deleteAction = new Action<>(Operator.DEL, List.of("del1", "del2"));
    actions.add(deleteAction);

    List<String> resultForward = Actions.generate(actions, true);
    List<String> resultBackward = Actions.generate(actions, false);

    assertTrue(resultForward.isEmpty(), "Generated list should be empty for DEL tokens when forward is true.");
    assertEquals(List.of("del1", "del2"), resultBackward, "Generated list should include all DEL tokens when forward is false.");
  }

  // --- Tests for isApplicable ---

  @Test
  void testIsApplicable_WithValidActions() {
    List<String> a = List.of("a", "b", "c");
    List<String> b = List.of("a", "x", "c");
    List<Action<String>> actions = List.of(
        new Action<>(Operator.MATCH, List.of("a")),
        new Action<>(Operator.DEL, List.of("b")),
        new Action<>(Operator.INS, List.of("x")),
        new Action<>(Operator.MATCH, List.of("c"))
    );
    assertTrue(Actions.isApplicable(a, b, actions));
  }

  @Test
  void testIsApplicable_WithEmptyInputsAndActions() {
    assertTrue(Actions.isApplicable(List.of(), List.of(), List.of()));
  }

  @Test
  void testIsApplicable_MatchMismatchInA() {
    List<String> a = List.of("a");
    List<String> b = List.of("a");
    List<Action<String>> actions = List.of(
        new Action<>(Operator.MATCH, List.of("b"))
    );
    assertFalse(Actions.isApplicable(a, b, actions));
  }

  @Test
  void testIsApplicable_MatchMismatchInB() {
    List<String> a = List.of("a");
    List<String> b = List.of("x");
    List<Action<String>> actions = List.of(
        new Action<>(Operator.MATCH, List.of("a"))
    );
    assertFalse(Actions.isApplicable(a, b, actions));
  }

  @Test
  void testIsApplicable_DelMismatch() {
    List<String> a = List.of("a");
    List<String> b = List.of();
    List<Action<String>> actions = List.of(
        new Action<>(Operator.DEL, List.of("b"))
    );
    assertFalse(Actions.isApplicable(a, b, actions));
  }

  @Test
  void testIsApplicable_InsMismatch() {
    List<String> a = List.of();
    List<String> b = List.of("a");
    List<Action<String>> actions = List.of(
        new Action<>(Operator.INS, List.of("b"))
    );
    assertFalse(Actions.isApplicable(a, b, actions));
  }

  @Test
  void testIsApplicable_OverflowA() {
    List<String> a = List.of();
    List<String> b = List.of();
    List<Action<String>> actions = List.of(
        new Action<>(Operator.DEL, List.of("a"))
    );
    assertFalse(Actions.isApplicable(a, b, actions));
  }

  @Test
  void testIsApplicable_OverflowB() {
    List<String> a = List.of();
    List<String> b = List.of();
    List<Action<String>> actions = List.of(
        new Action<>(Operator.INS, List.of("a"))
    );
    assertFalse(Actions.isApplicable(a, b, actions));
  }

  @Test
  void testIsApplicable_AllInserts() {
    List<String> a = List.of();
    List<String> b = List.of("x", "y");
    List<Action<String>> actions = List.of(
        new Action<>(Operator.INS, List.of("x", "y"))
    );
    assertTrue(Actions.isApplicable(a, b, actions));
  }

  @Test
  void testIsApplicable_AllDeletes() {
    List<String> a = List.of("x", "y");
    List<String> b = List.of();
    List<Action<String>> actions = List.of(
        new Action<>(Operator.DEL, List.of("x", "y"))
    );
    assertTrue(Actions.isApplicable(a, b, actions));
  }

  // --- Tests for handle ---

  @Test
  void testHandle_WithMixedActions() {
    List<Action<String>> actions = List.of(
        new Action<>(Operator.MATCH, List.of("a")),
        new Action<>(Operator.INS, List.of("b", "c")),
        new Action<>(Operator.DEL, List.of("d"))
    );
    List<Map.Entry<Operator, String>> received = new ArrayList<>();
    DiffHandler<String> handler = (op, token) -> received.add(new SimpleEntry<>(op, token));

    Actions.handle(actions, handler);

    assertEquals(List.of(
        new SimpleEntry<>(Operator.MATCH, "a"),
        new SimpleEntry<>(Operator.INS, "b"),
        new SimpleEntry<>(Operator.INS, "c"),
        new SimpleEntry<>(Operator.DEL, "d")
    ), received);
  }

  @Test
  void testHandle_WithEmptyActions() {
    List<Map.Entry<Operator, String>> received = new ArrayList<>();
    DiffHandler<String> handler = (op, token) -> received.add(new SimpleEntry<>(op, token));

    Actions.handle(Collections.emptyList(), handler);

    assertTrue(received.isEmpty());
  }

  // --- Tests for applyTo ---

  @Test
  void testApplyTo_CallsStartHandleEnd() {
    List<Action<String>> actions = List.of(
        new Action<>(Operator.MATCH, List.of("a")),
        new Action<>(Operator.INS, List.of("b"))
    );
    List<String> events = new ArrayList<>();
    DiffHandler<String> handler = new DiffHandler<>() {
      @Override public void start() { events.add("START"); }
      @Override public void handle(Operator operator, String token) { events.add(operator + ":" + token); }
      @Override public void end() { events.add("END"); }
    };

    Actions.applyTo(actions, handler);

    assertEquals(List.of("START", "=:a", "+:b", "END"), events);
  }

  @Test
  void testApplyTo_WithEmptyActions() {
    List<String> events = new ArrayList<>();
    DiffHandler<String> handler = new DiffHandler<>() {
      @Override public void start() { events.add("START"); }
      @Override public void handle(Operator operator, String token) { events.add(operator + ":" + token); }
      @Override public void end() { events.add("END"); }
    };

    Actions.applyTo(Collections.emptyList(), handler);

    assertEquals(List.of("START", "END"), events);
  }
}