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
 * An interface for any data that comes from a text node.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class LineEvent extends DiffXEventBase implements TextEvent {

  /**
   * The characters for this event.
   */
  private final CharSequence characters;

  /**
   * The line number.
   */
  private final int lineNumber;

  /**
   * Hashcode value for this class
   */
  private final int hashCode;

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
    this.hashCode = toHashCode(line);
  }

  @Override
  public String toString() {
    return "line:"+this.lineNumber+": \""+getCharacters()+'"';
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  /**
   * Returns <code>true</code> if the event is a character event and the content is equivalent.
   *
   * @param e The event to compare with this event.
   *
   * @return <code>true</code> if considered equal;
   *         <code>false</code> otherwise.
   */
  @Override
  public boolean equals(DiffXEvent e) {
    if (e == null) return false;
    if (this == e) return true;
    if (e.getClass() != LineEvent.class) return false;
    LineEvent ce = (LineEvent)e;
    return ce.characters.equals(this.characters);
  }

  /**
   * Returns the characters that this event represents.
   *
   * <p>Note: this method will return the characters as used by Java (ie. Unicode), they
   * may not be suitable for writing to an XML string.
   *
   * @return The characters that this event represents.
   */
  @Override
  public String getCharacters() {
    return this.characters.toString();
  }

  /**
   * @return The line number.
   */
  public int getLineNumber() {
    return this.lineNumber;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.writeXML(this.characters.toString());
  }

  @Override
  public StringBuffer toXML(StringBuffer xml) throws NullPointerException {
    xml.append(this.characters);
    return xml;
  }

  /**
   * Calculates the hashcode for this event.
   *
   * @param line The comment string.
   * @return a number suitable as a hashcode.
   */
  private int toHashCode(CharSequence line) {
    assert line != null;
    return 29*59 + line.hashCode();
  }

}
