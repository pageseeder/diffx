package org.pageseeder.diffx.load;

import junit.framework.AssertionFailedError;
import org.junit.Test;
import org.pageseeder.diffx.sequence.EventSequence;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import junit.framework.TestCase;

public class SequenceFoldingTest extends TestCase {

  @Test public void testEmpty() throws IOException {
    EventSequence sequence = new EventSequence();
    EventSequence out = SequenceFolding.forAllElements().fold(sequence);
    assertEquals(0, out.size());
  }

  @Test public void testSimple() throws IOException {
    EventSequence sequence = getSequence("<a/>");
    EventSequence out = SequenceFolding.forAllElements().fold(sequence);
    assertEquals(1, out.size());
  }

  @Test public void testSimple1a() throws IOException {
    EventSequence sequence = getSequence("<a><b/></a>");
    EventSequence out = SequenceFolding.forAllElements().fold(sequence);
    assertEquals(1, out.size());
  }

  @Test public void testSimple1b() throws IOException {
    EventSequence sequence = getSequence("<a><b/></a>");
    EventSequence out = SequenceFolding.forElements(new String[]{"a"}).fold(sequence);
    assertEquals(1, out.size());
  }

  @Test public void testSimple1c() throws IOException {
    EventSequence sequence = getSequence("<a><b/></a>");
    EventSequence out = SequenceFolding.forElements(new String[]{"b"}).fold(sequence);
    assertEquals(3, out.size());
  }

  private static EventSequence getSequence(String xml) {
    try {
      SAXRecorder recorder = new SAXRecorder();
      return recorder.process(new InputSource(new StringReader(xml)));
    } catch (LoadingException | IOException ex) {
      throw new AssertionFailedError("Unable record specified XML");
    }
  }

}
