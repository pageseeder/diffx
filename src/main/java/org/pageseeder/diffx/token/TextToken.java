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

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * An interface for any data that comes from a text node.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.5.0
 */
public interface TextToken extends XMLToken {

  /**
   * Returns the characters that this token represents.
   *
   * <p>Note: this method will return the raw characters as used by Java (i.e. Unicode),
   * some characters must be properly escaped when writing XML.
   *
   * @return The characters that this token represents.
   */
  String getCharacters();

  /**
   * Returns the type of this XML token.
   *
   * <p>This default implementation always returns {@code XMLTokenType.TEXT}, indicating
   * that this token represents text content within an XML document.
   *
   * @return The type of this XML token, which is always {@code XMLTokenType.TEXT}.
   */
  @Override
  default XMLTokenType getType() {
    return XMLTokenType.TEXT;
  }

  /**
   * Returns an empty string a text tokens have no name.
   *
   * @return An empty string if not applicable.
   */
  @Override
  default String getName() {
    return "";
  }

  /**
   * Returns the value of the token.
   *
   * <p>For text tokens, this corresponds to the character data associated with the token.
   * Specifically, this method delegates to {@link #getCharacters()} to retrieve the raw text value.
   *
   * @return The value of the token, as represented by the character data.
   */
  @Override
  default String getValue() {
    return getCharacters();
  }

  /**
   * Determines if this text token consists entirely of whitespace.
   *
   * @return true if the token consists entirely of whitespace characters,
   *         false otherwise. For non-text tokens, this always returns false.
   */
  @Override
  default boolean isWhitespace() {
    String value = getValue();
    for (int i = 0; i < value.length(); i++) {
      if (!Character.isWhitespace(value.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Compares this {@code TextToken} with the specified {@code XMLToken} for equality.
   *
   * <p>The tokens are considered equal if they are the same instance or if they are of the same type
   * and their values, as retrieved through {@link #getValue()}, are equal.
   *
   * @param token The {@code XMLToken} to compare with this token.
   * @return {@code true} if the specified token is equal to this token; {@code false} otherwise.
   */
  @Override
  default boolean equals(@Nullable XMLToken token) {
    if (token == this) return true;
    if (!(token instanceof TextToken)) return false;
    TextToken other = (TextToken) token;
    return getValue().equals(other.getValue());
  }

  /**
   * Returns the namespace URI of the token.
   *
   * <p>This implementation always returns an empty string, indicating that
   * the token is not namespace-aware or is not bound to any namespace.
   *
   * @return An empty string ("") representing the namespace URI.
   */
  @Override
  default String getNamespaceURI() {
    return XMLConstants.NULL_NS_URI;
  }

  @Override
  default void toXML(XMLWriter xml) throws IOException {
    xml.writeText(this.getCharacters());
  }

  @Override
  default void toXML(XMLStreamWriter xml) throws XMLStreamException {
    xml.writeCharacters(this.getCharacters());
  }

}
