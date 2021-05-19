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

import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.core.TextOnlyProcessor;
import org.pageseeder.diffx.test.RandomStringFactory;
import org.pageseeder.diffx.test.TestTokens;
import org.pageseeder.diffx.token.impl.CharToken;

import java.util.List;

/**
 * Utility class for profilers
 */
public final class Profilers {

  private Profilers(){}

  public static String getRandomString(int length, boolean spaces) {
    RandomStringFactory factory = new RandomStringFactory();
    return factory.getRandomString(length, spaces);
  }

  public static String vary(String source, double changes) {
    RandomStringFactory factory = new RandomStringFactory();
    return factory.vary(source, changes);
  }

  public static String padLeft(String s, int length) {
    StringBuilder out = new StringBuilder();
    for (int i=0; i < length-s.length(); i++) out.append(' ');
    out.append(s);
    return out.toString();
  }

  public static String padRight(String s, int length) {
    StringBuilder out = new StringBuilder();
    out.append(s);
    for (int i=0; i < length-s.length(); i++) out.append(' ');
    return out.toString();
  }

  public static String toName(DiffAlgorithm<?> algorithm) {
    return algorithm.getClass().getSimpleName();
  }

  public static Pair<List<CharToken>> getRandomStringPair(int length, boolean spaces, double variation) {
    String from = Profilers.getRandomString(length, false);
    String to = Profilers.vary(from, variation);
    List<CharToken> a = TestTokens.toCharTokens(from);
    List<CharToken> b = TestTokens.toCharTokens(to);
    return new Pair(a, b);
  }

}
