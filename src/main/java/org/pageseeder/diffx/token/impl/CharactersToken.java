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
package org.pageseeder.diffx.token.impl;

import org.pageseeder.diffx.token.TextToken;

/**
 * A token corresponds to the "characters" SAX event.
 *
 * <p>
 * This token can be used to represent the text content of entire element. Typically, this would
 * happen when there is no need to examine the text content of the node.
 *
 * @author Christophe Lauret
 *
 * @since 0.6.0
 * @version 0.9.0
 */
public final class CharactersToken extends CharactersTokenBase implements TextToken {

  /**
   * Creates a new characters token.
   *
   * @param text The char sequence.
   *
   * @throws NullPointerException If the given String is <code>null</code>.
   */
  public CharactersToken(CharSequence text) {
    super(text);
  }

  @Override
  public String toString() {
    return "\"" + getCharacters() + '"';
  }

}
