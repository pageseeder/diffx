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

import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.sequence.Namespace;
import org.pageseeder.diffx.sequence.PrefixMapping;
import org.pageseeder.diffx.token.*;
import org.pageseeder.diffx.util.Constants;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.Writer;

/**
 * A simple XML formatter that writes strictly what it is given.
 *
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class StrictXMLFormatter implements XMLDiffXFormatter {

  /**
   * The tag used for deletions.
   */
  private static final String del = "del";

  /**
   * The tag used for insertions.
   */
  private static final String ins = "ins";

  /**
   * The DiffX configuration to use
   */
  private DiffXConfig config = new DiffXConfig();

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
   * Set to <code>true</code> to indicate that there is an open 'ins' tag.
   */
  private boolean isInserting = false;

  /**
   * Set to <code>true</code> to indicate that there is an open 'del' tag.
   */
  private boolean isDeleting = false;

  private boolean isDocumentStart = true;

  // constructors -------------------------------------------------------------------------------

  /**
   * Creates a new formatter on the standard output.
   */
  public StrictXMLFormatter() {
    this(System.out);
  }

  /**
   * Creates a new formatter using the specified writer.
   *
   * @param w The writer to use.
   */
  public StrictXMLFormatter(Writer w) {
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
  public StrictXMLFormatter(OutputStream out) {
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
  public void format(Token token) {
    try {
      if (this.isDocumentStart && this.writeXMLDeclaration) {
        this.xml.writeStartDocument("utf-8", "1.0");
      }
      // an element to open
      if (token instanceof StartElementToken) {
        // close any ins / del tag
        if (this.isInserting) {
          closeIns();
        }
        if (this.isDeleting) {
          closeDel();
        }
        StartElementToken startToken = (StartElementToken) token;
        this.xml.writeStartElement(startToken.getURI(), startToken.getName());
        if (isDocumentStart) {
          this.xml.writeDefaultNamespace(XMLConstants.NULL_NS_URI);
        }
//        if (this.declareNamespace) {
//          this.xml.writeNamespace("dfx", Constants.BASE_NS_URI);
//          this.declareNamespace = false;
//        }

        // an element to close
      } else if (token instanceof EndElementToken) {
        // close any ins / del tag
        if (this.isInserting) {
          closeIns();
        }
        if (this.isDeleting) {
          closeDel();
        }
        this.xml.writeEndElement();

        // an attribute
      } else if (token instanceof AttributeToken) {
        AttributeToken attribute = (AttributeToken)token;
        if (attribute.getURI().isEmpty()) {
          this.xml.writeAttribute(attribute.getName(), attribute.getValue());
        } else {
          this.xml.writeAttribute(attribute.getURI(), attribute.getName(), attribute.getValue());
        }

        // this is text
      } else {
        if (this.isInserting) {
          closeIns();
        }
        if (this.isDeleting) {
          closeDel();
        }

        // a character sequence
        if (token instanceof TextToken) {
          this.xml.writeCharacters(((TextToken)token).getCharacters());
        }

      }
      this.xml.flush();
      this.isDocumentStart = false;
    } catch (XMLStreamException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void insert(Token token) {
    try {
      if (this.isDocumentStart && this.writeXMLDeclaration) {
        this.xml.writeStartDocument("utf-8", "1.0");
      }
      // insert element
      if (token instanceof StartElementToken) {
        if (this.isDeleting) {
          closeDel();
        }
        StartElementToken startElement = (StartElementToken) token;
        this.xml.writeStartElement(startElement.getURI(), startElement.getName());
        if (this.declareNamespace) {
          this.xml.writeNamespace("dfx", Constants.BASE_NS_URI);
        }
        this.xml.writeAttribute(Constants.BASE_NS_URI, "insert", "true");

        // an element to close
      } else if (token instanceof EndElementToken) {
        if (this.isDeleting) {
          closeDel();
        }
        this.xml.writeEndElement();

      } else if (token instanceof AttributeToken) {
        AttributeToken attribute = (AttributeToken)token;
        if (attribute.getURI().isEmpty()) {
          this.xml.writeAttribute(attribute.getName(), attribute.getValue());
        } else {
          this.xml.writeAttribute(attribute.getURI(), attribute.getName(), attribute.getValue());
        }

      } else {

        if (this.isDeleting) {
          closeDel();
        }
        // a word
        if (token instanceof TextToken) {
          if (!this.isInserting) {
            openIns();
          }
          this.xml.writeCharacters(((TextToken) token).getCharacters());
        }

      }
      this.xml.flush();
      this.isDocumentStart = false;
    } catch (XMLStreamException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void delete(Token token) throws IllegalStateException {
    try {
      if (this.isDocumentStart && this.writeXMLDeclaration) {
        this.xml.writeStartDocument("utf-8", "1.0");
      }
      if (this.isInserting) {
        closeIns();
      }

      // delete an element
      if (token instanceof StartElementToken) {
        StartElementToken startElement = (StartElementToken) token;
        this.xml.writeStartElement(startElement.getURI(), startElement.getName());
        if (this.declareNamespace) {
          this.xml.writeNamespace("dfx", Constants.BASE_NS_URI);
        }
        this.xml.writeAttribute(Constants.BASE_NS_URI, "delete", "true");

        // an element to close
      } else if (token instanceof EndElementToken) {
        this.xml.writeEndElement();

        // text
      } else {

        // a word
        if (token instanceof TextToken) {
          if (!this.isDeleting) {
            openDel();
          }
          this.xml.writeCharacters(((TextToken) token).getCharacters());
        }
      }
      this.xml.flush();
      this.isDocumentStart = false;
    } catch (XMLStreamException ex) {
      ex.printStackTrace();
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
    try {
      this.xml.writeDefaultNamespace(XMLConstants.NULL_NS_URI);
      for (Namespace namespace : mapping) {
          this.xml.writeNamespace(namespace.getPrefix(), namespace.getUri());
      }
    } catch (XMLStreamException ex) {
      ex.printStackTrace();
    }
  }

  // private helpers ----------------------------------------------------------------------------

  /**
   * Opens the 'ins' element, and update the state flags.
   */
  private void openIns() throws XMLStreamException {
    this.xml.writeStartElement(ins);
    this.isInserting = true;
  }

  /**
   * Opens the 'del' element, and update the state flags.
   */
  private void openDel() throws XMLStreamException  {
    this.xml.writeStartElement(del);
    this.isDeleting = true;
  }

  /**
   * Closes the 'ins' element, and update the state flags.
   */
  private void closeIns() throws XMLStreamException  {
    this.xml.writeEndElement();
    this.isInserting = false;
  }

  /**
   * Closes the 'del' element, and update the state flags.
   */
  private void closeDel() throws XMLStreamException  {
    this.xml.writeEndElement();
    this.isDeleting = false;
  }

}
