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
package org.pageseeder.diffx.handler;

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.XMLTokenType;
import org.pageseeder.diffx.token.impl.XMLEndElement;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.stream.XMLStreamWriter;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

/**
 * A diff filter which attempts to return the insertions and deletions in an order that will result
 * in a valid XML result.
 *
 * @author Christophe Lauret
 * @version 1.0.1
 * @since 0.9.0
 */
public final class PostXMLFixer extends DiffFilter<XMLToken> {

  private static final XMLToken NIL = new NilToken();

  public PostXMLFixer(DiffHandler<XMLToken> handler) {
    super(handler);
  }

  /**
   * Keeps track of start elements tokens without a matching end element.
   */
  private final Deque<Operation<StartElementToken>> unclosed = new ArrayDeque<>();

  /**
   * Deletions from the current list of successive edits.
   */
  private final Queue<XMLToken> deletions = new ArrayDeque<>();

  /**
   * Insertions from the current list of successive edits.
   */
  private final Queue<XMLToken> insertions = new ArrayDeque<>();

  /**
   * Last operator used (never null)
   */
  private Operator lastOperator = Operator.MATCH;

  /**
   * Last token (never null)
   */
  private XMLToken lastToken = NIL;

  /**
   * Flag indicating when the handler is unable to fix the XML.
   */
  private boolean hasError = false;

  @Override
  public void handle(@NotNull Operator operator, @NotNull XMLToken token) throws UncheckedIOException, IllegalStateException {
    if (operator == Operator.DEL) {
      this.deletions.add(token);
    } else if (operator == Operator.INS) {
      this.insertions.add(token);
    } else {
      flushChanges();
      if (token.getType() == XMLTokenType.END_ELEMENT && !matchStart(Operator.MATCH, (EndElementToken) token)) {
        sendMatchingEndElement();
      } else {
        send(operator, token);
      }
    }
  }

  private void flushChanges() {
    while (!this.insertions.isEmpty() || !this.deletions.isEmpty()) {

      // Flush attributes if the last token sent was an open element or attribute
      if (this.lastToken.getType() == XMLTokenType.START_ELEMENT || this.lastToken.getType() == XMLTokenType.ATTRIBUTE) {
        while (isAttribute(this.deletions.peek())) {
          send(Operator.DEL, this.deletions.remove());
        }
        while (isAttribute(this.insertions.peek())) {
          send(Operator.INS, this.insertions.remove());
        }
      }

      // At this point there are no attributes left, tokens can only be START_ELEMENT, END_ELEMENT, TEXT, and OTHER
      XMLToken nextInsertion = this.insertions.peek();
      XMLToken nextDeletion = this.deletions.peek();

      if (isEndElement(nextDeletion) && matchStart(Operator.DEL, (EndElementToken) nextDeletion)) {
        send(Operator.DEL, this.deletions.remove());
      } else if (isEndElement(nextInsertion) && matchStart(Operator.INS, (EndElementToken) nextInsertion)) {
        send(Operator.INS, this.insertions.remove());
      } else if (isEndElement(nextDeletion)) {
        this.hasError = true;
        sendMatchingEndElement();
        this.deletions.remove();
      } else if (isEndElement(nextInsertion)) {
        this.hasError = true;
        sendMatchingEndElement();
        this.insertions.remove();
      } else {
        if (this.lastOperator == Operator.DEL && nextDeletion != null)
          send(Operator.DEL, this.deletions.remove());
        else if (this.lastOperator == Operator.INS && nextInsertion != null)
          send(Operator.INS, this.insertions.remove());
        else if (nextDeletion != null)
          send(Operator.DEL, this.deletions.remove());
        else if (nextInsertion != null)
          send(Operator.INS, this.insertions.remove());
      }
    }
  }

  public boolean hasError() {
    return this.hasError;
  }

  private static boolean isEndElement(XMLToken token) {
    return token != null && token.getType() == XMLTokenType.END_ELEMENT;
  }

  private static boolean isAttribute(XMLToken token) {
    return token != null && token.getType() == XMLTokenType.ATTRIBUTE;
  }

  private boolean matchStart(Operator operator, EndElementToken token) {
    Operation<StartElementToken> op = this.unclosed.peek();
    if (op == null) return false;
    return op.operator() == operator && token.match(op.token());
  }

  /**
   * We ignore the reported end element token and send the matching end element token
   */
  private void sendMatchingEndElement() {
    Operation<StartElementToken> lastStart = this.unclosed.peek();
    if (lastStart != null) {
      EndElementToken end = toEndElementToken(lastStart.token());
      send(lastStart.operator(), end);
    }
  }

  private EndElementToken toEndElementToken(StartElementToken token) {
    return new XMLEndElement(token);
  }

  private void send(Operator operator, XMLToken token) {
    this.target.handle(operator, token);
    this.lastOperator = operator;
    this.lastToken = token;
    if (token.getType() == XMLTokenType.START_ELEMENT) {
      this.unclosed.push(new Operation<>(operator, (StartElementToken) token));
    } else if (token.getType() == XMLTokenType.END_ELEMENT) {
      this.unclosed.pop();
    }
  }

  @Override
  public void end() {
    flushChanges();
    // May be necessary if some unclosed unchanged element remains
    while (!this.unclosed.isEmpty()) {
      sendMatchingEndElement();
    }
  }

  private static class NilToken implements XMLToken {

    @Override
    public @NotNull XMLTokenType getType() {
      return XMLTokenType.OTHER;
    }

    @Override
    public boolean equals(XMLToken token) {
      return token == this;
    }

    @Override
    public void toXML(@NotNull XMLWriter xml) {
    }

    @Override
    public void toXML(@NotNull XMLStreamWriter xml) {
    }

    @Override
    public @NotNull String getName() {
      return "";
    }

    @Override
    public String getValue() {
      return null;
    }
  }

}

