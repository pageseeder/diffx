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
import org.pageseeder.diffx.algorithm.DiffAlgorithm;
import org.pageseeder.diffx.format.DefaultXMLDiffOutput;
import org.pageseeder.diffx.format.XMLDiffOutput;
import org.pageseeder.diffx.handler.ActionsBuffer;
import org.pageseeder.diffx.token.Token;
import org.pageseeder.diffx.xml.NamespaceSet;

import java.io.StringWriter;
import java.util.List;

/**
 * Utility class providing methods for dealing with actions
 */
public final class TestActions {

  public static List<Action> diffToActions(DiffAlgorithm algorithm, List<? extends Token> seq1, List<? extends Token> seq2) {
    ActionsBuffer handler = new ActionsBuffer();
    algorithm.diff(seq1, seq2, handler);
    return handler.getActions();
  }

  public static String toXML(List<Action> actions) {
    return toXML(actions, NamespaceSet.noNamespace());
  }

  public static String toXML(List<Action> actions, NamespaceSet namespaces) {
    StringWriter xml = new StringWriter();
    XMLDiffOutput output = new DefaultXMLDiffOutput(xml);
    output.setNamespaces(namespaces);
    output.setWriteXMLDeclaration(false);
    output.start();
    Actions.handle(actions, output);
    output.end();
    return xml.toString();
  }

}
