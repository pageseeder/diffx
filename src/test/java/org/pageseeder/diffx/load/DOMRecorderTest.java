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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.pageseeder.diffx.config.DiffXConfig;

/**
 * Test class for the DOM Recorder.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class DOMRecorderTest {

  public XMLRecorder configureRecorder(DiffXConfig config) {
    DOMRecorder recorder = new DOMRecorder();
    recorder.setConfig(config);
    return recorder;
  }

  @Nested
  @DisplayName("Text / No namespace")
  public class TextNoNamespace extends XMLRecorder_Text_NoNS {
    @Override
    public XMLRecorder newXMLRecorder(DiffXConfig config) {
      return configureRecorder(config);
    }
  }

  @Nested
  @DisplayName("Word / Namespace aware")
  public class WordNamespaceAware extends XMLRecorder_Word_NS {
    @Override
    public XMLRecorder newXMLRecorder(DiffXConfig config) {
      return configureRecorder(config);
    }
  }

  @Nested
  @DisplayName("SpaceWord / Namespace aware")
  public class SpaceWord_NamespaceAware extends XMLRecorder_SpaceWord_NS {
    @Override
    public XMLRecorder newXMLRecorder(DiffXConfig config) {
      return configureRecorder(config);
    }
  }
}
