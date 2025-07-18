/*
 * Copyright 2010-2025 Allette Systems (Australia)
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
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.XMLTokenType;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * Represents the "start of an XML document" token used in processing and diffing XML structures.
 *
 * <p>This token corresponds to the {@link XMLTokenType#START_DOCUMENT} type and signifies the beginning of
 * an XML document. It is primarily used in scenarios where XML tokens are compared or processed, and
 * its presence helps denote structural boundaries within the XML document.
 *
 * @author Christophe Lauret
 *
 * @since 1.2.0
 * @version 1.2.0
 */
public class StartDocumentToken implements XMLToken {

  @Override
  public @NotNull String getName() {
    return "";
  }

  @Override
  public @NotNull String getValue() {
    return "";
  }

  @Override
  public @NotNull XMLTokenType getType() {
    return XMLTokenType.START_DOCUMENT;
  }

  @Override
  public boolean equals(XMLToken token) {
    return token != null && token.getType() == XMLTokenType.START_DOCUMENT;
  }

  @Override
  public void toXML(@NotNull XMLStreamWriter xml) throws XMLStreamException {
    // Does nothing
  }

  @Override
  public void toXML(XMLWriter xmlWriter) throws IOException {
    // Does nothing
  }

}
