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

import org.pageseeder.diffx.load.LoadingException;
import org.pageseeder.diffx.load.SAXRecorder;
import junit.framework.TestCase;

import java.io.IOException;


/**
 * Test case for the event sequence utility.
 *
 * @author Christophe Lauret
 * @version 9 March 2005
 */
public final class EventSequenceUtilsTest extends TestCase {

  /**
   * Default constructor.
   *
   * @param name Name of the test.
   */
  public EventSequenceUtilsTest(String name) {
    super(name);
  }

  /**
   * Test the maximum depth.
   *
   * @throws IOException If an I/O error occurs.
   * @throws LoadingException If the loader cannot load the XML.
   */
  public void testMaxDepth1() throws IOException, LoadingException {
    EventSequence seq = new SAXRecorder().process("<a/>");
    int max = EventSequenceUtils.getMaxDepth(seq);
    assertEquals(1, max);
  }

  /**
   * Test the maximum depth.
   *
   * @throws IOException If an I/O error occurs.
   * @throws LoadingException If the loader cannot load the XML.
   */
  public void testMaxDepth2() throws IOException, LoadingException {
    EventSequence seq = new SAXRecorder().process("<a><a/></a>");
    int max = EventSequenceUtils.getMaxDepth(seq);
    assertEquals(2, max);
  }

  /**
   * Test the maximum depth.
   *
   * @throws IOException If an I/O error occurs.
   * @throws LoadingException If the loader cannot load the XML.
   */
  public void testMaxDepth3() throws IOException, LoadingException {
    EventSequence seq = new SAXRecorder().process("<a><b/><b/></a>");
    int max = EventSequenceUtils.getMaxDepth(seq);
    assertEquals(2, max);
  }

  /**
   * Test the maximum depth.
   *
   * @throws IOException If an I/O error occurs.
   * @throws LoadingException If the loader cannot load the XML.
   */
  public void testMaxDepth4() throws IOException, LoadingException {
    EventSequence seq = new SAXRecorder().process("<a><b><c/></b><b/></a>");
    int max = EventSequenceUtils.getMaxDepth(seq);
    assertEquals(3, max);
  }

  /**
   * Test the maximum depth.
   *
   * @throws IOException If an I/O error occurs.
   * @throws LoadingException If the loader cannot load the XML.
   */
  public void testMaxElementContent0() throws IOException, LoadingException {
    EventSequence seq = new SAXRecorder().process("<a/>");
    int max = EventSequenceUtils.getMaxElementContent(seq);
    assertEquals(0, max);
  }

  /**
   * Test the maximum depth.
   *
   * @throws IOException If an I/O error occurs.
   * @throws LoadingException If the loader cannot load the XML.
   */
  public void testMaxElementContent1() throws IOException, LoadingException {
    EventSequence seq = new SAXRecorder().process("<a>x</a>");
    int max = EventSequenceUtils.getMaxElementContent(seq);
    assertEquals(1, max);
  }

  /**
   * Test the maximum depth.
   *
   * @throws IOException If an I/O error occurs.
   * @throws LoadingException If the loader cannot load the XML.
   */
  public void testMaxElementContent2() throws IOException, LoadingException {
    EventSequence seq = new SAXRecorder().process("<a>x y</a>");
    int max = EventSequenceUtils.getMaxElementContent(seq);
    assertEquals(3, max);
  }

}
