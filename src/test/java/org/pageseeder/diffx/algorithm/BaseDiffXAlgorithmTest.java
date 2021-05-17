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
package org.pageseeder.diffx.algorithm;

import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.Actions;
import org.pageseeder.diffx.action.DiffResult;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.format.MultiplexFormatter;
import org.pageseeder.diffx.format.SmartXMLFormatter;
import org.pageseeder.diffx.format.XMLDiffXFormatter;
import org.pageseeder.diffx.load.LineLoader;
import org.pageseeder.diffx.load.SAXLoader;
import org.pageseeder.diffx.sequence.XMLSequence;
import org.pageseeder.diffx.test.ActionFormatter;
import org.pageseeder.diffx.test.DiffAssertions;
import org.pageseeder.diffx.test.TestActions;
import org.pageseeder.diffx.test.TestFormatter;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.impl.LineToken;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Deprecated
public abstract class BaseDiffXAlgorithmTest {

  /**
   * The loader used for the tests.
   */
  private final SAXLoader recorder = new SAXLoader();

  /**
   * The Diff-X algorithm being tested.
   */
  private transient DiffXAlgorithm diffx = null;

  public static String toXML(List<Action<XMLToken>> actions) throws IOException {
    StringWriter xml = new StringWriter();
    XMLDiffXFormatter formatter = new SmartXMLFormatter(xml);
    TestActions.format(actions, formatter);
    return xml.toString();
  }

  public static String toTestFormat(List<Action<XMLToken>> actions) throws IOException {
    TestFormatter formatter = new TestFormatter();
    TestActions.format(actions, formatter);
    return formatter.getOutput();
  }

  public static void assertMatchTestOutput(List<Action<XMLToken>> actions, String[] exp) throws IOException {
    // check the possible values
    String diffout = toTestFormat(actions);
    for (String s : exp) {
      if (s.equals(diffout)) return;
    }
    assertEquals(exp[0], diffout);
  }

  public static void assertIsWellFormedXML(String xml) throws IOException {
    try {
      InputSource source = new InputSource(new StringReader(xml));
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.newSAXParser().parse(source, new DefaultHandler());
    } catch (SAXException | ParserConfigurationException ex) {
      throw new AssertionError("XML is not well-formed");
    }
  }

  /**
   * Returns the Diff-X Algorithm instance from the specified sequences.
   *
   * @param seq1 The first sequence.
   * @param seq2 The second sequence.
   *
   * @return The Diff-X Algorithm instance.
   */
  public abstract DiffXAlgorithm makeDiffX(XMLSequence seq1, XMLSequence seq2);

