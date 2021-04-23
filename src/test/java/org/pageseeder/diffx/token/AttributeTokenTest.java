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
import org.pageseeder.diffx.test.RandomStringFactory;
import org.pageseeder.diffx.token.impl.XMLAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AttributeTokenTest {

  @Test
  void testEqualsNS() {
    testEquals(new TokenFactory(true));
  }

  @Test
  void testNotEqualsNS() {
    testNotEquals(new TokenFactory(true));
  }

  @Test
  void testEquals() {
    testEquals(new TokenFactory(false));
  }

  @Test
  void testNotEquals() {
    testNotEquals(new TokenFactory(false));
  }

  @Test
  public void testHashcodeClash() {
    RandomStringFactory factory = new RandomStringFactory();
    List<AttributeToken> tokens = new ArrayList<>();
    for (int i = 0; i < 10_000; i++) {
      tokens.add(new XMLAttribute(
          i % 2 == 0 ? "" : "https://example.org",
          factory.getRandomString(8, false),
          factory.getRandomString(16, false)));
    }
    TokenTest.assertHashCollisionLessThan(tokens, .001);
  }


  @Test
  public void testPerformance() {
    String[] uris = new String[]{ "", "https://example.org", "https://example.net"};
    String[] names = new String[]{ "alt", "title", "id", "value", "option", "name", "xml:title", "hidden"};
    String[] values = new String[]{ "",
        "1", "12", "123", "1234", "12345", "123456",
        "a", "ab", "abc", "abcd", "abcde", "abcdef",
        "some longer value", "other"};
    List<AttributeToken> tokens1 = new ArrayList<>();
    List<AttributeToken> tokens2 = new ArrayList<>();
    for (String uri : uris)
      for (String name : names)
        for (String value : values) {
          tokens1.add(new XMLAttribute(name, value));
          tokens2.add(new XMLAttribute(uri, name, value));
        }
    long t1 = 0;
    long t2 = 0;
    Random r = new Random();
    for (int i=0; i < 1000; i++) {
      if (r.nextBoolean()) {
        t1 += TokenTest.profileEquals(tokens1);
        t2 += TokenTest.profileEquals(tokens2);
      } else {
        t2 += TokenTest.profileEquals(tokens2);
        t1 += TokenTest.profileEquals(tokens1);
      }
    }
    System.out.println("T1="+(t1 / 100_000)+" "+(t1<t2 ? (t1-t2)/ 100_000 : ""));
    System.out.println("T2="+(t2 / 100_000)+" "+(t2<t1 ? (t2-t1)/ 100_000 : ""));
  }

  private void testEquals(TokenFactory factory) {
    AttributeToken token = factory.newAttribute("", "title", "test");
    AttributeToken other = factory.newAttribute("", "title", "test");
    TokenTest.assertEqualsNullIsFalse(token);
    TokenTest.assertEqualsIsReflexive(token);
    Assertions.assertEquals(token, other);
    Assertions.assertEquals(token.hashCode(), other.hashCode());
  }

  private void testNotEquals(TokenFactory factory) {
    AttributeToken[] tokens = new AttributeToken[]{
      factory.newAttribute("", "title", "test"),
      factory.newAttribute("", "title", "test2"),
      factory.newAttribute("", "title2", "test"),
      factory.newAttribute("https://example.org", "title", "test")
    };
    for (Token a : tokens) {
      for (Token b : tokens) {
        if (a != b) {
          Assertions.assertNotEquals(a, b);
          Assertions.assertNotEquals(a.hashCode(), b.hashCode());
        }
      }
    }
  }


}
