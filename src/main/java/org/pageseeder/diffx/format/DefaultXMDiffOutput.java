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
import org.pageseeder.diffx.event.impl.SpaceEvent;
import org.pageseeder.diffx.sequence.Namespace;
import org.pageseeder.diffx.sequence.PrefixMapping;
import org.pageseeder.xmlwriter.XMLWriter;
import org.pageseeder.xmlwriter.XMLWriterNSImpl;

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

  public static final Namespace DIFF_NAMESPACE = new Namespace(DIFF_NS_URI, DIFF_NS_PREFIX);

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = false;

  private final XMLWriter xml;

  private PrefixMapping mapping = PrefixMapping.noNamespace();

  private boolean includeXMLDeclaration;

  private List<String> insertedAttributes = new ArrayList<>();

  private List<String> deletedAttributes = new ArrayList<>();

  /**
   * Used to know if all elements have been closed, in which case the namespace
   * mapping should be redeclared before opening a new element
   */
  private int openElements = 0;

  public DefaultXMDiffOutput(Writer out) {
    this.xml = new XMLWriterNSImpl(out);
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
      if (includeXMLDeclaration)
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
  public void handle(Operator operator, DiffXEvent event) throws UncheckedIOException, IllegalStateException {
    if (DEBUG) System.err.println(operator.toString()+event);
    try {
      if (operator == Operator.MATCH) handleMatch(event);
      else handleClash(operator, event);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  private void handleMatch(DiffXEvent event) throws IOException {
    // namespaces declaration
    if (event instanceof OpenElementEvent) {
      if (this.openElements == 0) declareNamespaces();
      this.openElements++;
    } else if (event instanceof CloseElementEvent) {
      this.openElements--;
    }
    event.toXML(this.xml);
    // TODO x
//      if (event instanceof CharactersEventBase)
//        if (this.config.isIgnoreWhiteSpace() && !this.config.isPreserveWhiteSpace()) {
//          this.xml.writeXML(" ");
//        }
  }

  private void handleClash(Operator operator, DiffXEvent event) throws IOException {
    // We must flush the inserted/deleted attributes
    if (!(event instanceof AttributeEvent)) {
      this.flushAttributes();
    }
    // insert an attribute to specify
    if (event instanceof OpenElementEvent) {
      // namespaces declaration
      if (this.openElements == 0) {
        declareNamespaces();
        this.openElements++;
      }
      event.toXML(this.xml);
      this.xml.attribute(DIFF_NS_URI, operator == Operator.INS ? "ins" : "del", "true");

      // just output the new line
    } else if (event == SpaceEvent.NEW_LINE) {
      event.toXML(this.xml);

      // wrap the characters in a <ins> element
    } else if (event instanceof TextEvent) {
      this.xml.openElement(operator == Operator.INS ? "ins" : "del", false);
      event.toXML(this.xml);
      this.xml.closeElement();
      // TODO
//      if (!(event instanceof CharEvent) && this.config.isIgnoreWhiteSpace() && !this.config.isPreserveWhiteSpace()) {
//        this.xml.writeXML(" ");
//      }

    } else if (event instanceof AttributeEvent) {
      // display the attribute normally
      event.toXML(this.xml);
      String name = ((AttributeEvent)event).getName();
      if (operator == Operator.INS) this.insertedAttributes.add(name);
      else this.deletedAttributes.add(name);

    } else if (event instanceof CloseElementEvent) {
      this.openElements--;
      event.toXML(this.xml);

    } else {
      event.toXML(this.xml);
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

  /**
   * Flush the inserted or deleted attributes on the element.
   *
   * This method must be called before we finish writing the start element tag.
   */
  private void flushAttributes() throws IOException {
    if (!this.insertedAttributes.isEmpty()) {
      this.xml.attribute(DIFF_NS_URI, "ins", this.insertedAttributes.stream().collect(Collectors.joining(" ")));
      this.insertedAttributes.clear();
    }
    if (!this.deletedAttributes.isEmpty()) {
      this.xml.attribute(DIFF_NS_URI, "del", this.deletedAttributes.stream().collect(Collectors.joining(" ")));
      this.deletedAttributes.clear();
    }
  }
}
