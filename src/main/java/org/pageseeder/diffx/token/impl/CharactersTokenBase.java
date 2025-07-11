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

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.token.TextToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * A base class for all the characters tokens "characters" SAX event.
 *
 * @author Christophe Lauret
 *
 * @since 0.9.0
 * @version 1.2.0
 */
public abstract class CharactersTokenBase extends TokenBase implements TextToken {

  /**
   * The characters for this token.
   */
  private final String characters;

  /**
   * A suitable hashCode for this token.
   */
  private final int hashCode;

  /**
   * Creates a new characters token.
   *
   * @param seq The char sequence.
   *
   * @throws NullPointerException If the given String is <code>null</code>.
   */
  protected CharactersTokenBase(CharSequence seq) throws NullPointerException {
    this.characters = Objects.requireNonNull(seq.toString(), "The characters cannot be null, use \"\"");
    this.hashCode = toHashCode(seq);
  }

  @Override
  public final int hashCode() {
    return this.hashCode;
  }

  /**
   * Returns <code>true</code> if the token is a character token and its content is equivalent.
   *
   * @param token The token to compare with this token.
   *
   * @return <code>true</code> if considered equal; <code>false</code> otherwise.
   */
  @Override
  public final boolean equals(XMLToken token) {
    if (this == token)
      return true;
    if (!(token instanceof TextToken))
      return false;
    TextToken ce = (TextToken) token;
    return ce.getCharacters().equals(this.getCharacters());
  }

  /**
   * Returns the characters that this token represents.
   *
   * <p>
   * Note: this method will return the characters as used by Java (i.e. Unicode), they may not be
   * suitable for writing to an XML string.
   *
   * @return The characters that this token represents.
   */
  @Override
  public final String getCharacters() {
    return this.characters;
  }

  @Override
  public final void toXML(XMLWriter xml) throws IOException {
    xml.writeText(this.characters);
  }

  @Override
  public void toXML(@NotNull XMLStreamWriter xml) throws XMLStreamException {
    xml.writeCharacters(this.characters);
  }

  /**
   * Calculates the hashcode value from a string.
   *
   * @param s A string from which to calculate the hashcode.
   *
   * @return a suitable hashcode value.
   */
  private static int toHashCode(CharSequence s) {
    assert s != null;
    return 13 * 47 + s.hashCode();
  }

}
