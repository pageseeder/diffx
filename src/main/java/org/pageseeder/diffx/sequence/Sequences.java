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

import org.pageseeder.diffx.token.*;
import org.pageseeder.diffx.token.impl.TextListToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * A utility class for token sequences.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.6
 */
public final class Sequences {

  /**
   * Prevent creation of instances.
   */
  private Sequences() {
  }

  /**
   * Indicates whether the sequence corresponds to well-formed XML.
   *
   * @param sequence The sequence.
   *
   * @return <code>true</code> if the sequence is "well-formed";
   * <code>false</code> otherwise.
   */
  public static boolean isWellFormed(XMLSequence sequence) {
    if (sequence == null) return false;
    Stack<XMLToken> open = new Stack<>();
    XMLToken token;
    for (int i = 0; i < sequence.size(); i++) {
      token = sequence.getToken(i);
      if (token.getType() == XMLTokenType.START_ELEMENT) {
        open.push(token);
      } else if (token.getType() == XMLTokenType.END_ELEMENT) {
        if (open.empty()) return false;
        StartElementToken o = (StartElementToken) open.peek();
        String lastOpenElementName = o.getName();
        String closeElementName = token.getName();
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
  public static int getMaxDepth(XMLSequence sequence) {
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
  public static int getMaxElementContent(XMLSequence sequence) {
    int max = 0;
    int tmp = 0;
    for (int i = 0; i < sequence.size(); i++) {
      XMLToken token = sequence.getToken(i);
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

  /**
   * Fold consecutive text tokens into a single token while preserving the original tokens.
   *
   * @param input The input sequence
   *
   * @return The collapsed sequence.
   */
  public static XMLSequence foldText(XMLSequence input) {
    List<TextToken> text = new ArrayList<>();
    XMLSequence output = new XMLSequence(input.getNamespaces());
    for (XMLToken token : input) {
      if (token instanceof TextToken) {
        text.add((TextToken) token);
      } else {
        if (text.size() > 1) {
          output.addToken(new TextListToken(text));
          text.clear();
        } else if (text.size() == 1) {
          output.addToken(text.remove(0));
        }
        output.addToken(token);
      }
    }
    return output;
  }

}
