package org.pageseeder.diffx.test;

import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.Actions;
import org.pageseeder.diffx.format.SmartXMLFormatter;
import org.pageseeder.diffx.format.XMLDiffXFormatter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * Utility class providing methods for dealing with actions
 */
public final class TestActions {

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

}
