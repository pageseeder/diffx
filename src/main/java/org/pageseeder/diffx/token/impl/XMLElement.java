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
import org.pageseeder.diffx.token.ElementToken;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an XML element token with its corresponding start, end, and content tokens.
 * This class extends {@code TokenBase} and implements the {@code ElementToken} interface,
 * encapsulating the structure of an XML element including its associated data and behaviors.
 *
 * <p>The {@code XMLElement} provides methods to retrieve the start and end tokens, the element's
 * name and namespace URI, and the list of content tokens. Additionally, it implements methods
 * for hashing, equality checks, and XML writing.
 *
 * @author Christophe Lauret
 *
 * @version 1.2.0
 * @since 0.7.0
 */
public class XMLElement extends TokenBase implements ElementToken {

  private final StartElementToken start;

  private final EndElementToken end;

  private final List<XMLToken> content;

  private final int hashCode;

  public XMLElement(StartElementToken start, EndElementToken end, List<XMLToken> content) {
    this.start = start;
    this.end = end;
    this.content = content;
    this.hashCode = toHashCode(start, this.content);
  }

  @Override
  public StartElementToken getStart() {
    return start;
  }

  @Override
  public EndElementToken getEnd() {
    return end;
  }

  @Override
  public @NotNull String getName() {
    return this.start.getName();
  }

  @Override
  public @NotNull String getNamespaceURI() {
    return this.start.getNamespaceURI();
  }

  @Override
  public String getValue() {
    return null;
  }

  /**
   * @deprecated As of version 1.1.2, replaced by {@link #getContent()}
   */
  @Override
  @Deprecated(since = "1.1.2", forRemoval = true)
  public List<XMLToken> getEvents() {
    return this.tokens();
  }

  @Override
  public List<XMLToken> tokens() {
    List<XMLToken> tokens = new ArrayList<>(1 + content.size() + 1);
    tokens.add(start);
    tokens.addAll(content);
    tokens.add(end);
    return tokens;
  }

  @Override
  @Deprecated(since = "1.2.0", forRemoval = true)
  public List<XMLToken> getChildren() {
    return this.content;
  }

  @Override
  public List<XMLToken> getContent() {
    return this.content;
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  /**
   * Returns <code>true</code> if the token is an open element token.
   *
   * @param token The token to compare with this token.
   *
   * @return <code>true</code> if this token is equal to the specified token;
   * <code>false</code> otherwise.
   */
  @Override
  public boolean equals(XMLToken token) {
    if (token.getClass() != this.getClass()) return false;
    XMLElement element = (XMLElement) token;
    if (element.hashCode != this.hashCode) return false;
    if (!element.start.equals(this.start)) return false;
    if (element.content.size() != this.content.size()) return false;
    return element.content.equals(this.content);
  }

  @Override
  public String toString() {
    return "element: " + this.getName();
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    start.toXML(xml);
    for (XMLToken token : this.content) {
      token.toXML(xml);
    }
    end.toXML(xml);
  }

  @Override
  public void toXML(@NotNull XMLStreamWriter xml) throws XMLStreamException {
    start.toXML(xml);
    for (XMLToken token : this.content) {
      token.toXML(xml);
    }
    end.toXML(xml);
  }

  /**
   * Calculates the hashcode for this token.
   *
   * @param start The start element
   * @param content List of tokens
   *
   * @return a number suitable as a hashcode.
   */
  private static int toHashCode(StartElementToken start, List<XMLToken> content) {
    int result = 1;
    result = 31 * result + start.hashCode();
    for (XMLToken token : content)
      result = 31 * result + (token == null ? 0 : token.hashCode());
    return result;
  }

}
