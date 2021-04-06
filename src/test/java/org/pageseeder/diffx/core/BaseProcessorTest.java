package org.pageseeder.diffx.core;

import org.junit.Assert;
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.action.*;
import org.pageseeder.diffx.algorithm.DiffXAlgorithm;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.format.SmartXMLFormatter;
import org.pageseeder.diffx.format.XMLDiffXFormatter;
import org.pageseeder.diffx.handler.ActionHandler;
import org.pageseeder.diffx.handler.FormattingAdapter;
import org.pageseeder.diffx.handler.MuxHandler;
import org.pageseeder.diffx.load.SAXRecorder;
import org.pageseeder.diffx.load.TextRecorder;
import org.pageseeder.diffx.sequence.EventSequence;
import org.pageseeder.diffx.test.TestFormatter;
import org.pageseeder.diffx.test.TestHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

public abstract class BaseProcessorTest {

  /**
   * The loader used for the tests.
   */
  private final SAXRecorder recorder = new SAXRecorder();

  /**
   * @return The processor instance to use for texting.
   */
  public abstract DiffProcessor getDiffProcessor();

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
    List<? extends DiffXEvent> seq1 = toXMLEvents(xml1);
    List<? extends DiffXEvent> seq2 = toXMLEvents(xml2);
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
    List<? extends DiffXEvent> seq1 = toXMLEvents(xml1);
    List<? extends DiffXEvent> seq2 = toXMLEvents(xml2);
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
    DiffProcessor processor = getDiffProcessor();
    ActionHandler af = new ActionHandler();
    TestHandler tf = new TestHandler();
    MuxHandler handler = new MuxHandler(af, tf);
    processor.process(seq1.events(), seq2.events(), handler);
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
    DiffProcessor processor = getDiffProcessor();
    TestHandler handler = new TestHandler();
    processor.process(seq1.events(), seq2.events(), handler);
    return handler.getOutput();
  }

  /**
   * Print the error details.
   *
   * @param xml1 The new XML.
   * @param xml2 The old XML.
   * @param exp  The expected output
   * @throws IOException Should an error occur.
   */
  private void printErrorDetails(String xml1, String xml2, String[] exp) throws DiffXException, IOException {
    // print the XML on the console
    Writer sw = new StringWriter();
    List<DiffXEvent> events1 = toXMLEvents(xml1);
    List<DiffXEvent> events2 = toXMLEvents(xml2);
    DiffXFormatter sf = new SmartXMLFormatter(sw);
    getDiffProcessor().process(events1, events2, new FormattingAdapter(sf));
    TestHandler tf = new TestHandler();
    getDiffProcessor().process(events1, events2, tf);
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

  public final void assertDiffIsCorrect(List<? extends DiffXEvent> seq1, List<? extends DiffXEvent> seq2, List<Action> actions) {
    // Ensure that the diff is applicable
    Assert.assertTrue("The resulting diff is not applicable", Actions.isApplicable(seq1, seq2, actions));

    // Apply to second sequence to ensure we get the first
    List<DiffXEvent> got1 = Actions.apply(seq2, actions);
    Assert.assertEquals("Applying diff to #2 did not produce #1 ", seq1, got1);

    // Apply to first sequence to ensure we get the second
    List<DiffXEvent> got2 = Actions.apply(seq1, Actions.reverse(actions));
    Assert.assertEquals("Applying diff to #1 did not produce #2 ", seq2, got2);
  }

  public DiffResult processResult(List<? extends DiffXEvent> seq1, List<? extends DiffXEvent> seq2) throws IOException {
    DiffProcessor processor = getDiffProcessor();
    ActionHandler handler = new ActionHandler();
    processor.process(seq1, seq2, handler);
    return new DiffResult(handler.getActions());
  }

  public List<Action> diffToActions(List<? extends DiffXEvent> seq1, List<? extends DiffXEvent> seq2) throws IOException {
    DiffProcessor processor = getDiffProcessor();
    ActionHandler handler = new ActionHandler();
    processor.process(seq1, seq2, handler);
    return handler.getActions();
  }

  public static String toXML(List<Action> actions) throws IOException {
    StringWriter xml = new StringWriter();
    XMLDiffXFormatter formatter = new SmartXMLFormatter(xml);
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
  protected void printXMLErrorDetails(String xml1, String xml2, String[] exp, String got, List<Action> actions) {
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

  List<DiffXEvent> toXMLEvents(String xml) throws DiffXException, IOException {
    return xml.length() > 0? new SAXRecorder().process(xml).events() : Collections.emptyList();
  }

  List<DiffXEvent> toLineEvents(String text) throws DiffXException, IOException  {
    return new TextRecorder().process(text).events();
  }

}
