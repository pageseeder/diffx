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

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.token.TextToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.XMLTokenType;
import org.pageseeder.diffx.xml.Namespace;
import org.pageseeder.xmlwriter.XMLWriter;
import org.pageseeder.xmlwriter.XMLWriterNSImpl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

/**
 * Provide an XML report of the XML diff.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.9.0
 */
public class XMLDiffReporter extends XMLDiffOutputBase implements XMLDiffOutput {

  private final XMLWriter xml;

  public XMLDiffReporter(Writer out) {
    this.xml = new XMLWriterNSImpl(out);
  }

  @Override
  public void start() {
    try {
      if (includeXMLDeclaration)
        this.xml.xmlDecl();
      xml.openElement("diff-report", true);
      // Include any declarable namespace
      for (Namespace namespace : this.namespaces) {
        if (Namespace.isDeclarable(namespace)) {
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
  public void handle(@NotNull Operator operator, @NotNull XMLToken token) throws UncheckedIOException, IllegalStateException {
    try {
      xml.openElement(toElementName(operator));
      xml.attribute("type", token.getType().toString());
      if (!token.getName().isEmpty()) {
        xml.attribute("name", token.getName());
        xml.attribute("namespace-uri", token.getNamespaceURI());
      }
      if (token.getType() == XMLTokenType.ATTRIBUTE) {
        xml.attribute("value", token.getValue());
      }
      xml.attribute("class-name", token.getClass().getSimpleName());
      if (token.getType() == XMLTokenType.TEXT) {
        xml.writeText(((TextToken) token).getCharacters());
      }
      xml.closeElement();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  private static String toElementName(Operator operator) {
    assert operator != null;
    switch (operator) {
      case DEL:
        return "delete";
      case INS:
        return "insert";
      case MATCH:
        return "match";
      default:
        return "other";
    }
  }

}
