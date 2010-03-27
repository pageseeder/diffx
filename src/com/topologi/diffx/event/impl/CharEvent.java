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
import com.topologi.diffx.xml.XMLWriter;

/**
 * Event corresponding to a single character.
 * 
 * @author Christophe Lauret
 * @version 28 March 2010
 */
public final class CharEvent extends DiffXEventBase {

  /**
   * The character associated with this event.
   */
  public final char c;

  /**
   * Creates a new character event.
   * 
   * @param c The character to wrap.
   */
  public CharEvent(char c) {
    this.c = c;
  }

  /**
   * {@inheritDoc}
   */
  public int hashCode() {
    return 79 + this.c;
  }

  /**
   * {@inheritDoc}
   */
  public boolean equals(DiffXEvent e) {
    if (e.getClass() != this.getClass()) return false;
    return this.c == ((CharEvent)e).c;
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return "char: '"+this.c+'\'';
  }

  /**
   * {@inheritDoc}
   */
  public void toXML(XMLWriter xml) throws IOException {
    xml.writeText(this.c);
  }

  /**
   * {@inheritDoc}
   */
  public StringBuffer toXML(StringBuffer xml) throws NullPointerException {
    // TODO: ridiculously inefficient !
    return xml.append(ESC.toElementText(new char[]{this.c}, 0, 1));
  }

}
