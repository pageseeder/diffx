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

/**
 * An event corresponds to the "characters" SAX event.
 *
 * <p>
 * This event can be used to represent the text content of entire element. Typically, this would
 * happen when there is no need to examine the text content of the node.
 *
 * @author Christophe Lauret
 * @version 28 March 2010
 */
public final class CharactersEvent extends CharactersEventBase {

  /**
   * Creates a new characters event.
   *
   * @param seq The char sequence.
   *
   * @throws NullPointerException If the given String is <code>null</code>.
   */
  public CharactersEvent(CharSequence seq) throws NullPointerException {
    super(seq);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "characters: \""+getCharacters()+'"';
  }

}
