/*
 * Copyright (c) 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.load;

import org.jspecify.annotations.Nullable;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.load.text.TextTokenizer;

/**
 * Base class for XML loaders.
 *
 * @author Christophe Lauret
 *
 * @version 1.3.2
 * @since 0.9.0
 */
abstract class XMLLoaderBase implements XMLLoader {

  /**
   * The Diff configuration to use
   */
  protected DiffConfig config = DiffConfig.legacyDefault();

  /**
   * The {@code textTokenizer} variable represents an instance of a {@link TextTokenizer} used for tokenizing text in the context of the SAXLoader.
   *
   * <p>This variable is managed internally in the SAXLoader class and is used to facilitate
   * text processing operations during XML loading and parsing.
   *
   * <p>Note that the value of this variable can be {@code null}, indicating that no tokenizer has
   * been explicitly set or initialized.
   */
  protected @Nullable TextTokenizer textTokenizer;

  /**
   * Returns the configuration used by this loader.
   *
   * @return the configuration used by this loader.
   */
  public DiffConfig getConfig() {
    return this.config;
  }

  /**
   * Sets the configuration used by this loader.
   *
   * @param config The configuration used by this loader.
   */
  public void setConfig(DiffConfig config) {
    this.config = config;
  }

  /**
   * Configures the {@link TextTokenizer} used by this loader.
   *
   * <p>The {@link TextTokenizer} is responsible for splitting text into tokens as part of
   * the loading process. By setting a specific instance of {@link TextTokenizer},
   * custom tokenization logic can be applied during processing.
   *
   * <p>Passing {@code null} will reset the tokenizer, and any subsequent processing will rely
   * on a default tokenizer (if applicable).
   *
   * @param textTokenizer The {@link TextTokenizer} instance to use for tokenizing text,
   *                      or {@code null} to reset and use the default tokenizer.
   */
  public void setTextTokenizer(@Nullable TextTokenizer textTokenizer) {
    this.textTokenizer = textTokenizer;
  }

  /**
   * Retrieves the {@link TextTokenizer} instance used by this loader.
   *
   * <p>The {@link TextTokenizer} is responsible for splitting text into tokens during XML processing.
   * This method may return {@code null} if no tokenizer has been explicitly set or initialized.
   *
   * @return the {@link TextTokenizer} instance currently configured for text tokenization,
   *         or {@code null} if none is set.
   */
  public @Nullable TextTokenizer getTextTokenizer() {
    return textTokenizer;
  }
}
