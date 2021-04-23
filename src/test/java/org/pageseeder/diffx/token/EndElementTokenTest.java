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
import org.pageseeder.diffx.token.impl.XMLEndElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EndElementTokenTest {

  @Test
  void testEquals() {
    EndElementToken token = new XMLEndElement("https://example.org", "test");
    EndElementToken other = new XMLEndElement("https://example.org", "test");
    TokenTest.assertEqualsNullIsFalse(token);
    TokenTest.assertEqualsIsReflexive(token);
    Assertions.assertEquals(token, other);
    Assertions.assertEquals(token.hashCode(), other.hashCode());
  }

  @Test
  void testNotEquals() {
    List<EndElementToken> tokens = Arrays.asList(
        new XMLEndElement("test"),
        new XMLEndElement("test2"),
        new XMLEndElement("https://example.org", "test"),
        new XMLEndElement("https://example.org", "test2")
    );
    TokenTest.assertNotEqualsINotSame(tokens);
  }

  @Test
  public void testHashcodeCollisions() {
    RandomStringFactory factory = new RandomStringFactory();
    List<EndElementToken> tokens = new ArrayList<>();
    for (int i = 0; i < 10_000; i++) {
      tokens.add(new XMLEndElement(
          i % 2 == 0 ? "" : "https://example.org",
          factory.getRandomString(8, false)));
    }
    TokenTest.assertHashCollisionLessThan(tokens, .001);
  }


  @Test
  public void testPerformance() {
    String[] uris = new String[]{"", "https://example.org", "https://example.net"};
    String[] names = new String[]{"alt", "title", "id", "value", "option", "name", "hidden"};
    List<EndElementToken> tokens1 = new ArrayList<>();
    List<EndElementToken> tokens2 = new ArrayList<>();
    for (String uri : uris)
      for (String name : names) {
        tokens1.add(new XMLEndElement(name));
        tokens2.add(new XMLEndElement(uri, name));
      }
    long t1 = 0;
    long t2 = 0;
    Random r = new Random();
    for (int i = 0; i < 100_000; i++) {
      if (r.nextBoolean()) {
        t1 += TokenTest.profileEquals(tokens1);
        t2 += TokenTest.profileEquals(tokens2);
      } else {
        t2 += TokenTest.profileEquals(tokens2);
        t1 += TokenTest.profileEquals(tokens1);
      }
    }
    System.out.println("T1=" + (t1 / 1_000_000) + " " + (t1 < t2 ? (t1 - t2) / 1_000_000 : ""));
    System.out.println("T2=" + (t2 / 1_000_000) + " " + (t2 < t1 ? (t2 - t1) / 1_000_000 : ""));
  }

}
