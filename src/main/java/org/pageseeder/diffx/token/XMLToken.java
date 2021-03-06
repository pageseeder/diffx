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
 * <p>For convenience, this interface extends the <code>XMLWritable</code> and
 * <code>XMLStreamable</code> in order to write token as XML consistently.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public interface XMLToken extends Token, XMLWritable, XMLStreamable {

  /**
   * Returns the local name of the token.
   *
   * <p>This method should never return <code>null</code>.
   *
   * @return The local name of the attribute.
   */
  @NotNull String getName();

  /**
   * Returns the value of the token.
   *
   * <p>This method should never return <code>null</code>.
   *
   * @return The value of the attribute.
   */
  String getValue();

  /**
   * @return The type of token.
   */
  @NotNull XMLTokenType getType();

  /**
   * Indicates whether the specified token is equal to this token.
   *
   * @param token The token to compare it with this one.
   *
   * @return <code>true</code> if considered equals;
   * <code>false</code> otherwise.
   */
  boolean equals(XMLToken token);

  /**
   * Returns the namespace URI the token.
   *
   * <p>This method should return <code>""</code> (empty string) if the implementation
   * is not namespace aware or if the token is not bound to any namespace.
   *
   * @return The namespace URI the attribute belongs to or <code>""</code>.
   */
  default @NotNull String getNamespaceURI() {
    return XMLConstants.NULL_NS_URI;
  }

}
