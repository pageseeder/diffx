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
package org.pageseeder.diffx.test;

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.event.*;
import org.pageseeder.diffx.event.impl.CharEvent;
import org.pageseeder.diffx.event.impl.LineEvent;
import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;
import java.io.StringWriter;

/**
 * A Diff-X formatter implementation used solely for testing.
 *
 * <p>This formatter which write exactly what receives using the abstract representation of
 * each event and adding a plus / minus sign for insertions / deletion. This class is useful
 * to test the output of an algorithm.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @see Events
 */
public final class TestHandler implements DiffHandler {

  /**
   * Where the output goes.
   */
  private final StringBuilder out;

  /**
   * Creates a new test formatter
   */
  public TestHandler() {
    this.out = new StringBuilder();
  }

  /**
   * Writes the abstract representation.
   */
  public void handle(Operator operator, DiffXEvent event) {
    if (operator != Operator.KEEP) out.append(operator.toString());
    out.append(toSimpleString(operator, event));
  }

  /**
   * Formats the entire sequence by formatting each event.
   *
   * @param seq The event sequence to format
   * @throws IOException Should an I/O exception be thrown by the <code>format</code> method.
   */
  public void format(EventSequence seq) throws IOException {
    for (int i = 0; i < seq.size(); i++) {
      handle(Operator.KEEP, seq.getEvent(i));
    }
  }

  /**
   * Returns a simple representation for each code event.
   *
   * <p>This method will return <code>null</code> if it does not know how to format it.
   *
   * @param e The event to format
   * @return Its 'abstract' representation or <code>null</code>.
   */
  public static String toSimpleString(Operator operator, DiffXEvent e) {
    // an element to open
    if (e instanceof OpenElementEvent) return '<' + ((OpenElementEvent) e).getName() + '>';
    // an element to close
    if (e instanceof CloseElementEvent) return "</" + ((CloseElementEvent) e).getName() + '>';
    // an element
    if (e instanceof ElementEvent) return '<' + ((ElementEvent) e).getName() + "/>";
    // an attribute
    if (e instanceof AttributeEvent)
      return "@(" + ((AttributeEvent) e).getName() + '=' + ((AttributeEvent) e).getValue() + ')';
    // a single line
    if (e instanceof LineEvent) return "L" + ((LineEvent) e).getLineNumber();
    // a text event
    if (e instanceof TextEvent) {
      if (operator != Operator.KEEP) return "("+((TextEvent) e).getCharacters()+")";
      return ((TextEvent) e).getCharacters();
    }
    if (e instanceof CharEvent) {
      return Character.toString(((CharEvent) e).getChar());
    }
    // Anything else?
    return e.toString();
  }

  /**
   * @return The output of the handler.
   */
  public String getOutput() {
    return this.out.toString();
  }

}
