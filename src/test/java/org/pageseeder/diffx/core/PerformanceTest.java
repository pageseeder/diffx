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
import org.pageseeder.diffx.algorithm.*;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.profile.ProfileInfo;
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

  private static void generateXML(StringBuilder xml1, StringBuilder xml2, int elements) {
    // Generate content
    Random r = new Random();
    xml1.append("<root>\n");
    xml2.append("<root>\n");
    for (int i = 0; i < elements; i++) {
      int f = r.nextInt(10);
      String from = getRandomString(100 + f * 100, true);
      String to = (r.nextInt(10) < 3) ? vary(from, .05) : from;
      xml1.append("  <p>").append(from).append("</p>\n");
      xml2.append("  <p>").append(to).append("</p>\n");
    }
    xml1.append("</root>");
    xml2.append("</root>");
  }

  private static String getRandomString(int length, boolean spaces) {
    RandomStringFactory factory = new RandomStringFactory();
    return factory.getRandomString(length, spaces);
  }

  private static String vary(String source, double changes) {
    RandomStringFactory factory = new RandomStringFactory();
    return factory.vary(source, changes);
  }

  @Test
  public void compareRandomString_1000_10() {
    // Generate content
    String from = getRandomString(1000, false);
    String to = vary(from, .10);
    List<CharToken> second = TestTokens.toCharTokens(from);
    List<CharToken> first = TestTokens.toCharTokens(to);

    ProfileInfo.profileX(new DefaultXMLProcessor(), first, second, 10);
    ProfileInfo.profileX(new TextOnlyProcessor<CharToken>(), first, second, 10);
    ProfileInfo.profileX(new OptimisticXMLProcessor(), first, second, 10);
  }

  @Test
  public void compareGeneralAlgorithms1() {
    int[] lengths = new int[]{500, 1_000, 2_000, 5_000, 10_000};
    for (int length : lengths) {
      // Generate content
      String from = getRandomString(length, false);
      String to = vary(from, .25);
      List<CharToken> a = TestTokens.toCharTokens(from);
      List<CharToken> b = TestTokens.toCharTokens(to);

      ProfileInfo.profileX(new MyersGreedyAlgorithm<>(), a, b, 10);
      ProfileInfo.profileX(new MyersGreedyAlgorithm2<>(), a, b, 10);
      ProfileInfo.profileX(new MyersLinearAlgorithm<>(), a, b, 10);
      ProfileInfo.profileX(new KumarRanganAlgorithm<>(), a, b, 10);
      ProfileInfo.profileX(new HirschbergAlgorithm<>(), a, b, 10);
      ProfileInfo.profileX(new WagnerFischerAlgorithm<>(), a, b, 10);
    }
  }

  @Test
  public void compareGeneralAlgorithms2() {
    int[] lengths = new int[]{ 500, 1_000, 2_000, 5_000, 10_000 };
    for (int length : lengths) {
      // Generate content
      String from = getRandomString(length, false);
      String to = vary(from, .05);
      List<CharToken> a = TestTokens.toCharTokens(from);
      List<CharToken> b = TestTokens.toCharTokens(to);

      ProfileInfo.profileX(new MyersGreedyAlgorithm<>(), a, b, 10);
      ProfileInfo.profileX(new MyersGreedyAlgorithm2<>(), a, b, 10);
      ProfileInfo.profileX(new MyersLinearAlgorithm<>(), a, b, 10);
      ProfileInfo.profileX(new KumarRanganAlgorithm<>(), a, b, 10);
      ProfileInfo.profileX(new HirschbergAlgorithm<>(), a, b, 10);
      ProfileInfo.profileX(new WagnerFischerAlgorithm<>(), a, b, 10);
    }
  }

  @Test
  public void compareRandomString_1000_50() {
    // Generate content
    String from = getRandomString(1000, false);
    String to = vary(from, .50);
    List<CharToken> second = TestTokens.toCharTokens(from);
    List<CharToken> first = TestTokens.toCharTokens(to);

    ProfileInfo.profileX(new DefaultXMLProcessor(), first, second, 10);
    ProfileInfo.profileX(new TextOnlyProcessor<>(), first, second, 10);
    ProfileInfo.profileX(new OptimisticXMLProcessor(), first, second, 10);
  }

  @Test
  public void compareSingleElement_1000_20() throws DiffException {
    // Generate content
    String from = getRandomString(1000, true);
    String to = vary(from, .05);
    List<XMLToken> second = TestTokens.loadTokens("<root>" + from + "</root>", TextGranularity.SPACE_WORD);
    List<XMLToken> first = TestTokens.loadTokens("<root>" + to + "</root>", TextGranularity.SPACE_WORD);
    ProfileInfo.profileX(new DefaultXMLProcessor(), first, second, 10);
    ProfileInfo.profileX(new OptimisticXMLProcessor(), first, second, 10);
  }

  @Test
  public void compareShallowXML() throws DiffException {
    // Generate content
    StringBuilder xml1 = new StringBuilder();
    StringBuilder xml2 = new StringBuilder();
    generateXML(xml1, xml2, 50);

    // Parse tokens
    List<XMLToken> secondText = TestTokens.loadTokens(xml1.toString(), TextGranularity.TEXT);
    List<XMLToken> firstText = TestTokens.loadTokens(xml2.toString(), TextGranularity.TEXT);
    List<XMLToken> secondWord = TestTokens.loadTokens(xml1.toString(), TextGranularity.SPACE_WORD);
    List<XMLToken> firstWord = TestTokens.loadTokens(xml2.toString(), TextGranularity.SPACE_WORD);

    ProfileInfo.profileX(new DefaultXMLProcessor(), firstWord, secondWord, 10);
    ProfileInfo.profileX(new OptimisticXMLProcessor(), firstWord, secondWord, 10);
    ProfileInfo.profileX(new MyersGreedyXMLAlgorithm(), firstWord, secondWord, 10);

    ProfileInfo.profileX(new DefaultXMLProcessor(), firstText, firstText, 10);
    ProfileInfo.profileX(new OptimisticXMLProcessor(), firstText, secondText, 10);
    ProfileInfo.profileX(new MyersGreedyXMLAlgorithm(), firstText, secondText, 10);

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
    StringBuilder xml1 = new StringBuilder();
    StringBuilder xml2 = new StringBuilder();
    generateXML(xml1, xml2, 100);

    // Parse tokens
    List<XMLToken> secondText = TestTokens.loadTokens(xml1.toString(), TextGranularity.TEXT);
    List<XMLToken> firstText = TestTokens.loadTokens(xml2.toString(), TextGranularity.TEXT);

    OptimisticXMLProcessor coalescingProcessor = new OptimisticXMLProcessor();
    coalescingProcessor.setCoalesce(true);
    OptimisticXMLProcessor noCoalesceProcessor = new OptimisticXMLProcessor();
    noCoalesceProcessor.setCoalesce(false);
    ProfileInfo.profileX(coalescingProcessor, firstText, secondText, 10);
    ProfileInfo.profileX(noCoalesceProcessor, firstText, secondText, 10);
  }

}

