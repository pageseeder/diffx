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

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * A processing instruction token.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class XMLProcessingInstruction extends TokenBase implements XMLToken {

  /**
   * The target of the processing instruction.
   */
  private final String target;

  /**
   * The data of the processing instruction.
   */
  private final String data;

  /**
   * Hashcode value for this token.
   */
  private final int hashCode;

  /**
   * Creates a new processing instruction token.
   *
   * @param target The target of the processing instruction.
   * @param data   The data of the processing instruction.
   *
   * @throws NullPointerException if any of the argument is <code>null</code>.
   */
  public XMLProcessingInstruction(String target, String data) throws NullPointerException {
    this.target = Objects.requireNonNull(target, "Processing instruction target must not be null, use \"\" instead");
    this.data = Objects.requireNonNull(data, "Processing instruction data must not be null, use \"\" instead");
    this.hashCode = toHashCode(target, data);
  }

  /**
   * @return The target of the processing instruction.
   */
  public String getName() {
    return this.target;
  }

  /**
   * @return The data of the processing instruction.
   */
  public String getValue() {
    return this.data;
  }

  /**
   * @return The target of the processing instruction.
   */
  public String getTarget() {
    return this.target;
  }

  /**
   * @return The data of the processing instruction.
   */
  public String getData() {
    return this.data;
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  /**
   * Returns <code>true</code> if the token is a processing instruction.
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
    XMLProcessingInstruction pi = (XMLProcessingInstruction) token;
    return pi.target.equals(this.target) && pi.data.equals(this.data);
  }

  @Override
  public String toString() {
    return "pi: " + this.target + ": " + this.data;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.writePI(this.target, this.data);
  }

  @Override
  public void toXML(XMLStreamWriter xml) throws XMLStreamException {
    xml.writeProcessingInstruction(this.target, this.data);
  }

  @Override
  public XMLTokenType getType() {
    return XMLTokenType.PROCESSING_INSTRUCTION;
  }

  /**
   * Calculates the hashcode for this token.
   */
  private static int toHashCode(String target, String data) {
    int hash = 7;
    hash = hash * 103 + target.hashCode();
    hash = hash * 103 + data.hashCode();
    return hash;
  }

}
