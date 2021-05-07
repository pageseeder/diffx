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
package org.pageseeder.diffx.test;

import org.opentest4j.AssertionFailedError;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.Actions;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.Token;
import org.pageseeder.diffx.token.impl.XMLEndElement;
import org.pageseeder.diffx.token.impl.XMLStartElement;
import org.pageseeder.diffx.xml.NamespaceSet;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A utility class providing a collection of assertions for testing diff.
 */
public final class DiffAssertions {

  /**
   * Asserts that the applying list of actions generates a list of tokens that match the tokens
   */
  public static void assertActionsMatchEvents(List<? extends Token> tokens, List<Action> actions, boolean forward) {
    String exp = TestHandler.format(tokens);
    String got = TestHandler.format(Actions.generate(actions, forward));
    assertEquals(exp, got, "TestTokens generated by applying actions do not match the supplied tokens");
  }

  /**
   * Asserts that the list of actions is applicable to the specified sequences.
   */
  public static void assertIsApplicable(List<? extends Token> seqA, List<? extends Token> seqB, List<Action> actions) {
    assertTrue(Actions.isApplicable(seqA, seqB, actions), "The resulting diff is not applicable");
  }

  /**
   * Asserts that the list of actions lead to the correct XML
   */
  public static void assertIsCorrect(List<? extends Token> seqA, List<? extends Token> seqB, List<Action> actions) {
    // Apply actions to A to ensure we get B
    String gotB = TestTokens.toXML(Actions.generate(actions, true));
    String expB = TestTokens.toXML(seqB);
    assertEquals(expB, gotB, "Applying diff to A did not produce B");

    // Apply actions to B to ensure we get A
    String gotA = TestTokens.toXML(Actions.generate(actions, false));
    String expA = TestTokens.toXML(seqA);
    assertEquals(expA, gotA, "Applying diff to B did not produce A");
  }

  /**
   * Asserts that the list of actions lead to the correct XML
   */
  public static void assertIsCorrect(Sequence a, Sequence b, List<Action> actions) {
    // Apply actions to A to ensure we get B
    String gotB = TestTokens.toXML(Actions.generate(actions, true), b.getNamespaces());
    String expB = TestTokens.toXML(b);
    assertEquals(expB, gotB, "Applying diff to B did not produce B");

    //  Apply actions to B to ensure we get A
    String gotA = TestTokens.toXML(Actions.generate(actions, false), a.getNamespaces());
    String expA = TestTokens.toXML(a);
    assertEquals(expA, gotA, "Applying diff to B did not produce A");
  }

  public static void assertIsWellFormedXML(List<Action> actions) {
    List<Action> wrapped = new ArrayList<>();
    // We wrap the actions in case we have a completely different output
    StartElementToken root = new XMLStartElement("root");
    wrapped.add(new Action(Operator.MATCH, Collections.singletonList(root)));
    wrapped.addAll(actions);
    wrapped.add(new Action(Operator.MATCH, Collections.singletonList(new XMLEndElement(root))));
    DiffAssertions.assertIsWellFormedXML(TestActions.toXML(wrapped));
  }

  public static void assertIsWellFormedXML(List<Action> actions, NamespaceSet namespaces) {
    List<Action> wrapped = new ArrayList<>();
    // We wrap the actions in case we have a completely different output
    String defaultNamespaceURI = namespaces.getUri("");
    StartElementToken root = new XMLStartElement(defaultNamespaceURI, "root");
    wrapped.add(new Action(Operator.MATCH, Collections.singletonList(root)));
    wrapped.addAll(actions);
    wrapped.add(new Action(Operator.MATCH, Collections.singletonList(new XMLEndElement(root))));
    String xml = TestActions.toXML(wrapped, namespaces);
    DiffAssertions.assertIsWellFormedXML(xml);
  }

  /**
   * Asserts that the specified XML string is well formed.
   *
   * @param xml THe XML string to test
   */
  public static void assertIsWellFormedXML(String xml) {
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
    String output = toTestOutput(actions, NamespaceSet.noNamespace());
    for (String s : exp) {
      if (s.equals(output)) return;
    }
    assertEquals(exp[0], output);
  }

  public static void assertMatchTestOutput(List<Action> actions, String[] exp, NamespaceSet namespaces) {
    // check the possible values
    String output = toTestOutput(actions, namespaces);
    for (String s : exp) {
      if (s.equals(output)) return;
    }
    assertEquals(exp[0], output);
  }

  public static String toTestOutput(List<Action> actions, NamespaceSet namespaces) {
    TestHandler handler = new TestHandler(namespaces);
    Actions.handle(actions, handler);
    return handler.getOutput();
  }

  public static String toTestOutput(List<Action> actions) {
    TestHandler handler = new TestHandler(NamespaceSet.noNamespace());
    Actions.handle(actions, handler);
    return handler.getOutput();
  }

  public static void assertEqualsAny(String[] expected, String actual) {
    // check the possible values
    for (String exp : expected) {
      if (exp.equals(actual)) return;
    }
    throw new AssertionFailedError("Actual value did not match any of the expected values", expected, actual);
  }
}
