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
 * Defines how white spaces should be processed.
 *
 * <p>White space processing can have functional implications at all stages of diffing, including
 * when loading and formatting.
 *
 * @author Christophe Lauret
 * @version 10 May 2010
 */
public enum WhiteSpaceProcessing {

  /**
   * All white spaces should be completely ignored, this is the most efficient processing.
   */
  IGNORE,

  /**
   * White spaces should be preserved, that is they will be loaded and returned during formatting,
   * but the algorithm can consider them equivalent and will not report differences between white
   * spaces.
   */
  PRESERVE,

  /**
   * White spaces should be preserved throughout the process and compared.
   * All white space differences will be reported by the algorithm.
   * This is the most costly processing.
   */
  COMPARE

}
