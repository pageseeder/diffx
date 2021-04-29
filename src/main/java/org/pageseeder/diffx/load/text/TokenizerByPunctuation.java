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

import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.token.TextToken;
import org.pageseeder.diffx.token.impl.CharactersToken;
import org.pageseeder.diffx.token.impl.IgnorableSpaceToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tokenizer returning text between punctuation marks.
 *
 * <p>More precisely, each token contains text up to the specified punctuation mark.</p>
 *
 * <p>This tokenizer is useful when there a lot text but tokenizing by text node is too coarse.
 * Using the punctuation provides a compromise.</p>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TokenizerByPunctuation implements TextTokenizer {

  private final static String PUNCTUATION_MARKS = ".,?!;";

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
  public TokenizerByPunctuation(WhiteSpaceProcessing whitespace) {
    if (whitespace == null) throw new NullPointerException("the white space processing must be specified.");
    this.whitespace = whitespace;
  }

  @Override
  public List<TextToken> tokenize(CharSequence text) {
    if (text == null) throw new NullPointerException("Character sequence is null");
    if (text.length() == 0) return Collections.emptyList();
    List<TextToken> tokens = new ArrayList<>(text.length());

    Pattern p = Pattern.compile("[.,?!;]+");
    Matcher m = p.matcher(text);
    int index = 0;

    while (m.find()) {
      if (index < m.end()) {
        CharSequence chunk = text.subSequence(index, m.end());
        // Cannot be space as it necessarily contains a punctuation character
        tokens.add(new CharactersToken(chunk));
      }
      index = m.end();
    }

    if (index != text.length()) {
      CharSequence chunk = text.subSequence(index, text.length());
      TextToken token = toToken(chunk, this.whitespace);
      if (token != null)
        tokens.add(token);
    }

    return tokens;
  }

  private static TextToken toToken(CharSequence text, WhiteSpaceProcessing whitespace) {
    if (Tokenizers.isWhitespace(text))
      return whitespace == WhiteSpaceProcessing.IGNORE ? null : new IgnorableSpaceToken(text);
    return new CharactersToken(text);
  }

  public static List<TextToken> tokenize(CharSequence seq, WhiteSpaceProcessing whitespace) {
    TokenizerByPunctuation tokenizer = new TokenizerByPunctuation(whitespace);
    return tokenizer.tokenize(seq);
  }

}
