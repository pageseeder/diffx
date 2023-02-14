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
import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.api.LoadingException;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.xml.Sequence;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class XMLLoader_XXE extends XMLLoaderTest {

  @Override
  public DiffConfig getConfig() {
    return DiffConfig.getDefault().granularity(TextGranularity.WORD);
  }

  @Test
  @DisplayName("XXE with secret")
  public final void testXXEWithSecret() {
    assertThrows(LoadingException.class, () -> loadResource("xxe_secret.xml", getConfig()));
  }

  @Test
  @DisplayName("XXE with HTTP")
  public final void testXXEWithHTTP()  {
    assertThrows(LoadingException.class, () -> loadResource("xxe_http.xml", getConfig()));
  }

  @Test
  @DisplayName("XXE with secret (Allowed)")
  public final void testXXEWithSecret_Allowed() throws LoadingException, IOException {
    loadResource("xxe_secret.xml", getConfig().allowDoctypeDeclaration(true));
  }

  @Test
  @DisplayName("XXE with HTTP (Allowed)")
  public final void testXXEWithHTTP_Allowed() throws LoadingException, IOException {
    loadResource("xxe_http.xml", getConfig().allowDoctypeDeclaration(true));
  }

  @Test
  @DisplayName("XXE with expansion")
  public final void testXXEWithExpansion() {
    assertThrows(LoadingException.class, () -> loadResource("xxe_expansion.xml", getConfig()));
  }

  protected Sequence loadResource(String filename, DiffConfig config) throws LoadingException, IOException {
    URL url = XMLLoader_XXE.class.getClassLoader().getResource("test/load/" +filename);
    XMLLoader loaded = newXMLLoader(config);
    try (Reader reader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)) {
      return loaded.load(reader);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
  }
}
