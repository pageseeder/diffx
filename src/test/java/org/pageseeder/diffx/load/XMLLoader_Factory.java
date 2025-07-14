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


import org.junit.jupiter.api.Test;

import org.pageseeder.diffx.algorithm.*;
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.token.*;
import org.pageseeder.diffx.token.impl.WordToken;
import org.pageseeder.diffx.xml.Sequence;
import org.pageseeder.xmlwriter.XMLWriter;
import org.pageseeder.xmlwriter.XMLWriterNSImpl;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

class XMLLoader_Factory {

  @Test
  final void test_Factory() throws Exception{
    SAXLoader loader = new SAXLoader();
    loader.setConfig(DiffConfig.getDefault());
    XMLTokenProvider provider;

    long t = 0;
    Sequence sequence1 = new Sequence();
    for (int i = 0; i < 101; i++) {
      long t0 = System.nanoTime();
      InputSource source = new InputSource("src/test/resources/benchmark/large-table/left.psml");
      provider = new RecyclingTokenProvider(true);
      sequence1 = loader.load(source, provider);
      if (i > 0) t += (System.nanoTime() - t0) / 1000;
    }
    System.out.println("Time: "+(t/100)+"µs");
    System.out.println("Seq: "+sequence1.size());
    System.out.println("Ids: "+countIdentical(sequence1.tokens()).size());
    System.out.println("Eqs: "+countEqual(sequence1.tokens()).size());

    Sequence sequence2 = new Sequence();
    t = 0;
    for (int i = 0; i < 101; i++) {
      long t0 = System.nanoTime();
      InputSource source = new InputSource("src/test/resources/benchmark/large-table/left.psml");
      provider = new DefaultTokenProvider(true);
      sequence2 = loader.load(source, provider);
      if (i > 0) t += (System.nanoTime() - t0) / 1000;
    }
    System.out.println("Time: "+(t/100)+"µs");
    System.out.println("Seq: "+sequence2.size());
    System.out.println("Ids: "+countIdentical(sequence2.tokens()).size());
    System.out.println("Eqs: "+countEqual(sequence2.tokens()).size());

    System.out.println("1=2: "+sequence1.equals(sequence2));

  }

  @Test
  final void test_Factory2() throws Exception{
    DiffAlgorithm<XMLToken> algorithm = new KumarRanganAlgorithm<>();
    testDiffWithProvider(algorithm, new RecyclingTokenProvider(true));
    testDiffWithProvider(algorithm, new DefaultTokenProvider(true));
    testDiffWithProvider(algorithm, new RecyclingTokenProvider(false));
    testDiffWithProvider(algorithm, new DefaultTokenProvider(false));
  }

  @Test
  final void test_Text() throws Exception {
    SAXLoader loader = new SAXLoader();
    loader.setConfig(DiffConfig.getDefault().whitespace(WhiteSpaceProcessing.COMPARE));
    XMLTokenProvider provider = new RecyclingTokenProvider(true);
    InputSource sourceA = new InputSource("src/test/resources/benchmark/large-table/left.psml");
    InputSource sourceB = new InputSource("src/test/resources/benchmark/large-table/right.psml");
    Sequence sequenceA = loader.load(sourceA, provider);
    Sequence sequenceB = loader.load(sourceB, provider);
    Set<String> set = new HashSet<>();
    for (XMLToken token : sequenceA.tokens()) {
      if (token.getType() == XMLTokenType.TEXT && !token.isWhitespace()) {
        set.add(token.getValue().trim());
      }
    }
    for (XMLToken token : sequenceB.tokens()) {
      if (token.getType() == XMLTokenType.TEXT && !token.isWhitespace()) {
        set.add(token.getValue().trim());
      }
    }
    List<String> textTokens = new ArrayList<>(set);
    List<String> words = Files.readAllLines(Path.of("local/words.txt"));

    Map<XMLToken, XMLToken> map = new HashMap<>();
    for (String text : textTokens) {
      if (!text.matches("\\w+")) {
        System.out.println(text);
      }
      boolean noSub = text.equals("(") || text.equals(")") || text.equals(",") || text.equals(".") || text.equals(";");

      String sub = noSub? text : words.get(textTokens.indexOf(text));
      map.put(new WordToken(text), new WordToken(sub));
      map.put(new WordToken(" "+text), new WordToken(" "+sub));
    }

    File fileA0 = new File("src/test/resources/benchmark/large-table/leftx.psml");
    subAndSaveSequence(sequenceA, fileA0, map);

    File fileB0 = new File("src/test/resources/benchmark/large-table/rightx.psml");
    subAndSaveSequence(sequenceB, fileB0, map);

  }

