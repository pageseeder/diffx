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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.DiffException;
import org.pageseeder.diffx.algorithm.*;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.profile.Pair;
import org.pageseeder.diffx.profile.ProfileInfo;
import org.pageseeder.diffx.profile.Profilers;
import org.pageseeder.diffx.xml.Sequence;
import org.pageseeder.diffx.test.DOMUtils;
import org.pageseeder.diffx.test.RandomStringFactory;
import org.pageseeder.diffx.test.RandomXMLFactory;
import org.pageseeder.diffx.test.TestTokens;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.impl.CharToken;
import org.w3c.dom.Document;

import java.util.*;

public class PerformanceTest {

  private static void generateXML(StringBuilder xmlA, StringBuilder xmlB, int elements) {
    // Generate content
    Random r = new Random();
    xmlA.append("<root>\n");
    xmlB.append("<root>\n");
    for (int i = 0; i < elements; i++) {
      int f = r.nextInt(10);
      String from = Profilers.getRandomString(100 + f * 100, true);
      String to = (r.nextInt(10) < 3) ? Profilers.vary(from, .05) : from;
      xmlA.append("  <p>").append(from).append("</p>\n");
      xmlB.append("  <p>").append(to).append("</p>\n");
    }
    xmlA.append("</root>");
    xmlB.append("</root>");
  }

  @Test
  @DisplayName("General algorithm / 500 to 10,000 chars / 5% variation")
  public void compareGeneralAlgorithms_var5pct() {
    int[] lengths = new int[]{ 500, 1_000, 2_000, 5_000, 10_000 };
    for (int length : lengths) {
      Pair<List<CharToken>> p = Profilers.getRandomStringPair(length, false, .05);
      ProfileInfo.profileX(new MyersGreedyAlgorithm<>(), p.a, p.b, 10);
      ProfileInfo.profileX(new MyersGreedyAlgorithm2<>(), p.a, p.b, 10);
      ProfileInfo.profileX(new MyersLinearAlgorithm<>(), p.a, p.b, 10);
      ProfileInfo.profileX(new KumarRanganAlgorithm<>(), p.a, p.b, 10);
      ProfileInfo.profileX(new HirschbergAlgorithm<>(), p.a, p.b, 10);
      ProfileInfo.profileX(new WagnerFischerAlgorithm<>(), p.a, p.b, 10);
    }
  }

  @Test
  @DisplayName("General algorithm / 500 to 10,000 chars / 25% variation")
  public void compareGeneralAlgorithms_var25pct() {
    int[] lengths = new int[]{ 500, 1_000, 2_000, 5_000, 10_000 };
    for (int length : lengths) {
      Pair<List<CharToken>> p = Profilers.getRandomStringPair(length, false, .25);
      ProfileInfo.profileX(new MyersGreedyAlgorithm<>(), p.a, p.b, 10);
      ProfileInfo.profileX(new MyersGreedyAlgorithm2<>(), p.a, p.b, 10);
      ProfileInfo.profileX(new MyersLinearAlgorithm<>(), p.a, p.b, 10);
      ProfileInfo.profileX(new KumarRanganAlgorithm<>(), p.a, p.b, 10);
      ProfileInfo.profileX(new HirschbergAlgorithm<>(), p.a, p.b, 10);
      ProfileInfo.profileX(new WagnerFischerAlgorithm<>(), p.a, p.b, 10);
    }
  }

  @Test
  @DisplayName("XML processors / 1,000 chars / 10% variation")
  public void compareRandomString_1000_10() {
    Pair<List<CharToken>> p = Profilers.getRandomStringPair(1_000, false, .10);
    ProfileInfo.profileX(new DefaultXMLProcessor(), p.a, p.b, 10);
    ProfileInfo.profileX(new TextOnlyProcessor<>(), p.a, p.b, 10);
    ProfileInfo.profileX(new OptimisticXMLProcessor(), p.a, p.b, 10);
  }

  @Test
  @DisplayName("XML processors / 1,000 chars / 50% variation")
  public void compareRandomString_1000_50() {
    Pair<List<CharToken>> p = Profilers.getRandomStringPair(1_000, false, .50);
    ProfileInfo.profileX(new DefaultXMLProcessor(), p.a, p.b, 10);
    ProfileInfo.profileX(new TextOnlyProcessor<>(), p.a, p.b, 10);
    ProfileInfo.profileX(new OptimisticXMLProcessor(), p.a, p.b, 10);
  }

