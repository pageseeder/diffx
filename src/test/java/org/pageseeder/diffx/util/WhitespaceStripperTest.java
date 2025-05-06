/*
 * Copyright 2010-2025 Allette Systems (Australia)
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
package org.pageseeder.diffx.util;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.api.LoadingException;
import org.pageseeder.diffx.load.SAXLoader;
import org.pageseeder.diffx.load.XMLLoader;
import org.pageseeder.diffx.xml.Sequence;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WhitespaceStripperTest {

  /**
   * Tests the {@link WhitespaceStripper#strip(Sequence)} method.
   * Ensures that it removes whitespace within elements specified in the ignore list.
   */
  @Test
  void testStrip_RemovesWhitespaceInIgnoredElements() {
    Sequence input = load("<ul>  <li>test</li>  </ul>");
    Sequence expect = load("<ul><li>test</li></ul>");
    WhitespaceStripper stripper = new WhitespaceStripper("ul");
    Sequence result = stripper.strip(input);
    assertEquals(expect, result);
  }

  /**
   * Tests that whitespace outside the ignored elements is preserved.
   */
  @Test
  void testStrip_PreservesWhitespaceOutsideIgnoredElements() {
    Sequence input = load("<p>Do not <i>remove</i>  </p>");
    WhitespaceStripper stripper = new WhitespaceStripper("ul");
    Sequence result = stripper.strip(input);
    assertEquals(input, result);
  }

  /**
   * Ensures that elements not included in the ignore list are unaffected.
   */
  @Test
  void testStrip_DoesNotAffectElementsOutsideIgnoreList() {
    Sequence input = load("<ul>  \n<li>  </li>\n</ul>");
    Sequence expect = load("<ul><li>  </li></ul>");
    WhitespaceStripper stripper = new WhitespaceStripper("ul");
    Sequence result = stripper.strip(input);
    assertEquals(expect, result);
  }

  private Sequence load(String xml) {
    try {
      XMLLoader loader = new SAXLoader();
      return loader.load(new StringReader(xml));
    } catch (LoadingException | IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}