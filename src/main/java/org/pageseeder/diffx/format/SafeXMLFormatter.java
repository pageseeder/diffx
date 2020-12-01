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
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.event.AttributeEvent;
import org.pageseeder.diffx.event.CloseElementEvent;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.OpenElementEvent;
import org.pageseeder.diffx.event.impl.CharEvent;
import org.pageseeder.diffx.event.impl.CharactersEventBase;
import org.pageseeder.diffx.event.impl.SpaceEvent;
import org.pageseeder.diffx.sequence.PrefixMapping;
import org.pageseeder.diffx.util.Constants;
import org.pageseeder.diffx.util.Formatting;
import org.pageseeder.xmlwriter.XMLWriterNSImpl;

import java.io.IOException;
import java.io.PrintWriter;
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
 * @version 11 May 2010
 */
public final class SafeXMLFormatter implements XMLDiffXFormatter {

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = false;

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

  // constructors -------------------------------------------------------------------------------

  /**
   * Creates a new formatter on the standard output.
   *
   * <p>This constructor is equivalent to:
   * <pre>new SmartXMLFormatter(new PrintWriter(System.out));</pre>.
   *
   * @see System#out
   *
   * @throws IOException should an I/O exception occurs.
   */
  public SafeXMLFormatter() throws IOException {
    this(new PrintWriter(System.out));
  }

  /**
   * Creates a new formatter using the specified writer.
   *
   * @param w The writer to use.
   *
   * @throws IOException should an I/O exception occurs.
   */
  public SafeXMLFormatter(Writer w) throws IOException {
    this.xml = new XMLWriterNSImpl(w, false);
    if (this.writeXMLDeclaration) {
      this.xml.xmlDecl();
      this.writeXMLDeclaration = false;
    }
  }

  // methods ------------------------------------------------------------------------------------

  @Override
  public void format(DiffXEvent e) throws IOException {
    if (DEBUG) {
      System.err.println("="+e);
    }
    // namespaces declaration
    if (e instanceof OpenElementEvent) {
      if (this.openElements == 0) Formatting.declareNamespaces(this.xml, this.mapping);
      this.openElements++;
    } else if (e instanceof CloseElementEvent) {
      this.openElements--;
    }
    e.toXML(this.xml);
    if (e instanceof CharactersEventBase)
      if (this.config.getWhiteSpaceProcessing() == WhiteSpaceProcessing.IGNORE) {
        this.xml.writeXML(" ");
      }
    this.xml.flush();
  }

  @Override
  public void insert(DiffXEvent e) throws IOException {
    if (DEBUG) {
      System.err.println("+"+e);
    }
    // insert an attribute to specify
    if (e instanceof OpenElementEvent) {
      // namespaces declaration
      if (this.openElements == 0) {
        Formatting.declareNamespaces(this.xml, this.mapping);
        this.openElements++;
      }
      e.toXML(this.xml);
      this.xml.attribute("dfx:insert", "true");

      // just output the new line
    } else if (e == SpaceEvent.NEW_LINE) {
      e.toXML(this.xml);

      // wrap the characters in a <ins> element
    } else if (e instanceof CharactersEventBase) {
      this.xml.openElement(Constants.BASE_NS_URI, "ins", false); //this.xml.openElement("ins", false);
      e.toXML(this.xml);
      this.xml.closeElement();
      if (this.config.getWhiteSpaceProcessing() == WhiteSpaceProcessing.IGNORE) {
        this.xml.writeXML(" ");
      }

      // display the attribute normally
    } else if (e instanceof AttributeEvent) {
      e.toXML(this.xml);
      this.xml.attribute("ins:"+((AttributeEvent)e).getName(), "true");

      // wrap the char in a <ins> element
    } else if (e instanceof CharEvent) {
      this.xml.openElement(Constants.BASE_NS_URI, "ins", false); //this.xml.openElement("ins", false);
      e.toXML(this.xml);
      this.xml.closeElement();

    } else if (e instanceof CloseElementEvent) {
      this.openElements--;
      e.toXML(this.xml);

    // just format naturally
    } else {
      e.toXML(this.xml);
    }
    this.xml.flush();
  }

  @Override
  public void delete(DiffXEvent e) throws IOException {
    if (DEBUG) {
      System.err.println("-"+e);
    }
    // insert an attribute to specify
    if (e instanceof OpenElementEvent) {
      // namespaces declaration
      if (this.openElements == 0) {
        Formatting.declareNamespaces(this.xml, this.mapping);
        this.openElements++;
      }
      e.toXML(this.xml);
      this.xml.attribute("dfx:delete", "true");

      // just output the new line
    } else if (e == SpaceEvent.NEW_LINE) {
      e.toXML(this.xml);

      // wrap the characters in a <del> element
    } else if (e instanceof CharactersEventBase) {
      this.xml.openElement(Constants.BASE_NS_URI, "del", false); //this.xml.openElement("del", false);
      e.toXML(this.xml);
      this.xml.closeElement();
      if (this.config.getWhiteSpaceProcessing() == WhiteSpaceProcessing.IGNORE) {
        this.xml.writeXML(" ");
      }

      // put the attribute as part of the 'delete' namespace
    } else if (e instanceof AttributeEvent) {
      this.xml.attribute("del:"+((AttributeEvent)e).getName(), ((AttributeEvent)e).getValue());

      // wrap the char in a <del> element
    } else if (e instanceof CharEvent) {
      this.xml.openElement(Constants.BASE_NS_URI, "del", false); //this.xml.openElement("del", false);
      e.toXML(this.xml);
      this.xml.closeElement();

    } else if (e instanceof CloseElementEvent) {
      this.openElements--;
      e.toXML(this.xml);

    // just format naturally
    } else {
      e.toXML(this.xml);
    }
    this.xml.flush();
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
   * Replaces the prefix mapping.
   *
   * @param mapping The prefix mapping to add.
   */
  @Override
  public void declarePrefixMapping(PrefixMapping mapping) {
    this.mapping = mapping;
  }

}
