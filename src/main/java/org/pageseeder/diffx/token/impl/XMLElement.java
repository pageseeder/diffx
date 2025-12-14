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

import org.jspecify.annotations.Nullable;
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
import java.util.Objects;

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
 * @version 1.2.2
 * @since 0.7.0
 */
public class XMLElement extends TokenBase implements ElementToken {

  /**
   * Represents the starting token of an XML element.
   */
  private final StartElementToken start;

  /**
   * Represents the token corresponding to the end of an XML element.
   */
  private final EndElementToken end;

  /**
   * A collection of {@link XMLToken} instances representing the child tokens
   * contained within an XML element.
   *
   * <p>This list encapsulates all XML content, including child elements,
   * attributes, and character data that belongs to this element.</p>
   *
   * <p>Immutability is enforced to ensure thread safety and the structural
   * consistency of the {@link XMLElement} class.</p>
   */
  private final List<XMLToken> content;

  /**
   * The precalculated hash code for this element, based on its start element token
   * and content tokens. This value is computed during the construction of the
   * XMLElement instance to ensure efficient retrieval of the hash code during
   * hash-based operations.
   *
   * This variable is final, indicating that the computed hash code for the
   * object is immutable and does not change after initialization.
   */
  private final int hashCode;

  public XMLElement(StartElementToken start, EndElementToken end, List<XMLToken> content) {
    this.start = Objects.requireNonNull(start, "The start element must not be null.");
    this.end = Objects.requireNonNull(end, "The end element must not be null.");
    this.content = Objects.requireNonNull(content, "The content must not be null.");
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
  public String getName() {
    return this.start.getName();
  }

  @Override
  public String getNamespaceURI() {
    return this.start.getNamespaceURI();
  }

  @Override
  public @Nullable String getValue() {
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

  /**
   * @deprecated As of version 1.2.0, replaced by {@link #getContent()}
   */
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
  public boolean equals(@Nullable XMLToken token) {
    if (token == null) return false;
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
  public void toXML(XMLStreamWriter xml) throws XMLStreamException {
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
