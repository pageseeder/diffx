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
 * A text token representing a word.
 *
 * @author Christophe Lauret
 *
 * @since 0.9.0
 * @version 0.9.0
 */
public final class WordToken extends CharactersTokenBase implements TextToken {

  /**
   * Creates a new word token.
   *
   * @param w The word as a string.
   *
   * @throws NullPointerException If the given String is <code>null</code>.
   */
  public WordToken(CharSequence w) {
    super(w);
  }

  @Override
  public String toString() {
    return "\"" + getCharacters() + '"';
  }

}
