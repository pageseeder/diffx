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

import java.util.List;

public interface ElementToken extends XMLToken {

  /**
   * @return The local name of the element.
   */
  @NotNull String getName();

  /**
   * @return The namespace URI the element belongs to.
   */
  @NotNull String getNamespaceURI();

  /**
   * Returns all the tokens for this element, starting with the
   * <code>StartElementToken</code> and ending with the <code>EndElementToken</code>.
   *
   * @deprecated Use getTokens() instead.
   *
   * @return the list of tokens making up this element
   */
//  @Deprecated(since = "1.1.2", forRemoval = true)
  @Deprecated
  List<XMLToken> getEvents();

  /**
   * Returns all the tokens for this element, starting with the
   * <code>StartElementToken</code> and ending with the <code>EndElementToken</code>.
   *
   * @return the list of tokens making up this element
   */
  List<XMLToken> tokens();

  /**
   * Returns all the tokens for this element, starting with the
   * <code>StartElementToken</code> and ending with the <code>EndElementToken</code>.
   *
   * @return the list of tokens making up this element
   */
  List<XMLToken> getChildren();

  @Override
  default @NotNull XMLTokenType getType() {
    return XMLTokenType.ELEMENT;
  }

}
