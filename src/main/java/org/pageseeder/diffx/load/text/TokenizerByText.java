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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.event.TextToken;
import org.pageseeder.diffx.event.impl.CharactersToken;
import org.pageseeder.diffx.event.impl.IgnorableSpaceToken;
import org.pageseeder.diffx.event.impl.SpaceToken;

/**
 * The tokenizer for characters tokens.
 *
 * <p>This class is not synchronized.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TokenizerByText implements TextTokenizer {

  /**
   * Define the whitespace processing.
   */
  private final WhiteSpaceProcessing whitespace;

  /**
   * Creates a new tokenizer.
   *
   * @param whitespace the whitespace processing for this tokenizer.
   *
   * @throws NullPointerException if the white space processing is not specified.
   */
  public TokenizerByText(WhiteSpaceProcessing whitespace) {
    if (whitespace == null) throw new NullPointerException("the white space processing must be specified.");
    this.whitespace = whitespace;
  }

  @Override
  public List<TextToken> tokenize(CharSequence seq) {
    if (seq == null) throw new NullPointerException("Character sequence is null");
    if (seq.length() == 0) return Collections.emptyList();
    int x = TokenizerUtils.getLeadingWhiteSpace(seq);
    int y = TokenizerUtils.getTrailingWhiteSpace(seq);
    // no leading or trailing spaces return a singleton in all configurations
    if (x == 0 && y == 0) {
      TextToken e = new CharactersToken(seq);
      return Collections.singletonList(e);
    }
    // The text node is only white space (white space = leading space)
    if (x == seq.length()) {
      switch (this.whitespace) {
        case COMPARE:
          return Collections.singletonList(SpaceToken.getInstance(seq.toString()));
        case PRESERVE:
          return Collections.singletonList(new IgnorableSpaceToken(seq.toString()));
        case IGNORE:
          return Collections.emptyList();
        default:
      }
      TextToken e = new CharactersToken(seq);
      return Collections.singletonList(e);
    }
    // some trailing or leading whitespace, behaviour changes depending on whitespace processing
    List<TextToken> tokens = null;
    switch (this.whitespace) {
      case COMPARE:
        tokens = new ArrayList<>(1 + (x > 0 ? 1 : 0) + (y > 0 ? 1 : 0));
        if (x > 0) {
          tokens.add(SpaceToken.getInstance(seq.subSequence(0, x)));
        }
        tokens.add(new CharactersToken(seq.subSequence(x, seq.length()-y)));
        if (y > 0) {
          tokens.add(SpaceToken.getInstance(seq.subSequence(seq.length()-y, seq.length())));
        }
        break;
      case PRESERVE:
        tokens = new ArrayList<>(1 + (x > 0 ? 1 : 0) + (y > 0 ? 1 : 0));
        if (x > 0) {
          tokens.add(new IgnorableSpaceToken(seq.subSequence(0, x)));
        }
        tokens.add(new CharactersToken(seq.subSequence(x, seq.length()-y)));
        if (y > 0) {
          tokens.add(new IgnorableSpaceToken(seq.subSequence(seq.length()-y, seq.length())));
        }
        break;
      case IGNORE:
        TextToken e = new CharactersToken(seq.subSequence(x, seq.length()-y));
        tokens = Collections.singletonList(e);
        break;
      default:
    }
    return tokens;
  }

  /**
   * Always <code>TextGranularity.CHARACTER</code>.
   */
  @Override
  public TextGranularity granularity() {
    return TextGranularity.TEXT;
  }

}
