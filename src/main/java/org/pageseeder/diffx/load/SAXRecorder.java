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

import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.sequence.EventSequence;
import org.xml.sax.InputSource;

import java.io.IOException;

/**
 * Provided for backward compatibility
 *
 * @deprecated Replaced by {@link SAXLoader}.
 */
@Deprecated
public class SAXRecorder {

  private final SAXLoader loader = new SAXLoader();

  public EventSequence process(InputSource source) throws DiffXException, IOException {
    return new EventSequence(loader.load(source));
  }

  public void setConfig(DiffXConfig config) {
    this.loader.setConfig(config);
  }

}
