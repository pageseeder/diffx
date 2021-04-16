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

import org.pageseeder.diffx.token.TextToken;

/**
 * A particular type of token reserved for white spaces.
 *
 * @author Christophe Lauret
 * @version 27 March 2010
 */
public final class SpaceToken extends CharactersTokenBase implements TextToken {

  /**
   * A static instance for the single white spaces.
   *
   * <p>Use this constant instead of creating new instances
   */
  public static final SpaceToken SINGLE_WHITESPACE = new SpaceToken(" ");

  /**
   * A static instance for the double white spaces.
   *
   * <p>Use this constant instead of creating new instances
   */
  public static final SpaceToken DOUBLE_WHITESPACE = new SpaceToken("  ");

  /**
   * A static instance for the new lines.
   *
   * <p>Use this constant instead of creating new instances
   */
  public static final SpaceToken NEW_LINE = new SpaceToken("\n");

  /**
   * A static instance for tabs.
   *
   * <p>Use this constant instead of creating new instances
   */
  public static final SpaceToken TAB = new SpaceToken("\t");

  /**
   * Creates a new space token.
   *
   * @param s The space as a string.
   *
   * @throws NullPointerException If the given String is <code>null</code>.
   */
  public SpaceToken(CharSequence s) throws NullPointerException {
    super(s);
  }

  @Override
  public String toString() {
    return "space: \""+toString(getCharacters().toCharArray())+'"';
  }

  /**
   * Returns the white space token corresponding to the given string.
   *
   * @param space The string for the white space token.
   *
   * @return A readable string.
   */
  public static SpaceToken getInstance(CharSequence space) {
    // check constants
    if (" ".contentEquals(space))  return SINGLE_WHITESPACE;
    if ("  ".contentEquals(space)) return DOUBLE_WHITESPACE;
    if ("\n".contentEquals(space)) return NEW_LINE;
    if ("\t".contentEquals(space)) return TAB;
    // create a new instance
    return new SpaceToken(space);
  }

  /**
   * Returns the white space token corresponding to the given string.
   *
   * @param c The string for the white space token.
   *
   * @return A readable string.
   */
  public static SpaceToken getInstance(char c) {
    // check constants
    if (c == ' ')  return SINGLE_WHITESPACE;
    if (c == '\n') return NEW_LINE;
    if (c == '\t') return TAB;
    // create a new instance
    return new SpaceToken(String.valueOf(c));
  }

  /**
   * Returns the white space characters as a readable string.
   *
   * @param chars The whitespace characters
   *
   * @return A readable string.
   */
  private static String toString(char[] chars) {
    StringBuilder out = new StringBuilder();
    for (char c : chars) {
      switch(c) {
        case '\n':
          out.append("\\n");
          break;
        case '\t':
          out.append("\\t");
          break;
        default :
          out.append(c);
      }
    }
    return out.toString();
  }

}
