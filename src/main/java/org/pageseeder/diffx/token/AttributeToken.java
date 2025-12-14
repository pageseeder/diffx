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

import org.jspecify.annotations.Nullable;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * A token for attributes.
 *
 * @author Christophe Lauret
 *
 * @since 0.5.0
 * @version 1.2.2
 */
public interface AttributeToken extends XMLToken {

  /**
   * Returns the local name of the attribute.
   *
   * <p>This method should never return <code>null</code>.
   *
   * @return The local name of the attribute.
   */
  String getName();

  /**
   * Returns the value of the attribute.
   *
   * <p>This method should never return <code>null</code>.
   *
   * @return The value of the attribute.
   */
  String getValue();

  /**
   * Returns the namespace URI of the attribute.
   *
   * <p>This method should return an empty string if the implementation
   * is not namespace-aware or if the attribute is not bound to any namespace.
   *
   * @return The namespace URI the attribute belongs to, or an empty string if none.
   */
  @Override
  String getNamespaceURI();

  @Override
  default XMLTokenType getType() {
    return XMLTokenType.ATTRIBUTE;
  }

  /**
   * Indicates whether the specified token is equal to this attribute token.
   *
   * <p>Two attribute tokens are considered equal if they have the same namespace URI,
   * name, and value.</p>
   *
   * @param token The token to compare it with this one.
   *
   * @return <code>true</code> if considered equals; <code>false</code> otherwise.
   */
  @Override
  default boolean equals(@Nullable XMLToken token) {
    if (token == this) return true;
    if (!(token instanceof AttributeToken)) return false;
    AttributeToken other = (AttributeToken) token;
    return getNamespaceURI().equals(other.getNamespaceURI()) &&
        getName().equals(other.getName()) &&
        getValue().equals(other.getValue());
  }

  @Override
  default void toXML(XMLWriter xml) throws IOException {
    xml.attribute(this.getNamespaceURI(), this.getName(), this.getValue());
  }

  @Override
  default void toXML(XMLStreamWriter xml) throws XMLStreamException {
    // We shouldn't specify a namespace URI if empty on an XMLStreamWriter
    if (this.getNamespaceURI().isEmpty())
      xml.writeAttribute(this.getName(), this.getValue());
    else
      xml.writeAttribute(this.getNamespaceURI(), this.getName(), this.getValue());
  }

}
