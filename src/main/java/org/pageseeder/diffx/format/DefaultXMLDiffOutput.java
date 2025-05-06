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

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.api.Operator;
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
 * <p>Implementation note: this class uses the namespace prefixes 'dfx' and 'del', in the
 * future it should be possible to configure which prefixes to use for each namespace, but
 * in this version the namespace prefix mapping is hardcoded.
 *
 * <p>A limitation of this output is that it cannot report inserted/deleted attributes
 * with a namespace prefix.</p>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class DefaultXMLDiffOutput extends XMLDiffOutputBase implements XMLDiffOutput {

  /**
   * The output goes here.
   */
  final XMLWriterNSImpl xml;

  /**
   * Required to keep track of namespaces
   */
  private int level = 0;

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
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  public void handle(@NotNull Operator operator, XMLToken token) throws UncheckedIOException, IllegalStateException {
    if (this.level == 0) {
      declareNamespaces();
    }
    try {
      if (operator.isEdit()) {
        handleEdit(operator, token);
      } else {
        handleMatch(token);
      }
      if (token.getType() == XMLTokenType.START_ELEMENT) this.level++;
      else if (token.getType() == XMLTokenType.END_ELEMENT) this.level--;

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

  void handleMatch(XMLToken token) throws IOException {
    token.toXML(this.xml);
  }

  void handleEdit(Operator operator, XMLToken token) throws IOException {
    if (token.getType() == XMLTokenType.START_ELEMENT) {
      token.toXML(this.xml);
      // insert an attribute to specify operator
      this.xml.attribute(getDiffNamespace().getUri(), operator == Operator.INS ? "insert" : "delete", "true");

    } else if (token.getType() == XMLTokenType.ATTRIBUTE) {
      AttributeToken attribute = (AttributeToken) token;
      // NB We can't report inserted/deleted attributes with namespaces
      if (operator == Operator.INS) {
        token.toXML(this.xml);
        if (hasNoPrefix(attribute))
          this.xml.attribute(getDiffNamespace(Operator.INS).getUri(), attribute.getName(), "true");
      } else {
        if (hasNoPrefix(attribute))
          this.xml.attribute(getDiffNamespace(Operator.DEL).getUri(), attribute.getName(), attribute.getValue());
      }

    } else if (token == SpaceToken.NEW_LINE) {
      // just output the new line
      if (operator == Operator.INS) {
        token.toXML(this.xml);
      }

    } else if (token.getType() == XMLTokenType.TEXT) {
      // wrap the characters in a <ins/del> element
      this.xml.openElement(getDiffNamespace().getUri(), toElement(operator), false);
      token.toXML(this.xml);
      this.xml.closeElement();

    } else if (token.getType() == XMLTokenType.END_ELEMENT) {
      token.toXML(this.xml);

    } else {
      // Only include inserted content
      if (operator == Operator.INS) {
        token.toXML(this.xml);
      }
    }
  }

  private boolean hasNoPrefix(AttributeToken attribute) {
    if (attribute.getName().indexOf(':') != -1) return false;
    String prefix = this.namespaces.getPrefix(attribute.getNamespaceURI());
    return prefix == null || prefix.isEmpty();
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
