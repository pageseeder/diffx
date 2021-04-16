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
package org.pageseeder.diffx.sequence;

import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.Token;

import java.util.Stack;

/**
 * A utility class for token sequences.
 *
 * @author Christophe Lauret
 * @version 3 April 2005
 *
 * @since 0.6
 */
public final class EventSequenceUtils {

  /**
   * Prevent creation of instances.
   */
  private EventSequenceUtils() {
  }

  /**
   * Indicates whether the sequence corresponds to well-formed XML.
   *
   * @param sequence The sequence.
   *
   * @return <code>true</code> if the sequence is "well-formed";
   *         <code>false</code> otherwise.
   */
  public static boolean isWellFormed(EventSequence sequence) {
    // TODO: if the sequence is null ??
    if (sequence == null) return false;
    Stack<Token> open = new Stack<>();
    Token token;
    for (int i = 0; i < sequence.size(); i++) {
      token = sequence.getToken(i);
      if (token instanceof StartElementToken) {
        open.push(token);
      } else if (token instanceof EndElementToken) {
        if (open.empty()) return false;
        StartElementToken o = (StartElementToken)open.peek();
        String lastOpenElementName = o.getName();
        String closeElementName = ((EndElementToken)token).getName();
        if (!closeElementName.equals(lastOpenElementName)) return false;
      }
    }
    return open.empty();
  }

  /**
   * Returns the maximum depth of the sequence.
   *
   * <p>This method assumes that the sequence is well-formed, and counts
   * the maximum number of open element tokens.
   *
   * @param sequence The sequence
   *
   * @return The maximum depth.
   */
  public static int getMaxDepth(EventSequence sequence) {
    int max = 0;
    int depth = 0;
    for (int i = 0; i < sequence.size(); i++) {
      if (sequence.getToken(i) instanceof StartElementToken) {
        depth++;
      } else if (sequence.getToken(i) instanceof EndElementToken) {
        depth--;
      }
      if (depth > max) {
        max = depth;
      }
    }
    return max;
  }

  /**
   * Returns the maximum number of token inside an element tag.
   *
   * <p>This method assumes that the sequence is well-formed.
   *
   * @param sequence The sequence.
   *
   * @return The maximum number of tokens.
   */
  public static int getMaxElementContent(EventSequence sequence) {
    int max = 0;
    int tmp = 0;
    for (int i = 0; i < sequence.size(); i++) {
      Token token = sequence.getToken(i);
      if (token instanceof StartElementToken) {
        tmp = 0;
      } else if (token instanceof EndElementToken) {
        if (tmp > max) {
          max = tmp;
        }
      } else {
        tmp++;
      }
    }
    return max;
  }

}
