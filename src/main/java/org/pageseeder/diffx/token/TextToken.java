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
package org.pageseeder.diffx.token;

import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * An interface for any data that comes from a text node.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.5.0
 */
public interface TextToken extends Token {

  /**
   * Returns the characters that this token represents.
   *
   * <p>Note: this method will return the characters as used by Java (ie. Unicode), they
   * may not be suitable for writing to an XML string.
   *
   * @return The characters that this token represents.
   */
  String getCharacters();

  @Override
  default TokenType getType() {
    return TokenType.TEXT;
  }

  @Override
  default void toXML(XMLWriter xml) throws IOException {
    xml.writeText(this.getCharacters());
  }

  @Override
  default void toXML(XMLStreamWriter xml) throws XMLStreamException {
    xml.writeCharacters(this.getCharacters());
  }

}
