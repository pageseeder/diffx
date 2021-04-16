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

import org.pageseeder.diffx.token.Token;
import org.pageseeder.diffx.token.TokenType;
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.IOException;

/**
 * A branch of XML data.
 *
 * <p>A branch of XML data must start and end with the same element.
 *
 * <p>Implementation note: this class wraps an array of DiffX tokens and does not give
 * access to this array, so it can be considered immutable.
 *
 * @author Christophe Lauret
 * @version 27 March 2010
 */
public final class XMLBranchToken extends TokenBase implements Token {

  /**
   * The array of Diff-X tokens that make up the branch.
   */
  private final Token[] branch;

  /**
   * Pre-calculated hashcode to speed up equal comparison.
   */
  private final int hashCode;

  /**
   * Creates a new XML branch.
   *
   * @param tokens The array of tokens that make up the branch.
   */
  public XMLBranchToken(Token[] tokens) {
    this.branch = tokens;
    this.hashCode = toHashCode(tokens);
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  /**
   * Returns <code>true</code> if the diffX tokens in the branch are all equal.
   *
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Token token) {
    if (token.getClass() != this.getClass()) return false;
    if (token.hashCode() != this.hashCode) return false;
    XMLBranchToken be = (XMLBranchToken) token;
    // branch must have the same length
    if (this.branch.length != be.branch.length) return false;
    // every single token must be equal
    for (int i = 0; i < this.branch.length; i++) {
      if (!be.branch[i].equals(this.branch[i]))
        return false;
    }
    // if we arrive here they are equal
    return true;
  }

  /**
   * Write the DiffX tokens in order.
   *
   * {@inheritDoc}
   */
  @Override
  public void toXML(XMLWriter xml) throws IOException {
    for (Token element : this.branch) {
      element.toXML(xml);
    }
  }

  @Override
  public StringBuffer toXML(StringBuffer xml) throws NullPointerException {
    for (Token element : this.branch) {
      element.toXML(xml);
    }
    return xml;
  }

  @Override
  public TokenType getType() {
    return TokenType.ELEMENT;
  }

  /**
   * Calculates the hashcode for this token.
   *
   * @param tokens Events to calculate the value from.
   * @return a number suitable as a hashcode.
   */
  private static int toHashCode(Token[] tokens) {
    int hash = 17;
    for (Token token : tokens) {
      hash = hash * 13 + (token != null? token.hashCode() : 0);
    }
    return hash;
  }
}
