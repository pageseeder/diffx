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

import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.OperationsBuffer;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.XMLTokenFactory;
import org.pageseeder.diffx.token.impl.CharToken;
import org.pageseeder.diffx.token.impl.XMLAttribute;
import org.pageseeder.diffx.token.impl.XMLEndElement;
import org.pageseeder.diffx.token.impl.XMLStartElement;

import java.util.List;

public class TestOperations {


  public static List<Operation<XMLToken>> toXMLOperations(String... ops) {
    OperationsBuffer<XMLToken> source = new OperationsBuffer<>();
    for (String op : ops) {
      String t = op;
      // Identify the operator
      Operator o = Operator.MATCH;
      if (op.startsWith("+")) o = Operator.INS;
      else if (op.startsWith("-")) o = Operator.DEL;
      if (o != Operator.MATCH) t = op.substring(1);
      // Identify the token
      XMLToken token;
      if (t.startsWith("</")) token = new XMLEndElement(t.substring(2, t.length()-1));
      else if (t.startsWith("<")) token = new XMLStartElement(t.substring(1, t.length()-1));
      else if (t.startsWith("@") && t.contains("=")) token = new XMLAttribute(t.substring(1, t.indexOf('=')), t.substring(t.indexOf('=')+1));
      else token = TestTokens.toTextToken(t);
      source.handle(o, token);
    }
    return source.getOperations();
  }

  public static List<Operation<XMLToken>> toTextOperations(String... ops) {
    OperationsBuffer<XMLToken> source = new OperationsBuffer<>();
    for (String op : ops) {
      if (op.startsWith("+")) source.handle(Operator.INS, TestTokens.toTextToken(op.substring(1)));
      else if (op.startsWith("-")) source.handle(Operator.DEL, TestTokens.toTextToken(op.substring(1)));
      else source.handle(Operator.MATCH, TestTokens.toTextToken(op));
    }
    return source.getOperations();
  }

  public static List<Operation<XMLToken>> toCharOperations(String ops) {
    OperationsBuffer<XMLToken> source = new OperationsBuffer<>();
    char[] chars = ops.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];
      if (c == '+' && (i + 1) < chars.length) source.handle(Operator.INS, new CharToken(chars[++i]));
      else if (c == '-' && (i + 1) < chars.length) source.handle(Operator.DEL, new CharToken(chars[++i]));
      else source.handle(Operator.MATCH, new CharToken(c));
    }
    return source.getOperations();
  }

}
