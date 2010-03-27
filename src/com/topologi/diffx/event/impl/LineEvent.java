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
 * An interface for any data that comes from a text node.
 * 
 * @author Christophe Lauret
 * @version 10 March 2005
 */
public final class LineEvent extends DiffXEventBase
                             implements TextEvent {

  /**
   * The characters for this event.
   */
  private final CharSequence characters;

  /**
   * The line number.
   */
  private final int lineNumber;

  /**
   * Creates a new line event.
   * 
   * @param line       The char sequence.
   * @param lineNumber The line number.
   * 
   * @throws NullPointerException If the given String is <code>null</code>. 
   */
  public LineEvent(CharSequence line, int lineNumber) throws NullPointerException {
    if (line == null)
      throw new NullPointerException("The line cannot be null, use \"\"");
    this.characters = line;
    this.lineNumber = lineNumber;
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return "line:"+this.lineNumber+": \""+this.getCharacters()+'"';
  }

  /**
   * {@inheritDoc}
   */
  public int hashCode() {
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
  public boolean equals(DiffXEvent e) {
    if (e == null) return false; 
    if (this == e) return true;
    if (e.getClass() != LineEvent.class) return false;
    LineEvent ce = (LineEvent)e;
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
  public String getCharacters() {
    return this.characters.toString();
  }

  /**
   * Returns the line number.
   * 
   * @return The line number.
   */
  public int getLineNumber() {
    return this.lineNumber;
  }

  /**
   * {@inheritDoc}
   */
  public void toXML(XMLWriter xml) throws IOException {
    xml.writeXML(this.characters.toString());
  }

  /**
   * {@inheritDoc}
   */
  public StringBuffer toXML(StringBuffer xml) throws NullPointerException {
    xml.append(this.characters);
    return xml;
  }

}