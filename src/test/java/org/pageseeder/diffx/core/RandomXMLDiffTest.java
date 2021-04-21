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
package org.pageseeder.diffx.core;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.sequence.PrefixMapping;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.test.*;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test case for Diff-X algorithm implementations.
 *
 * <p>This class extends the the level 1 tests, and expect algorithms to produce
 * results of better quality, that is which serialised form always produce
 * well-formed XML.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class RandomXMLDiffTest extends AlgorithmTest {

  @Test
  public final void testRandom1() throws DiffXException {
    RandomXMLFactory factory = new RandomXMLFactory();
    for (int i=0; i < 100; i++) {
      Document docA = factory.getRandomXML(3,3);
      Document docB = factory.vary(docA, .2);
      assertDiffXMLRandomOK(DOMUtils.toString(docA, true), DOMUtils.toString(docB, true));
    }
  }

  @Test
  public final void testRandom2() throws DiffXException {
    RandomXMLFactory factory = new RandomXMLFactory();
    for (int i=0; i < 100; i++) {
      Document docA = factory.getRandomXML(3,3);
      Document docB = factory.vary(docA, .3);
      assertDiffXMLRandomOK(DOMUtils.toString(docA, true), DOMUtils.toString(docB, true));
    }
  }

  @Test
  public final void testRandom3() throws DiffXException {
    RandomXMLFactory factory = new RandomXMLFactory();
    for (int i=0; i < 100; i++) {
      Document docA = factory.getRandomXML(6,2);
      Document docB = factory.vary(docA, .2);
      assertDiffXMLRandomOK(DOMUtils.toString(docA, true), DOMUtils.toString(docB, true));
    }
  }

  @Test
  public final void testRandom4() throws DiffXException {
    RandomXMLFactory factory = new RandomXMLFactory();
    for (int i=0; i < 100; i++) {
      Document docA = factory.getRandomXML(3,10);
      Document docB = factory.vary(docA, .2);
      assertDiffXMLRandomOK(DOMUtils.toString(docA, true), DOMUtils.toString(docB, true));
    }
  }

  @Test
  public final void testRandom5() throws DiffXException {
    RandomHTMLFactory factory = new RandomHTMLFactory();
    for (int i=0; i < 10; i++) {
      Document docA = factory.nextDocument();
      Document docB = factory.vary(docA, .25);
      assertDiffXMLRandomOK(DOMUtils.toString(docA, true), DOMUtils.toString(docB, true));
    }
  }

  // helpers
  // --------------------------------------------------------------------------

  private void assertDiffXMLRandomOK(String docA, String docB) throws DiffXException {
    // Record XML
    Sequence seq1 = Events.recordXMLSequence(docA, TextGranularity.SPACE_WORD);
    Sequence seq2 = Events.recordXMLSequence(docB, TextGranularity.SPACE_WORD);
    System.out.println();
    PrefixMapping mapping = PrefixMapping.merge(seq1.getPrefixMapping(), seq2.getPrefixMapping());
    // Process as list of actions
    long t1 = System.nanoTime();
    List<Action> actions = TestActions.diffToActions(getDiffAlgorithm(), seq1.tokens(), seq2.tokens());
    long t2 = System.nanoTime();
    System.out.println(seq1.size()+"/"+seq2.size()+" -> "+((t2-t1) / (seq1.size()+seq2.size())));
    try {
      DiffAssertions.assertIsCorrect(seq1, seq2, actions);
      DiffAssertions.assertIsWellFormedXML(actions, mapping);
    } catch (AssertionError ex) {
      printXMLErrorDetails(docA, docB, new String[0], TestActions.toXML(actions, mapping), actions);
      throw ex;
    }
  }

}
