/*
 * Copyright 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
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
import org.pageseeder.diffx.event.*;
import org.pageseeder.diffx.event.impl.SpaceToken;
import org.pageseeder.diffx.sequence.Namespace;
import org.pageseeder.diffx.sequence.PrefixMapping;
import org.pageseeder.xmlwriter.XMLWriter;
import org.pageseeder.xmlwriter.XMLWriterNSImpl;

import javax.xml.XMLConstants;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultXMDiffOutput implements XMLDiffOutput {

  /**
   * The namespace URI reserved for the diff.
   */
  public static final String DIFF_NS_URI = "https://www.pageseeder.org/diffx";

  /**
   * The prefix used by diff by default.
   */
  public static final String DIFF_NS_PREFIX = "diff";

  /**
   * The namespace used for diff elements.
   */
  public static final Namespace DIFF_NAMESPACE = new Namespace(DIFF_NS_URI, DIFF_NS_PREFIX);

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = false;

  private final XMLWriter xml;

  private PrefixMapping mapping = PrefixMapping.noNamespace();

  /**
   * {@code true} (default) to include the XML namespace declaration when the {@link #start()} method is called.
   */
  private boolean includeXMLDeclaration = true;

  /**
   * {@code true} (default) to use the diff namespace for the {@code <ins/>} and {@code <del/>} elements.
   */
  private boolean useDiffNamespaceForElements = true;

  /**
   * Holds the list of attributes inserted to the previous element.
   */
  private final List<AttributeToken> insertedAttributes = new ArrayList<>();

  /**
   * Holds the list of attributes deleted from the previous element.
   */
  private final List<AttributeToken> deletedAttributes = new ArrayList<>();

  /**
   * Used to know if all elements have been closed, in which case the namespace
   * mapping should be redeclared before opening a new element
   */
  private int openElements = 0;

  public DefaultXMDiffOutput(Writer out) {
    this.xml = new XMLWriterNSImpl(out);
  }

  public void useDiffNamespaceForElements(boolean yes) {
    this.useDiffNamespaceForElements = yes;
  }

  @Override
  public void setWriteXMLDeclaration(boolean show) {
    this.includeXMLDeclaration = show;
  }

  @Override
  public void declarePrefixMapping(PrefixMapping mapping) {
    this.mapping = mapping;
  }

  @Override
  public void start() {
    try {
      if (this.includeXMLDeclaration)
        this.xml.xmlDecl();
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
  public void handle(Operator operator, Token token) throws UncheckedIOException, IllegalStateException {
    if (DEBUG) System.err.println(operator.toString()+token);
    try {
      // We must flush the inserted/deleted attributes
      if (!(token instanceof AttributeToken)) {
        this.flushAttributes();
      }
      // namespaces declaration
      if (token instanceof StartElementToken) {
        if (this.openElements == 0) declareNamespaces();
        this.openElements++;
      } else if (token instanceof EndElementToken) {
        this.openElements--;
      }
      // Handle matches and clashes
      if (operator == Operator.MATCH) handleMatch(token);
      else handleClash(operator, token);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  private void handleMatch(Token token) throws IOException {
    token.toXML(this.xml);
    this.insertSpaceIfRequired(token);
  }

  private void handleClash(Operator operator, Token token) throws IOException {
    // insert an attribute to specify
    if (token instanceof StartElementToken) {
      // namespaces declaration
      if (this.openElements == 0) {
        declareNamespaces();
        this.openElements++;
      }
      token.toXML(this.xml);
      this.xml.attribute(DIFF_NS_URI, operator == Operator.INS ? "ins" : "del", "true");

      // just output the new line
    } else if (token == SpaceToken.NEW_LINE) {
      token.toXML(this.xml);

      // wrap the characters in a <ins> element
    } else if (token instanceof TextToken) {
      this.xml.openElement(DIFF_NS_URI, operator == Operator.INS ? "ins" : "del", false);
      token.toXML(this.xml);
      this.xml.closeElement();
      this.insertSpaceIfRequired(token);

    } else if (token instanceof AttributeToken) {
      if (operator == Operator.INS) {
        token.toXML(this.xml);
        this.insertedAttributes.add((AttributeToken) token);
      } else {
        this.deletedAttributes.add((AttributeToken) token);
      }

    } else if (token instanceof EndElementToken) {
      this.openElements--;
      token.toXML(this.xml);

    } else {
      token.toXML(this.xml);
    }
  }

  /**
   * Write the namespaces mapping to the XML output
   */
  private void declareNamespaces() {
    // TODO Change so that there is no side-effect
    PrefixMapping diff = new PrefixMapping(DIFF_NAMESPACE);
    diff.add(this.mapping);
    for (Namespace namespace : diff) {
      this.xml.setPrefixMapping(namespace.getUri(), namespace.getPrefix());
    }
  }

  private void insertSpaceIfRequired(Token token) throws IOException {
//    if (token instanceof TextToken && !(token instanceof CharToken) && this.config.isIgnoreWhiteSpace() && !this.config.isPreserveWhiteSpace()) {
//      this.xml.writeXML(" ");
//    }
  }

  /**
   * Flush the inserted or deleted attributes on the element.
   *
   * This method must be called before we finish writing the start element tag.
   */
  private void flushAttributes() throws IOException {
    String namespace = useDiffNamespaceForElements ? DIFF_NS_URI : XMLConstants.NULL_NS_URI;
    // Attributes first
    if (!this.insertedAttributes.isEmpty()) {
      this.xml.attribute(DIFF_NS_URI, "ins-attributes", this.insertedAttributes.stream().map(AttributeToken::getName).collect(Collectors.joining(" ")));
    }
    if (!this.deletedAttributes.isEmpty()) {
      this.xml.attribute(DIFF_NS_URI, "del-attributes", this.deletedAttributes.stream().map(AttributeToken::getName).collect(Collectors.joining(" ")));
    }
    // Elements
    if (!this.insertedAttributes.isEmpty()) {
      this.xml.openElement(namespace, "ins", false);
      for (AttributeToken attribute : this.insertedAttributes) {
        this.xml.attribute(attribute.getURI(), attribute.getName(), attribute.getValue());
      }
      this.xml.closeElement();
      this.insertedAttributes.clear();
    }
    if (!this.deletedAttributes.isEmpty()) {
      this.xml.openElement(namespace, "del", false);
      for (AttributeToken attribute : this.deletedAttributes) {
        this.xml.attribute(attribute.getURI(), attribute.getName(), attribute.getValue());
      }
      this.xml.closeElement();
      this.deletedAttributes.clear();
    }
  }

}
