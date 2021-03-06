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

import org.pageseeder.diffx.config.DiffConfig;

/**
 * Base class for XML loaders.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
abstract class XMLLoaderBase implements XMLLoader {

  /**
   * The Diff configuration to use
   */
  protected DiffConfig config = DiffConfig.legacyDefault();

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

}
