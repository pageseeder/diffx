package org.pageseeder.diffx.core;

import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.handler.ActionHandler;

import java.util.List;

public abstract class BaseProcessorTest {

  /**
   * @return The processor instance to use for texting.
   */
  public abstract DiffAlgorithm getDiffAlgorithm();

  public List<Action> diffToActions(List<? extends DiffXEvent> seq1, List<? extends DiffXEvent> seq2) {
    DiffAlgorithm processor = getDiffAlgorithm();
    ActionHandler handler = new ActionHandler();
    processor.diff(seq1, seq2, handler);
    return handler.getActions();
  }

  /**
   * Print the error details.
   */
  protected void printXMLErrorDetails(String xml1, String xml2, String[] exp, String got, List<Action> actions) {
    System.err.println("+------------------------------------------------");
    System.err.println("| Input A: \"" + xml1 + "\"");
    System.err.println("| Input B: \"" + xml2 + "\"");
    System.err.println("| Output:  \"" + got + "\"");
    System.err.print("| Expect:  ");
    for (String s : exp) System.err.print("\"" + s + "\" ");
    System.err.println();
    System.err.print("| Actions: ");
    for (Action action : actions) {
      System.err.print(action.operator() == Operator.DEL ? '-' : action.operator() == Operator.INS ? '+' : '=');
      System.err.print(action.events());
    }
    System.err.println();
  }

}
