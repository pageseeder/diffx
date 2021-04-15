package org.pageseeder.diffx.test;

import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.Actions;
import org.pageseeder.diffx.core.DiffAlgorithm;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.format.SmartXMLFormatter;
import org.pageseeder.diffx.format.XMLDiffXFormatter;
import org.pageseeder.diffx.handler.ActionHandler;
import org.pageseeder.diffx.sequence.PrefixMapping;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * Utility class providing methods for dealing with actions
 */
public final class TestActions {


  public static List<Action> diffToActions(DiffAlgorithm algorithm, List<? extends DiffXEvent> seq1, List<? extends DiffXEvent> seq2) {
    ActionHandler handler = new ActionHandler();
    algorithm.diff(seq1, seq2, handler);
    return handler.getActions();
  }


  public static String toXML(List<Action> actions) {
    try {
      StringWriter xml = new StringWriter();
      XMLDiffXFormatter formatter = new SmartXMLFormatter(xml);
      Actions.format(actions, formatter);
      return xml.toString();
    } catch (IOException ex) {
      // Should not occur
      throw new UncheckedIOException("Unable to check assertions due to", ex);
    }
  }

  public static String toXML(List<Action> actions, PrefixMapping mapping) {
    try {
      StringWriter xml = new StringWriter();
      XMLDiffXFormatter formatter = new SmartXMLFormatter(xml);
      formatter.declarePrefixMapping(mapping);
      Actions.format(actions, formatter);
      return xml.toString();
    } catch (IOException ex) {
      // Should not occur
      throw new UncheckedIOException("Unable to check assertions due to", ex);
    }
  }

}
