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

/**
 * XMLToken corresponding to a single character.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.7.0
 */
public final class CharToken extends TokenBase implements TextToken {

  /**
   * The character associated with this token.
   */
  public final char c;

  /**
   * Creates a new character token.
   *
   * @param c The character to wrap.
   */
  public CharToken(char c) {
    this.c = c;
  }

  public char getChar() {
    return this.c;
  }

  @Override
  public int hashCode() {
    return this.c;
  }

  @Override
  public boolean equals(XMLToken token) {
    if (token.getClass() != this.getClass()) return false;
    return this.c == ((CharToken) token).c;
  }

  @Override
  public String toString() {
    return Character.toString(this.c);
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.writeText(this.c);
  }

  @Override
  public void toXML(@NotNull XMLStreamWriter xml) throws XMLStreamException {
    xml.writeCharacters(new char[]{this.c}, 0, 1);
  }

  @Override
  public String getCharacters() {
    return Character.toString(this.c);
  }
}
