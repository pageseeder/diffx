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
package org.pageseeder.diffx.test;

import java.util.Random;

public class RandomStringFactory {

  private final Random R = new Random();

  private final String LOWER_ALPHA = "abcdefghijklmnopqrstuvwxyz";

  private final char[] vocabulary;

  public RandomStringFactory() {
    this.vocabulary = LOWER_ALPHA.toCharArray();
  }

  public RandomStringFactory(String vocabulary) {
    this.vocabulary = vocabulary.toCharArray();
  }

  public char nextChar() {
    return this.vocabulary[R.nextInt(this.vocabulary.length)];
  }

  /**
   * Returns a random string of specified length.
   */
  public String getRandomString(int length, boolean spaces) {
    StringBuilder out = new StringBuilder();
    while (out.length() < length) {
      out.append(nextChar());
      if (spaces && R.nextInt(5) == 1) out.append(' ');
    }
    return out.toString();
  }

  /**
   * Make variations on the specified String
   *
   * @param source  The source string
   * @param changes The percentage of changes (from 0.0 to 1.0)
   * @return A variation according to
   */
  public String vary(String source, double changes) {
    Random r = new Random();
    StringBuilder out = new StringBuilder();
    for (char c : source.toCharArray()) {
      if (changes > r.nextDouble()) {
        int type = r.nextInt(3);
        if (type == 0) {
          // Mutate
          out.append(nextChar());
        } else if (type == 1) {
          // insert
          out.append(c);
          out.append(nextChar());
        }
      } else {
        out.append(c);
      }
    }
    return out.toString();
  }

//  public String mutate(String source, int count) {
//    StringBuilder out = new StringBuilder();
//    for (char c : source.toCharArray()) {
//      if (changes > r.nextDouble()) {
//        out.append(nextChar());
//      } else {
//        out.append(c);
//      }
//    }
//    return out.toString();
//  }
//
//  public String insert(String source, int count) {
//    Random r = new Random();
//    StringBuilder out = new StringBuilder();
//    for (char c : source.toCharArray()) {
//      boolean before = r.nextBoolean();
//      if (before) out.append(c);
//      if (changes > r.nextDouble()) {
//        out.append(nextChar());
//      }
//      if (!before) out.append(c);
//    }
//    return out.toString();
//  }
//
//  public String delete(String source, int count) {
//    Random r = new Random();
//    StringBuilder out = new StringBuilder();
//    for (char c : source.toCharArray()) {
//      if (changes < r.nextDouble()) {
//        out.append(c);
//      }
//    }
//    return out.toString();
//  }


  public String mutatePercent(String source, double changes) {
    Random r = new Random();
    StringBuilder out = new StringBuilder();
    for (char c : source.toCharArray()) {
      if (changes > r.nextDouble()) {
        out.append(nextChar());
      } else {
        out.append(c);
      }
    }
    return out.toString();
  }

  public String insertPercent(String source, double changes) {
    Random r = new Random();
    StringBuilder out = new StringBuilder();
    for (char c : source.toCharArray()) {
      boolean before = r.nextBoolean();
      if (before) out.append(c);
      if (changes > r.nextDouble()) {
        out.append(nextChar());
      }
      if (!before) out.append(c);
    }
    return out.toString();
  }

  public String deletePercent(String source, double changes) {
    Random r = new Random();
    StringBuilder out = new StringBuilder();
    for (char c : source.toCharArray()) {
      if (changes < r.nextDouble()) {
        out.append(c);
      }
    }
    return out.toString();
  }

}
