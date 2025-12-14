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

import java.util.List;

/**
 * Represents an XML element token that encapsulates the complete structure
 * of an XML element, including its start, end, and content. This interface
 * extends {@link XMLToken} to provide additional methods specific to XML
 * elements, including access to the start and end tokens, the element's
 * local name, its namespace, and the associated content between the
 * start and end tags.
 *
 * <p>Implementations of this interface are expected to handle XML element
 * tokens and their associated operations, including retrieval of start
 * and end tokens, child tokens, attributes, and descendant elements.
 *
 * @author Christophe Lauret
 *
 * @version 1.2.0
 * @since 0.7.0
 */
public interface ElementToken extends XMLToken {

  /**
   * @return The local name of the element.
   */
  String getName();

  /**
   * @return The namespace URI the element belongs to.
   */
  @Override
  String getNamespaceURI();

  /**
   * Retrieves the starting element token that corresponds to the beginning
   * of this element. The starting token provides information regarding the
   * element name and its namespace URI.
   *
   * @return The StartElementToken representing the beginning of the element.
   */
  StartElementToken getStart();

  /**
   * Retrieves the ending element token that corresponds to the end of this
   * element. The ending token provides information regarding the element name
   * and its namespace URI.
   *
   * @return The EndElementToken representing the end of the element.
   */
  EndElementToken getEnd();

  /**
   * Returns all the tokens for this element, starting with the
   * <code>StartElementToken</code> and ending with the <code>EndElementToken</code>.
   *
   * @return the list of tokens making up this element
   */
  List<XMLToken> tokens();

  /**
   * Returns all the tokens between the start and end element tokens.
   *
   * <p>If this element has attributes, these are returned first.</p>
   *
   * @return the list of tokens between the start and end element tokens.
   *
   * @deprecated Use getContent() instead.
   */
  @Deprecated(since = "1.2.0", forRemoval = true)
  List<XMLToken> getChildren();

  /**
   * Returns all the tokens contained within this element, excluding the start and end element tokens.
   *
   * <p>The returned list may include attribute tokens, text tokens, descendant elements, and other content
   * tokens present between the start and end tags of this element. If the element has attributes, they typically
   * appear first in the list.
   *
   * @return the list of tokens that are present between the start and end element tokens.
   */
  List<XMLToken> getContent();

  @Override
  default XMLTokenType getType() {
    return XMLTokenType.ELEMENT;
  }

}