  @Test
  @DisplayName("XML processors / Single element 1,000 chars / 5% variation")
  public void compareSingleElement_1000_20() throws DiffException {
    String from = Profilers.getRandomString(1000, true);
    String to = Profilers.vary(from, .05);
    List<XMLToken> a = TestTokens.loadTokens("<root>" + from + "</root>", TextGranularity.SPACE_WORD);
    List<XMLToken> b = TestTokens.loadTokens("<root>" + to + "</root>", TextGranularity.SPACE_WORD);
    ProfileInfo.profileX(new DefaultXMLProcessor(), a, b,10);
    ProfileInfo.profileX(new OptimisticXMLProcessor(), a, b, 10);
  }

  @Test
  public void compareShallowXML() throws DiffException {
    // Generate content
    StringBuilder xmlA = new StringBuilder();
    StringBuilder xmlB = new StringBuilder();
    generateXML(xmlA, xmlB, 50);

    // Parse tokens
    List<XMLToken> xmlAText = TestTokens.loadTokens(xmlA.toString(), TextGranularity.TEXT);
    List<XMLToken> xmlBText = TestTokens.loadTokens(xmlB.toString(), TextGranularity.TEXT);
    List<XMLToken> xmlAWord = TestTokens.loadTokens(xmlA.toString(), TextGranularity.SPACE_WORD);
    List<XMLToken> xmlBWord = TestTokens.loadTokens(xmlB.toString(), TextGranularity.SPACE_WORD);

    ProfileInfo.profileX(new DefaultXMLProcessor(), xmlAWord, xmlBWord,10);
    ProfileInfo.profileX(new OptimisticXMLProcessor(), xmlAWord, xmlBWord,10);
    ProfileInfo.profileX(new MyersGreedyXMLAlgorithm(), xmlAWord, xmlBWord,10);

    ProfileInfo.profileX(new DefaultXMLProcessor(), xmlBText, xmlBText, 10);
    ProfileInfo.profileX(new OptimisticXMLProcessor(), xmlBText, xmlAText, 10);
    ProfileInfo.profileX(new MyersGreedyXMLAlgorithm(), xmlBText, xmlAText, 10);

  }

  @Test
  public void compareXMLProcessors() throws DiffException {
    int[] lengths = new int[]{500, 1000, 2000, 5000, 10000};
    for (int length : lengths) {
      // Generate content
      RandomXMLFactory factory = new RandomXMLFactory();
      Document from = factory.getRandomXML(5, 5);
      Document to = factory.vary(from, .2);
      Sequence second = TestTokens.loadSequence(DOMUtils.toString(from, true), TextGranularity.WORD);
      Sequence first = TestTokens.loadSequence(DOMUtils.toString(to, true), TextGranularity.WORD);

      ProfileInfo.profileX(new DefaultXMLProcessor(), first.tokens(), second.tokens(), 10);
      ProfileInfo.profileX(new OptimisticXMLProcessor(), first.tokens(), second.tokens(), 10);
      ProfileInfo.profileX(new MyersGreedyXMLAlgorithm(), first.tokens(), second.tokens(), 10);
    }
  }


  @Test
  public void compareXMLAlgorithms() throws DiffException {
    int[] lengths = new int[]{ 500, 1000, 2000, 5000, 10000 };
    for (int length : lengths) {
      // Generate content
      RandomXMLFactory factory = new RandomXMLFactory();
      Document from = factory.getRandomXML(5, 10);
      Document to = factory.vary(from, .2);
      Sequence a = TestTokens.loadSequence(DOMUtils.toString(from, true), TextGranularity.WORD);
      Sequence b = TestTokens.loadSequence(DOMUtils.toString(to, true), TextGranularity.WORD);

      ProfileInfo.profileX(new MatrixXMLAlgorithm(), a.tokens(), b.tokens(), 10);
      ProfileInfo.profileX(new MyersGreedyXMLAlgorithm(), a.tokens(), b.tokens(), 10);
    }
  }

  @Test
  public void compareCoalesce() throws DiffException {
    // Generate content
    StringBuilder xmlA = new StringBuilder();
    StringBuilder xmlB = new StringBuilder();
    generateXML(xmlA, xmlB, 100);

    // Parse tokens
    List<XMLToken> secondText = TestTokens.loadTokens(xmlA.toString(), TextGranularity.TEXT);
    List<XMLToken> firstText = TestTokens.loadTokens(xmlB.toString(), TextGranularity.TEXT);

    OptimisticXMLProcessor coalescingProcessor = new OptimisticXMLProcessor();
    coalescingProcessor.setCoalesce(true);
    OptimisticXMLProcessor noCoalesceProcessor = new OptimisticXMLProcessor();
    noCoalesceProcessor.setCoalesce(false);
    ProfileInfo.profileX(coalescingProcessor, firstText, secondText, 10);
    ProfileInfo.profileX(noCoalesceProcessor, firstText, secondText, 10);
  }

}

