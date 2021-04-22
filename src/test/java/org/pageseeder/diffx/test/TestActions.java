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

import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.Actions;
import org.pageseeder.diffx.core.DiffAlgorithm;
import org.pageseeder.diffx.format.SmartXMLDiffOutput;
import org.pageseeder.diffx.format.XMLDiffXFormatter;
import org.pageseeder.diffx.handler.ActionHandler;
import org.pageseeder.diffx.sequence.PrefixMapping;
import org.pageseeder.diffx.token.Token;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * Utility class providing methods for dealing with actions
 */
public final class TestActions {


  public static List<Action> diffToActions(DiffAlgorithm algorithm, List<? extends Token> seq1, List<? extends Token> seq2) {
    ActionHandler handler = new ActionHandler();
    algorithm.diff(seq1, seq2, handler);
    return handler.getActions();
  }


  public static String toXML(List<Action> actions) {
    try {
      StringWriter xml = new StringWriter();
      XMLDiffXFormatter formatter = new SmartXMLDiffOutput(xml);
      Actions.format(actions, formatter);
      return xml.toString();
    } catch (IOException ex) {
      // Should not occur
      throw new UncheckedIOException("Unable to check assertions due to", ex);
    }
  }

  public static String toXML(List<Action> actions, PrefixMapping mapping) {
    try {
      StringWriter xml = new StringWriter();
      XMLDiffXFormatter formatter = new SmartXMLDiffOutput(xml);
      formatter.declarePrefixMapping(mapping);
      Actions.format(actions, formatter);
      return xml.toString();
    } catch (IOException ex) {
      // Should not occur
      throw new UncheckedIOException("Unable to check assertions due to", ex);
    }
  }

}
