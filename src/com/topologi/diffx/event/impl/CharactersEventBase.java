/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.event.impl;

import java.io.IOException;

import com.topologi.diffx.event.DiffXEvent;
import com.topologi.diffx.event.TextEvent;
import com.topologi.diffx.xml.XMLWriter;

/**
 * A base class for all the characters events "characters" SAX event.
 * 
 * @author Christophe Lauret
 * @version 15 December 2004
 */
public abstract class CharactersEventBase extends DiffXEventBase
                                          implements TextEvent {

  /**
   * The characters for this event.
   */
  private final String characters;

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
  }

  /**
   * {@inheritDoc}
   */
  public final int hashCode() {
    return this.characters.hashCode();
  }

  /**
   * Returns <code>true</code> if the event is a character event and the content is equivalent.  
   * 
   * @param e The event to compare with this event.
   * 
   * @return <code>true</code> if considered equal;
   *         <code>false</code> otherwise.
   */
  public final boolean equals(DiffXEvent e) {
    if (this == e) return true;
    if (e.getClass() != this.getClass()) return false;
    CharactersEventBase ce = (CharactersEventBase)e;
    return (ce.characters.equals(this.characters));
  }

  /**
   * Returns the characters that this event represents.
   * 
   * <p>Note: this method will return the characters as used by Java (ie. Unicode), they
   * may not be suitable for writing to an XML string.
   * 
   * @return The characters that this event represents.
   */
  public final String getCharacters() {
    return this.characters;
  }

  /**
   * {@inheritDoc}
   */
  public final void toXML(XMLWriter xml) throws IOException {
    xml.writeText(this.characters);
  }

  /**
   * {@inheritDoc}
   */
  public final StringBuffer toXML(StringBuffer xml) throws NullPointerException {
    xml.append(ESC.toElementText(this.characters));
    return xml;
  }

}
