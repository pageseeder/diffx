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
import org.pageseeder.diffx.sequence.Namespace;
import org.pageseeder.diffx.sequence.PrefixMapping;
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
   * The XML recorder to use.
   */
  private static final DiffXConfig NOT_NS_AWARE;

  static {
    NOT_NS_AWARE = new DiffXConfig();
    NOT_NS_AWARE.setNamespaceAware(false);
  }

  /**
   * Returns the Diff-X recorder to test.
   *
   * @param config The configuration to use for the recorder.
   *
   * @return The Diff-X Algorithm instance.
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
    Sequence seq = record(xml, config);
    try {
      assertEquals(exp.size(), seq.size());
      assertEquals(exp, seq);
    } catch (AssertionError ex) {
      System.err.println("_____________");
      System.err.println("* Expected:");
      PrintWriter pw1 = new PrintWriter(System.err);
      exp.export(pw1);
      pw1.flush();
      System.err.println("* But got:");
      PrintWriter pw2 = new PrintWriter(System.err);
      seq.export(pw2);
      pw2.flush();
      System.err.println("* Prefix Mapping:");
      PrefixMapping mapping = seq.getPrefixMapping();
      for (Namespace namespace : mapping) {
        System.err.println(namespace.getUri() + " -> " + namespace.getPrefix());
      }
      throw ex;
    }
  }

  private Sequence record(String xml, DiffXConfig config) throws LoadingException {
    try (Reader reader = new StringReader(xml)) {
      XMLRecorder recorder = newXMLRecorder(config);
      return recorder.process(new InputSource(reader));
    } catch (IOException ex) {
      // Shouldn't
      throw new UncheckedIOException(ex);
    }
  }

}
