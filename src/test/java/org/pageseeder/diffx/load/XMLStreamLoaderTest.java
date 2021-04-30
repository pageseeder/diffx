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
import org.pageseeder.diffx.config.DiffConfig;

/**
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class XMLStreamLoaderTest {

  public XMLLoader configureLoader(DiffConfig config) {
    XMLStreamLoader loader = new XMLStreamLoader();
    loader.setConfig(config);
    return loader;
  }

  @Nested
  @DisplayName("Text / No namespace")
  public class Text_NoNamespace extends XMLLoader_Text_NoNS {
    @Override
    public XMLLoader newXMLLoader(DiffConfig config) {
      return configureLoader(config);
    }
  }

  @Nested
  @DisplayName("Word / Namespace aware")
  public class Word_NamespaceAware extends XMLLoader_Word_NS {
    @Override
    public XMLLoader newXMLLoader(DiffConfig config) {
      return configureLoader(config);
    }
  }

  @Nested
  @DisplayName("SpaceWord / Namespace aware")
  public class SpaceWord_NamespaceAware extends XMLLoader_SpaceWord_NS {
    @Override
    public XMLLoader newXMLLoader(DiffConfig config) {
      return configureLoader(config);
    }
  }
}
