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

import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.xml.Namespace;
import org.pageseeder.diffx.xml.PrefixMapping;
import org.pageseeder.diffx.sequence.Sequence;
import org.xml.sax.InputSource;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Base test class for the XML recorders.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class XMLRecorderTest {

  /**
   * @param config The configuration to use for the recorder.
   *
   * @return A new recorder instance for testing
   */
  public abstract XMLRecorder newXMLRecorder(DiffXConfig config);

  /**
   * @return The Diff-X config instance.
   */
  public abstract DiffXConfig getConfig();

  /**
   * Checks that the given XML is equivalent to the given token sequence.
   *
   * @param exp    The expected token sequence.
   * @param xml    The XML to test.
   * @param config The configuration to use for the XML
   *
   * @throws LoadingException Should an error occur while parsing XML.
   */
  public final void assertEquivalent(Sequence exp, String xml, DiffXConfig config)
      throws LoadingException {
    Sequence got = record(xml, config);
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
      PrefixMapping mapping = got.getPrefixMapping();
      for (Namespace namespace : mapping) {
        System.err.println(namespace.getUri() + " -> " + namespace.getPrefix());
      }
      throw ex;
    }
  }

  protected Sequence record(String xml, DiffXConfig config) throws LoadingException {
    try (Reader reader = new StringReader(xml)) {
      XMLRecorder recorder = newXMLRecorder(config);
      return recorder.process(new InputSource(reader));
    } catch (IOException ex) {
      // Shouldn't
      throw new UncheckedIOException(ex);
    }
  }

}
