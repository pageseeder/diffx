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
package org.pageseeder.diffx.test;

import org.junit.Assert;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.Actions;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.OpenElementEvent;
import org.pageseeder.diffx.event.impl.CloseElementEventImpl;
import org.pageseeder.diffx.event.impl.OpenElementEventImpl;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A utility class providing a collection of assertions for testing diff.
 */
public final class DiffAssertions {

  /**
   * Ensure that the list of actions is applicable to the specified sequences.
   */
  public static void assertIsApplicable(List<? extends DiffXEvent> seq1, List<? extends DiffXEvent> seq2, List<Action> actions) {
    Assert.assertTrue("The resulting diff is not applicable", Actions.isApplicable(seq1, seq2, actions));
  }

  /**
   * Asserts that the list of actions lead to the correct XML
   */
  public static void assertIsCorrect(List<? extends DiffXEvent> seq1, List<? extends DiffXEvent> seq2, List<Action> actions) {
    // Apply to second sequence to ensure we get the first
    String got1 = Events.toXML(Actions.generate(actions, true));
    String exp1 = Events.toXML(seq1);
    Assert.assertEquals("Applying diff to #2 did not produce #1 ", exp1, got1);

    // Apply to first sequence to ensure we get the second
    String got2 = Events.toXML(Actions.generate(actions, false));
    String exp2 = Events.toXML(seq2);
    Assert.assertEquals("Applying diff to #1 did not produce #2 ", exp2, got2);
  }


  public static void assertIsWellFormedXML(List<Action> actions) {
    List<Action> wrapped = new ArrayList<>();
    // We wrap the actions in case we have a completely different output
    OpenElementEvent root = new OpenElementEventImpl("root");
    wrapped.add(new Action(Operator.MATCH, Collections.singletonList(root)));
    wrapped.addAll(actions);
    wrapped.add(new Action(Operator.MATCH, Collections.singletonList(new CloseElementEventImpl(root))));
    DiffAssertions.assertIsWellFormedXML(TestActions.toXML(wrapped));
  }

  /**
   * Asserts that the specified XML string is well formed.
   *
   * @param xml THe XML string to test
   */
  public static void assertIsWellFormedXML(String xml)  {
    try {
      InputSource source = new InputSource(new StringReader(xml));
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.newSAXParser().parse(source, new DefaultHandler());
    } catch (SAXException ex) {
      throw new AssertionError("XML is not well-formed");
    } catch (ParserConfigurationException ex) {
      throw new IllegalStateException("Unable to check assertions due to", ex);
    } catch (IOException ex) {
      throw new UncheckedIOException("Unable to check assertions due to", ex);
    }
  }

  public static void assertMatchTestOutput(List<Action> actions, String[] exp) {
    // check the possible values
    String output = toTestOutput(actions);
    for (String s : exp) {
      if (s.equals(output)) return;
    }
    Assert.assertEquals(exp[0], output);
  }

  public static String toTestOutput(List<Action> actions) {
    try {
      TestHandler handler = new TestHandler();
      Actions.format(actions, handler);
      return handler.getOutput();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

}
