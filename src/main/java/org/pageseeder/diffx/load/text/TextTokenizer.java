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

import java.util.List;

/**
 * An interface for text tokenizers.
 *
 * <p>Text tokenizers are used to return a list of {@link org.pageseeder.diffx.token.TextToken}
 * from a piece of text.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public interface TextTokenizer {

  /**
   * Returns the list of {@link TextToken} corresponding to the specified character sequence.
   *
   * @param seq the character sequence to tokenize.
   *
   * @return the corresponding list.
   */
  List<TextToken> tokenize(CharSequence seq);

  /**
   * @return the text granularity of this tokenizer.
   */
  TextGranularity granularity();

}
