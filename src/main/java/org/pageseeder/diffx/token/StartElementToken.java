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
package org.pageseeder.diffx.token;

import org.jetbrains.annotations.NotNull;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * The token corresponding to the <code>startElement</code> SAX event.
 *
 * @author Christophe Lauret
 *
 * @since 0.5.0
 * @version 1.2.2
 */
public interface StartElementToken extends XMLToken {

  @Override
  default @NotNull XMLTokenType getType() {
    return XMLTokenType.START_ELEMENT;
  }

  @Override
  default @NotNull String getValue() {
    return "";
  }

  /**
   * Indicates whether the specified token is equal to this start element token.
   *
   * <p>Two start element tokens are considered equal if they have the same namespace URI
   * and name.</p>
   *
   * @param token The token to compare it with this one.
   *
   * @return <code>true</code> if considered equals; <code>false</code> otherwise.
   */
  @Override
  default boolean equals(XMLToken token) {
    if (token == this) return true;
    if (!(token instanceof StartElementToken)) return false;
    StartElementToken other = (StartElementToken) token;
    return getNamespaceURI().equals(other.getNamespaceURI()) &&
        getName().equals(other.getName());
  }

  @Override
  default void toXML(@NotNull XMLWriter xml) throws IOException {
    xml.openElement(this.getNamespaceURI(), this.getName(), false);
  }

  @Override
  default void toXML(@NotNull XMLStreamWriter xml) throws XMLStreamException {
    // We shouldn't specify a namespace URI if empty on an XMLStreamWriter
    if (this.getNamespaceURI().isEmpty()) {
      xml.writeStartElement(this.getName());
    } else {
      xml.writeStartElement(this.getNamespaceURI(), this.getName());
    }
  }

}
