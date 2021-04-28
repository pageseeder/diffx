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
import org.pageseeder.diffx.token.*;
import org.pageseeder.diffx.token.impl.SpaceToken;
import org.pageseeder.diffx.xml.Namespace;
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
public final class DefaultXMLDiffOutput extends XMLDiffOutputBase implements XMLDiffOutput {

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = false;

  /**
   * The output goes here.
   */
  final XMLWriterNSImpl xml;

  /**
   * Creates a new output on the standard output.
   *
   * @see System#out
   */
  public DefaultXMLDiffOutput() {
    this(new PrintWriter(System.out));
  }

  /**
   * Creates a new formatter using the specified writer.
   *
   * @param w The writer to use.
   */
  public DefaultXMLDiffOutput(Writer w) {
    this.xml = new XMLWriterNSImpl(w, false);
  }

  @Override
  public void start() {
    try {
      if (this.includeXMLDeclaration) {
        this.xml.xmlDecl();
      }
      declareNamespaces();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  public void handle(Operator operator, Token token) throws UncheckedIOException, IllegalStateException {
    if (DEBUG) System.err.println(operator.toString() + token);
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

  void handleMatch(Token token) throws IOException {
    token.toXML(this.xml);
  }

  void handleEdit(Operator operator, Token token) throws IOException {
    if (token instanceof StartElementToken) {
      token.toXML(this.xml);
      // insert an attribute to specify operator
      this.xml.attribute(getDiffNamespace().getUri(), operator == Operator.INS ? "insert" : "delete", "true");

    } else if (token instanceof AttributeToken) {
      AttributeToken attribute = (AttributeToken) token;
      // NB We can't report inserted/deleted attributes with namespaces
      if (operator == Operator.INS) {
        token.toXML(this.xml);
        if (!hasPrefix(attribute))
          this.xml.attribute(getDiffNamespace(Operator.INS).getUri(), attribute.getName(), "true");
      } else {
        if (!hasPrefix(attribute))
          this.xml.attribute(getDiffNamespace(Operator.DEL).getUri(), attribute.getName(), attribute.getValue());
      }

    } else if (token == SpaceToken.NEW_LINE) {
      // just output the new line
      token.toXML(this.xml);

    } else if (token instanceof TextToken) {
      // wrap the characters in a <ins/del> element
      this.xml.openElement(getDiffNamespace().getUri(), toElement(operator), false);
      token.toXML(this.xml);
      this.xml.closeElement();

    } else if (token instanceof EndElementToken) {
      token.toXML(this.xml);

      // just format naturally
    } else {
      token.toXML(this.xml);
    }
  }

  private boolean hasPrefix(AttributeToken attribute) {
    if (attribute.getName().indexOf(':') != -1) return true;
    String prefix = this.namespaces.getPrefix(attribute.getNamespaceURI());
    return prefix != null && !prefix.isEmpty();
  }

  /**
   * Write the namespace mapping to the XML output
   */
  private void declareNamespaces() {
    Namespace diffNamespace = getDiffNamespace();
    Namespace insNamespace = getDiffNamespace(Operator.INS);
    Namespace delNamespace = getDiffNamespace(Operator.DEL);
    this.xml.setPrefixMapping(diffNamespace.getUri(), diffNamespace.getPrefix());
    this.xml.setPrefixMapping(insNamespace.getUri(), insNamespace.getPrefix());
    this.xml.setPrefixMapping(delNamespace.getUri(), delNamespace.getPrefix());
    if (this.namespaces != null) {
      for (Namespace namespace : this.namespaces) {
        this.xml.setPrefixMapping(namespace.getUri(), namespace.getPrefix());
      }
    }
  }

  /**
   * @return "ins" for insertions or "del" for deletions
   */
  private static String toElement(Operator operator) {
    return operator == Operator.INS ? "ins" : "del";
  }
}
