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
import org.pageseeder.diffx.token.AttributeToken;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.Token;
import org.pageseeder.diffx.token.impl.CharToken;
import org.pageseeder.diffx.token.impl.CharactersTokenBase;
import org.pageseeder.diffx.token.impl.SpaceToken;
import org.pageseeder.diffx.util.Formatting;
import org.pageseeder.xmlwriter.XMLWriterNSImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;

/**
 * An XML formatter that tries to rectify the errors affecting the well-formedness of the XML.
 *
 * <p>This class will always close the elements correctly by maintaining a stack of parent
 * elements.
 *
 * <p>Implementation note: this classes uses the namespace prefixes 'dfx' and 'del', in the
 * future it should be possible to configure which prefixes to use for each namespace, but
 * in this version the namespace prefix mapping is hardcoded.
 *
 * <p>A limitation of this output is that it cannot report inserted/deleted attributes
 * with a namespace prefix.</p>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.5.0
 */
public final class SmartXMLDiffOutput implements XMLDiffXFormatter, XMLDiffOutput {

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = false;

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

  /**
   * Set to <code>true</code> to include the XML declaration. This attribute is
   * set to <code>false</code> when the {@link #setWriteXMLDeclaration(boolean)}
   * is called with <code>false</code> or once the XML declaration has been written.
   */
  private transient boolean writeXMLDeclaration = true;

  /**
   * Used to know if all elements have been closed, in which case the namespace
   * mapping should be redeclared before opening a new element
   */
  private int openElements = 0;

  // constructors -------------------------------------------------------------------------------

  /**
   * Creates a new formatter on the standard output.
   *
   * <p>This constructor is equivalent to:
   * <pre>new SmartXMLFormatter(new PrintWriter(System.out));</pre>.
   *
   * @throws IOException should an I/O exception occurs.
   * @see System#out
   */
  public SmartXMLDiffOutput() throws IOException {
    this(new PrintWriter(System.out));
  }

  /**
   * Creates a new formatter using the specified writer.
   *
   * @param w The writer to use.
   *
   * @throws IOException should an I/O exception occurs.
   */
  public SmartXMLDiffOutput(Writer w) throws IOException {
    this.xml = new XMLWriterNSImpl(w, false);
  }

  // methods ------------------------------------------------------------------------------------


  @Override
  public void start() {
    if (this.writeXMLDeclaration) {
      try {
        this.xml.xmlDecl();
        this.writeXMLDeclaration = false;
      } catch (IOException ex) {
        throw new UncheckedIOException(ex);
      }
    }
  }

  @Override
  public void handle(Operator operator, Token token) throws UncheckedIOException, IllegalStateException {
    if (DEBUG) {
      System.err.println(operator+""+token);
    }
    try {
      if (operator.isEdit()) {
        handleEdit(operator, token);
      } else {
        handleMatch(token);
      }
      this.xml.flush();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  public void end() {
    try {
      this.xml.flush();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  public void format(Token token) throws IOException {
    handleMatch(token);
    this.xml.flush();
  }

  @Override
  public void insert(Token token) throws IOException {
    handleEdit(Operator.INS, token);
    this.xml.flush();
  }

  @Override
  public void delete(Token token) throws IOException {
    handleEdit(Operator.DEL, token);
    this.xml.flush();
  }

  private void handleMatch(Token token) throws IOException {
    if (token instanceof StartElementToken) {
      if (this.openElements == 0) Formatting.declareNamespaces(this.xml, this.mapping);
      this.openElements++;
    } else if (token instanceof EndElementToken) {
      this.openElements--;
    }
    token.toXML(this.xml);
    if (token instanceof CharactersTokenBase) {
      if (this.config.isIgnoreWhiteSpace() && !this.config.isPreserveWhiteSpace()) {
        this.xml.writeXML(" ");
      }
    }
  }

  void handleEdit(Operator operator, Token token) throws IOException {
    if (token instanceof StartElementToken) {
      // namespaces declaration
      if (this.openElements == 0) Formatting.declareNamespaces(this.xml, this.mapping);
      this.openElements++;
      token.toXML(this.xml);
      // insert an attribute to specify
      this.xml.attribute(operator == Operator.INS ? "dfx:insert" : "dfx:delete", "true");

    // display the attribute normally
    } else if (token instanceof AttributeToken) {
      AttributeToken attribute = (AttributeToken)token;
      // NB We can't report inserted/deleted attributes with namespaces
      if (operator == Operator.INS) {
        token.toXML(this.xml);
        if (attribute.getName().indexOf(':') == -1)
          this.xml.attribute("ins:" + attribute.getName(), "true");
      } else {
        if (attribute.getName().indexOf(':') == -1)
          this.xml.attribute("del:" + attribute.getName(), attribute.getValue());
      }

    // just output the new line
    } else if (token == SpaceToken.NEW_LINE) {
      token.toXML(this.xml);

    // wrap the characters in a <ins/del> element
    } else if (token instanceof CharactersTokenBase) {
      this.xml.openElement(getTag(operator), false);
      token.toXML(this.xml);
      this.xml.closeElement();
      if (this.config.isIgnoreWhiteSpace() && !this.config.isPreserveWhiteSpace()) {
        this.xml.writeXML(" ");
      }

    // wrap the char in a <ins/del> element
    } else if (token instanceof CharToken) {
      this.xml.openElement(getTag(operator), false);
      token.toXML(this.xml);
      this.xml.closeElement();

    } else if (token instanceof EndElementToken) {
      this.openElements--;
      token.toXML(this.xml);

    // just format naturally
    } else {
      token.toXML(this.xml);
    }

  }

  @Override
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

  private String getTag(Operator operator) {
    return operator == Operator.INS ? "ins" : "del";
  }
}
