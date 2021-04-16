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
import org.pageseeder.diffx.token.TextToken;
import org.pageseeder.diffx.token.impl.CharactersToken;
import org.pageseeder.diffx.token.impl.SpaceToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The tokenizer for characters tokens.
 *
 * <p>This class is not synchronized.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TokenizerByChar implements TextTokenizer {

  /**
   * Creates a new tokenizer.
   */
  public TokenizerByChar() {
  }

  @Override
  public List<TextToken> tokenize(CharSequence seq) {
    if (seq == null) throw new NullPointerException("Character sequence is null");
    if (seq.length() == 0) return Collections.emptyList();
    List<TextToken> tokens = new ArrayList<>(seq.length());
    char c;
    for (int i=0; i < seq.length(); i++) {
      c = seq.charAt(i);
      TextToken token;
      if (Character.isWhitespace(c)) {
        token = SpaceToken.getInstance(c);
      } else {
        token = new CharactersToken(Character.toString(c));
      }
      tokens.add(token);
    }
    return tokens;
  }

  /**
   * Always <code>TextGranularity.CHARACTER</code>.
   */
  @Override
  public TextGranularity granularity() {
    return TextGranularity.CHARACTER;
  }
}
