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

package org.pageseeder.diffx.test;

import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.XMLTokenType;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A generic implementation of a token for testing.
 *
 * <p>We don't use a build-in XML token type to prevent algorithm and processor implementations
 * from triggering any optimization or coalescing.</p>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class GeneralToken implements XMLToken {

  final char c;

  public GeneralToken(char c) {
    this.c = c;
  }

  public static List<GeneralToken> toList(String string) {
    List<GeneralToken> s = new ArrayList<>();
    for (char c : string.toCharArray()) {
      s.add(new GeneralToken(c));
    }
    return s;
  }

  @Override
  public XMLTokenType getType() {
    return XMLTokenType.OTHER;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GeneralToken that = (GeneralToken) o;
    return c == that.c;
  }

  public boolean equals(XMLToken o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GeneralToken that = (GeneralToken) o;
    return c == that.c;
  }

  @Override
  public int hashCode() {
    return Objects.hash(c);
  }

  @Override
  public void toXML(XMLWriter xml) {
  }

  @Override
  public void toXML(XMLStreamWriter xml) {
  }

  @Override
  public String toString() {
    return Character.toString(c);
  }

  @Override
  public String getName() {
    return "";
  }

  @Override
  public String getNamespaceURI() {
    return XMLConstants.NULL_NS_URI;
  }

  @Override
  public String getValue() {
    return null;
  }
}