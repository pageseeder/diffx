/*
 * Copyright 2010-2021 Allette Systems (Australia)
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
import org.pageseeder.diffx.event.*;
import org.pageseeder.diffx.sequence.Namespace;
import org.pageseeder.diffx.sequence.PrefixMapping;
import org.pageseeder.xmlwriter.XMLWriter;
import org.pageseeder.xmlwriter.XMLWriterNSImpl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

/**
 * Provide an XML report of the XML diff.
 */
public class XMLDiffReporter implements XMLDiffOutput {

  private final XMLWriter xml;

  private PrefixMapping mapping = PrefixMapping.noNamespace();

  private boolean includeXMLDeclaration;

  public XMLDiffReporter(Writer out) {
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
      xml.openElement("diff-report", true);
      // Include any namespace (except XML and no namespace)
      for (Namespace namespace : this.mapping) {
        if (!Namespace.NO_NAMESPACE.equals(namespace)
            && !Namespace.XML_NAMESPACE.equals(namespace)) {
          xml.openElement("namespace");
          xml.attribute("uri", namespace.getUri());
          xml.attribute("prefix", namespace.getPrefix());
          xml.openElement("namespace");
        }
      }
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  public void end() {
    try {
      xml.closeElement();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  public void handle(Operator operator, DiffXEvent event) throws UncheckedIOException, IllegalStateException {
    try {
      xml.openElement(toElementName(operator));
      xml.attribute("type", event.getType());
      if (event instanceof Namespaceable) {
        xml.attribute("name", ((Namespaceable)event).getName());
        xml.attribute("namespace-uri", ((Namespaceable)event).getURI());
      }
      if (event instanceof AttributeEvent) {
        xml.attribute("value", ((AttributeEvent)event).getValue());
      }
      xml.attribute("class-name", event.getClass().getSimpleName());
      if (event instanceof TextEvent) {
        xml.writeText(((TextEvent) event).getCharacters());
      }
      xml.closeElement();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  private static String toElementName(Operator operator) {
    assert operator != null;
    switch (operator) {
      case DEL: return "delete";
      case INS: return "insert";
      case MATCH: return "match";
      default: return "other";
    }
  }

}
