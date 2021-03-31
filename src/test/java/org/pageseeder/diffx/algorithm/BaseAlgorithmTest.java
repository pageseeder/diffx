package org.pageseeder.diffx.algorithm;

import org.junit.Assert;
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.action.*;
import org.pageseeder.diffx.format.*;
import org.pageseeder.diffx.load.SAXRecorder;
import org.pageseeder.diffx.load.TextRecorder;
import org.pageseeder.diffx.sequence.EventSequence;
import org.pageseeder.diffx.test.TestFormatter;
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

public abstract class BaseAlgorithmTest {

  /**
   * The loader used for the tests.
   */
  private final SAXRecorder recorder = new SAXRecorder();

  /**
   * The Diff-X algorithm being tested.
   */
  private transient DiffXAlgorithm diffx = null;

  /**
   * Returns the Diff-X Algorithm instance from the specified sequences.
   *
   * @param seq1 The first sequence.
   * @param seq2 The second sequence.
   * @return The Diff-X Algorithm instance.
   */
  public abstract DiffXAlgorithm makeDiffX(EventSequence seq1, EventSequence seq2);


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
    SAXRecorder recorder = new SAXRecorder();
    EventSequence seq1 = "".equals(xml1) ? new EventSequence(0) : recorder.process(xml1);
    EventSequence seq2 = "".equals(xml2) ? new EventSequence(0) : recorder.process(xml2);
    // Process as list of actions
    DiffResult result = processResult(seq1, seq2);
    try {
      assertDiffIsCorrect(seq1, seq2, result.actions());
      assertDiffIsWellFormedXML(result.actions());
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
        Assert.assertEquals(exp[0], diffout);
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
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  public final void assertDiffXMLOK2(String xml1, String xml2, String[] exp)
      throws IOException, DiffXException {
    // Record XML
    SAXRecorder recorder = new SAXRecorder();
    EventSequence seq1 = "".equals(xml1) ? new EventSequence(0) : recorder.process(xml1);
    EventSequence seq2 = "".equals(xml2) ? new EventSequence(0) : recorder.process(xml2);
    // Process as list of actions
    List<Action> actions = diffToActions(seq1, seq2);
    try {
      assertDiffIsCorrect(seq1, seq2, actions);
      assertDiffIsWellFormedXML(actions);
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
   * @return The diff output.
   * @throws IOException           Should an I/O exception occur.
   * @throws DiffXException        Should an error occur while parsing XML.
   * @throws IllegalStateException Should the factory fail to create DiffX algorithm.
   */
  private String processDiffXML(String xml1, String xml2)
      throws IOException, DiffXException, IllegalStateException {
    // process the strings
    EventSequence seq1 = "".equals(xml1) ? new EventSequence(0) : this.recorder.process(xml1);
    EventSequence seq2 = "".equals(xml2) ? new EventSequence(0) : this.recorder.process(xml2);

    this.diffx = makeDiffX(seq1, seq2);
    MultiplexFormatter mf = new MultiplexFormatter();
    ActionFormatter af = new ActionFormatter();
    TestFormatter tf = new TestFormatter();
    mf.add(af);
    mf.add(tf);
    this.diffx.process(tf);
    // check for validity
    List<Action> actions = af.getActions();
    Assert.assertTrue(Actions.isApplicable(seq1.events(), seq2.events(), actions));
    return tf.getOutput();
  }

  /**
   * Asserts that the diffx operation went right.
   *
   * @param text1 The first XML to compare with diffx.
   * @param text2 The first XML to compare with diffx.
   * @param exp   The expected result as formatted by the TestFormatter.
   * @throws IOException    Should an I/O exception occur.
   * @throws DiffXException Should an error occur while parsing XML.
   */
  protected void assertDiffTextOK(String text1, String text2, String[] exp)
      throws IOException, DiffXException {
    // process the strings
    String diffout = processDiffText(text1, text2);
    // check the possible values
    boolean ok = false;
    try {
      for (String s : exp) {
        ok = ok || s.equals(diffout);
      }
      if (!ok)
        Assert.assertEquals(exp[0], diffout);
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
   * @return The diff output.
   * @throws IOException           Should an I/O exception occur.
   * @throws DiffXException        Should an error occur while parsing XML.
   * @throws IllegalStateException Should the factory fail to create DiffX algorithm.
   */
  protected String processDiffText(String text1, String text2)
      throws IOException, DiffXException, IllegalStateException {
    // process the strings
    TextRecorder textRecorder = new TextRecorder();
    EventSequence seq1 = textRecorder.process(text1);
    EventSequence seq2 = textRecorder.process(text2);
    this.diffx = makeDiffX(seq1, seq2);
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
    System.err.println(sw.toString());
    System.err.println("| Normalised Diff-X output:");
    System.err.println(tf.getOutput());
    for (int i = 0; i < exp.length; i++) {
      System.err.println("| Expected output #" + i);
      System.err.println(exp[i]);
    }
  }

  public final void assertDiffIsCorrect(EventSequence seq1, EventSequence seq2, List<Action> actions) throws IOException {
    // Ensure that the diff is applicable
    Assert.assertTrue("The resulting diff is not applicable", Actions.isApplicable(seq1.events(), seq2.events(), actions));

    // Apply to second sequence to ensure we get the first
    EventSequence got1 = Actions.apply(seq2, actions);
    Assert.assertEquals("Applying diff to #2 did not produce #1 ", seq1, got1);

    // Apply to first sequence to ensure we get the second
    EventSequence got2 = Actions.apply(seq1, Actions.reverse(actions));
    Assert.assertEquals("Applying diff to #1 did not produce #2 ", seq2, got2);
  }

  public DiffResult processResult(EventSequence seq1, EventSequence seq2) throws IOException {
    DiffXAlgorithm diffx = makeDiffX(seq1, seq2);
    ActionFormatter formatter = new ActionFormatter();
    diffx.process(formatter);
    return new DiffResult(formatter.getActions());
  }

  public List<Action> diffToActions(EventSequence seq1, EventSequence seq2) throws IOException {
    DiffXAlgorithm diffx = makeDiffX(seq1, seq2);
    ActionFormatter formatter = new ActionFormatter();
    diffx.process(formatter);
    return formatter.getActions();
  }

  public static String toXML(List<Action> actions) throws IOException {
    StringWriter xml = new StringWriter();
    XMLDiffXFormatter formatter = new BasicXMLFormatter(xml);
    Actions.format(actions, formatter);
    return xml.toString();
  }

  public static String toTestFormat(List<Action> actions) throws IOException {
    TestFormatter formatter = new TestFormatter();
    Actions.format(actions, formatter);
    return formatter.getOutput();
  }

  public static void assertDiffIsWellFormedXML(List<Action> actions) throws IOException {
    assertIsWellFormedXML(toXML(actions));
  }

  public static void assertMatchTestOutput(List<Action> actions, String[] exp) throws IOException {
    // check the possible values
    String diffout = toTestFormat(actions);
    for (String s : exp) {
      if (s.equals(diffout)) return;
    }
    Assert.assertEquals(exp[0], diffout);
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
   * Print the error details.
   */
  private void printXMLErrorDetails(String xml1, String xml2, String[] exp, String got, List<Action> actions) {
    System.err.println("+------------------------------------------------");
    System.err.println("| Input A: \"" + xml1 + "\"");
    System.err.println("| Input B: \"" + xml2 + "\"");
    System.err.println("| Output:  \"" + got + "\"");
    System.err.print("| Expect:  ");
    for (String s : exp) System.err.print("\"" + s + "\" ");
    System.err.println();
    System.err.print("| Actions: ");
    for (Action action : actions) {
      System.err.print(action.type() == Operator.DEL ? '-' : action.type() == Operator.INS ? '+' : '=');
      System.err.print(action.events());
    }
    System.err.println();
  }

}
