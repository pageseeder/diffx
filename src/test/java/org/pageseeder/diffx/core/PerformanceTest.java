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
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.sequence.XMLSequence;
import org.pageseeder.diffx.test.DOMUtils;
import org.pageseeder.diffx.test.RandomStringFactory;
import org.pageseeder.diffx.test.RandomXMLFactory;
import org.pageseeder.diffx.test.TestTokens;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.impl.CharToken;
import org.w3c.dom.Document;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class PerformanceTest {

  private static final DiffHandler VOID_HANDLER = (operator, token) -> {
  };

  private static <T> ProfileInfo profileX(DiffAlgorithm<T> algorithm, List<? extends T> a, List<? extends T> b, int times, boolean quiet) {
    ProfileInfo info = new ProfileInfo(algorithm, times, a.size(), b.size());
    if (algorithm instanceof WagnerFischerAlgorithm && (1L*a.size()*b.size()) > Integer.MAX_VALUE)
      return info;
    // We do a dry run first
    info.first = profile(algorithm, a, b);
    long total = 0;
    for (int i = 0; i < times; i++) {
      long t = profile(algorithm, a, b);
      total += t;
    }
    info.total = total;
    if (!quiet) System.out.println(info);
    return info;
  }

  private static <T> ProfileInfo profileX(DiffAlgorithm<T> algorithm, List<? extends T> a, List<? extends T> b, int times) {
    return profileX(algorithm, a, b, times, false);
  }


  private static <T> long profile(DiffAlgorithm<T> algorithm, List<? extends T> a, List<? extends T> b) {
    long t0 = System.nanoTime();
    algorithm.diff(a, b, VOID_HANDLER);
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
    profileX(new TextOnlyProcessor<CharToken>(), first, second, 10);
    profileX(new OptimisticXMLProcessor(), first, second, 10);
  }

  @Test
  public void compareGeneralAlgorithms1() {
    int[] lengths = new int[]{500, 1_000, 2_000, 5_000, 10_000};
    for (int length : lengths) {
      // Generate content
      String from = getRandomString(length, false);
      String to = vary(from, .25);
      List<CharToken> second = TestTokens.toCharTokens(from);
      List<CharToken> first = TestTokens.toCharTokens(to);

      profileX(new TextOnlyProcessor<CharToken>(TextOnlyProcessor.Algorithm.MYER_GREEDY), first, second, 10);
      profileX(new TextOnlyProcessor<CharToken>(TextOnlyProcessor.Algorithm.MYER_LINEAR), first, second, 10);
      profileX(new TextOnlyProcessor<CharToken>(TextOnlyProcessor.Algorithm.KUMAR_RANGAN), first, second, 10);
      profileX(new TextOnlyProcessor<CharToken>(TextOnlyProcessor.Algorithm.HIRSCHBERG), first, second, 10);
      profileX(new TextOnlyProcessor<CharToken>(TextOnlyProcessor.Algorithm.WAGNER_FISCHER), first, second, 10);
    }
  }

  @Test
  public void compareGeneralAlgorithms2() {
    int[] lengths = new int[]{500, 1_000, 2_000, 5_000, 10_000, 20_000};
    for (int length : lengths) {
      // Generate content
      String from = getRandomString(length, false);
      String to = vary(from, .05);
      List<CharToken> second = TestTokens.toCharTokens(from);
      List<CharToken> first = TestTokens.toCharTokens(to);

      profileX(new TextOnlyProcessor<>(TextOnlyProcessor.Algorithm.MYER_LINEAR), first, second, 10);
      profileX(new TextOnlyProcessor<>(TextOnlyProcessor.Algorithm.MYER_GREEDY), first, second, 10);
      profileX(new TextOnlyProcessor<>(TextOnlyProcessor.Algorithm.KUMAR_RANGAN), first, second, 10);
      profileX(new TextOnlyProcessor<>(TextOnlyProcessor.Algorithm.HIRSCHBERG), first, second, 10);
      profileX(new TextOnlyProcessor<>(TextOnlyProcessor.Algorithm.WAGNER_FISCHER), first, second, 10);
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
      XMLSequence second = TestTokens.loadSequence(DOMUtils.toString(from, true), TextGranularity.WORD);
      XMLSequence first = TestTokens.loadSequence(DOMUtils.toString(to, true), TextGranularity.WORD);

      profileX(new DefaultXMLProcessor(), first.tokens(), second.tokens(), 10);
      profileX(new OptimisticXMLProcessor(), first.tokens(), second.tokens(), 10);
      profileX(new MyersGreedyXMLAlgorithm(), first.tokens(), second.tokens(), 10);
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
      XMLSequence a = TestTokens.loadSequence(DOMUtils.toString(from, true), TextGranularity.WORD);
      XMLSequence b = TestTokens.loadSequence(DOMUtils.toString(to, true), TextGranularity.WORD);

      profileX(new MatrixXMLAlgorithm(), a.tokens(), b.tokens(), 10);
      profileX(new MyersGreedyXMLAlgorithm(), a.tokens(), b.tokens(), 10);
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

  public static void main(String[] args) {
    compareSizeGeneric();
    compareDifferenceGeneric();
  }

  private static void compareSizeGeneric() {
    final int times = 100;
    int[] lengths = new int[]{500, 1_000, 2_000, 5_000, 10_000, 20_000, 50_000};
    double[] variations = new double[]{ .05, .25 };
    NumberFormat sizeFormat = new DecimalFormat("#,###");

    List<DiffAlgorithm<CharToken>> algorithms = new ArrayList<>();
    algorithms.add(new MyersGreedyAlgorithm<>());
    algorithms.add(new MyersLinearAlgorithm<>());
    algorithms.add(new KumarRanganAlgorithm<>());
    algorithms.add(new HirschbergAlgorithm<>());
    algorithms.add(new WagnerFischerAlgorithm<>());

    for (double variation : variations) {
      System.out.println("Variation: "+(variation*100)+"%");
      Map<String, List<ProfileInfo>> results = new HashMap<>();
      for (DiffAlgorithm<CharToken> algorithm : algorithms) {
        results.put(algorithm.getClass().getSimpleName(), new ArrayList<>());
      }
      for (int length : lengths) {
        // Generate content
        String from = getRandomString(length, false);
        String to = vary(from, variation);
        List<CharToken> a = TestTokens.toCharTokens(from);
        List<CharToken> b = TestTokens.toCharTokens(to);

        for (DiffAlgorithm<CharToken> algorithm : algorithms) {
          ProfileInfo info = profileX(algorithm, a, b, times, true);
          results.get(algorithm.getClass().getSimpleName()).add(info);
        }
      }

      // Print results
      System.out.print("| Algorithm               |");
      for (int length : lengths) System.out.print(" "+padLeft(sizeFormat.format(length), 7)+" |");
      System.out.println();
      System.out.print("| ----------------------- |");
      for (int length : lengths) System.out.print(" ------- |");
      System.out.println();
      for (Map.Entry<String, List<ProfileInfo>> entry : results.entrySet()) {
        System.out.print("| "+padRight(entry.getKey(), 23) +" |");
        for (ProfileInfo info : entry.getValue()) {
          System.out.print(" "+padLeft(info.average() +"ms", 7)+" |");
        }
        System.out.println();
      }
      System.out.println("");
    }
  }


  private static void compareDifferenceGeneric() {
    final int times = 100;
    int[] lengths = new int[]{ 10_000 };
    double[] variations = new double[]{ .01, .05, .25, .5, .75, .99 };
    NumberFormat sizeFormat = new DecimalFormat("#,###");

    List<DiffAlgorithm<CharToken>> algorithms = new ArrayList<>();
    algorithms.add(new MyersGreedyAlgorithm<>());
    algorithms.add(new MyersLinearAlgorithm<>());
    algorithms.add(new KumarRanganAlgorithm<>());
    algorithms.add(new HirschbergAlgorithm<>());
    algorithms.add(new WagnerFischerAlgorithm<>());

    for (int length : lengths) {
      System.out.println("Length: "+sizeFormat.format(length));
      Map<String, List<ProfileInfo>> results = new HashMap<>();
      for (DiffAlgorithm<CharToken> algorithm : algorithms) {
        results.put(algorithm.getClass().getSimpleName(), new ArrayList<>());
      }
      for (double variation : variations) {
        // Generate content
        String from = getRandomString(length, false);
        String to = vary(from, variation);
        List<CharToken> a = TestTokens.toCharTokens(from);
        List<CharToken> b = TestTokens.toCharTokens(to);

        for (DiffAlgorithm<CharToken> algorithm : algorithms) {
          ProfileInfo info = profileX(algorithm, a, b, times, true);
          results.get(algorithm.getClass().getSimpleName()).add(info);
        }
      }

      // Print results
      System.out.print("| Algorithm               |");
      for (double variation : variations) System.out.print(" "+padLeft(""+variation*100, 7)+"% |");
      System.out.println();
      System.out.print("| ----------------------- |");
      for (double variation : variations) System.out.print(" ------- |");
      System.out.println();
      for (Map.Entry<String, List<ProfileInfo>> entry : results.entrySet()) {
        System.out.print("| "+padRight(entry.getKey(), 23) +" |");
        for (ProfileInfo info : entry.getValue()) {
          System.out.print(" "+padLeft(info.average() +"ms", 7)+" |");
        }
        System.out.println();
      }
      System.out.println("");
    }
  }




  private static class ProfileInfo {

    private final String algorithm;
    private final int times;
    private final int sizeA;
    private final int sizeB;

    private long first;
    private long total;

    ProfileInfo(DiffAlgorithm<?> algorithm, int times, int sizeA, int sizeB) {
      this.algorithm = algorithm.getClass().getSimpleName();
      this.times = times;
      this.sizeA = sizeA;
      this.sizeB = sizeB;
    }

    double average() {
      return this.total * 1.0 / this.times;
    }

    @Override
    public String toString() {
      StringBuilder out = new StringBuilder();
      out.append(this.algorithm.toString());
      out.append("\t").append(sizeA).append("/").append(sizeB).append(" tokens");
      out.append("\tFirst:").append(first).append("ms");
      out.append("\tAvg:").append(average()).append("ms");
      return out.toString();
    }

  }

  private static String padLeft(String s, int length) {
    StringBuilder out = new StringBuilder();
    for (int i=0; i < length-s.length(); i++) out.append(' ');
    out.append(s);
    return out.toString();
  }

  private static String padRight(String s, int length) {
    StringBuilder out = new StringBuilder();
    out.append(s);
    for (int i=0; i < length-s.length(); i++) out.append(' ');
    return out.toString();
  }
}

