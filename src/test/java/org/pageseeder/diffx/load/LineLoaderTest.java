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
import org.pageseeder.diffx.token.impl.LineToken;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class LineLoaderTest {

  final LineLoader loader = new LineLoader();

  @Test
  public void testSimpleLine0() {
    String text = "line 1\n"
        + "line2\n";
    List<LineToken> exp = new ArrayList<>();
    exp.add(new LineToken("line 1", 1));
    exp.add(new LineToken("line2", 2));
    assertEqualsText(exp, text);
  }

  @Test
  public void testSimpleLine2() {
    String text = "line #1\n"
        + "line #2\n"
        + "line #3\n"
        + "line #4";
    List<LineToken> exp = new ArrayList<>();
    exp.add(new LineToken("line #1", 1));
    exp.add(new LineToken("line #2", 2));
    exp.add(new LineToken("line #3", 3));
    exp.add(new LineToken("line #4", 4));
    assertEqualsText(exp, text);
  }

  @Test
  public void testEmptyLine() {
    String text = "line #1\n"
        + "\n"
        + "line #3\n"
        + "line #4";
    List<LineToken> exp = new ArrayList<>();
    exp.add(new LineToken("line #1", 1));
    exp.add(new LineToken("", 2));
    exp.add(new LineToken("line #3", 3));
    exp.add(new LineToken("line #4", 4));
    assertEqualsText(exp, text);
  }

  @Test
  public void testXMLLine0() {
    String text = "<a>XX</a>";
    List<LineToken> exp = new ArrayList<>();
    exp.add(new LineToken("<a>XX</a>", 1));
    assertEqualsText(exp, text);
  }

  @Test
  public void testEncoding1() {
    String text = "&lt;";
    List<LineToken> exp = new ArrayList<>();
    exp.add(new LineToken("&lt;", 1));
    assertEqualsText(exp, text);
  }

  @Test
  public void testEncoding3() {
    String xml = "&#x8012;";
    List<LineToken> exp = new ArrayList<>();
    exp.add(new LineToken("&#x8012;", 1));
    assertEqualsText(exp, xml);
  }

  /**
   * @param exp  The expected list.
   * @param text The text to parse
   */
  private void assertEqualsText(List<LineToken> exp, String text) {
    List<LineToken> seq = this.loader.load(text);
    assertEquals(exp.size(), seq.size());
    assertEquals(exp, seq);
  }

}
