/*
 * Copyright 2010-2015 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
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
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.impl.CharEvent;
import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.test.Events;
import org.pageseeder.diffx.test.RandomStringFactory;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class PerformanceTest {

  private static final DiffHandler VOID_HANDLER = (operator, event) -> {};

  @Test
  public void compareRandomString_1000_10() {
    // Generate content
    String from = getRandomString(1000, false);
    String to = vary(from, .10);
    List<CharEvent> second = Events.toCharEvents(from);
    List<CharEvent> first = Events.toCharEvents(to);

    profileX(new DefaultXMLProcessor(), first, second, 10);
    profileX(new TextOnlyProcessor(), first, second, 10);
    profileX(new ProgressiveXMLProcessor(), first, second, 10);
  }

  @Test
  public void compareGeneralAlgorithms() {
    int[] lengths = new int[]{500, 1000, 2000, 5000, 10000};
    for (int length : lengths) {
      // Generate content
      String from = getRandomString(length, false);
      String to = vary(from, .2);
      List<CharEvent> second = Events.toCharEvents(from);
      List<CharEvent> first = Events.toCharEvents(to);

      profileX(new TextOnlyProcessor(TextOnlyProcessor.Algorithm.KUMAR_RANGAN), first, second, 2);
      profileX(new TextOnlyProcessor(TextOnlyProcessor.Algorithm.HIRSCHBERG), first, second, 2);
      profileX(new TextOnlyProcessor(TextOnlyProcessor.Algorithm.WAGNER_FISCHER), first, second, 10);
    }
  }

//  @Test
//  public void compareHirshbergVariations() throws IOException {
//    String from = getRandomString(2000, false);
//    String to = vary(from, .2);
//    List<CharEvent> second = Events.toCharEvents(from);
//    List<CharEvent> first = Events.toCharEvents(to);
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


  @Test
  public void compareRandomString_1000_50() {
    // Generate content
    String from = getRandomString(1000, false);
    String to = vary(from, .50);
    List<CharEvent> second = Events.toCharEvents(from);
    List<CharEvent> first = Events.toCharEvents(to);

    profileX(new DefaultXMLProcessor(), first, second, 10);
    profileX(new TextOnlyProcessor(), first, second, 10);
    profileX(new ProgressiveXMLProcessor(), first, second, 10);
  }

  @Test
  public void compareSingleElement_1000_20() throws IOException, DiffXException {
    // Generate content
    String from = getRandomString(1000, true);
    String to = vary(from, .05);
    List<DiffXEvent> second = Events.recordXMLEvents("<root>"+from+"</root>", TextGranularity.SPACE_WORD);
    List<DiffXEvent> first = Events.recordXMLEvents("<root>"+to+"</root>", TextGranularity.SPACE_WORD);
    profileX(new DefaultXMLProcessor(), first, second, 10);
    profileX(new ProgressiveXMLProcessor(), first, second, 10);
  }

  @Test
  public void compareShallowXML() throws IOException, DiffXException {
    // Generate content
    StringBuilder xml1 = new StringBuilder();
    StringBuilder xml2 = new StringBuilder();
    generateXML(xml1, xml2, 10);

    // Parse events
    List<DiffXEvent> secondText = Events.recordXMLEvents(xml1.toString(), TextGranularity.TEXT);
    List<DiffXEvent> firstText = Events.recordXMLEvents(xml2.toString(), TextGranularity.TEXT);
    List<DiffXEvent> secondWord = Events.recordXMLEvents(xml1.toString(), TextGranularity.SPACE_WORD);
    List<DiffXEvent> firstWord = Events.recordXMLEvents(xml2.toString(), TextGranularity.SPACE_WORD);

    profileX(new DefaultXMLProcessor(), firstWord, secondWord, 10);
    profileX(new ProgressiveXMLProcessor(), firstText, secondText, 10);
  }

  @Test
  public void compareCoalesce() throws IOException, DiffXException {
    // Generate content
    StringBuilder xml1 = new StringBuilder();
    StringBuilder xml2 = new StringBuilder();
    generateXML(xml1, xml2, 100);

    // Parse events
    List<DiffXEvent> secondText = Events.recordXMLEvents(xml1.toString(), TextGranularity.TEXT);
    List<DiffXEvent> firstText = Events.recordXMLEvents(xml2.toString(), TextGranularity.TEXT);

    ProgressiveXMLProcessor coalescingProcessor = new ProgressiveXMLProcessor();
    coalescingProcessor.setCoalesce(true);
    ProgressiveXMLProcessor noCoalesceProcessor = new ProgressiveXMLProcessor();
    noCoalesceProcessor.setCoalesce(false);
    profileX(coalescingProcessor, firstText, secondText, 10);
    profileX(noCoalesceProcessor, firstText, secondText, 10);

  }

  private static long profileX(DiffAlgorithm algorithm, List<? extends DiffXEvent> first, List<? extends DiffXEvent> second, int times) {
    System.out.print(algorithm.toString());
    System.out.print("\t"+first.size()+"/"+second.size()+" events");
    // We do a dry run first
    long f = profile(algorithm, first, second);
    System.out.print(" First:"+f+"ms");
    long total = 0;
    for (int i=0; i < times; i++) {
      long t = profile(algorithm, first, second);
      total += t;
    }
    System.out.println(" Avg:"+(total*1.0 / times)+"ms");
    return total;
  }

  private static long profile(DiffAlgorithm algorithm, List<? extends DiffXEvent> first, List<? extends DiffXEvent> second) {
    long t0 = System.nanoTime();
    algorithm.diff(first, second, VOID_HANDLER);
    long t1 = System.nanoTime();
    return (t1 - t0) / 1_000_000;
  }

  private static void generateXML(StringBuilder xml1, StringBuilder xml2, int elements) {
    // Generate content
    Random r = new Random();
    xml1.append("<root>\n");
    xml2.append("<root>\n");
    for (int i = 0; i < elements; i++) {
      int f = r.nextInt(10);
      String from = getRandomString(100+ f*100, true);
      String to = (r.nextInt(10) < 3)? vary(from, .05) : from;
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

}
