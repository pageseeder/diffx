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
import org.pageseeder.diffx.xml.Namespace;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.io.Writer;

/**
 * A simple XML diff output that writes strictly what it is given.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class StrictXMLDiffOutput extends XMLDiffOutputBase implements XMLDiffOutput {

  /**
   * The tag used for deletions.
   */
  private static final String DEL_TAG = "del";

  /**
   * The tag used for insertions.
   */
  private static final String INS_TAG = "ins";

  /**
   * XML output
   */
  private final XMLStreamWriter xml;

  /**
   * Set to <code>false</code> once the prefix mapping has been declared.
   */
  private boolean declareNamespace = true;

  /**
   * Operator for the last open tag (set to MATCH when none)
   */
  private Operator lastOperatorTag = Operator.MATCH;

  /**
   * Creates a new formatter on the standard output.
   */
  public StrictXMLDiffOutput() {
    this(System.out);
  }

  /**
   * Creates a new formatter using the specified writer.
   *
   * @param w The writer to use.
   */
  public StrictXMLDiffOutput(Writer w) {
    XMLOutputFactory output = XMLOutputFactory.newInstance();
    output.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
    try {
      this.xml = output.createXMLStreamWriter(w);
    } catch (XMLStreamException ex) {
      throw new IllegalStateException(ex);
    }
  }

  /**
   * Creates a new formatter using the specified writer.
   *
   * @param out The output stream to use.
   */
  public StrictXMLDiffOutput(OutputStream out) {
    XMLOutputFactory output = XMLOutputFactory.newInstance();
    output.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
    try {
      this.xml = output.createXMLStreamWriter(out);
    } catch (XMLStreamException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Override
  public void start() {
    try {
      if (this.includeXMLDeclaration) {
        this.xml.writeStartDocument("utf-8", "1.0");
      }
      this.xml.setDefaultNamespace(null);
      for (Namespace namespace : this.namespaces) {
        String uri = namespace.getUri();
        if (!uri.isEmpty()) {
          this.xml.setPrefix(namespace.getPrefix(), uri);
        }
      }
    } catch (XMLStreamException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Override
  public void handle(@NotNull Operator operator, XMLToken token) throws UncheckedIOException, IllegalStateException {
    try {
      if (token.getType() != XMLTokenType.ATTRIBUTE) {
        // close any ins / del tag that don't match the current operator
        if (this.lastOperatorTag.isEdit() && this.lastOperatorTag != operator) {
          this.xml.writeEndElement();
          this.lastOperatorTag = Operator.MATCH;
        }
      }

      // an element to open
      if (token instanceof StartElementToken) {
        token.toXML(this.xml);
        if (this.declareNamespace) {
          this.xml.writeNamespace(getDiffNamespace().getPrefix(), getDiffNamespace().getUri());
          this.declareNamespace = false; // TODO Check if needed
        }
        if (operator == Operator.INS)
          this.xml.writeAttribute(getDiffNamespace().getUri(), "insert", "true");
        if (operator == Operator.DEL)
          this.xml.writeAttribute(getDiffNamespace().getUri(), "delete", "true");

        // an element to close
      } else if (token instanceof EndElementToken) {
        token.toXML(this.xml);

        // an attribute
      } else if (token instanceof AttributeToken) {
        if (operator != Operator.DEL) {
          token.toXML(this.xml);
        }

      } else if (token instanceof TextToken) {
        if (operator.isEdit() && this.lastOperatorTag != operator) {
          this.xml.writeStartElement(operator == Operator.INS ? INS_TAG : DEL_TAG);
          this.lastOperatorTag = operator;
        }
        token.toXML(this.xml);

      } else {
        token.toXML(this.xml);
      }
      this.xml.flush();
    } catch (XMLStreamException ex) {
      ex.printStackTrace();
    }
  }

}
