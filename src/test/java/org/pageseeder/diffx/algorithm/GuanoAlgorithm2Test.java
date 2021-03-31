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
package org.pageseeder.diffx.algorithm;

import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;

/**
 * Test case for Guano Diff-X algorithm.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class GuanoAlgorithm2Test extends BaseAlgorithmTest {

  public DiffXAlgorithm makeDiffX(EventSequence first, EventSequence second) {
    return new GuanoAlgorithm(first, second);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testSticky() throws IOException, DiffXException {
    String xml1 = "<a>a white cat</a>";
    String xml2 = "<a>a black hat</a>";
    String exp1 = "<a>$w{a}$s{ }+$w{white}$s{ }+$w{cat}-$w{black}$s{ }-$w{hat}</a>";
    String exp2 = "<a>$w{a}$s{ }+$w{black}$s{ }+$w{cat}-$w{white}$s{ }-$w{hat}</a>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

  /**
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void testList() throws IOException, DiffXException {
    String xml1 = "<ul><li>blue</li><li>red</li><li>green</li></ul>";
    String xml2 = "<ul><li>black</li><li>red</li><li>green</li></ul>";
    String exp1 = "<ul><li>+$w{blue}-$w{black}</li><li>$w{red}</li><li>$w{green}</li></ul>";
    String exp2 = "<ul><li>+$w{black}-$w{blue}</li><li>$w{red}</li><li>$w{green}</li></ul>";
    assertDiffXMLOK(xml1, xml2, exp1);
    assertDiffXMLOK(xml2, xml1, exp2);
  }

}
