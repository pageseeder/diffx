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
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.token.AttributeToken;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.Token;
import org.pageseeder.diffx.token.impl.CharToken;
import org.pageseeder.diffx.token.impl.CharactersTokenBase;
import org.pageseeder.diffx.token.impl.SpaceToken;
import org.pageseeder.diffx.util.Constants;
import org.pageseeder.diffx.util.Formatting;
import org.pageseeder.diffx.xml.PrefixMapping;
import org.pageseeder.xmlwriter.XMLWriterNSImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;

/**
 * An XML formatter that tries to ensure that the output XML will be well-formed.
 *
 * <p>This class will always close the elements correctly by maintaining a stack of parent elements.
 *
 * <p>Implementation note: this classes uses the namespace prefixes 'dfx' and 'del', in the
 * future it should be possible to configure which prefixes to use for each namespace, but
 * in this version the namespace prefix mapping is hard-coded.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.6.0
 */
public final class SafeXMLDiffOutput implements XMLDiffOutput {

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

  // state variables ----------------------------------------------------------------------------

  /**
   * Set to <code>true</code> to include the XML declaration.
   *
   * <p>This attribute is set to <code>false</code> when the {@link #setWriteXMLDeclaration(boolean)}
   * is called with <code>false</code> or once the XML declaration has been written.
   */
  private transient boolean writeXMLDeclaration = true;

  /**
   * Used to know if all elements have been closed, in which case the namespace
   * mapping should be redeclared before opening a new element
   */
  private int openElements = 0;

  /**
   * Creates a new formatter on the standard output.
   *
   * @see System#out
   */
  public SafeXMLDiffOutput() throws IOException {
    this(new PrintWriter(System.out));
  }

  /**
   * Creates a new formatter using the specified writer.
   *
   * @param w The writer to use.
   */
  public SafeXMLDiffOutput(Writer w) {
    this.xml = new XMLWriterNSImpl(w, false);
  }

  @Override
  public void start() {
    try {
      if (this.writeXMLDeclaration) {
        this.xml.xmlDecl();
        this.writeXMLDeclaration = false;
      }
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  public void handle(Operator operator, Token token) throws UncheckedIOException, IllegalStateException {
    if (DEBUG) {
      System.err.println(operator.toString() + token);
    }
    try {
      // Maintain stack
      if (token instanceof StartElementToken) {
        // Declare namespaces
        if (this.openElements == 0) Formatting.declareNamespaces(this.xml, this.mapping);
        this.openElements++;
      } else if (token instanceof EndElementToken) {
        this.openElements--;
      }

      // FORMAT
      if (operator == Operator.MATCH) {
        token.toXML(this.xml);
        if (token instanceof CharactersTokenBase) {
          if (this.config.getWhiteSpaceProcessing() == WhiteSpaceProcessing.IGNORE) {
            this.xml.writeXML(" ");
          }
        }
      } else {
        handleEdit(operator, token);
      }

    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  private void handleEdit(Operator operator, Token token) throws IOException {
    if (token instanceof StartElementToken) {
      token.toXML(this.xml);
      this.xml.attribute(operator == Operator.INS ? "dfx:insert" : "dfx:delete", "true");

      // just output the new line
    } else if (token == SpaceToken.NEW_LINE) {
      token.toXML(this.xml);

      // wrap the characters in a <ins> element
    } else if (token instanceof CharactersTokenBase) {
      this.xml.openElement(Constants.BASE_NS_URI, toElement(operator), false);
      token.toXML(this.xml);
      this.xml.closeElement();
      if (this.config.getWhiteSpaceProcessing() == WhiteSpaceProcessing.IGNORE) {
        this.xml.writeXML(" ");
      }

      // display the attribute normally
    } else if (token instanceof AttributeToken) {
      if (operator == Operator.INS) {
        token.toXML(this.xml);
        this.xml.attribute("ins:" + ((AttributeToken) token).getName(), "true");
      } else {
        // Don't include deleted attributes
        this.xml.attribute("del:" + ((AttributeToken) token).getName(), ((AttributeToken) token).getValue());
      }

      // wrap the char in a <ins> element
    } else if (token instanceof CharToken) {
      this.xml.openElement(Constants.BASE_NS_URI, toElement(operator), false);
      token.toXML(this.xml);
      this.xml.closeElement();

    } else {
      token.toXML(this.xml);
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
  public void setWriteXMLDeclaration(boolean show) {
    this.writeXMLDeclaration = show;
  }

  /**
   * Replaces the prefix mapping.
   *
   * @param mapping The prefix mapping to add.
   */
  @Override
  public void setPrefixMapping(PrefixMapping mapping) {
    this.mapping = mapping;
  }

  /**
   * @return "ins" for insertions or "del" for deletions
   */
  private static String toElement(Operator operator) {
    return operator == Operator.INS ? "ins" : "del";
  }

}
