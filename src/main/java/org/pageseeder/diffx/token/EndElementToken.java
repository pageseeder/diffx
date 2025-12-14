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
 * The token corresponding to the <code>endElement</code> SAX event.
 *
 * @author Christophe Lauret
 *
 * @version 1.3.0
 * @since 0.5.0
 */
public interface EndElementToken extends XMLToken {

  /**
   * Returns the local name of the element.
   *
   * @return The local name of the element.
   */
  String getName();

  @Override
  default String getValue() {
    return "";
  }

  /**
   * Returns the namespace URI the element belongs to.
   *
   * <p>This method should return <code>null</code> if the implementation
   * is not namespace-aware.
   *
   * @return The namespace URI the element belongs to.
   */
  @Override
  String getNamespaceURI();

  /**
   * Returns the corresponding start element.
   *
   * @return The corresponding start element.
   */
  StartElementToken getStartElement();

  /**
   * Indicates whether the specified open element token matches this close
   * element token.
   *
   * <p>This method first checks whether the open element token is the same as
   * token returned by the {@link #getStartElement()} method, if not it simply
   * compares the name of the element and the namespace URI it belongs to.
   *
   * @param token The open element token to test.
   *
   * @return <code>true</code> if there is a match;
   * <code>false</code> otherwise.
   */
  boolean match(StartElementToken token);

  /**
   * Compares this {@code EndElementToken} instance with the specified {@code XMLToken} for equality.
   *
   * <p>This method first checks if the given token is the same instance as this token. If not, it verifies
   * whether the given token is an instance of {@code EndElementToken} and compares their namespace URIs
   * and element names for equality.
   *
   * @param token The {@code XMLToken} to compare with this token.
   * @return {@code true} if the specified token is equal to this token; {@code false} otherwise.
   */
  @Override
  default boolean equals(@Nullable XMLToken token) {
    if (token == this) return true;
    if (!(token instanceof EndElementToken)) return false;
    EndElementToken other = (EndElementToken) token;
    return getNamespaceURI().equals(other.getNamespaceURI()) &&
        getName().equals(other.getName());
  }

  @Override
  default XMLTokenType getType() {
    return XMLTokenType.END_ELEMENT;
  }

  @Override
  default void toXML(XMLWriter xml) throws IOException {
    xml.closeElement();
  }

  @Override
  default void toXML(XMLStreamWriter xml) throws XMLStreamException {
    xml.writeEndElement();
  }

}
