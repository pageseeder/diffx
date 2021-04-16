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

import org.pageseeder.diffx.event.Token;
import org.pageseeder.diffx.event.TextToken;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * A particular type of event reserved for ignored white spaces.
 *
 * <p>
 * This class can be used to ignore whitespaces for processing but preserve them for formatting.
 * This event keeps the exact formatting of the white space henceby preserving it, but will consider
 * any instances of this class to be equal regardless of their actual formatting so that the
 * algorithm ignores the differences.
 *
 * @author Christophe Lauret
 * @version 28 March 2010
 */
public final class IgnorableSpaceToken implements TextToken {

  /**
   * The characters for this event.
   */
  private final String characters;

  /**
   * Creates a new ignorable white space event.
   *
   * @param seq The char sequence.
   *
   * @throws NullPointerException If the given String is <code>null</code>.
   */
  public IgnorableSpaceToken(CharSequence seq) throws NullPointerException {
    if (seq == null)
      throw new NullPointerException("The characters cannot be null, use \"\"");
    this.characters = seq.toString();
  }

  /**
   * Returns "ignorable-space".
   *
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ignorable-space";
  }

  /**
   * Always returns the same value.
   *
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return 71;
  }

  /**
   * Returns <code>true</code> if the event is an ignorable white space, regardless of the
   * characters that it matches.
   *
   * @param o The event to compare with this event.
   *
   * @return <code>true</code> if considered equal;
   *         <code>false</code> otherwise.
   */
  @Override
  public boolean equals(Object o) {
    return o.getClass() == this.getClass();
  }

  /**
   * Returns <code>true</code> if the event is an ignorable white space, regardless of the
   * characters that it matches.
   *
   * @param e The event to compare with this event.
   *
   * @return <code>true</code> if considered equal;
   *         <code>false</code> otherwise.
   */
  @Override
  public boolean equals(Token e) {
    if (this == e)
      return true;
    return e.getClass() == this.getClass();
    // always return true
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
  public String getCharacters() {
    return this.characters;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    // we don't need to parse the whitespace characters.
    xml.writeXML(this.characters);
  }

  @Override
  public String toXML() {
    return this.characters;
  }

  @Override
  public StringBuffer toXML(StringBuffer xml) throws NullPointerException {
    xml.append(this.characters);
    return xml;
  }

}
