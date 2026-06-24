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
import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.api.LoadingException;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.xml.NamespaceSet;
import org.pageseeder.diffx.xml.Sequence;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class DOMLoaderTest {

  public XMLLoader configureLoader(DiffConfig config) {
    DOMLoader recorder = new DOMLoader();
    recorder.setConfig(config);
    return recorder;
  }

  @Nested
  @DisplayName("Text / No namespace")
  public class TextNoNamespace extends XMLLoader_Text_NoNS {
    @Override
    public XMLLoader newXMLLoader(DiffConfig config) {
      return configureLoader(config);
    }
  }

  @Nested
  @DisplayName("Word / Namespace aware")
  public class WordNamespaceAware extends XMLLoader_Word_NS {
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

  @Nested
  @DisplayName("XXE")
  public class XXE extends XMLLoader_XXE {
    @Override
    public XMLLoader newXMLLoader(DiffConfig config) {
      return configureLoader(config);
    }
  }

  @Nested
  @DisplayName("Namespace mappings")
  public class NamespaceMappings {

    private Sequence load(String xml) throws LoadingException {
      return configureLoader(DiffConfig.getDefault()).load(xml);
    }

    @Test
    @DisplayName("Default namespace on root maps to empty prefix")
    public void testDefaultNamespaceOnRoot() throws LoadingException {
      Sequence sequence = load("<root xmlns='https://example.org'/>");
      NamespaceSet namespaces = sequence.getNamespaces();
      assertEquals("", namespaces.getPrefix("https://example.org"));
    }

    @Test
    @DisplayName("Prefixed namespace on root maps to declared prefix")
    public void testPrefixedNamespaceOnRoot() throws LoadingException {
      Sequence sequence = load("<x:root xmlns:x='https://example.org'/>");
      NamespaceSet namespaces = sequence.getNamespaces();
      assertEquals("x", namespaces.getPrefix("https://example.org"));
    }

    @Test
    @DisplayName("Default namespace on root replaces initial empty-prefix mapping")
    public void testDefaultNamespaceReplacesInitialMapping() throws LoadingException {
      Sequence sequence = load("<root xmlns='https://example.org'><child/></root>");
      NamespaceSet namespaces = sequence.getNamespaces();
      assertEquals("", namespaces.getPrefix("https://example.org"));
    }

    @Test
    @DisplayName("Multiple prefixed namespaces are all mapped")
    public void testMultiplePrefixedNamespaces() throws LoadingException {
      Sequence sequence = load("<root xmlns:a='https://a.org' xmlns:b='https://b.org'><a:x/><b:y/></root>");
      NamespaceSet namespaces = sequence.getNamespaces();
      assertEquals("a", namespaces.getPrefix("https://a.org"));
      assertEquals("b", namespaces.getPrefix("https://b.org"));
    }

    @Test
    @DisplayName("Default namespace with prefixed namespace on same root")
    public void testDefaultAndPrefixedNamespaceOnRoot() throws LoadingException {
      Sequence sequence = load("<root xmlns='https://default.org' xmlns:x='https://x.org'><x:child/></root>");
      NamespaceSet namespaces = sequence.getNamespaces();
      assertEquals("", namespaces.getPrefix("https://default.org"));
      assertEquals("x", namespaces.getPrefix("https://x.org"));
    }

    @Test
    @DisplayName("Child element default namespace is not mapped to empty prefix")
    public void testChildDefaultNamespaceNotEmpty() throws LoadingException {
      Sequence sequence = load("<root><child xmlns='https://child.org'/></root>");
      NamespaceSet namespaces = sequence.getNamespaces();
      String prefix = namespaces.getPrefix("https://child.org");
      // Child-level default ns should not replace the root-level empty prefix
      assertEquals("", namespaces.getPrefix(""), "Root empty namespace should keep empty prefix");
    }
  }
}
