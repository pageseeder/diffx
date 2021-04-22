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
public final class ProcessingInstructionToken extends TokenBase implements Token {

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
  public ProcessingInstructionToken(String target, String data) throws NullPointerException {
    this.target = Objects.requireNonNull(target);
    this.data = Objects.requireNonNull(data);
    this.hashCode = toHashCode(target, data);
  }

  /**
   * Returns the target of the processing instruction.
   *
   * @return The target of the processing instruction.
   */
  public String getTarget() {
    return this.target;
  }

  /**
   * Returns the data of the processing instruction.
   *
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
  public boolean equals(Token token) {
    if (token.getClass() != this.getClass()) return false;
    ProcessingInstructionToken pi = (ProcessingInstructionToken) token;
    // TODO: handle nulls
    return pi.target.equals(this.target)
        && pi.data.equals(this.data);
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
  public TokenType getType() {
    return TokenType.OTHER;
  }

  /**
   * Calculates the hashcode for this token.
   *
   * @param s1 A string to calculate the value from.
   * @param s2 Another string to calculate the value from.
   *
   * @return a number suitable as a hashcode.
   */
  private static int toHashCode(String s1, String s2) {
    int hash = 7;
    hash = hash * 103 + (s1 != null ? s1.hashCode() : 0);
    hash = hash * 103 + (s2 != null ? s2.hashCode() : 0);
    return hash;
  }

}
