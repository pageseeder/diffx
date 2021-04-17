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

import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.core.TextOnlyProcessor;
import org.pageseeder.diffx.load.text.TextTokenizer;
import org.pageseeder.diffx.load.text.TokenizerBySpaceWord;
import org.pageseeder.diffx.token.TextToken;
import org.pageseeder.diffx.token.Token;

import java.util.List;

public final class CompareReplaceFilter extends DiffFilter implements DiffHandler {

  // TODO initialize using config
  private final TextTokenizer tokenizer = new TokenizerBySpaceWord(WhiteSpaceProcessing.PRESERVE);

  /**
   * The previous text operation.
   */
  private Operation previous = null;

  public CompareReplaceFilter(DiffHandler target) {
    super(target);
  }

  @Override
  public void handle(Operator operator, Token token) {
    if (token instanceof TextToken && (operator == Operator.DEL || operator == Operator.INS)) {
      if (this.previous != null) {
        diff((TextToken) token, (TextToken) this.previous.token(), operator == Operator.INS);
        this.previous = null;
      } else {
        flushPrevious();
        this.previous = new Operation(operator, token);
      }
    } else {
      flushPrevious();
      this.target.handle(operator, token);
    }
  }

  private void diff(TextToken a, TextToken b, boolean positive) {
    List<TextToken> tokensA = this.tokenizer.tokenize(a.getCharacters());
    List<TextToken> tokensB = this.tokenizer.tokenize(b.getCharacters());
    TextOnlyProcessor diff = new TextOnlyProcessor();
    if (positive)
      diff.diff(tokensA, tokensB, this.target);
    else
      diff.diff(tokensB, tokensA, this.target);
  }

  /**
   * Flush the previous text token to the target handler and clear the buffer if there is any text token.
   */
  public void flushPrevious() {
    if (this.previous != null) {
      this.target.handle(this.previous.operator(), this.previous.token());
      this.previous = null;
    }
  }

  @Override
  public void end() {
    this.flushPrevious();
    this.target.end();
  }

  @Override
  public String toString() {
    return "CompareReplaceFilter -> " + target;
  }
}
