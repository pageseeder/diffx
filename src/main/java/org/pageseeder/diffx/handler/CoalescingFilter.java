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
package org.pageseeder.diffx.handler;

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.token.TextToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.impl.CharactersToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Coalesces consecutive text tokens for the same operation.
 *
 * <p>This handler is </p>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.9.0
 */
public final class CoalescingFilter extends DiffFilter<XMLToken> implements DiffHandler<XMLToken> {

  /**
   * Buffer of text token to coalesce.
   */
  private final List<TextToken> buffer = new ArrayList<>();

  /**
   * Buffer of text token to coalesce using opposite operation of current.
   */
  private final List<TextToken> altBuffer = new ArrayList<>();

  /**
   * The operator used for the last token in the buffer.
   */
  private Operator current = Operator.MATCH;

  public CoalescingFilter(DiffHandler<XMLToken> target) {
    super(target);
  }

  @Override
  public void handle(@NotNull Operator operator, @NotNull XMLToken token) throws IllegalStateException {
    if (token instanceof TextToken) {
      handleText((TextToken) token, operator);
    } else {
      flushText();
      this.target.handle(operator, token);
    }
  }

  @Override
  public void end() {
    flushText();
    this.target.end();
  }

  private void handleText(@NotNull TextToken token, @NotNull Operator operator) {
    if (this.current == operator) {
      // Same operator simply add the token
      this.buffer.add(token);
    } else {
      if (this.current == Operator.MATCH || operator == Operator.MATCH) {
        // Operator is match, flush and update
        this.flushText();
        this.current = operator;
        this.buffer.add(token);
      } else {
        // Current is INS or DEL
        this.altBuffer.add(token);
      }
    }
  }

  /**
   * Flush the text to the target handler and clear the buffer if there are any text tokens.
   */
  public void flushText() {
    if (this.buffer.size() > 0) {
      TextToken text = coalesceText(this.buffer);
      this.target.handle(this.current, text);
      this.buffer.clear();
    }
    if (this.current != Operator.MATCH && !this.altBuffer.isEmpty()) {
      TextToken other = coalesceText(this.altBuffer);
      this.target.handle(this.current.flip(), other);
      this.altBuffer.clear();
    }
  }

  /**
   * Coalesce text tokens into a single one.
   *
   * @param tokens A list of text tokens
   *
   * @return A single text token
   */
  public static TextToken coalesceText(@NotNull List<TextToken> tokens) {
    // If there's only one token, no need to coalesce
    if (tokens.size() == 1) return tokens.get(0);
    // Concatenate text of all text nodes
    StringBuilder text = new StringBuilder();
    for (TextToken token : tokens) {
      text.append(token.getCharacters());
    }
    return new CharactersToken(text.toString());
  }

  /**
   * Coalesce tokens that all text nodes
   *
   * @param tokens A list of tokens
   *
   * @return A list of tokens with text tokens coalesced.
   */
  public static List<? extends XMLToken> coalesce(@NotNull List<? extends XMLToken> tokens) {
    // If there's only one token, no need to coalesce
    if (tokens.size() <= 1) return tokens;
    List<XMLToken> coalesced = new ArrayList<>();
    CoalescingFilter filter = new CoalescingFilter((operator, token) -> coalesced.add(token));
    for (XMLToken token : tokens) filter.handle(Operator.MATCH, token);
    return coalesced;
  }

  @Override
  public String toString() {
    return "CoalescingFilter -> " + target;
  }
}
