/*
 * Copyright (c) 2010-2023 Allette Systems (Australia)
 *    http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.token.impl;

import org.jspecify.annotations.Nullable;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.XMLTokenType;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.stream.XMLStreamWriter;

/**
 * Represents a singleton special token with no value or XML representation.
 *
 * <p>This token can be used to signify the absence of meaningful data or a placeholder
 * in XML-related operations.
 *
 * @author Christophe Lauret
 *
 * @since 1.1.2
 * @version 1.2.2
 */
public final class NilToken implements XMLToken {

  private static final XMLToken NIL = new NilToken();

  private NilToken() {}

  /**
   * Provides a singleton instance of the NilToken.
   *
   * @return The singleton instance of NilToken that represents a special token with no value or XML representation.
   */
  public static XMLToken getInstance() {
    return NIL;
  }

  @Override
  public XMLTokenType getType() {
    return XMLTokenType.OTHER;
  }

  @Override
  public boolean equals(@Nullable XMLToken token) {
    return token == this;
  }

  @Override
  public void toXML(XMLWriter xml) {
    // This token has no XML representation
  }

  @Override
  public void toXML(XMLStreamWriter xml) {
    // This token has no XML representation
  }

  @Override
  public String getName() {
    return "";
  }

  @Override
  public String getValue() { return ""; }
}