  private static void subAndSaveSequence(Sequence seq, File file, Map<XMLToken, XMLToken> map) throws IOException {
    try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
      XMLWriter xml = new XMLWriterNSImpl(writer);
      for (XMLToken token : seq.tokens()) {
        if (token.getType() == XMLTokenType.TEXT && !token.isWhitespace()) {
          map.get(token).toXML(xml);
        } else {
          token.toXML(xml);
        }
      }
      xml.flush();
      xml.close();
    }
  }



  private static void testDiffWithProvider(DiffAlgorithm<XMLToken> algorithm, XMLTokenProvider provider) throws Exception {
    SAXLoader loader = new SAXLoader();
    loader.setConfig(DiffConfig.getDefault());
    long t = System.nanoTime();
    int n = 10;

    System.out.println("== "+provider.getClass().getSimpleName() +"("+provider.isNamespaceAware()+") ==");
    System.out.println("--Load");
    InputSource sourceA = new InputSource("src/test/resources/benchmark/large-table/left.psml");
    InputSource sourceB = new InputSource("src/test/resources/benchmark/large-table/right.psml");
    Sequence sequenceA = loader.load(sourceA, provider);
    Sequence sequenceB = loader.load(sourceB, provider);
    t = (System.nanoTime() - t) / 1_000_000;
    System.out.println("Load time: "+(t/n)+"ms");

    System.out.println("--Diff");
    t = 0;
    for (int i = 0; i < n+1; i++) {
      long t0 = System.nanoTime();
      algorithm.diff(sequenceA, sequenceB, (operator, token) -> {});
      long t1 = (System.nanoTime() - t0) / 1_000_000;
      if (i > 0) t += t1;
      System.out.print("#"+i+": "+t1+"ms ");
    }
    System.out.println();
    System.out.println("Diff time: "+(t/n)+"ms");

  }

  @Test
  void compareMemoryConsumption() throws Exception {
    DiffAlgorithm<XMLToken> algorithm = new KumarRanganAlgorithm<>();

    // Test with different providers and measure memory
    measureMemoryUsage("DefaultTokenProvider(true)", algorithm, new DefaultTokenProvider(true));
    measureMemoryUsage("DefaultTokenProvider(true)", algorithm, new DefaultTokenProvider(true));
    measureMemoryUsage("DefaultTokenProvider(false)", algorithm, new DefaultTokenProvider(false));
    measureMemoryUsage("DefaultTokenProvider(false)", algorithm, new DefaultTokenProvider(false));
    measureMemoryUsage("RecyclingTokenProvider(true)", algorithm, new RecyclingTokenProvider(true));
    measureMemoryUsage("RecyclingTokenProvider(true)", algorithm, new RecyclingTokenProvider(true));
    measureMemoryUsage("RecyclingTokenProvider(false)", algorithm, new RecyclingTokenProvider(false));
    measureMemoryUsage("RecyclingTokenProvider(false)", algorithm, new RecyclingTokenProvider(false));
  }

  private static void measureMemoryUsage(String name, DiffAlgorithm<XMLToken> algorithm,
                                         XMLTokenProvider provider) throws Exception {
    System.out.println("== " + name + " ==");

    // Setup
    SAXLoader loader = new SAXLoader();
    loader.setConfig(DiffConfig.getDefault());

    // Force GC to get a clean baseline
    System.gc();
    long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

    // Load and process
    InputSource sourceA = new InputSource("src/test/resources/benchmark/large-table/left.psml");
    InputSource sourceB = new InputSource("src/test/resources/benchmark/large-table/right.psml");
    Sequence sequenceA = loader.load(sourceA, provider);
    Sequence sequenceB = loader.load(sourceB, provider);

    // Perform diff
    algorithm.diff(sequenceA, sequenceB, (operator, token) -> {});

    long memoryJustAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    // Force GC again to stabilize measurement
    System.gc();
    long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

    // Report results
    System.out.println("Memory used: " + (memoryJustAfter - memoryBefore) / 1024 + " KB => " + (memoryAfter - memoryBefore) / 1024 + " KB");
//    System.out.println("Sequence A size: " + sequenceA.size());
//    System.out.println("Sequence B size: " + sequenceB.size());
//    System.out.println("Unique objects A: " + countIdentical(sequenceA.tokens()).size());
//    System.out.println("Unique objects B: " + countIdentical(sequenceB.tokens()).size());
  }


  /**
   * Counts occurrences of each unique object instance in a list using reference equality (==).
   *
   * @param <T> the type of elements in the list
   * @param list the list to analyze
   * @return a map where keys are object instances and values are their counts
   */
  static <T> Map<T, Integer> countIdentical(List<T> list) {
    if (list == null) return Collections.emptyMap();
    Map<T, Integer> countMap = new IdentityHashMap<>();
    for (T item : list) {
      countMap.put(item, countMap.getOrDefault(item, 0) + 1);
    }
    return countMap;
  }

  static <T> Map<T, Integer> countEqual(List<T> list) {
    if (list == null) return Collections.emptyMap();
    Map<T, Integer> countMap = new HashMap<>();
    for (T item : list) {
      countMap.put(item, countMap.getOrDefault(item, 0) + 1);
    }
    return countMap;
  }

}
