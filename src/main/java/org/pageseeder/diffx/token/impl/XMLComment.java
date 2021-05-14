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

import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.XMLTokenType;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * A comment token.
 *
 * @author Christophe Lauret
 * @author Jason Harrop
 * @version 0.9.0
 * @since 0.6.0
 */
public final class XMLComment extends TokenBase implements XMLToken {

  /**
   * The comment string.
   */
  private final String comment;

  /**
   * Hashcode value for this token.
   */
  private final int hashCode;

  /**
   * Creates a new comment token.
   *
   * @param comment the comment string.
   *
   * @throws NullPointerException if the comment is <code>null</code>.
   */
  public XMLComment(String comment) throws NullPointerException {
    this.comment = Objects.requireNonNull(comment, "Comment must not be null, use \"\" instead");
    this.hashCode = toHashcode(comment);
  }

  /**
   * @return the comment string.
   */
  public String getComment() {
    return this.comment;
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  /**
   * Returns <code>true</code> if the token is a comment token.
   *
   * @param token The token to compare with this token.
   *
   * @return <code>true</code> if this token is equal to the specified token;
   * <code>false</code> otherwise.
   */
  @Override
  public boolean equals(XMLToken token) {
    if (token == null) return false;
    if (token.getClass() != this.getClass()) return false;
    XMLComment other = (XMLComment) token;
    return other.comment.equals(this.comment);
  }

  @Override
  public String toString() {
    return "comment: " + this.comment;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.writeComment(this.comment);
  }

  @Override
  public void toXML(XMLStreamWriter xml) throws XMLStreamException {
    xml.writeComment(this.comment);
  }

  @Override
  public XMLTokenType getType() {
    return XMLTokenType.COMMENT;
  }

  @Override
  public String getName() {
    return "";
  }

  @Override
  public String getValue() {
    return this.comment;
  }

  /**
   * Calculates the hashcode for this token.
   *
   * @param comment The comment string.
   *
   * @return a number suitable as a hashcode.
   */
  private static int toHashcode(String comment) {
    return 19 * 37 + comment.hashCode();
  }

}
