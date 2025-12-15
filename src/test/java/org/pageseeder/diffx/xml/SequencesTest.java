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
package org.pageseeder.diffx.xml;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.api.LoadingException;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.load.SAXLoader;
import org.pageseeder.diffx.test.TestTokens;
import org.pageseeder.diffx.token.TextToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test case for the token sequence utility.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
final class SequencesTest {

  /**
   * Test the maximum depth.
   *
   * @throws LoadingException If the loader cannot load the XML.
   */
  @Test
  void testMaxDepth1() throws LoadingException {
    Sequence seq = new SAXLoader().load("<a/>");
    int max = Sequences.getMaxDepth(seq);
    assertEquals(1, max);
  }

  /**
   * Test the maximum depth.
   *
   * @throws LoadingException If the loader cannot load the XML.
   */
  @Test
  void testMaxDepth2() throws LoadingException {
    Sequence seq = new SAXLoader().load("<a><a/></a>");
    int max = Sequences.getMaxDepth(seq);
    assertEquals(2, max);
  }

  /**
   * Test the maximum depth.
   *
   * @throws LoadingException If the loader cannot load the XML.
   */
  @Test
  void testMaxDepth3() throws LoadingException {
    Sequence seq = new SAXLoader().load("<a><b/><b/></a>");
    int max = Sequences.getMaxDepth(seq);
    assertEquals(2, max);
  }

  /**
   * Test the maximum depth.
   *
   * @throws LoadingException If the loader cannot load the XML.
   */
  @Test
  void testMaxDepth4() throws LoadingException {
    Sequence seq = new SAXLoader().load("<a><b><c/></b><b/></a>");
    int max = Sequences.getMaxDepth(seq);
    assertEquals(3, max);
  }

  /**
   * Test the maximum depth.
   *
   * @throws LoadingException If the loader cannot load the XML.
   */
  @Test
  void testMaxElementContent0() throws LoadingException {
    Sequence seq = new SAXLoader().load("<a/>");
    int max = Sequences.getMaxElementContent(seq);
    assertEquals(0, max);
  }

  /**
   * Test the maximum depth.
   *
   * @throws LoadingException If the loader cannot load the XML.
   */
  @Test
  void testMaxElementContent1() throws LoadingException {
    Sequence seq = new SAXLoader().load("<a>x</a>");
    int max = Sequences.getMaxElementContent(seq);
    assertEquals(1, max);
  }

  /**
   * Test the maximum depth.
   *
   * @throws LoadingException If the loader cannot load the XML.
   */
  @Test
  void testMaxElementContent2() throws LoadingException {
    Sequence seq = new SAXLoader().load("<a>x y</a>");
    int max = Sequences.getMaxElementContent(seq);
    assertEquals(3, max);
  }

  @Test
  void testFoldText1() throws LoadingException {
    Sequence input = TestTokens.loadSequence("<a/>", TextGranularity.SPACE_WORD);
    Sequence output = Sequences.foldText(input);
    assertEquals(input, output);
  }

  @Test
  void testFoldText2() throws LoadingException {
    Sequence input = TestTokens.loadSequence("<a>black</a>", TextGranularity.SPACE_WORD);
    Sequence output = Sequences.foldText(input);
    assertEquals(input, output);
  }

  @Test
  void testFoldText3() throws LoadingException {
    Sequence input = TestTokens.loadSequence("<a>black cat</a>", TextGranularity.SPACE_WORD);
    Sequence output = Sequences.foldText(input);
    assertEquals(input.size() - 1, output.size());
    assertTrue(output.getToken(1) instanceof TextToken);
    assertEquals("black cat", ((TextToken) output.getToken(1)).getCharacters());
  }

  @Test
  void testFoldText4() throws LoadingException {
    Sequence input = TestTokens.loadSequence("<p>a<b> black</b> cat</p>", TextGranularity.SPACE_WORD);
    Sequence output = Sequences.foldText(input);
    assertEquals(input, output);
  }

  @Test
  void testFoldText5() throws LoadingException {
    Sequence input = TestTokens.loadSequence("<p>a black<b> cat</b></p>", TextGranularity.TEXT);
    Sequence output = Sequences.foldText(input);
    assertEquals(input.size() - 1, output.size());
  }

}
