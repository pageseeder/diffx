/*
 * Copyright 2010-2025 Allette Systems (Australia)
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
package org.pageseeder.diffx.token.impl;

import org.jspecify.annotations.Nullable;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.XMLTokenType;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * Represents the "End Document" token in an XML processing context.
 *
 * <p>This token signifies the conclusion of an XML document and is primarily used
 * in scenarios involving XML token comparison or processing. Its type is
 * defined as {@link XMLTokenType#END_DOCUMENT}.
 *
 * <p>As an implementation of the {@link XMLToken} interface, this token provides
 * standard methods to interact with XML, although its contributions to XML
 * serialization are effectively null (e.g., no XML output is generated).
 *
 * <p>Equality is based on whether the compared instance is also an
 * {@code EndDocumentToken}.
 *
 * @author Christophe Lauret
 *
 * @since 1.2.0
 * @version 1.2.0
 */
public final class EndDocumentToken implements XMLToken {

  @Override
  public String getName() {
    return "";
  }

  @Override
  public String getValue() {
    return "";
  }

  @Override
  public XMLTokenType getType() {
    return XMLTokenType.END_DOCUMENT;
  }

  @Override
  public boolean equals(@Nullable XMLToken token) {
    return token != null && token.getType() == XMLTokenType.END_DOCUMENT;
  }

  @Override
  public void toXML(XMLStreamWriter xml) throws XMLStreamException {
    // Does nothing
  }

  @Override
  public void toXML(XMLWriter xmlWriter) throws IOException {
    // Does nothing
  }

}
