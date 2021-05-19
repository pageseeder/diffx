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

import org.pageseeder.diffx.api.LoadingException;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.xml.Sequence;
import org.pageseeder.diffx.xml.Namespace;
import org.pageseeder.diffx.xml.NamespaceSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Base test class for the XML loaders.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class XMLLoaderTest {

  /**
   * @param config The configuration to use for the recorder.
   *
   * @return A new recorder instance for testing
   */
  public abstract XMLLoader newXMLLoader(DiffConfig config);

  /**
   * @return The Diff-X config instance.
   */
  public abstract DiffConfig getConfig();

  /**
   * Checks that the given XML is equivalent to the given token sequence.
   *
   * @param exp    The expected token sequence.
   * @param xml    The XML to test.
   * @param config The configuration to use for the XML
   *
   * @throws LoadingException Should an error occur while parsing XML.
   */
  public final void assertEquivalent(Sequence exp, String xml, DiffConfig config)
      throws LoadingException {
    Sequence got = load(xml, config);
    try {
      assertEquals(exp.size(), got.size());
      assertEquals(exp, got);
    } catch (AssertionError ex) {
      System.err.println("_____________");
      System.err.println("* Expected:");
      System.err.println(exp.tokens());
      System.err.println("* But got:");
      System.err.println(got.tokens());
      System.err.println("* Prefix Mapping:");
      NamespaceSet namespaces = got.getNamespaces();
      for (Namespace namespace : namespaces) {
        System.err.println(namespace.getUri() + " -> " + namespace.getPrefix());
      }
      throw ex;
    }
  }

  protected Sequence load(String xml, DiffConfig config) throws LoadingException {
    XMLLoader loaded = newXMLLoader(config);
    return loaded.load(xml);
  }

}
