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

import org.pageseeder.diffx.algorithm.WagnerFischerAlgorithm;
import org.pageseeder.diffx.api.DiffAlgorithm;

import java.util.List;

public final class ProfileInfo {

  private final String algorithm;
  private final int times;
  private final int sizeA;
  private final int sizeB;

  private long first;
  private long total;

  ProfileInfo(DiffAlgorithm<?> algorithm, int times, int sizeA, int sizeB) {
    this.algorithm = Profilers.toName(algorithm);
    this.times = times;
    this.sizeA = sizeA;
    this.sizeB = sizeB;
  }

  long average() {
    return this.total / this.times;
  }

  @Override
  public String toString() {
    String out = this.algorithm +
        "\t" + this.sizeA + "/" + this.sizeB + " tokens" +
        "\tFirst:" + this.first + "µs" +
        "\tAvg:" + average() + "µs";
    return out;
  }

  public static <T> ProfileInfo profileX(DiffAlgorithm<T> algorithm, List<? extends T> a, List<? extends T> b, int times, boolean quiet) {
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

  public static <T> ProfileInfo profileX(DiffAlgorithm<T> algorithm, List<? extends T> a, List<? extends T> b, int times) {
    return profileX(algorithm, a, b, times, false);
  }

  public static <T> long profile(DiffAlgorithm<T> algorithm, List<? extends T> a, List<? extends T> b) {
    long t0 = System.nanoTime();
    algorithm.diff(a, b, (operator, token) -> {});
    long t1 = System.nanoTime();
    return (t1 - t0) / 1_000;
  }

}
