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

import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * A token for attributes.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.5.0
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
   * Returns the namespace URI the attribute belongs to.
   *
   * <p>This method should return <code>null</code> if the implementation
   * is not namespace aware or if the attribute is not bound to any namespace.
   *
   * @return The namespace URI the attribute belongs to or <code>null</code>.
   */
  String getNamespaceURI();

  @Override
  default XMLTokenType getType() {
    return XMLTokenType.ATTRIBUTE;
  }

  @Override
  default boolean equals(XMLToken token) {
    if (!(token instanceof AttributeToken)) return false;
    if (token.hashCode() != this.hashCode()) return false;
    AttributeToken other = (AttributeToken) token;
    return other.getName().equals(this.getName())
        && other.getValue().equals(this.getValue())
        && other.getNamespaceURI().equals(this.getNamespaceURI());
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
