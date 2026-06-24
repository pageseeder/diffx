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

import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.token.*;
import org.pageseeder.diffx.token.impl.SpaceToken;
import org.pageseeder.diffx.xml.Namespace;
import org.pageseeder.diffx.xml.NamespaceSet;
import org.pageseeder.xmlwriter.XMLWriter;
import org.pageseeder.xmlwriter.XMLWriterNSImpl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * An XML diff output that produces a complete representation of the differences between two XML
 * documents, including enough information to reconstruct both inputs.
 *
 * <h2>Output format</h2>
 *
 * <p>This output uses a diff namespace (by default {@code https://www.pageseeder.org/diffx}
 * with prefix {@code diff}) to annotate differences. The format varies by token type:
 *
 * <h3>Elements</h3>
 * <p>Matching elements are output as-is. Inserted or deleted elements are output with a
 * {@code diff:ins="true"} or {@code diff:del="true"} attribute on the start element tag.
 *
 * <h3>Text</h3>
 * <p>Matching text is output as-is. Inserted or deleted text is wrapped in a {@code <diff:ins>}
 * or {@code <diff:del>} element respectively.
 *
 * <h3>Attributes</h3>
 * <p>Matching attributes are output as-is. Inserted attributes are written on the element
 * and also listed in a {@code diff:ins-attributes} attribute. Deleted attributes are listed
 * in a {@code diff:del-attributes} attribute. Both inserted and deleted attributes are also
 * output as attributes on a child {@code <diff:ins>} or {@code <diff:del>} element for
 * completeness.
 *
 * <h3>Comments and processing instructions</h3>
 * <p>Inserted comments and processing instructions are output as-is. Deleted comments and
 * processing instructions are omitted. There is no diff markup for these token types as
 * XML provides no mechanism to annotate them.
 *
 * <h3>New lines</h3>
 * <p>Matching new lines are output as-is. Inserted new lines are output without diff markup.
 * Deleted new lines are omitted.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.9.0
 */
public final class CompleteXMLDiffOutput extends XMLDiffOutputBase implements XMLDiffOutput {

  /**
   * The underlying XML writer.
   */
  private final XMLWriter xml;

  /**
   * Holds the list of attributes inserted to the previous element.
   */
  private final List<AttributeToken> insertedAttributes = new ArrayList<>();

  /**
   * Holds the list of attributes deleted from the previous element.
   */
  private final List<AttributeToken> deletedAttributes = new ArrayList<>();

  /**
   * Namespace URI used to report differences.
   */
  private String diffNamespaceUri = getDiffNamespace().getUri();

  public CompleteXMLDiffOutput(Writer out) {
    this.xml = new XMLWriterNSImpl(out);
  }

  @Override
  public void start() {
    this.diffNamespaceUri = getDiffNamespace().getUri();
    try {
      if (this.includeXMLDeclaration)
        this.xml.xmlDecl();
      declareNamespaces();
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
  public void handle(Operator operator, XMLToken token) throws UncheckedIOException, IllegalStateException {
    try {
      // We must flush the inserted/deleted attributes
      if (token.getType() != XMLTokenType.ATTRIBUTE) {
        this.flushAttributes();
      }
      // Handle matches and clashes
      if (operator == Operator.MATCH) handleMatch(token);
      else handleEdit(operator, token);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  private void handleMatch(XMLToken token) throws IOException {
    token.toXML(this.xml);
  }

  @SuppressWarnings("java:S3776") // Flat dispatch on token type, splitting would reduce clarity
  private void handleEdit(Operator operator, XMLToken token) throws IOException {
    if (token instanceof StartElementToken) {
      token.toXML(this.xml);
      // insert an attribute to specify if inserted or deleted
      this.xml.attribute(this.diffNamespaceUri, operator == Operator.INS ? "ins" : "del", "true");

    } else if (token == SpaceToken.NEW_LINE) {
      // just output the new line
      if (operator == Operator.INS) {
        token.toXML(this.xml);
      }

    } else if (token instanceof TextToken) {
      // wrap the characters in a <ins> / <del> element
      this.xml.openElement(this.diffNamespaceUri, operator == Operator.INS ? "ins" : "del", false);
      token.toXML(this.xml);
      this.xml.closeElement();

    } else if (token instanceof AttributeToken) {
      if (operator == Operator.INS) {
        token.toXML(this.xml);
        this.insertedAttributes.add((AttributeToken) token);
      } else {
        this.deletedAttributes.add((AttributeToken) token);
      }

    } else if (token instanceof EndElementToken) {
      token.toXML(this.xml);

    } else {
      if (operator == Operator.INS) {
        token.toXML(this.xml);
      }
    }
  }

  /**
   * Write the namespaces mapping to the XML output
   */
  private void declareNamespaces() {
    Namespace diff = getDiffNamespace();
    this.xml.setPrefixMapping(diff.getUri(), diff.getPrefix());
    for (Namespace namespace : this.namespaces) {
      this.xml.setPrefixMapping(namespace.getUri(), namespace.getPrefix());
    }
  }

  /**
   * Flush the inserted or deleted attributes on the element.
   * <p>
   * This method must be called before we finish writing the start element tag.
   */
  private void flushAttributes() throws IOException {
    // Attributes first
    if (!this.insertedAttributes.isEmpty()) {
      String names = getQNames(this.insertedAttributes, this.namespaces);
      this.xml.attribute(this.diffNamespaceUri, "ins-attributes", names);
    }
    if (!this.deletedAttributes.isEmpty()) {
      String names = getQNames(this.deletedAttributes, this.namespaces);
      this.xml.attribute(this.diffNamespaceUri, "del-attributes", names);
    }
    // Elements
    if (!this.insertedAttributes.isEmpty()) {
      this.xml.openElement(this.diffNamespaceUri, "ins", false);
      for (AttributeToken attribute : this.insertedAttributes) {
        this.xml.attribute(attribute.getNamespaceURI(), attribute.getName(), attribute.getValue());
      }
      this.xml.closeElement();
      this.insertedAttributes.clear();
    }
    if (!this.deletedAttributes.isEmpty()) {
      this.xml.openElement(this.diffNamespaceUri, "del", false);
      for (AttributeToken attribute : this.deletedAttributes) {
        this.xml.attribute(attribute.getNamespaceURI(), attribute.getName(), attribute.getValue());
      }
      this.xml.closeElement();
      this.deletedAttributes.clear();
    }
  }

  private static String getQNames(List<AttributeToken> attributes, NamespaceSet namespaces) {
    StringBuilder names = new StringBuilder();
    for (int i = 0; i < attributes.size(); i++) {
      if (i > 0) names.append(' ');
      names.append(getQName(attributes.get(i), namespaces));
    }
    return names.toString();
  }

  private static String getQName(AttributeToken attribute, NamespaceSet namespaces) {
    // Colon cannot be at index 0 in a valid XML attribute name
    if (attribute.getName().indexOf(':') > 0) return attribute.getName(); //NOSONAR java:S2692
    String prefix = namespaces.getPrefix(attribute.getNamespaceURI());
    return prefix != null && !prefix.isEmpty() ? prefix + ":" + attribute.getName() : attribute.getName();
  }

}
