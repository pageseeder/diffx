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
package org.pageseeder.diffx.format;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.token.*;
import org.pageseeder.diffx.token.impl.*;

/**
 * A simple formatter to write the short string version of the tokens.
 *
 * @author Christophe Lauret
 * @version 18 March 2005
 */
public final class ShortStringFormatter implements DiffXFormatter {

  //  class attributes ---------------------------------------------------------------------------

  /**
   * The output goes here.
   */
  private final Writer out;

  //  constructors -------------------------------------------------------------------------------

  /**
   * Creates a new formatter on the standard output.
   *
   * @see System#out
   *
   * @throws IOException should an I/O exception occurs.
   */
  public ShortStringFormatter() {
    this(new PrintWriter(System.out));
  }

  /**
   * Creates a new formatter using the specified writer.
   *
   * @param w The writer to use.
   */
  public ShortStringFormatter(Writer w) {
    this.out = w;
  }

  // methods ------------------------------------------------------------------------------

  /**
   * Writes the token as a short string.
   *
   * {@inheritDoc}
   */
  @Override
  public void format(Token e) throws IOException, IllegalStateException {
    this.out.write(toShortString(e));
  }

  /**
   * Writes the token as a short string preceded by '+'.
   *
   * {@inheritDoc}
   */
  @Override
  public void insert(Token e) throws IOException, IllegalStateException {
    this.out.write("+");
    this.out.write(toShortString(e));
  }

  /**
   * Writes the token as a short string preceded by '+'.
   *
   * {@inheritDoc}
   */
  @Override
  public void delete(Token e) throws IOException, IllegalStateException {
    this.out.write("-");
    this.out.write(toShortString(e));
  }

  /**
   * Ignored.
   *
   * {@inheritDoc}
   */
  @Override
  public void setConfig(DiffXConfig config) {
  }

  // private helpers ----------------------------------------------------------------------

  /**
   * Returns the short string for the given token.
   *
   * @param token The token.
   *
   * @return The short string for the given token.
   */
  public static String toShortString(Token token) {
    // an element to open
    if (token instanceof StartElementToken)
      return '<'+((StartElementToken)token).getName()+'>';
    // an element to close
    if (token instanceof EndElementToken)
      return "</"+((EndElementToken)token).getName()+'>';
    // an attribute
    if (token instanceof AttributeToken)
      return "@"+((AttributeToken)token).getName();
    // a word
    if (token instanceof WordToken)
      return '"'+((CharactersToken)token).getCharacters()+'"';
    // a white space token
    if (token instanceof SpaceToken)
      return "_s_";
    // a single character
    if (token instanceof CharToken)
      return "'"+((CharToken)token).getChar()+'\'';
    // an ignorable space token
    if (token instanceof IgnorableSpaceToken)
      return "_i_";
    if (token instanceof ElementToken)
      return '<'+((ElementToken)token).getName()+"/>";
    // a single line
    if (token instanceof LineToken) return "L#"+((LineToken)token).getLineNumber();
    if (token instanceof CharactersToken)
      return '"'+((CharactersToken)token).getCharacters()+'"';
    if (token == null) return "-";
    return "???";
  }

}
