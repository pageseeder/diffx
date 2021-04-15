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
import org.pageseeder.diffx.sequence.PrefixMapping;

import java.util.List;

/**
 * A handler implementation used solely for testing.
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

  private final PrefixMapping mapping;

  /**
   * Where the output goes.
   */
  private final StringBuilder out;

  /**
   * Creates a new test formatter
   */
  public TestHandler() {
    this.mapping = PrefixMapping.noNamespace();
    this.out = new StringBuilder();
  }

  /**
   * Creates a new test formatter
   */
  public TestHandler(PrefixMapping mapping) {
    this.mapping = mapping;
    this.out = new StringBuilder();
  }

  /**
   * Writes the abstract representation.
   */
  public void handle(Operator operator, DiffXEvent event) {
    if (operator != Operator.MATCH) out.append(operator.toString());
    out.append(toSimpleString(operator, event, this.mapping));
  }


  // Static helpers -------------------------------------------------------------------

  /**
   * Formats the entire sequence by formatting each event.
   *
   * @param seq The event sequence to format
   */
  public static String format(EventSequence seq) {
    TestHandler handler = new TestHandler();
    for (DiffXEvent event : seq) {
      handler.handle(Operator.MATCH, event);
    }
    return handler.getOutput();
  }

  /**
   * Formats the entire sequence by formatting each event.
   *
   * @param events The events to format
   */
  public static String format(List<? extends DiffXEvent> events) {
    TestHandler handler = new TestHandler();
    for (DiffXEvent event : events) {
      handler.handle(Operator.MATCH, event);
    }
    return handler.getOutput();
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
    return toSimpleString(operator, e, PrefixMapping.noNamespace());
  }

  /**
   * Returns a simple representation for each code event.
   *
   * <p>This method will return <code>null</code> if it does not know how to format it.
   *
   * @param e The event to format
   * @return Its 'abstract' representation or <code>null</code>.
   */
  public static String toSimpleString(Operator operator, DiffXEvent e, PrefixMapping mapping) {
    // an element to open
    if (e instanceof OpenElementEvent) {
      OpenElementEvent open = (OpenElementEvent)e;
      return '<' + getQName(open.getURI(), open.getName(), mapping) + '>';
    }
    // an element to close
    if (e instanceof CloseElementEvent) {
      CloseElementEvent close = (CloseElementEvent)e;
      return "</" + getQName(close.getURI(), close.getName(), mapping) + '>';
    }
    // an element
    if (e instanceof ElementEvent) {
      ElementEvent element = (ElementEvent)e;
      return '<' + getQName(element.getURI(), element.getName(), mapping) + "/>";
    }
    // an attribute
    if (e instanceof AttributeEvent) {
      return "@(" + ((AttributeEvent) e).getName() + '=' + ((AttributeEvent) e).getValue() + ')';
    }
    // a single line
    if (e instanceof LineEvent) return "L" + ((LineEvent) e).getLineNumber();
    if (e instanceof CharEvent) {
      return Character.toString(((CharEvent) e).getChar());
    }
    // a text event
    if (e instanceof TextEvent) {
      String chars = ((TextEvent) e).getCharacters();
      if (operator != Operator.MATCH && chars.length() > 1) return "("+chars+")";
      return chars;
    }
    // Anything else?
    return e.toString();
  }

  private static String getQName(String uri, String name, PrefixMapping mapping) {
    if (uri.isEmpty()) return name;
    String prefix = mapping.getPrefix(uri);
    if (prefix == null) prefix = "{"+uri+"}";
    return prefix.isEmpty() ? name : (prefix+':'+name);
  }

  /**
   * @return The output of the handler.
   */
  public String getOutput() {
    return this.out.toString();
  }

  @Override
  public String toString() {
    return "TestHandler";
  }

}
