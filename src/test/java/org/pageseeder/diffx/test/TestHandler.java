/*
 * Copyright (c) 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.test;

import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.OperationsBuffer;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.sequence.XMLSequence;
import org.pageseeder.diffx.token.*;
import org.pageseeder.diffx.token.impl.*;
import org.pageseeder.diffx.xml.NamespaceSet;

import java.util.Arrays;
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
 * @see TestTokens
 */
public final class TestHandler implements DiffHandler<XMLToken> {

  private final NamespaceSet namespaces;

  /**
   * Where the output goes.
   */
  private final StringBuilder out;

  /**
   * Creates a new test formatter
   */
  public TestHandler() {
    this.namespaces = NamespaceSet.noNamespace();
    this.out = new StringBuilder();
  }

  /**
   * Creates a new test formatter
   */
  public TestHandler(NamespaceSet namespaces) {
    this.namespaces = namespaces;
    this.out = new StringBuilder();
  }

  /**
   * Formats the entire sequence by formatting each token.
   *
   * @param seq The token sequence to format
   */
  public static String format(XMLSequence seq) {
    TestHandler handler = new TestHandler();
    for (XMLToken token : seq) {
      handler.handle(Operator.MATCH, token);
    }
    return handler.getOutput();
  }

  /**
   * Formats the entire sequence by formatting each token.
   *
   * @param tokens The tokens to format
   */
  public static String format(List<? extends XMLToken> tokens) {
    TestHandler handler = new TestHandler();
    for (XMLToken token : tokens) {
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
   *
   * @return Its 'abstract' representation or <code>null</code>.
   */
  public static String toSimpleString(Operator operator, XMLToken token) {
    return toSimpleString(operator, token, NamespaceSet.noNamespace());
  }

  /**
   * Returns a simple representation for each code token.
   *
   * <p>This method will return <code>null</code> if it does not know how to format it.
   *
   * @param token The token to format
   *
   * @return Its 'abstract' representation or <code>null</code>.
   */
  public static String toSimpleString(Operator operator, XMLToken token, NamespaceSet namespaces) {
    // an element to open
    if (token instanceof StartElementToken) {
      StartElementToken open = (StartElementToken) token;
      return '<' + getQName(open.getNamespaceURI(), open.getName(), namespaces) + '>';
    }
    // an element to close
    if (token instanceof EndElementToken) {
      EndElementToken close = (EndElementToken) token;
      return "</" + getQName(close.getNamespaceURI(), close.getName(), namespaces) + '>';
    }
    // an element
    if (token instanceof ElementToken) {
      ElementToken element = (ElementToken) token;
      return '<' + getQName(element.getNamespaceURI(), element.getName(), namespaces) + "/>";
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
      if (operator != Operator.MATCH && chars.length() > 1) return "(" + chars + ")";
      return chars;
    }
    // Anything else?
    return token.toString();
  }

  private static String getQName(String uri, String name, NamespaceSet namespaces) {
    if (uri.isEmpty()) return name;
    String prefix = namespaces.getPrefix(uri);
    if (prefix == null) prefix = "{" + uri + "}";
    return prefix.isEmpty() ? name : (prefix + ':' + name);
  }

  public static List<Operation<XMLToken>> parse(String ops) {
    OperationsBuffer<XMLToken> source = new OperationsBuffer<>();
    char[] chars = ops.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      Operator operator = Operator.MATCH;
      if (chars[i] == '+' && (i + 1) < chars.length) {
        operator = Operator.INS;
        i++;
      } else if (chars[i] == '-' && (i + 1) < chars.length) {
        operator = Operator.DEL;
        i++;
      }
      i = parseToken(chars, i, source, operator);
    }
    return source.getOperations();
  }

  private static int parseToken(char[] chars, int i, OperationsBuffer<XMLToken> source, Operator operator) {
    int j = i;
    XMLToken token;
    if (isStartElement(chars, i)) {
      token = new XMLStartElement(Character.toString(chars[i + 1]));
      j = i + 2;
    } else if (isEndElement(chars, i)) {
      token = new XMLEndElement(Character.toString(chars[i + 2]));
      j = i + 3;
    } else if (isText(chars, i)) {
      int to = indexOf(chars, ')', i + 1);
      token = new CharactersToken(new String(Arrays.copyOfRange(chars, i + 1, to)));
      j = to;
    } else if (isAttribute(chars, i)) {
      char name = chars[i + 1];
      if ((i + 3) < chars.length && chars[i + 2] == '=') {
        token = new XMLAttribute(Character.toString(name), Character.toString(chars[i + 3]));
        j = i + 3;
      } else {
        token = new XMLAttribute(Character.toString(name), "");
        j = i + 1;
      }
    } else if (isSpace(chars, i)) {
      token = SpaceToken.getInstance(chars[i]);
    } else {
      token = new CharToken(chars[i]);
    }
    // Add the token and operator
    source.handle(operator, token);
    return j;
  }

  private static int indexOf(char[] chars, char c, int from) {
    for (int i = from; i < chars.length; i++) if (chars[i] == c) return i;
    return -1;
  }

  private static boolean isSpace(char[] chars, int i) {
    return chars[i] == ' ' || chars[i] == '\n' || chars[i] == '\t';
  }

  private static boolean isStartElement(char[] chars, int i) {
    return chars[i] == '<' && (i + 2) < chars.length && chars[i + 2] == '>';
  }

  private static boolean isEndElement(char[] chars, int i) {
    return chars[i] == '<' && (i + 3) < chars.length && chars[i + 1] == '/' && chars[i + 3] == '>';
  }

  private static boolean isAttribute(char[] chars, int i) {
    return chars[i] == '@' && (i + 1) < chars.length;
  }

  private static boolean isText(char[] chars, int i) {
    return chars[i] == '(' && (i + 2) < chars.length && indexOf(chars, ')', i + 1) > i + 2;
  }

  /**
   * Writes the abstract representation.
   */
  public void handle(Operator operator, XMLToken token) {
    if (operator != Operator.MATCH) out.append(operator.toString());
    out.append(toSimpleString(operator, token, this.namespaces));
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
