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
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.algorithm.MyersGreedyXMLAlgorithm;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.test.DOMUtils;
import org.pageseeder.diffx.test.RandomStringFactory;
import org.pageseeder.diffx.test.RandomXMLFactory;
import org.pageseeder.diffx.test.TestTokens;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.impl.CharToken;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Random;

public class PerformanceTest {

  private static final DiffHandler VOID_HANDLER = (operator, token) -> {
  };

  private static long profileX(DiffAlgorithm algorithm, List<? extends XMLToken> first, List<? extends XMLToken> second, int times) {
    System.out.print(algorithm.toString());
    System.out.print("\t" + first.size() + "/" + second.size() + " tokens");
    // We do a dry run first
    long f = profile(algorithm, first, second);
    System.out.print(" First:" + f + "ms");
    long total = 0;
    for (int i = 0; i < times; i++) {
      long t = profile(algorithm, first, second);
      total += t;
    }
    System.out.println(" Avg:" + (total * 1.0 / times) + "ms");
    return total;
  }

  private static long profile(DiffAlgorithm algorithm, List<? extends XMLToken> first, List<? extends XMLToken> second) {
    long t0 = System.nanoTime();
    algorithm.diff(first, second, VOID_HANDLER);
    long t1 = System.nanoTime();
    return (t1 - t0) / 1_000_000;
  }

//  @Test
//  public void compareHirshbergVariations() throws IOException {
//    String from = getRandomString(2000, false);
//    String to = vary(from, .2);
//    List<CharToken> second = TestTokens.toCharTokens(from);
//    List<CharToken> first = TestTokens.toCharTokens(to);
//
//    // warm up
//    profileX(new TextOnlyProcessor(TextOnlyProcessor.Algorithm.WAGNER_FISCHER), first, second, 10);
//    profileX(new TextOnlyProcessor(TextOnlyProcessor.Algorithm.HIRSCHBERG), first, second, 10);
//
//    Random r = new Random();
//    int total1 = 0;
//    int total2 = 0;
//    for (int i=0; i < 2000; i++) {
//      if (i % 100 == 0) System.out.println(i+"...");
//      if (r.nextInt(2) == 0) {
//        total2 += profile(new TextOnlyProcessor(2), first, second);
//        total1 += profile(new TextOnlyProcessor(1), first, second);
//      } else {
//        total1 += profile(new TextOnlyProcessor(1), first, second);
//        total2 += profile(new TextOnlyProcessor(2), first, second);
//      }
//    }
//    System.out.println();
//    System.out.println("Total #1 "+new TextOnlyProcessor(1).toString()+": "+total1);
//    System.out.println("Total #2 "+new TextOnlyProcessor(2).toString()+": "+total2);
//
//    double pct = (total1 > total2)
//        ? (total1 - total2)*100.0 / total1
//        : (total2 - total1)*100.0 / total2;
//
//    System.out.println("Faster: "+((total1 > total2) ? "#2" : "#1")+" by "+pct+"%");
//  }

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

    profileX(new DefaultXMLProcessor(), first, second, 10);
    profileX(new TextOnlyProcessor(), first, second, 10);
    profileX(new OptimisticXMLProcessor(), first, second, 10);
  }

  @Test
  public void compareGeneralAlgorithms1() {
    int[] lengths = new int[]{500, 1000, 2000, 5000, 10000};
    for (int length : lengths) {
      // Generate content
      String from = getRandomString(length, false);
      String to = vary(from, .25);
      List<CharToken> second = TestTokens.toCharTokens(from);
      List<CharToken> first = TestTokens.toCharTokens(to);

      profileX(new TextOnlyProcessor<CharToken>(TextOnlyProcessor.Algorithm.MYER_LINEAR), first, second, 10);
      profileX(new TextOnlyProcessor<CharToken>(TextOnlyProcessor.Algorithm.MYER_GREEDY), first, second, 10);
      profileX(new TextOnlyProcessor<CharToken>(TextOnlyProcessor.Algorithm.KUMAR_RANGAN), first, second, 10);
      profileX(new TextOnlyProcessor<CharToken>(TextOnlyProcessor.Algorithm.HIRSCHBERG), first, second, 10);
      profileX(new TextOnlyProcessor<CharToken>(TextOnlyProcessor.Algorithm.WAGNER_FISCHER), first, second, 10);
    }
  }

  @Test
  public void compareGeneralAlgorithms2() {
    int[] lengths = new int[]{500, 1_000, 2_000, 5_000, 10_000};
    for (int length : lengths) {
      // Generate content
      String from = getRandomString(length, false);
      String to = vary(from, .05);
      List<CharToken> second = TestTokens.toCharTokens(from);
      List<CharToken> first = TestTokens.toCharTokens(to);

      profileX(new TextOnlyProcessor<CharToken>(TextOnlyProcessor.Algorithm.MYER_LINEAR), first, second, 10);
      profileX(new TextOnlyProcessor<CharToken>(TextOnlyProcessor.Algorithm.MYER_GREEDY), first, second, 10);
      profileX(new TextOnlyProcessor<CharToken>(TextOnlyProcessor.Algorithm.KUMAR_RANGAN), first, second, 10);
      profileX(new TextOnlyProcessor<CharToken>(TextOnlyProcessor.Algorithm.HIRSCHBERG), first, second, 10);
      profileX(new TextOnlyProcessor<CharToken>(TextOnlyProcessor.Algorithm.WAGNER_FISCHER), first, second, 10);
    }
  }

  @Test
  public void compareRandomString_1000_50() {
    // Generate content
    String from = getRandomString(1000, false);
    String to = vary(from, .50);
    List<CharToken> second = TestTokens.toCharTokens(from);
    List<CharToken> first = TestTokens.toCharTokens(to);

    profileX(new DefaultXMLProcessor(), first, second, 10);
    profileX(new TextOnlyProcessor<CharToken>(), first, second, 10);
    profileX(new OptimisticXMLProcessor(), first, second, 10);
  }

  @Test
  public void compareSingleElement_1000_20() throws DiffException {
    // Generate content
    String from = getRandomString(1000, true);
    String to = vary(from, .05);
    List<XMLToken> second = TestTokens.loadTokens("<root>" + from + "</root>", TextGranularity.SPACE_WORD);
    List<XMLToken> first = TestTokens.loadTokens("<root>" + to + "</root>", TextGranularity.SPACE_WORD);
    profileX(new DefaultXMLProcessor(), first, second, 10);
    profileX(new OptimisticXMLProcessor(), first, second, 10);
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

    profileX(new DefaultXMLProcessor(), firstWord, secondWord, 10);
    profileX(new OptimisticXMLProcessor(), firstWord, secondWord, 10);
    profileX(new MyersGreedyXMLAlgorithm(), firstWord, secondWord, 10);

    profileX(new DefaultXMLProcessor(), firstText, firstText, 10);
    profileX(new OptimisticXMLProcessor(), firstText, secondText, 10);
    profileX(new MyersGreedyXMLAlgorithm(), firstText, secondText, 10);

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

      profileX(new DefaultXMLProcessor(), first.tokens(), second.tokens(), 10);
      profileX(new OptimisticXMLProcessor(), first.tokens(), second.tokens(), 10);
      profileX(new MyersGreedyXMLAlgorithm(), first.tokens(), second.tokens(), 10);
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
    profileX(coalescingProcessor, firstText, secondText, 10);
    profileX(noCoalesceProcessor, firstText, secondText, 10);

  }

}