  /**
   * Asserts that the Diff-X operation for XML meets expectations.
   *
   * @param xml1 The first XML to compare with diffx.
   * @param xml2 The first XML to compare with diffx.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void assertDiffXMLOK(String xml1, String xml2) throws IOException, DiffXException {
    // Record XML
    SAXLoader recorder = new SAXLoader();
    XMLSequence seq1 = "".equals(xml1) ? new XMLSequence(0) : recorder.load(xml1);
    XMLSequence seq2 = "".equals(xml2) ? new XMLSequence(0) : recorder.load(xml2);
    // Process as list of actions
    DiffResult<XMLToken> result = processResult(seq1, seq2);
    try {
      assertDiffIsCorrect(seq1, seq2, result.actions());
      DiffAssertions.assertIsWellFormedXML(result.actions());
    } catch (AssertionError ex) {
      printXMLErrorDetails(xml1, xml2, new String[]{}, toXML(result.actions()), result.actions());
      throw ex;
    }
  }

  /**
   * Asserts that the Diff-X operation for XML meets expectations.
   *
   * @param xml1 The first XML to compare with diffx.
   * @param xml2 The first XML to compare with diffx.
   * @param exp  The expected result as formatted by the TestFormatter.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void assertDiffXMLOK(String xml1, String xml2, String exp)
      throws IOException, DiffXException {
    assertDiffXMLOK(xml1, xml2, new String[]{exp});
  }

  /**
   * Asserts that the Diff-X operation for XML meets expectations.
   *
   * @param xml1 The first XML to compare with diffx.
   * @param xml2 The first XML to compare with diffx.
   * @param exp  The expected result as formatted by the TestFormatter.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void assertDiffXMLOK2(String xml1, String xml2, String exp)
      throws IOException, DiffXException {
    assertDiffXMLOK2(xml1, xml2, new String[]{exp});
  }

  /**
   * Asserts that the Diff-X operation for XML meets expectations.
   *
   * @param xml1 The first XML to compare with diffx.
   * @param xml2 The first XML to compare with diffx.
   * @param exp  The expected result as formatted by the TestFormatter.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void assertDiffXMLOK(String xml1, String xml2, String[] exp)
      throws IOException, DiffXException {
    // process the strings
    String diffout = processDiffXML(xml1, xml2);
    // check the possible values
    boolean ok = false;
    try {
      for (String s : exp) {
        ok = ok || s.equals(diffout);
      }
      if (!ok)
        assertEquals(exp[0], diffout);
    } catch (AssertionError ex) {
      printErrorDetails(xml1, xml2, exp);
      throw ex;
    }
  }

  /**
   * Asserts that the Diff-X operation for XML meets expectations.
   *
   * @param xml1 The first XML to compare with diffx.
   * @param xml2 The first XML to compare with diffx.
   * @param exp  The expected result as formatted by the TestFormatter.
   *
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void assertDiffXMLOK2(String xml1, String xml2, String[] exp)
      throws IOException, DiffXException {
    // Record XML
    SAXLoader recorder = new SAXLoader();
    XMLSequence seq1 = "".equals(xml1) ? new XMLSequence(0) : recorder.load(xml1);
    XMLSequence seq2 = "".equals(xml2) ? new XMLSequence(0) : recorder.load(xml2);
    // Process as list of actions
    List<Action<XMLToken>> actions = diffToActions(seq1, seq2);
    try {
      assertDiffIsCorrect(seq1, seq2, actions);
      DiffAssertions.assertIsWellFormedXML(actions);
      assertMatchTestOutput(actions, exp);
    } catch (AssertionError ex) {
      printXMLErrorDetails(xml1, xml2, exp, toXML(actions), actions);
      throw ex;
    }
  }

  /**
   * Processes the diff and returns the result
   *
   * @param xml1 The first XML doc.
   * @param xml2 The second XML doc.
   *
   * @return The diff output.
   * @throws IOException           Should an I/O exception occur.
   * @throws DiffXException        Should an error occur while parsing XML.
   * @throws IllegalStateException Should the factory fail to create DiffX algorithm.
   */
  private String processDiffXML(String xml1, String xml2)
      throws IOException, DiffXException, IllegalStateException {
    // process the strings
    XMLSequence seq1 = "".equals(xml1) ? new XMLSequence(0) : this.recorder.load(xml1);
    XMLSequence seq2 = "".equals(xml2) ? new XMLSequence(0) : this.recorder.load(xml2);

    this.diffx = makeDiffX(seq1, seq2);
    MultiplexFormatter mf = new MultiplexFormatter();
    ActionFormatter af = new ActionFormatter();
    TestFormatter tf = new TestFormatter();
    mf.add(af);
    mf.add(tf);
    this.diffx.process(tf);
    // check for validity
    List<Action<XMLToken>> actions = af.getActions();
    assertTrue(Actions.isApplicable(seq1.tokens(), seq2.tokens(), actions));
    return tf.getOutput();
  }

  /**
   * Asserts that the diffx operation went right.
   *
   * @param text1 The first XML to compare with diffx.
   * @param text2 The first XML to compare with diffx.
   * @param exp   The expected result as formatted by the TestFormatter.
   *
   * @throws IOException Should an I/O exception occur.
   */
  protected void assertDiffTextOK(String text1, String text2, String[] exp) throws IOException {
    // process the strings
    String diffout = processDiffText(text1, text2);
    // check the possible values
    boolean ok = false;
    try {
      for (String s : exp) {
        ok = ok || s.equals(diffout);
      }
      if (!ok)
        assertEquals(exp[0], diffout);
    } catch (AssertionError ex) {
      printErrorDetails(text1, text2, exp);
      throw ex;
    }
  }

