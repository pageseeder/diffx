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
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * A particular type of token reserved for ignored white spaces.
 *
 * <p>
 * This class can be used to ignore whitespaces for processing but preserve them for formatting.
 * This token keeps the exact formatting of the white space henceby preserving it, but will consider
 * any instances of this class to be equal regardless of their actual formatting so that the
 * algorithm ignores the differences.
 *
 * @author Christophe Lauret
 * @version 28 March 2010
 */
public final class IgnorableSpaceToken implements TextToken {

  /**
   * The characters for this token.
   */
  private final String characters;

  /**
   * Creates a new ignorable white space token.
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
   */
  @Override
  public String toString() {
    return "ignorable-space";
  }

  /**
   * Always returns the same value.
   */
  @Override
  public int hashCode() {
    return 71;
  }

  /**
   * Returns <code>true</code> if the token is an ignorable white space, regardless of the
   * characters that it matches.
   *
   * @param o The token to compare with this token.
   *
   * @return <code>true</code> if considered equal;
   * <code>false</code> otherwise.
   */
  @Override
  public boolean equals(Object o) {
    return o.getClass() == this.getClass();
  }

  /**
   * Returns <code>true</code> if the token is an ignorable white space, regardless of the
   * characters that it matches.
   *
   * @param token The token to compare with this token.
   *
   * @return <code>true</code> if considered equal;
   * <code>false</code> otherwise.
   */
  @Override
  public boolean equals(XMLToken token) {
    if (this == token)
      return true;
    return token.getClass() == this.getClass();
    // always return true
  }

  /**
   * Returns the characters that this token represents.
   *
   * <p>
   * Note: this method will return the characters as used by Java (ie. Unicode), they may not be
   * suitable for writing to an XML string.
   *
   * @return The characters that this token represents.
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
  public void toXML(XMLStreamWriter xml) throws XMLStreamException {
    xml.writeCharacters(this.characters);
  }

}
