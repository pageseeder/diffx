/*
 * Copyright 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
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

package org.pageseeder.diffx.core;

import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;

abstract class ProcessorTest {

  protected static final DiffConfig COMPARE_SPACE_WORDS = new DiffConfig(WhiteSpaceProcessing.COMPARE, TextGranularity.SPACE_WORD);

  protected static final DiffConfig COMPARE_TEXT = new DiffConfig(WhiteSpaceProcessing.COMPARE, TextGranularity.TEXT);

  protected static final DiffConfig PRESERVE_SPACE_WORDS = new DiffConfig(WhiteSpaceProcessing.PRESERVE, TextGranularity.SPACE_WORD);

  protected static final DiffConfig PRESERVE_TEXT = new DiffConfig(WhiteSpaceProcessing.PRESERVE, TextGranularity.TEXT);

  /**
   * @return The processor instance to use for texting.
   */
  public abstract DiffProcessor getProcessor();

}
