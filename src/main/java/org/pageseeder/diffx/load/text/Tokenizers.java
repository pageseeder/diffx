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
package org.pageseeder.diffx.load.text;

/**
 * A utility class for tokenizers.
 *
 * @author Christophe Lauret
 * @version 11 May 2010
 */
final class Tokenizers {

  /**
   * Utility class.
   */
  private Tokenizers() {
  }

  public static boolean isWhitespace(CharSequence text) {
    return getLeadingWhiteSpace(text) == text.length();
  }

  /**
   * Returns the length in characters of the leading white space in the given char sequence.
   *
   * @param s the char sequence to look at.
   *
   * @return the number of whitespace characters at the beginning of the sequence..
   */
  public static int getLeadingWhiteSpace(CharSequence s) {
    int i = 0;
    if (0 == s.length()) return 0;
    char c = s.charAt(0);
    while (c == ' ' || c == '\t' || c == '\n') {
      i++;
      if (i == s.length()) {
        break;
      }
      c = s.charAt(i);
    }
    return i;
  }

  /**
   * Returns the length in characters of the trailing white spaces in the given char sequence.
   *
   * @param s the char sequence to look at.
   *
   * @return the number of whitespace characters at the end of the sequence..
   */
  public static int getTrailingWhiteSpace(CharSequence s) {
    int i = 0;
    if (s.length() == 0) return 0;
    char c = s.charAt(s.length() - 1 - i);
    while (c == ' ' || c == '\t' || c == '\n') {
      i++;
      if (i == s.length()) {
        break;
      }
      c = s.charAt(s.length() - 1 - i);
    }
    return i;
  }

}
