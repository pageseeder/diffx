/*
 * Copyright 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
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

package org.pageseeder.diffx.profile;

import org.pageseeder.diffx.algorithm.*;
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.token.impl.CharToken;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class AlgorithmProfiler {

  public static void main(String[] args) {
    int times = 10;
    compareBySize(times);
    compareByDifference(times);

//    compareSideBySide(new MyersGreedyAlgorithm<>(), new MyersGreedyAlgorithm2<>());
  }

  private static void compareBySize(int times) {
    int[] lengths = new int[]{500, 1_000, 2_000, 5_000, 10_000, 20_000, 50_000};
    double[] variations = new double[]{ .05, .25 };
    NumberFormat sizeFormat = new DecimalFormat("#,###");

    List<DiffAlgorithm<CharToken>> algorithms = new ArrayList<>();
    algorithms.add(new MyersGreedyAlgorithm<>());
    algorithms.add(new MyersGreedyAlgorithm2<>());
    algorithms.add(new MyersLinearAlgorithm<>());
    algorithms.add(new KumarRanganAlgorithm<>());
    algorithms.add(new HirschbergAlgorithm<>());
    algorithms.add(new WagnerFischerAlgorithm<>());

    for (double variation : variations) {
      System.out.println("Variation: "+(variation*100)+"%");
      Map<String, List<ProfileInfo>> results = new HashMap<>();
      for (DiffAlgorithm<CharToken> algorithm : algorithms) {
        results.put(Profilers.toName(algorithm), new ArrayList<>());
      }
      for (int length : lengths) {
        Pair<List<CharToken>> p = Profilers.getRandomStringPair(length, false, variation);
        for (DiffAlgorithm<CharToken> algorithm : algorithms) {
          ProfileInfo info = ProfileInfo.profileX(algorithm, p.a, p.b, times, true);
          results.get(Profilers.toName(algorithm)).add(info);
        }
      }

      // Print results
      System.out.print("| Algorithm               |");
      for (int length : lengths) System.out.print(" "+Profilers.padLeft(sizeFormat.format(length), 9)+" |");
      System.out.println();
      System.out.print("| ----------------------- |");
      for (int ignored : lengths) System.out.print(" --------- |");
      System.out.println();
      for (Map.Entry<String, List<ProfileInfo>> entry : sortEntries(results)) {
        System.out.print("| "+Profilers.padRight(entry.getKey(), 23) +" |");
        for (ProfileInfo info : entry.getValue()) {
          System.out.print(" "+Profilers.padLeft(sizeFormat.format(info.average()), 9)+" |");
        }
        System.out.println();
      }
      System.out.println("");
    }
  }

  private static void compareByDifference(int times) {
    int[] lengths = new int[]{ 10_000 };
    double[] variations = new double[]{ .01, .05, .25, .5, .75, .99 };
    NumberFormat sizeFormat = new DecimalFormat("#,###");

    List<DiffAlgorithm<CharToken>> algorithms = new ArrayList<>();
    algorithms.add(new MyersGreedyAlgorithm<>());
    algorithms.add(new MyersGreedyAlgorithm2<>());
    algorithms.add(new MyersLinearAlgorithm<>());
    algorithms.add(new KumarRanganAlgorithm<>());
    algorithms.add(new HirschbergAlgorithm<>());
    algorithms.add(new WagnerFischerAlgorithm<>());

    for (int length : lengths) {
      System.out.println("Length: "+sizeFormat.format(length));
      Map<String, List<ProfileInfo>> results = new HashMap<>();
      for (DiffAlgorithm<CharToken> algorithm : algorithms) {
        results.put(Profilers.toName(algorithm), new ArrayList<>());
      }
      for (double variation : variations) {
        Pair<List<CharToken>> p = Profilers.getRandomStringPair(length, false, variation);
        for (DiffAlgorithm<CharToken> algorithm : algorithms) {
          ProfileInfo info = ProfileInfo.profileX(algorithm, p.a, p.b, times, true);
          results.get(Profilers.toName(algorithm)).add(info);
        }
      }

      // Print results
      System.out.print("| Algorithm               |");
      for (double variation : variations) System.out.print(" "+Profilers.padLeft(sizeFormat.format(variation*100), 7)+"% |");
      System.out.println();
      System.out.print("| ----------------------- |");
      for (double ignored : variations) System.out.print(" -------- |");
      System.out.println();
      for (Map.Entry<String, List<ProfileInfo>> entry : sortEntries(results)) {
        System.out.print("| "+Profilers.padRight(entry.getKey(), 23) +" |");
        for (ProfileInfo info : entry.getValue()) {
          System.out.print(" "+Profilers.padLeft(sizeFormat.format(info.average()), 8)+" |");
        }
        System.out.println();
      }
      System.out.println("");
    }
  }


  private static void compareSideBySide(DiffAlgorithm<CharToken> diff1, DiffAlgorithm<CharToken> diff2) {
    Pair<List<CharToken>> p = Profilers.getRandomStringPair(10_000, false, .25);

    // warm up
    ProfileInfo.profileX(diff1, p.a, p.b, 10);
    ProfileInfo.profileX(diff2, p.a, p.b, 10);

    int total1 = 0;
    int total2 = 0;
    for (int i=0; i < 1000; i++) {
      if (i % 100 == 0) System.out.println(i+"... "+ (total1 > total2? Profilers.toName(diff2) : Profilers.toName(diff1) ));
      if (i % 2 == 0) {
        total2 += ProfileInfo.profile(diff2, p.a, p.b);
        total1 += ProfileInfo.profile(diff1, p.a, p.b);
      } else {
        total1 += ProfileInfo.profile(diff1, p.a, p.b);
        total2 += ProfileInfo.profile(diff2, p.a, p.b);
      }
    }
    System.out.println();
    System.out.println("Total "+Profilers.toName(diff1)+": "+total1);
    System.out.println("Total "+Profilers.toName(diff2)+": "+total2);

    double pct = (total1 > total2)
        ? (total1 - total2)*100.0 / total1
        : (total2 - total1)*100.0 / total2;

    System.out.println("Faster: "+((total1 > total2) ? Profilers.toName(diff2) : Profilers.toName(diff1))+" by "+pct+"%");
  }


  private static Iterable<Map.Entry<String, List<ProfileInfo>>> sortEntries(Map<String, List<ProfileInfo>> results) {
    List<Map.Entry<String, List<ProfileInfo>>> entries = new ArrayList<>(results.entrySet());
    entries.sort((entrya, entryb) -> (int)(entrya.getValue().get(0).average() - entryb.getValue().get(0).average()));
    return entries;
  }

}
