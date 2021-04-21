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
package org.pageseeder.diffx.format;

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.sequence.PrefixMapping;
import org.pageseeder.diffx.token.*;
import org.pageseeder.diffx.util.Constants;
import org.pageseeder.diffx.util.Formatting;
import org.pageseeder.xmlwriter.XMLWriterNSImpl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Stack;

/**
 * A XML formatter that provides a convenient XML formatting.
 *
 * <p>Nodes that have not changed are kept the way they are.
 *
 * <p>Elements that have been modified
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class ConvenientXMLDiffOutput implements XMLDiffOutput {

  // class attributes ---------------------------------------------------------------------------

  /**
   * The output goes here.
   */
  private final XMLWriterNSImpl xml;

  /**
   * The DiffX configuration to use
   */
  private DiffXConfig config = new DiffXConfig();

  /**
   * The prefix mapping
   */
  private PrefixMapping mapping = null;

  // state variables ----------------------------------------------------------------------------

  /**
   * Set to <code>true</code> to include the XML declaration. This attribute is
   * set to <code>false</code> when the {@link #setWriteXMLDeclaration(boolean)}
   * is called with <code>false</code> or once the XML declaration has been written.
   */
  private transient boolean writeXMLDeclaration = true;

  /**
   * Indicates whether the XML writer has been setup already.
   */
  private transient boolean isSetup = false;

  /**
   * Used to know if all elements have been closed, in which case the namespace
   * mapping should be redeclared before opening a new element
   */
  private int openElements = 0;

  /**
   * Indicates whether some text is being inserted or removed.
   */
  private Operator textFormat = Operator.MATCH;

  /**
   * A stack of attributes to insert.
   */
  private final transient Stack<AttributeToken> insAttributes = new Stack<>();

  /**
   * A stack of attributes to delete.
   */
  private final transient Stack<AttributeToken> delAttributes = new Stack<>();

  /**
   * Creates a new formatter using the specified writer.
   *
   * @param w The writer to use.
   *
   * @throws NullPointerException If the specified writer is <code>null</code>.
   */
  public ConvenientXMLDiffOutput(Writer w) throws NullPointerException {
    if (w == null)
      throw new NullPointerException("The XML formatter requires a writer");
    this.xml = new XMLWriterNSImpl(w, false);
  }

  @Override
  public void start() {
    if (!this.isSetup) {
      try {
        setUpXML();
      } catch (IOException ex) {
        throw new UncheckedIOException(ex);
      }
    }
  }

  @Override
  public void handle(Operator operator, Token token) throws UncheckedIOException, IllegalStateException {
    try {
      if (operator == Operator.MATCH) handleMatch(token);
      else handleChange(operator, token);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  private void handleMatch(Token token) throws IOException {
    if (!this.isSetup) {
      setUpXML();
    }
    endTextChange();
    if (!(token instanceof AttributeToken)) {
      flushAttributes();
    }
    // namespaces declaration
    if (token instanceof StartElementToken) {
      if (this.openElements == 0) Formatting.declareNamespaces(this.xml, this.mapping);
      this.openElements++;
    } else if (token instanceof EndElementToken) {
      this.openElements--;
    }
    token.toXML(this.xml);
    if (token instanceof TextToken)
      if (this.config.isIgnoreWhiteSpace() && !this.config.isPreserveWhiteSpace()) {
        this.xml.writeXML(" ");
      }
    this.xml.flush();
  }

  /**
   * Reports a change in XML.
   *
   * @param operator The modification flag (positive for inserts, negative for deletes).
   * @param token   The diff-x token that has been inserted or deleted.
   *
   * @throws IOException an I/O exception if an error occurs.
   */
  private void handleChange(Operator operator, Token token) throws IOException {
    if (!this.isSetup) {
      setUpXML();
    }

    // change in element
    if (token instanceof StartElementToken) {
      flushAttributes();
      endTextChange();
      // namespaces declaration
      if (this.openElements == 0) {
        Formatting.declareNamespaces(this.xml, this.mapping);
        this.openElements++;
      }
      token.toXML(this.xml);
      this.xml.attribute(Constants.BASE_NS_URI, operator == Operator.INS ? "insert" : "delete", "true");

      // change in element
    } else if (token instanceof EndElementToken) {
      flushAttributes();
      endTextChange();
      this.xml.closeElement();
      this.openElements--;

      // change in text
    } else if (token instanceof TextToken) {
      flushAttributes();
      switchTextChange(operator);
      token.toXML(this.xml);
      if (this.config.isIgnoreWhiteSpace() && !this.config.isPreserveWhiteSpace()) {
        this.xml.writeXML(" ");
      }

      // put the attribute as part of the 'delete' namespace
    } else if (token instanceof AttributeToken) {
      if (operator == Operator.INS) {
        token.toXML(this.xml);
        this.insAttributes.push((AttributeToken) token);
      } else {
        this.delAttributes.push((AttributeToken) token);
      }

      // just format naturally
    } else {
      flushAttributes();
      endTextChange();
      token.toXML(this.xml);
    }
    this.xml.flush();
  }

  public void setConfig(DiffXConfig config) {
    this.config = config;
  }

  @Override
  public void setWriteXMLDeclaration(boolean show) {
    this.writeXMLDeclaration = show;
  }

  /**
   * Adds the prefix mapping to this class.
   *
   * @param mapping The prefix mapping to add.
   */
  @Override
  public void declarePrefixMapping(PrefixMapping mapping) {
    this.mapping = mapping;
  }

  // private helpers ----------------------------------------------------------------------------

  /**
   * Set up the XML.
   *
   * @throws IOException an I/O exception if an error occurs.
   */
  private void setUpXML() throws IOException {
    if (this.writeXMLDeclaration) {
      this.xml.xmlDecl();
    }
    Formatting.declareNamespaces(this.xml, this.mapping);
    this.writeXMLDeclaration = false;
    this.isSetup = true;
  }

  /**
   * Formats the end of a text change.
   *
   * @throws IOException If throws by XMl writer.
   */
  private void endTextChange() throws IOException {
    if (this.textFormat != Operator.MATCH) {
      this.xml.closeElement();
      this.textFormat = Operator.MATCH;
    }
  }

  /**
   * Switch between text changes.
   *
   * @param operator The operator
   *
   * @throws IOException If throws by XMl writer.
   */
  private void switchTextChange(Operator operator) throws IOException {
    // insert
    if (operator == Operator.INS) {
      if (this.textFormat == Operator.DEL) {
        this.xml.closeElement();
      }
      if (this.textFormat != Operator.INS) {
        this.xml.openElement(Constants.BASE_NS_URI, "ins", false);
        this.textFormat = Operator.INS;
      }
      // delete
    } else {
      if (this.textFormat == Operator.INS) {
        this.xml.closeElement();
      }
      if (this.textFormat != Operator.DEL) {
        this.xml.openElement(Constants.BASE_NS_URI, "del", false);
        this.textFormat = Operator.DEL;
      }
    }
  }

  /**
   * Writes any attribute that has not be written.
   *
   * @throws IOException Should an I/O error occur.
   */
  private void flushAttributes() throws IOException {
    flushAttributes(this.insAttributes, Operator.DEL);
    flushAttributes(this.delAttributes, Operator.INS);
  }

  /**
   * Writes any attribute that has not be written.
   *
   * @param attributes The attribute stack.
   * @param operator   The operator
   *
   * @throws IOException Should an I/O error occur.
   */
  private void flushAttributes(Stack<AttributeToken> attributes, Operator operator) throws IOException {
    while (!attributes.empty()) {
      AttributeToken att = attributes.pop();
      this.xml.openElement(Constants.BASE_NS_URI, operator == Operator.INS ? "ins" : "del", false);
      this.xml.attribute(att.getURI(), att.getName(), att.getValue());
      this.xml.closeElement();
    }
  }

}
