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
package org.pageseeder.diffx.token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TokenTest {

  public static void assertEqualsNullIsFalse(Token token) {
    Assertions.assertFalse(token.equals(null));
  }

  public static void assertEqualsIsReflexive(Token token) {
    Assertions.assertTrue(token.equals(token));
  }



  public static void assertHashCollisionLessThan(List<? extends Token> tokens, double percent) {
    int clashCount = 0;
    for (int i=0; i < tokens.size(); i++) {
      for (int j=i; j < tokens.size(); j++) {
        Token a = tokens.get(i);
        Token b = tokens.get(j);
        boolean equals = a.equals(b);
        boolean sameHash = a.hashCode() == b.hashCode();
        if (equals != sameHash) {
          clashCount += 1;
        }
      }
    }
    // Total is triangular number of number tokens: n(n+1)/2
    int total = (tokens.size() * (tokens.size() + 1)) / 2;
    double clashPercent = clashCount / (double)total;
    Assertions.assertTrue(clashPercent < percent, "Too many hash collisions: "+clashCount+"/"+total+" "+clashPercent*100+"%");
  }

  public static long profileEquals(List<? extends Token> tokens) {
    long t0 = System.nanoTime();
    for (Token a : tokens) {
      for (Token b : tokens) {
        a.equals(b);
      }
    }
    long t1 = System.nanoTime();
    return t1 - t0;
  }

}
