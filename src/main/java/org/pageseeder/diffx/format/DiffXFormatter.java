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
package org.pageseeder.diffx.format;

import java.io.IOException;

import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.token.Token;

/**
 * An interface for formatting the output of the Diff-X algorithm.
 *
 * @author Christophe Lauret
 *
 * @version 0.6.0
 */
public interface DiffXFormatter {

  /**
   * Formats the specified token.
   *
   * @param token The token to format
   *
   * @throws IOException Should an I/O exception occurs while formatting.
   * @throws IllegalStateException If the formatter is not in a state to run this method.
   */
  void format(Token token) throws IOException, IllegalStateException;

  /**
   * Formats the specified inserted token.
   *
   * @param token The token to format
   *
   * @throws IOException Should an I/O exception occurs while formatting.
   * @throws IllegalStateException If the formatter is not in a state to run this method.
   */
  void insert(Token token) throws IOException, IllegalStateException;

  /**
   * Formats the specified deleted token.
   *
   * @param token The token to format
   *
   * @throws IOException           Should an I/O exception occurs while formatting.
   * @throws IllegalStateException If the formatter is not in a state to run this method.
   */
  void delete(Token token) throws IOException, IllegalStateException;

  /**
   * Sets the configuration to use with this formatter.
   *
   * @param config The configuration to use.
   */
  void setConfig(DiffXConfig config);

}
