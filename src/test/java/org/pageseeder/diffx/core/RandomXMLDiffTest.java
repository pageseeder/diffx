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
import org.pageseeder.diffx.DiffException;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.algorithm.AlgorithmTest;
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.xml.Sequence;
import org.pageseeder.diffx.test.*;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.xml.NamespaceSet;
import org.w3c.dom.Document;

import java.util.List;

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
public abstract class RandomXMLDiffTest extends AlgorithmTest<XMLToken> {

  @Test
  public final void testRandom1() throws DiffException {
    RandomXMLFactory factory = new RandomXMLFactory();
    for (int i = 0; i < 100; i++) {
      Document docA = factory.getRandomXML(3, 3);
      Document docB = factory.vary(docA, .2);
      assertDiffXMLRandomOK(DOMUtils.toString(docA, true), DOMUtils.toString(docB, true));
    }
  }

  @Test
  public final void testRandom2() throws DiffException {
    RandomXMLFactory factory = new RandomXMLFactory();
    for (int i = 0; i < 100; i++) {
      Document docA = factory.getRandomXML(3, 3);
      Document docB = factory.vary(docA, .3);
      assertDiffXMLRandomOK(DOMUtils.toString(docA, true), DOMUtils.toString(docB, true));
    }
  }

  @Test
  public final void testRandom3() throws DiffException {
    RandomXMLFactory factory = new RandomXMLFactory();
    for (int i = 0; i < 100; i++) {
      Document docA = factory.getRandomXML(6, 2);
      Document docB = factory.vary(docA, .2);
      assertDiffXMLRandomOK(DOMUtils.toString(docA, true), DOMUtils.toString(docB, true));
    }
  }

  @Test
  public final void testRandom4() throws DiffException {
    RandomXMLFactory factory = new RandomXMLFactory();
    for (int i = 0; i < 100; i++) {
      Document docA = factory.getRandomXML(3, 10);
      Document docB = factory.vary(docA, .2);
      assertDiffXMLRandomOK(DOMUtils.toString(docA, true), DOMUtils.toString(docB, true));
    }
  }

//  @Test
//  public final void testRandom5() throws DiffException {
//    RandomHTMLFactory factory = new RandomHTMLFactory();
//    for (int i=0; i < 10; i++) {
//      Document docA = factory.nextDocument();
//      Document docB = factory.vary(docA, .25);
//      assertDiffXMLRandomOK(DOMUtils.toString(docA, true), DOMUtils.toString(docB, true));
//    }
//  }

  // helpers
  // --------------------------------------------------------------------------

  private void assertDiffXMLRandomOK(String docA, String docB) throws DiffException {
    // Record XML
    Sequence seqA = TestTokens.loadSequence(docA, TextGranularity.SPACE_WORD);
    Sequence seqB = TestTokens.loadSequence(docB, TextGranularity.SPACE_WORD);
    NamespaceSet namespaces = NamespaceSet.merge(seqA.getNamespaces(), seqB.getNamespaces());
    // Process as list of actions
    DiffAlgorithm<XMLToken> algo = getDiffAlgorithm();
    List<Action<XMLToken>> actions = TestActions.diffToActions(algo, seqA.tokens(), seqB.tokens());

    try {
      DiffAssertions.assertIsCorrect(seqA, seqB, actions);
      DiffAssertions.assertIsWellFormedXML(actions, namespaces);
    } catch (AssertionError ex) {
      printXMLErrorDetails(docA, docB, new String[0], TestActions.toXML(actions, namespaces), actions);
      throw ex;
    }
  }

}
