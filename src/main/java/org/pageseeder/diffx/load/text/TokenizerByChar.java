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

import org.pageseeder.diffx.token.TextToken;
import org.pageseeder.diffx.token.impl.CharactersToken;
import org.pageseeder.diffx.token.impl.SpaceToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Tokenizes text creating a text token instance of every character.
 *
 * <p>Obviously, given the number of tokens that this tokenizer generates, it should only be used for
 * short strings or testing.
 *
 * @author Christophe Lauret
 *
 * @version 1.3.0
 * @since 0.9.0
 */
public final class TokenizerByChar implements TextTokenizer {

  @Override
  public List<TextToken> tokenize(CharSequence text) {
    Objects.requireNonNull(text, "Character sequence is null");
    if (text.length() == 0) return List.of();
    List<TextToken> tokens = new ArrayList<>(text.length());
    char c;
    for (int i = 0; i < text.length(); i++) {
      c = text.charAt(i);
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

}
