/*
 * Copyright 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
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
import java.util.Arrays;
import java.util.List;

/**
 * A single text token containing a list of text tokens.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.9.0
 */
public final class TextListToken implements TextToken {

  /**
   * Underlying list of tokens.
   */
  private final TextToken[] tokens;

  public TextListToken(List<? extends TextToken> tokens) {
    this.tokens = tokens.toArray(new TextToken[0]);
  }

  @Override
  public String getCharacters() {
    StringBuilder chars = new StringBuilder();
    for (TextToken token : tokens) chars.append(token.getCharacters());
    return chars.toString();
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    for (TextToken text : tokens) {
      text.toXML(xml);
    }
  }

  @Override
  public void toXML(@NotNull XMLStreamWriter xml) throws XMLStreamException {
    for (TextToken text : tokens) {
      text.toXML(xml);
    }
  }

  @Override
  public String toString() {
    return Arrays.toString(this.tokens);
  }

  @Override
  public int hashCode() {
    return getCharacters().hashCode();
  }

  @Override
  public boolean equals(XMLToken token) {
    if (token == this) return true;
    if (!(token instanceof TextToken)) return false;
    if (this.hashCode() != token.hashCode()) return false;
    TextToken other = (TextToken) token;
    return this.getCharacters().equals(other.getCharacters());
  }

}
