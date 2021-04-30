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
package org.pageseeder.diffx.config;

/**
 * Defines how should be tokenized and compared.
 *
 * @author Christophe Lauret
 * @version 10 May 2010
 */
public enum TextGranularity {

  /**
   * Differences should be reported at the character level.
   */
  CHARACTER,

  /**
   * Differences should be reported at the word level.
   */
  WORD,

  /**
   * Differences should be reported at the word level but may include space before.
   */
  SPACE_WORD,

  /**
   * Differences are reported by comparing text between punctuation marks.
   * <p>
   * This is finer than text but coarser than word.
   */
  PUNCTUATION,

  /**
   * Differences should be reported for the entire text node.
   */
  TEXT

}
