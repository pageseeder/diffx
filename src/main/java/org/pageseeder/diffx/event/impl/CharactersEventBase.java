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

import java.io.IOException;

import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.TextEvent;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * A base class for all the characters events "characters" SAX event.
 *
 * @author Christophe Lauret
 * @version 28 March 2010
 */
public abstract class CharactersEventBase extends DiffXEventBase implements TextEvent {

  /**
   * The characters for this event.
   */
  private final String characters;

  /**
   * A suitable hashCode for this event.
   */
  private final int hashCode;

  /**
   * Creates a new characters event.
   *
   * @param seq The char sequence.
   *
   * @throws NullPointerException If the given String is <code>null</code>.
   */
  public CharactersEventBase(CharSequence seq) throws NullPointerException {
    if (seq == null)
      throw new NullPointerException("The characters cannot be null, use \"\"");
    this.characters = seq.toString();
    this.hashCode = toHashCode(seq);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int hashCode() {
    return this.hashCode;
  }

  /**
   * Returns <code>true</code> if the event is a character event and its content is equivalent.
   *
   * @param e The event to compare with this event.
   *
   * @return <code>true</code> if considered equal; <code>false</code> otherwise.
   */
  @Override
  public final boolean equals(DiffXEvent e) {
    if (this == e)
      return true;
    if (e.getClass() != this.getClass())
      return false;
    CharactersEventBase ce = (CharactersEventBase) e;
    return ce.characters.equals(this.characters);
  }

  /**
   * Returns the characters that this event represents.
   *
   * <p>
   * Note: this method will return the characters as used by Java (ie. Unicode), they may not be
   * suitable for writing to an XML string.
   *
   * @return The characters that this event represents.
   */
  @Override
  public final String getCharacters() {
    return this.characters;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void toXML(XMLWriter xml) throws IOException {
    xml.writeText(this.characters);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final StringBuffer toXML(StringBuffer xml) throws NullPointerException {
    xml.append(ESC.toElementText(this.characters));
    return xml;
  }

  /**
   * Calculates the hashcode value from a string.
   *
   * @param s A string from which to calculate the hashcode.
   * @return a suitable hashcode value.
   */
  private static int toHashCode(CharSequence s) {
    return 13 * 47 + (s != null ? s.hashCode() : 0);
  }

}
