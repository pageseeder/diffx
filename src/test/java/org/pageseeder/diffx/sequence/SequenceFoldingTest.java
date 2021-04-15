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
package org.pageseeder.diffx.sequence;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.load.LoadingException;
import org.pageseeder.diffx.load.SAXRecorder;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;

public class SequenceFoldingTest {

  @Test
  public void testEmpty() {
    EventSequence sequence = new EventSequence();
    EventSequence out = SequenceFolding.forAllElements().fold(sequence);
    assertEquals(0, out.size());
  }

  @Test
  public void testSimple() {
    EventSequence sequence = getSequence("<a/>");
    EventSequence out = SequenceFolding.forAllElements().fold(sequence);
    assertEquals(1, out.size());
  }

  @Test
  public void testSimple1a() {
    EventSequence sequence = getSequence("<a><b/></a>");
    EventSequence out = SequenceFolding.forAllElements().fold(sequence);
    assertEquals(1, out.size());
  }

  @Test
  public void testSimple1b() {
    EventSequence sequence = getSequence("<a><b/></a>");
    EventSequence out = SequenceFolding.forElements(new String[]{"a"}).fold(sequence);
    assertEquals(1, out.size());
  }

  @Test
  public void testSimple1c() {
    EventSequence sequence = getSequence("<a><b/></a>");
    EventSequence out = SequenceFolding.forElements(new String[]{"b"}).fold(sequence);
    assertEquals(3, out.size());
  }

  @Test
  public void testDeep1a() {
    EventSequence sequence = getSequence("<a><b><c><d>x</d></c></b></a>");
    EventSequence out = SequenceFolding.forElements(new String[]{"a"}).fold(sequence);
    assertEquals(1, out.size());
  }

  @Test
  public void testDeep1b() {
    EventSequence sequence = getSequence("<a><b><c><d>x</d></c></b></a>");
    EventSequence out = SequenceFolding.forElements(new String[]{"b"}).fold(sequence);
    assertEquals(3, out.size());
  }

  @Test
  public void testDeep1c() {
    EventSequence sequence = getSequence("<a><b><c><d>x</d></c></b></a>");
    EventSequence out = SequenceFolding.forElements(new String[]{"c"}).fold(sequence);
    assertEquals(5, out.size());
  }

  @Test
  public void testDeep1d() {
    EventSequence sequence = getSequence("<a><b><c><d>x</d></c></b></a>");
    EventSequence out = SequenceFolding.forElements(new String[]{"d"}).fold(sequence);
    assertEquals(7, out.size());
  }

  private static EventSequence getSequence(String xml) {
    try {
      SAXRecorder recorder = new SAXRecorder();
      return recorder.process(new InputSource(new StringReader(xml)));
    } catch (LoadingException | IOException ex) {
      throw new AssertionError("Unable record specified XML");
    }
  }

}
