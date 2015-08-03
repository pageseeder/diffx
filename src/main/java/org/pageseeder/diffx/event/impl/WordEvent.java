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
package org.pageseeder.diffx.event.impl;

import org.pageseeder.diffx.event.TextEvent;

/**
 * A text event representing a word.
 *
 * @author Christophe Lauret
 * @version 27 March 2010
 */
public final class WordEvent extends CharactersEventBase implements TextEvent {

  /**
   * Creates a new word event.
   *
   * @param w The word as a string.
   *
   * @throws NullPointerException If the given String is <code>null</code>.
   */
  public WordEvent(CharSequence w) throws NullPointerException {
    super(w);
  }

  @Override
  public String toString() {
    return "word: \""+getCharacters()+'"';
  }

}
