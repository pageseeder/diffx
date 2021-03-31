package org.pageseeder.diffx.algorithm;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.ActionFormatter;
import org.pageseeder.diffx.action.Actions;
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.format.MultiplexFormatter;
import org.pageseeder.diffx.format.SmartXMLFormatter;
import org.pageseeder.diffx.load.SAXRecorder;
import org.pageseeder.diffx.sequence.SequenceFolding;
import org.pageseeder.diffx.load.TextRecorder;
import org.pageseeder.diffx.sequence.EventSequence;
import org.pageseeder.diffx.test.TestFormatter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

public abstract class BaseAlgorithmTest extends TestCase {

  /**
   * The loader used for the tests.
   */
  private final SAXRecorder recorder = new SAXRecorder();

  /**
   * The Diff-X algorithm being tested.
   */
  private transient DiffXAlgorithm diffx = null;

  public BaseAlgorithmTest(String name) {
    super(name);
  }

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
    } catch (AssertionFailedError ex) {
      printErrorDetails(xml1, xml2, exp);
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

    EventSequence s1 = SequenceFolding.forElements(new String[]{"li"}).fold(seq1);
    EventSequence s2 = SequenceFolding.forElements(new String[]{"li"}).fold(seq2);

    this.diffx = makeDiffX(s1, s2);
    MultiplexFormatter mf = new MultiplexFormatter();
    ActionFormatter af = new ActionFormatter();
    TestFormatter tf = new TestFormatter();
    mf.add(af);
    mf.add(tf);
    this.diffx.process(tf);
    // check for validity
    List<Action> actions = af.getActions();
    assertTrue(Actions.isApplicable(s1.events(), s2.events(), actions));
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
        assertEquals(exp[0], diffout);
    } catch (AssertionFailedError ex) {
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
}
