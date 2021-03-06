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

import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.config.TextGranularity;

/**
 * Factory for tokenizers.
 *
 * <p>This class is designed to returned tokenizers that corresponds to the given configuration.
 *
 * @author Christophe Lauret
 * @version 0.7.0
 */
public final class TokenizerFactory {

  /**
   * No public instantiation
   */
  private TokenizerFactory() {
  }

  /**
   * Returns the text tokenizer.
   *
   * @param config The configuration to use.
   *
   * @return the corresponding tokenizer.
   * @throws NullPointerException If the configuration is <code>null</code>.
   */
  public static TextTokenizer get(DiffConfig config) {
    if (config == null) throw new NullPointerException("The config should be specified");
    TextGranularity granularity = config.granularity();
    switch (granularity) {
      case CHARACTER:
        return new TokenizerByChar();
      case WORD:
        return new TokenizerByWord(config.whitespace());
      case SPACE_WORD:
        return new TokenizerBySpaceWord(config.whitespace());
      case PUNCTUATION:
        return new TokenizerByPunctuation(config.whitespace());
      case TEXT:
        return new TokenizerByText(config.whitespace());
      default:
        throw new IllegalArgumentException("Unsupported text granularity " + granularity);
    }
  }

}
