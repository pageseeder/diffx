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

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.XMLTokenType;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.stream.XMLStreamWriter;

/**
 * A utility XML token that does not represent any XML.
 *
 * @author Christophe Lauret
 * @version 1.1.2
 * @since 1.1.2
 */
public final class NilToken implements XMLToken {

  @Override
  public @NotNull XMLTokenType getType() {
    return XMLTokenType.OTHER;
  }

  @Override
  public boolean equals(XMLToken token) {
    return token instanceof NilToken;
  }

  @Override
  public void toXML(@NotNull XMLWriter xml) {
    // This token has no XML representation
  }

  @Override
  public void toXML(@NotNull XMLStreamWriter xml) {
    // This token has no XML representation
  }

  @Override
  public @NotNull String getName() {
    return "";
  }

  @Override
  public String getValue() {
    return null;
  }
}
