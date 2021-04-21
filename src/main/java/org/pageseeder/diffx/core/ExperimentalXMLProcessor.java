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
package org.pageseeder.diffx.core;

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.handler.CoalescingFilter;
import org.pageseeder.diffx.handler.DiffFilter;
import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.Token;
import org.pageseeder.diffx.token.TokenType;
import org.pageseeder.diffx.token.impl.EndElementTokenImpl;
import org.pageseeder.diffx.token.impl.EndElementTokenNSImpl;
import org.pageseeder.diffx.token.impl.StartElementTokenImpl;
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.UncheckedIOException;
import java.util.*;

public class ExperimentalXMLProcessor implements DiffProcessor {

  private boolean coalesce = false;

  /**
   * Set whether to consecutive text operations should be coalesced into a single operation.
   *
   * @param coalesce <code>true</code> to coalesce; <code>false</code> to leave a separate operations.
   */
  public void setCoalesce(boolean coalesce) {
    this.coalesce = coalesce;
  }

  @Override
  public void diff(List<? extends Token> first, List<? extends Token> second, DiffHandler handler) {
    DiffAlgorithm algorithm = new HirschbergAlgorithm();
    DiffHandler actual = new PostXMLFixer(getFilter(handler));
    actual.start();
    algorithm.diff(first, second, actual);
    actual.end();
  }

  private DiffHandler getFilter(DiffHandler handler) {
    return this.coalesce ? new CoalescingFilter(handler) : handler;
  }

  @Override
  public String toString() {
    return "DefaultXMLProcessor{" +
        "coalesce=" + coalesce +
        '}';
  }

  /**
   * A diff filter which attempts to return the insertions and deletions in an order that will result
   * in a valid XML result.
   */
  private static class PostXMLFixer extends DiffFilter {

    public PostXMLFixer(DiffHandler handler) {
      super(handler);
    }

    private final Deque<StartOperation> unclosed = new ArrayDeque<>();

    private final Queue<Token> deletions = new ArrayDeque<>();

    private final Queue<Token> insertions = new ArrayDeque<>();

    private Operator lastOperator = Operator.MATCH;

    private Token lastToken = new VoidToken();

    @Override
    public void handle(Operator operator, Token token) throws UncheckedIOException, IllegalStateException {
      if (operator == Operator.DEL) {
        this.deletions.add(token);
      } else if (operator == Operator.INS) {
        this.insertions.add(token);
      } else {
        flushChanges();
        if (token.getType() == TokenType.END_ELEMENT && !matchStart(Operator.MATCH, (EndElementToken)token)) {
          sendMatchingEndElement();
        } else {
          send(operator, token);
        }
      }
    }

    private void flushChanges() {
      while (!this.insertions.isEmpty() || !this.deletions.isEmpty()) {

        // Flush attributes if the last token sent was an open element or attribute
        if (this.lastToken.getType() == TokenType.START_ELEMENT || this.lastToken.getType() == TokenType.ATTRIBUTE) {
          while (isAttribute(this.insertions.peek())) {
            send(Operator.INS, this.insertions.remove());
          }
          while (isAttribute(this.deletions.peek())) {
            send(Operator.DEL, this.deletions.remove());
          }
        }

        // At this point there are no attributes left, tokens can only be START_ELEMENT, END_ELEMENT, TEXT, and OTHER
        Token nextInsertion = this.insertions.peek();
        Token nextDeletion = this.deletions.peek();

        if (isEndElement(nextInsertion) && matchStart(Operator.INS, (EndElementToken)nextInsertion)) {
          send(Operator.INS, this.insertions.remove());
        } else if (isEndElement(nextDeletion) && matchStart(Operator.DEL, (EndElementToken)nextDeletion)) {
          send(Operator.DEL, this.deletions.remove());
        } else if (isEndElement(nextInsertion)) {
          sendMatchingEndElement();
          this.insertions.remove();
        } else if (isEndElement(nextDeletion)) {
          sendMatchingEndElement();
          this.deletions.remove();
        } else {
          if (this.lastOperator == Operator.INS && nextInsertion != null) send(Operator.INS, this.insertions.remove());
          else if (this.lastOperator == Operator.DEL && nextDeletion != null) send(Operator.DEL, this.deletions.remove());
          else if (nextInsertion != null) send(Operator.INS, this.insertions.remove());
          else if (nextDeletion != null) send(Operator.DEL, this.deletions.remove());
        }

      }
    }

    private static boolean isEndElement(Token token) {
      return token != null && token.getType() == TokenType.END_ELEMENT;
    }

    private static boolean isAttribute(Token token) {
      return token != null && token.getType() == TokenType.ATTRIBUTE;
    }

    private boolean matchStart(Operator operator, EndElementToken token) {
      StartOperation op = this.unclosed.peek();
      if (op == null) return false;
      return op.operator == operator && token.match(op.token);
    }

    /**
     * We ignore the reported end element token and send the matching end element token
     */
    private void sendMatchingEndElement() {
      StartOperation lastStart = this.unclosed.peek();
      if (lastStart != null) {
        EndElementToken end = toEndElementToken(lastStart.token);
        send(lastStart.operator, end);
      }
    }

    private EndElementToken toEndElementToken(StartElementToken token) {
      if (token instanceof StartElementTokenImpl)
        return new EndElementTokenImpl(token);
      return new EndElementTokenNSImpl(token);
    }

    private void send(Operator operator, Token token) {
      this.target.handle(operator, token);
      this.lastOperator = operator;
      this.lastToken = token;
      if (token.getType() == TokenType.START_ELEMENT) {
        this.unclosed.push(new StartOperation(operator, (StartElementToken)token));
      } else if (token.getType() == TokenType.END_ELEMENT) {
        this.unclosed.pop();
      }
    }

    @Override
    public void end() {
      this.flushChanges();
    }
  }

  private static class StartOperation {
    private final Operator operator;
    private final StartElementToken token;
    StartOperation(Operator operator, StartElementToken token) {
      this.operator = operator;
      this.token = token;
    }
    public Operator operator() {
      return operator;
    }
    public StartElementToken token() {
      return token;
    }
  }

  private static class VoidToken implements Token {
    @Override
    public TokenType getType() {
      return TokenType.OTHER;
    }
    @Override
    public boolean equals(Token token) {
      return token == this;
    }
    @Override
    public StringBuffer toXML(StringBuffer xml) {
      return xml;
    }
    public String toXML() {
      return "";
    }
    @Override
    public void toXML(XMLWriter xml) {
    }
  }

}
