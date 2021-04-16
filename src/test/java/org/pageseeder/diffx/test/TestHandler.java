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
import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.sequence.EventSequence;
import org.pageseeder.diffx.sequence.PrefixMapping;
import org.pageseeder.diffx.token.*;
import org.pageseeder.diffx.token.impl.CharToken;
import org.pageseeder.diffx.token.impl.LineToken;

import java.util.List;

/**
 * A handler implementation used solely for testing.
 *
 * <p>This formatter which write exactly what receives using the abstract representation of
 * each token and adding a plus / minus sign for insertions / deletion. This class is useful
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
  public void handle(Operator operator, Token token) {
    if (operator != Operator.MATCH) out.append(operator.toString());
    out.append(toSimpleString(operator, token, this.mapping));
  }


  // Static helpers -------------------------------------------------------------------

  /**
   * Formats the entire sequence by formatting each token.
   *
   * @param seq The token sequence to format
   */
  public static String format(EventSequence seq) {
    TestHandler handler = new TestHandler();
    for (Token token : seq) {
      handler.handle(Operator.MATCH, token);
    }
    return handler.getOutput();
  }

  /**
   * Formats the entire sequence by formatting each token.
   *
   * @param tokens The tokens to format
   */
  public static String format(List<? extends Token> tokens) {
    TestHandler handler = new TestHandler();
    for (Token token : tokens) {
      handler.handle(Operator.MATCH, token);
    }
    return handler.getOutput();
  }

  /**
   * Returns a simple representation for each code token.
   *
   * <p>This method will return <code>null</code> if it does not know how to format it.
   *
   * @param token The token to format
   * @return Its 'abstract' representation or <code>null</code>.
   */
  public static String toSimpleString(Operator operator, Token token) {
    return toSimpleString(operator, token, PrefixMapping.noNamespace());
  }

  /**
   * Returns a simple representation for each code token.
   *
   * <p>This method will return <code>null</code> if it does not know how to format it.
   *
   * @param token The token to format
   * @return Its 'abstract' representation or <code>null</code>.
   */
  public static String toSimpleString(Operator operator, Token token, PrefixMapping mapping) {
    // an element to open
    if (token instanceof StartElementToken) {
      StartElementToken open = (StartElementToken)token;
      return '<' + getQName(open.getURI(), open.getName(), mapping) + '>';
    }
    // an element to close
    if (token instanceof EndElementToken) {
      EndElementToken close = (EndElementToken)token;
      return "</" + getQName(close.getURI(), close.getName(), mapping) + '>';
    }
    // an element
    if (token instanceof ElementToken) {
      ElementToken element = (ElementToken)token;
      return '<' + getQName(element.getURI(), element.getName(), mapping) + "/>";
    }
    // an attribute
    if (token instanceof AttributeToken) {
      return "@(" + ((AttributeToken) token).getName() + '=' + ((AttributeToken) token).getValue() + ')';
    }
    // a single line
    if (token instanceof LineToken) return "L" + ((LineToken) token).getLineNumber();
    if (token instanceof CharToken) {
      return Character.toString(((CharToken) token).getChar());
    }
    // a text token
    if (token instanceof TextToken) {
      String chars = ((TextToken) token).getCharacters();
      if (operator != Operator.MATCH && chars.length() > 1) return "("+chars+")";
      return chars;
    }
    // Anything else?
    return token.toString();
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
