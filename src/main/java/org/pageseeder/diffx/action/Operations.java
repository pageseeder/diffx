package org.pageseeder.diffx.action;

import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for operations.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class Operations {

  private Operations() {}

  /**
   * Generates the list of events from the list of operations.
   *
   * @param operations  The list of operations.
   * @param positive <code>true</code> for generating the new sequence;
   *                 <code>false</code> for generating the old sequence.
   */
  public static List<DiffXEvent> generate(List<Operation> operations, boolean positive) {
    List<DiffXEvent> generated = new LinkedList<>();
    for (Operation operation : operations) {
      if (positive ? operation.operator() == Operator.INS : operation.operator() == Operator.DEL) {
        generated.add(operation.event());
      } else if (operation.operator() == Operator.KEEP) {
        generated.add(operation.event());
      }
    }
    return generated;
  }

  /**
   * Reverse the operations by swapping the INS and DEL.
   */
  public static List<Operation> reverse(List<Operation> operations) {
    List<Operation> reverse = new ArrayList<>(operations.size());
    for (Operation operation : operations) {
      reverse.add(operation.reverse());
    }
    return reverse;
  }


  /**
   * Apply the specified list of operations to the input sequence and return the corresponding output.
   */
  public static EventSequence apply(EventSequence input, List<Operation> operations) {
    List<DiffXEvent> events = apply(input.events(), operations);
    EventSequence out = new EventSequence(events.size());
    out.addEvents(events);
    return out;
  }

  /**
   * Apply the specified list of action to the input sequence and return the corresponding output.
   */
  public static List<DiffXEvent> apply(List<DiffXEvent> input, List<Operation> operations) {
    List<DiffXEvent> out = new ArrayList<>(input.size());
    int i = 0;
    try {
      for (Operation operation : operations) {
        switch (operation.operator()) {
          case KEEP:
            out.add(input.get(i));
            i++;
            break;
          case INS:
            out.add(operation.event());
            break;
          case DEL:
            i++;
            break;
        }
      }
    } catch (IndexOutOfBoundsException ex) {
      throw new IllegalArgumentException("Actions cannot be applied to specified input", ex);
    }
    if (i != input.size()) {
      throw new IllegalArgumentException("Actions do not match specified input");
    }
    return out;
  }

  public static void format(List<Operation> operations, DiffXFormatter formatter) throws IOException {
    for (Operation operation : operations) {
      switch (operation.operator()) {
        case KEEP:
          formatter.format(operation.event());
          break;
        case INS:
          formatter.insert(operation.event());
          break;
        case DEL:
          formatter.delete(operation.event());
          break;
        default:
      }
    }
  }
}
