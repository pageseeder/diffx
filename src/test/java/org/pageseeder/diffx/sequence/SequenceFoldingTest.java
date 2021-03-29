package org.pageseeder.diffx.sequence;

import junit.framework.AssertionFailedError;
import org.junit.Test;
import org.pageseeder.diffx.load.LoadingException;
import org.pageseeder.diffx.load.SAXRecorder;
import org.pageseeder.diffx.sequence.EventSequence;
import org.pageseeder.diffx.sequence.SequenceFolding;
import org.xml.sax.InputSource;

import java.io.IOException;
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

  @Test public void testDeep1a() throws IOException {
    EventSequence sequence = getSequence("<a><b><c><d>x</d></c></b></a>");
    EventSequence out = SequenceFolding.forElements(new String[]{"a"}).fold(sequence);
    assertEquals(1, out.size());
  }

  @Test public void testDeep1b() throws IOException {
    EventSequence sequence = getSequence("<a><b><c><d>x</d></c></b></a>");
    EventSequence out = SequenceFolding.forElements(new String[]{"b"}).fold(sequence);
    assertEquals(3, out.size());
  }

  @Test public void testDeep1c() throws IOException {
    EventSequence sequence = getSequence("<a><b><c><d>x</d></c></b></a>");
    EventSequence out = SequenceFolding.forElements(new String[]{"c"}).fold(sequence);
    assertEquals(5, out.size());
  }

  @Test public void testDeep1d() throws IOException {
    EventSequence sequence = getSequence("<a><b><c><d>x</d></c></b></a>");
    EventSequence out = SequenceFolding.forElements(new String[]{"d"}).fold(sequence);
    assertEquals(7, out.size());
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
