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
import org.pageseeder.diffx.action.ActionsBuffer;
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.format.DefaultXMLDiffOutput;
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.format.XMLDiffOutput;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.xml.NamespaceSet;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * Utility class providing methods for dealing with actions
 */
public final class TestActions {

  public static <T> List<Action<T>> diffToActions(DiffAlgorithm<T> algorithm, List<? extends T> seqA, List<? extends T> seqB) {
    ActionsBuffer<T> handler = new ActionsBuffer<>();
    algorithm.diff(seqA, seqB, handler);
    return handler.getActions();
  }

  public static String toXML(List<Action<XMLToken>> actions) {
    return toXML(actions, NamespaceSet.noNamespace());
  }

  public static String toXML(List<Action<XMLToken>> actions, NamespaceSet namespaces) {
    StringWriter xml = new StringWriter();
    XMLDiffOutput output = new DefaultXMLDiffOutput(xml);
    output.setNamespaces(namespaces);
    output.setWriteXMLDeclaration(false);
    output.start();
    Actions.handle(actions, output);
    output.end();
    return xml.toString();
  }

  @Deprecated
  public static void format(List<Action<XMLToken>> actions, DiffXFormatter formatter) throws IOException {
    for (Action<XMLToken> action : actions) {
      switch (action.operator()) {
        case MATCH:
          for (XMLToken token : action.tokens()) {
            formatter.format(token);
          }
          break;
        case INS:
          for (XMLToken token : action.tokens()) {
            formatter.insert(token);
          }
          break;
        case DEL:
          for (XMLToken token : action.tokens()) {
            formatter.delete(token);
          }
          break;
        default:
      }
    }
  }

}
