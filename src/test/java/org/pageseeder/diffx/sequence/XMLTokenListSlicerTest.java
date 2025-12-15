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
package org.pageseeder.diffx.sequence;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.DiffException;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.load.SAXLoader;
import org.pageseeder.diffx.token.impl.CharactersToken;
import org.pageseeder.diffx.token.impl.XMLEndElement;
import org.pageseeder.diffx.token.impl.XMLStartElement;
import org.pageseeder.diffx.xml.Sequence;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test case for the sequence slicer.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
final class XMLTokenListSlicerTest {

  /**
   * The loader used for the tests.
   */
  private final SAXLoader loader = new SAXLoader();

  /**
   * The first sequence.
   */
  private Sequence seqA;

  /**
   * The second sequence.
   */
  private Sequence seqB;

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testStart0() throws IOException, DiffException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX</a>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(3);
    exp.addToken(new XMLStartElement("a"));
    exp.addToken(new CharactersToken("XXX"));
    exp.addToken(new XMLEndElement("a"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testStart1() throws IOException, DiffException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>yyy</a>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(1);
    exp.addToken(new XMLStartElement("a"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testStart2() throws IOException, DiffException {
    String xml1 = "<a>XXX </a>";
    String xml2 = "<a>XXX</a>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(2);
    exp.addToken(new XMLStartElement("a"));
    exp.addToken(new CharactersToken("XXX"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testStart3() throws IOException, DiffException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX </a>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(2);
    exp.addToken(new XMLStartElement("a"));
    exp.addToken(new CharactersToken("XXX"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  @Disabled
  void testStart4() throws IOException, DiffException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX YYY</a>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(2);
    exp.addToken(new XMLStartElement("a"));
    exp.addToken(new CharactersToken("XXX"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testStart5() throws IOException, DiffException {
    String xml1 = "<a><b/></a>";
    String xml2 = "<a><c/></a>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(1);
    exp.addToken(new XMLStartElement("a"));
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testStart6() throws IOException, DiffException {
    String xml1 = "<a/>";
    String xml2 = "<b/>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(0);
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testStart7() throws IOException, DiffException {
    String xml1 = "<a>X</a>";
    String xml2 = "<b>X</b>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(0);
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testStart8() throws IOException, DiffException {
    String xml1 = "<a><b>X</b></a>";
    String xml2 = "<b><a>X</a></b>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(0);
    assertStartOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testEnd0() throws IOException, DiffException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX</a>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence();
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testEnd1() throws IOException, DiffException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>yyy</a>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(1);
    exp.addToken(new XMLEndElement("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testEnd2() throws IOException, DiffException {
    String xml1 = "<a>XXX </a>";
    String xml2 = "<a>XXX</a>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(1);
    exp.addToken(new XMLEndElement("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testEnd3() throws IOException, DiffException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX </a>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(1);
    exp.addToken(new XMLEndElement("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testEnd4() throws IOException, DiffException {
    String xml1 = "<a>XXX</a>";
    String xml2 = "<a>XXX YYY</a>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(1);
    exp.addToken(new XMLEndElement("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testEnd5() throws IOException, DiffException {
    String xml1 = "<a><b/></a>";
    String xml2 = "<a><c/></a>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(1);
    exp.addToken(new XMLEndElement("a"));
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testEnd6() throws IOException, DiffException {
    String xml1 = "<a/>";
    String xml2 = "<b/>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(0);
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testEnd7() throws IOException, DiffException {
    String xml1 = "<a>X</a>";
    String xml2 = "<b>X</b>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(0);
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testEnd8() throws IOException, DiffException {
    String xml1 = "<a><b>X</b></a>";
    String xml2 = "<b><a>X</a></b>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence exp = new Sequence(0);
    assertEndOK(slicer, exp);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testStartEnd0() throws IOException, DiffException {
    String xml1 = "<a><b>WWW</b></a>";
    String xml2 = "<a><b>VVV</b></a>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence start = new Sequence(1);
    start.addToken(new XMLStartElement("a"));
    Sequence end = new Sequence(1);
    end.addToken(new XMLEndElement("a"));
    assertStartOK(slicer, start);
    assertEndOK(slicer, end);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testStartEnd1() throws IOException, DiffException {
    String xml1 = "<a><b/><b>WWW</b></a>";
    String xml2 = "<a><b/><b>VVV</b></a>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence start = new Sequence(3);
    start.addToken(new XMLStartElement("a"));
    start.addToken(new XMLStartElement("b"));
    start.addToken(new XMLEndElement("b"));
    Sequence end = new Sequence(1);
    end.addToken(new XMLEndElement("a"));
    assertStartOK(slicer, start);
    assertEndOK(slicer, end);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testStartEnd2() throws IOException, DiffException {
    String xml1 = "<a><e>tt</e><b>WWW</b><c>xxx</c></a>";
    String xml2 = "<a><e>tt</e><b>VVV</b><c>xxx</c></a>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence start = new Sequence(4);
    start.addToken(new XMLStartElement("a"));
    start.addToken(new XMLStartElement("e"));
    start.addToken(new CharactersToken("tt"));
    start.addToken(new XMLEndElement("e"));
    Sequence end = new Sequence(4);
    end.addToken(new XMLStartElement("c"));
    end.addToken(new CharactersToken("xxx"));
    end.addToken(new XMLEndElement("c"));
    end.addToken(new XMLEndElement("a"));
    assertStartOK(slicer, start);
    assertEndOK(slicer, end);
  }

  /**
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  @Test
  void testStartEnd3() throws IOException, DiffException {
    String xml1 = "<a><e>t</e><b>WWW</b><c>xx</c></a>";
    String xml2 = "<a><e>tt</e><b>VVV</b><c>xxx</c></a>";
    TokenListSlicer slicer = init(xml1, xml2);
    Sequence start = new Sequence(1);
    start.addToken(new XMLStartElement("a"));
    Sequence end = new Sequence(1);
    end.addToken(new XMLEndElement("a"));
    assertStartOK(slicer, start);
    assertEndOK(slicer, end);
  }

// helpers ------------------------------------------------------------------------------------


  /**
   * Prepare the sequences and returns a sequence slicer on them.
   *
   * @param xmlA The first XML to test.
   * @param xmlB The second XML to test.
   *
   * @return The sequence slicer on the 2 sequences.
   * @throws IOException   Should an I/O exception occur.
   * @throws DiffException Should an error occur while parsing XML with SAX.
   */
  private TokenListSlicer init(String xmlA, String xmlB) throws IOException, DiffException {
    this.loader.setConfig(DiffConfig.getDefault().granularity(TextGranularity.TEXT));
    this.seqA = this.loader.load(xmlA);
    this.seqB = this.loader.load(xmlB);
    return new TokenListSlicer(this.seqA, this.seqB);
  }

  /**
   * Asserts that the sliceStart operation is OK.
   *
   * @param slicer The slicer to test.
   * @param exp    The expected start sub sequence.
   */
  private void assertStartOK(TokenListSlicer slicer, Sequence exp) {
    slicer.analyze();
    assertEquals(exp.tokens(), slicer.getStart());
  }

  /**
   * Asserts that the sliceEnd operation is OK.
   *
   * @param slicer The slicer to test.
   * @param exp    The expected start sub sequence.
   */
  private void assertEndOK(TokenListSlicer slicer, Sequence exp) {
    slicer.analyze();
    assertEquals(exp.tokens(), slicer.getEnd());
  }

}
