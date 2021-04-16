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

import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.event.TextToken;
import org.pageseeder.diffx.event.impl.IgnorableSpaceToken;
import org.pageseeder.diffx.event.impl.SpaceToken;
import org.pageseeder.diffx.event.impl.WordToken;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The tokenizer for characters tokens.
 *
 * <p>This class is not synchronized.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TokenizerBySpaceWord implements TextTokenizer {

  /**
   * Map characters to tokens in order to recycle tokens as they are created.
   */
  private final Map<String, TextToken> recycling = new HashMap<>();

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
  public TokenizerBySpaceWord(WhiteSpaceProcessing whitespace) {
    if (whitespace == null) throw new NullPointerException("the white space processing must be specified.");
    this.whitespace = whitespace;
  }

  @Override
  public List<TextToken> tokenize(CharSequence seq) {
    if (seq == null) throw new NullPointerException("Character sequence is null");
    if (seq.length() == 0) return Collections.emptyList();
    // We assume that on average we generate 1 token per 4 chars
    List<TextToken> tokens = new ArrayList<>(seq.length() / 4);

    Pattern p = Pattern.compile("( ?[A-Za-z_'-]+)|(\\S)");
    Matcher m = p.matcher(seq);
    int index = 0;

    // Add segments before each match found
    while (m.find()) {
      if (index != m.start() && whitespace != WhiteSpaceProcessing.IGNORE) {
        String space = seq.subSequence(index, m.start()).toString();
        tokens.add(getSpaceEvent(space));
      }
      // We don't even need to record a white space if they are ignored!
      String word = seq.subSequence(m.start(), m.end()).toString();
      tokens.add(getWordEvent(word));
      index = m.end();
    }

    // Add remaining word if any
    if (index != seq.length()) {
      String space = seq.subSequence(index, seq.length()).toString();
      tokens.add(getSpaceEvent(space));
    }

    return tokens;
  }

  /**
   * Always <code>TextGranularity.WORD</code>.
   */
  @Override
  public TextGranularity granularity() {
    return TextGranularity.SPACE_WORD;
  }

  public static List<TextToken> tokenize(CharSequence seq, WhiteSpaceProcessing whitespace) {
    TokenizerBySpaceWord tokenizer = new TokenizerBySpaceWord(whitespace);
    return tokenizer.tokenize(seq);
  }

  /**
   * Returns the word token corresponding to the specified characters.
   *
   * @param word the characters of the word
   * @return the corresponding word token
   */
  private TextToken getWordEvent(String word) {
    TextToken e = this.recycling.get(word);
    if (e == null) {
      e = new WordToken(word);
      this.recycling.put(word, e);
    }
    return e;
  }

  /**
   * Returns the space token corresponding to the specified characters.
   *
   * @param space the characters of the space
   * @return the corresponding space token
   */
  private TextToken getSpaceEvent(String space) {
    // preserve the actual white space used
    TextToken e = this.recycling.get(space);
    if (e == null) {
      if (this.whitespace == WhiteSpaceProcessing.PRESERVE) {
        e = new IgnorableSpaceToken(space);
      } else {
        e = SpaceToken.getInstance(space);
      }
      this.recycling.put(space, e);
    }
    return e;
  }

}
