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
import org.pageseeder.diffx.sequence.Namespace;
import org.pageseeder.diffx.sequence.PrefixMapping;
import org.pageseeder.diffx.token.*;
import org.pageseeder.diffx.token.impl.CommentToken;
import org.pageseeder.diffx.util.Constants;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.ProcessingInstruction;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.io.Writer;

/**
 * A simple XML diff output that writes strictly what it is given.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class StrictXMLDiffOutput implements XMLDiffXFormatter, XMLDiffOutput {

  /**
   * The tag used for deletions.
   */
  private static final String DEL_TAG = "del";

  /**
   * The tag used for insertions.
   */
  private static final String INS_TAG = "ins";

  /**
   * The DiffX configuration to use
   */
  private DiffXConfig config = new DiffXConfig();

  /**
   * XML output
   */
  private final XMLStreamWriter xml;

  /**
   * Set to <code>true</code> to include the XML declaration. This attribute is
   * set to <code>false</code> when the {@link #setWriteXMLDeclaration(boolean)}
   * is called with <code>false</code> or once the XML declaration has been written.
   */
  private boolean writeXMLDeclaration = true;

  /**
   * Set to <code>false</code> once the prefix mapping has been declared.
   */
  private boolean declareNamespace = true;

  /**
   * Operator for the last open tag (set to MATCH when none)
   */
  private Operator lastOperatorTag = Operator.MATCH;

  private boolean isDocumentStart = true;

  // constructors -------------------------------------------------------------------------------

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
      this.xml.setPrefix(XMLConstants.DEFAULT_NS_PREFIX,XMLConstants.NULL_NS_URI);
      this.xml.setPrefix("dfx", Constants.BASE_NS_URI);
      this.xml.setDefaultNamespace(XMLConstants.NULL_NS_URI);
    } catch (XMLStreamException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Override
  public void start() {
    if (this.writeXMLDeclaration) {
      try {
        this.xml.writeStartDocument("utf-8", "1.0");
      } catch (XMLStreamException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  @Override
  public void handle(Operator operator, Token token) throws UncheckedIOException, IllegalStateException {
    try {
      if (token.getType() != TokenType.ATTRIBUTE) {
        // close any ins / del tag that don't match the current operator
        if (this.lastOperatorTag.isEdit() && this.lastOperatorTag != operator) {
          this.xml.writeEndElement();
          this.lastOperatorTag = Operator.MATCH;
        }
      }

      // an element to open
      if (token instanceof StartElementToken) {
        StartElementToken startToken = (StartElementToken) token;
        this.xml.writeStartElement(startToken.getURI(), startToken.getName());
        if (isDocumentStart) {
          this.xml.writeDefaultNamespace(XMLConstants.NULL_NS_URI);
        }
        if (this.declareNamespace) {
          this.xml.writeNamespace("dfx", Constants.BASE_NS_URI);
          this.declareNamespace = false; // TODO Check if needed
        }
        if (operator == Operator.INS)
          this.xml.writeAttribute(Constants.BASE_NS_URI, "insert", "true");
        if (operator == Operator.DEL)
          this.xml.writeAttribute(Constants.BASE_NS_URI, "delete", "true");

        // an element to close
      } else if (token instanceof EndElementToken) {
        this.xml.writeEndElement();

        // an attribute
      } else if (token instanceof AttributeToken) {
        if (operator != Operator.DEL) {
          AttributeToken attribute = (AttributeToken)token;
          if (attribute.getURI().isEmpty()) {
            this.xml.writeAttribute(attribute.getName(), attribute.getValue());
          } else {
            this.xml.writeAttribute(attribute.getURI(), attribute.getName(), attribute.getValue());
          }
        }

      } else if (token instanceof TextToken) {
          if (operator.isEdit() && this.lastOperatorTag != operator) {
            this.xml.writeStartElement(operator == Operator.INS ? INS_TAG : DEL_TAG);
            this.lastOperatorTag = operator;
          }
          this.xml.writeCharacters(((TextToken)token).getCharacters());

      } else if (token instanceof ProcessingInstruction) {
        ProcessingInstruction pi = (ProcessingInstruction)token;
        this.xml.writeProcessingInstruction(pi.getTarget(), pi.getData());

      } else if (token instanceof CommentToken) {
        CommentToken comment = (CommentToken)token;
        this.xml.writeComment(comment.getComment());

      } else {
        // Fallback on string
        this.xml.writeCharacters(token.toString());
      }
      this.xml.flush();
    } catch (XMLStreamException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void format(Token token) {
    if (this.isDocumentStart) {
      start();
      this.isDocumentStart = false;
    }
    handle(Operator.MATCH, token);
  }

  @Override
  public void insert(Token token) {
    if (this.isDocumentStart) {
      start();
      this.isDocumentStart = false;
    }
    handle(Operator.INS, token);
  }

  @Override
  public void delete(Token token) throws IllegalStateException {
    if (this.isDocumentStart) {
      start();
      this.isDocumentStart = false;
    }
    handle(Operator.DEL, token);
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
    try {
      this.xml.writeDefaultNamespace(XMLConstants.NULL_NS_URI);
      for (Namespace namespace : mapping) {
        this.xml.writeNamespace(namespace.getPrefix(), namespace.getUri());
      }
    } catch (XMLStreamException ex) {
      ex.printStackTrace();
    }
  }


}
