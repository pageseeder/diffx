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

public class XMLElement extends TokenBase implements ElementToken {

  private final StartElementToken start;

  private final EndElementToken end;

  private final List<XMLToken> children;

  private final int hashCode;

  public XMLElement(StartElementToken start, EndElementToken end, List<XMLToken> children) {
    this.start = start;
    this.end = end;
    this.children = children;
    this.hashCode = toHashCode(start, this.children);
  }

  public StartElementToken getStart() {
    return start;
  }

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

  @Override
  public List<XMLToken> getEvents() {
    return this.tokens();
  }

  @Override
  public List<XMLToken> tokens() {
    List<XMLToken> tokens = new ArrayList<>(1 + children.size() + 1);
    tokens.add(start);
    tokens.addAll(children);
    tokens.add(end);
    return tokens;
  }

  @Override
  public List<XMLToken> getChildren() {
    return this.children;
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
    if (element.children.size() != this.children.size()) return false;
    return element.children.equals(this.children);
  }

  @Override
  public String toString() {
    return "element: " + this.getName();
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    start.toXML(xml);
    for (XMLToken token : this.children) {
      token.toXML(xml);
    }
    end.toXML(xml);
  }

  @Override
  public void toXML(XMLStreamWriter xml) throws XMLStreamException {
    start.toXML(xml);
    for (XMLToken token : this.children) {
      token.toXML(xml);
    }
    end.toXML(xml);
  }

  /**
   * Calculates the hashcode for this token.
   *
   * @param start The start element
   * @param children List of tokens
   *
   * @return a number suitable as a hashcode.
   */
  private static int toHashCode(StartElementToken start, List<XMLToken> children) {
    int result = 1;
    result = 31 * result + start.hashCode();
    for (XMLToken token : children)
      result = 31 * result + (token == null ? 0 : token.hashCode());
    return result;
  }

}
