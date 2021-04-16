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

import org.pageseeder.diffx.token.ElementToken;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.Token;
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ElementTokenImpl extends TokenBase implements ElementToken {

  private final List<Token> tokens;

  private final int hashCode;

  public ElementTokenImpl(StartElementToken open, EndElementToken close, List<Token> children) {
    this.tokens = new ArrayList<>();
    this.tokens.add(open);
    this.tokens.addAll(children);
    this.tokens.add(close);
    this.hashCode = toHashCode(this.tokens);
  }

  @Override
  public String getName() {
    return ((StartElementToken)this.tokens.get(0)).getName();
  }

  @Override
  public String getURI() {
    return ((StartElementToken)this.tokens.get(0)).getURI();
  }

  @Override
  public List<Token> getEvents() {
    return this.tokens;
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
   *         <code>false</code> otherwise.
   */
  @Override
  public boolean equals(Token token) {
    if (token.getClass() != this.getClass()) return false;
    ElementTokenImpl element = (ElementTokenImpl)token;
    if (element.hashCode != this.hashCode) return false;
    if (element.tokens.size() != this.tokens.size()) return false;
    return element.tokens.equals(this.tokens);
  }

  @Override
  public String toString() {
    return "element: "+this.getName();
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    for (Token token : this.tokens) {
      token.toXML(xml);
    }
  }

  @Override
  public StringBuffer toXML(StringBuffer xml) {
    for (Token token : this.tokens) {
      token.toXML(xml);
    }
    return xml;
  }

  /**
   * Calculates the hashcode for this token.
   *
   * @param tokens List of tokens
   * @return a number suitable as a hashcode.
   */
  private static int toHashCode(List<Token> tokens) {
    int result = 1;
    for (Token token : tokens)
      result = 31 * result + (token == null ? 0 : token.hashCode());
    return result;
  }

}
