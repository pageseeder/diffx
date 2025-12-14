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
import org.pageseeder.diffx.xml.XMLStreamable;
import org.pageseeder.xmlwriter.XMLWritable;

import javax.xml.XMLConstants;

/**
 * Defines an XML token that can be processed for diffing.
 *
 * <p>As many equality checks are performed, implementations must provide efficient
 * {@link #equals(XMLToken)} and {@link #hashCode()} methods.</p>
 *
 * <p>Most equality checks are performed against other unequal tokens, so equality checks
 * should generally precompute or cache the hashcode and use it in equality check to speed
 * things up.</p>
 *
 * <p>Note: While this interface defines a type-specific {@link #equals(XMLToken)} method for
 * performance optimization, implementations MUST also override the standard {@link Object#equals(Object)}
 * method to maintain consistent behavior with Java collections and standard equality operations.
 * The implementation of {@link Object#equals(Object)} should delegate to {@link #equals(XMLToken)}
 * after the appropriate type checking.</p>
 *
 * <p>For convenience, this interface extends the <code>XMLWritable</code> and
 * <code>XMLStreamable</code> in order to write token as XML consistently.
 *
 * @author Christophe Lauret
 *
 * @since 0.9.0
 * @version 1.2.2
 */
public interface XMLToken extends Token, XMLWritable, XMLStreamable {

  /**
   * @return The type of token.
   */
  XMLTokenType getType();

  /**
   * Returns the name of the token.
   *
   * <p>For attributes and elements, this is the local name when namespace-aware or
   * the qname if not namespace-aware.</p>
   *
   * <p>For processing instructions, this is the target.</p>
   *
   * <p>Other tokens should generally return an empty string.</p>
   *
   * <p>This method should never return <code>null</code>.
   *
   * @return The name of the token.
   */
  String getName();

  /**
   * Returns the value of the token.
   *
   * <p>For attributes, this is the attribute value.</p>
   *
   * <p>For text tokens, this is the character data.</p>
   *
   * <p>For processing instructions, this is the data.</p>
   *
   * <p>Other tokens, including elements, should generally return an empty string.</p>
   *
   * <p>This method should never return <code>null</code>.
   *
   * @return The value of the token.
   */
  String getValue();

  /**
   * Returns the namespace URI the token.
   *
   * <p>This method should return <code>""</code> (empty string) if the implementation
   * is not namespace-aware or if the token is not bound to any namespace.
   *
   * @return The namespace URI the attribute belongs to or <code>""</code>.
   */
  default String getNamespaceURI() {
    return XMLConstants.NULL_NS_URI;
  }

  /**
   * Indicates whether this token consists entirely of whitespace.
   *
   * <p>This is primarily meaningful for text tokens, but having it
   * on the base interface simplifies the processing of token sequences.
   *
   * @return true if the token consists entirely of whitespace characters,
   *         false otherwise. For non-text tokens, this always returns false.
   */
  default boolean isWhitespace() {
    return false;
  }

  /**
   * Indicates whether the specified token is equal to this token.
   *
   * <p>Implementations of this interface MUST also override {@link Object#equals(Object)}
   * to maintain consistent behavior with Java collections.</p>
   *
   * @param token The token to compare it with this one.
   *
   * @return <code>true</code> if considered equals; <code>false</code> otherwise.
   */
  boolean equals(@Nullable XMLToken token);

}