  /**
   * Processes the diff and returns the result
   *
   * @param text1 The first text.
   * @param text2 The second text.
   *
   * @return The diff output.
   * @throws IOException           Should an I/O exception occur.
   * @throws IllegalStateException Should the factory fail to create DiffX algorithm.
   */
  protected String processDiffText(String text1, String text2)
      throws IOException, IllegalStateException {
    // process the strings
    LineLoader loader = new LineLoader();
    List<LineToken> linesA = loader.load(text1);
    List<LineToken> linesB = loader.load(text2);
    XMLSequence seqA = new XMLSequence();
    seqA.addAll(linesA);
    XMLSequence seqB = new XMLSequence();
    seqA.addAll(linesB);
    this.diffx = makeDiffX(seqA, seqB);
    TestFormatter tf = new TestFormatter();
    this.diffx.process(tf);
    return tf.getOutput();
  }

  /**
   * Print the error details.
   *
   * @param xml1 The new XML.
   * @param xml2 The old XML.
   * @param exp  The expected output
   *
   * @throws IOException Should an error occur.
   */
  private void printErrorDetails(String xml1, String xml2, String[] exp) throws IOException {
    // print the XML on the console
    Writer sw = new StringWriter();
    DiffXFormatter sf = new SmartXMLFormatter(sw);
    this.diffx.process(sf);
    TestFormatter tf = new TestFormatter();
    this.diffx.process(tf);
    System.err.println("+------------------------------------------------");
    System.err.println("| New XML:");
    System.err.println(xml1);
    System.err.println("| Old XML:");
    System.err.println(xml2);
    System.err.println("| Diff-X XML Output:");
    System.err.println(sw);
    System.err.println("| Normalised Diff-X output:");
    System.err.println(tf.getOutput());
    for (int i = 0; i < exp.length; i++) {
      System.err.println("| Expected output #" + i);
      System.err.println(exp[i]);
    }
  }

  public final void assertDiffIsCorrect(XMLSequence seq1, XMLSequence seq2, List<Action<XMLToken>> actions) {
    // Ensure that the diff is applicable
    assertTrue(Actions.isApplicable(seq2.tokens(), seq1.tokens(), actions), "The resulting diff is not applicable");

    // Apply to second sequence to ensure we get the first
    XMLSequence got1 = Actions.apply(seq2, actions);
    assertEquals(seq1, got1, "Applying diff to #2 did not produce #1");

    // Apply to first sequence to ensure we get the second
    XMLSequence got2 = Actions.apply(seq1, Actions.flip(actions));
    assertEquals(seq2, got2, "Applying diff to #1 did not produce #2");
  }

  public DiffResult<XMLToken> processResult(XMLSequence seq1, XMLSequence seq2) throws IOException {
    DiffXAlgorithm diffx = makeDiffX(seq1, seq2);
    ActionFormatter formatter = new ActionFormatter();
    diffx.process(formatter);
    return new DiffResult<>(formatter.getActions());
  }

  public List<Action<XMLToken>> diffToActions(XMLSequence seq1, XMLSequence seq2) throws IOException {
    DiffXAlgorithm diffx = makeDiffX(seq1, seq2);
    ActionFormatter formatter = new ActionFormatter();
    diffx.process(formatter);
    return formatter.getActions();
  }

  /**
   * Print the error details.
   */
  protected void printXMLErrorDetails(String xml1, String xml2, String[] exp, String got, List<Action<XMLToken>> actions) {
    System.err.println("+------------------------------------------------");
    System.err.println("| Input A: \"" + xml1 + "\"");
    System.err.println("| Input B: \"" + xml2 + "\"");
    System.err.println("| Output:  \"" + got + "\"");
    System.err.print("| Expect:  ");
    for (String s : exp) System.err.print("\"" + s + "\" ");
    System.err.println();
    System.err.print("| Actions: ");
    for (Action<XMLToken> action : actions) {
      System.err.print(action.operator() == Operator.DEL ? '-' : action.operator() == Operator.INS ? '+' : '=');
      System.err.print(action.tokens());
    }
    System.err.println();
  }

}
