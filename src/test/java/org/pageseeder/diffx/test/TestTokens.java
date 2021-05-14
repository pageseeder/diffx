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

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.format.DefaultXMLDiffOutput;
import org.pageseeder.diffx.load.DOMLoader;
import org.pageseeder.diffx.load.LineLoader;
import org.pageseeder.diffx.load.LoadingException;
import org.pageseeder.diffx.load.SAXLoader;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.token.TextToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.impl.CharToken;
import org.pageseeder.diffx.token.impl.IgnorableSpaceToken;
import org.pageseeder.diffx.token.impl.WordToken;
import org.pageseeder.diffx.xml.NamespaceSet;
import org.w3c.dom.Document;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for tokens and testing.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TestTokens {

  /**
   * Prevents creation of instances.
   */
  private TestTokens() {
  }

  public static TextToken toTextToken(String text) {
    if (text.matches("\\s+")) {
      return new IgnorableSpaceToken(text);
    }
    return new WordToken(text);
  }

  public static List<TextToken> toTextTokens(String... words) {
    List<TextToken> tokens = new ArrayList<>();
    for (String word : words) {
      tokens.add(toTextToken(word));
    }
    return tokens;
  }

  public static List<CharToken> toCharTokens(String string) {
    List<CharToken> s = new ArrayList<>();
    for (char c : string.toCharArray()) {
      s.add(new CharToken(c));
    }
    return s;
  }

  public static List<XMLToken> loadTokens(String xml, TextGranularity granularity) throws LoadingException {
    return loadTokens(xml, DiffConfig.getDefault().granularity(granularity));
  }

  public static List<XMLToken> loadTokens(String xml, DiffConfig config) throws LoadingException {
    return loadSequence(xml, config).tokens();
  }

  public static Sequence loadSequence(String xml, TextGranularity granularity) throws LoadingException {
    return loadSequence(xml, DiffConfig.getDefault().granularity(granularity));
  }

  public static Sequence loadSequence(String xml, DiffConfig config) throws LoadingException {
    if (xml.isEmpty()) return new Sequence();
    SAXLoader loader = new SAXLoader();
    loader.setConfig(config);
    return loader.load(xml);
  }

  public static Sequence loadSequence(Document document, TextGranularity granularity) throws LoadingException {
    DOMLoader loader = new DOMLoader();
    DiffConfig config = DiffConfig.getDefault().granularity(granularity);
    loader.setConfig(config);
    return loader.load(document);
  }

  public static List<XMLToken> loadLineEvents(String text) {
    if (text.isEmpty()) return Collections.emptyList();
    return new LineLoader().load(text).tokens();
  }

  public static String toXML(List<? extends XMLToken> tokens) {
    return toXML(tokens, new NamespaceSet());
  }

  public static String toXML(List<? extends XMLToken> tokens, NamespaceSet namespaces) {
    StringWriter xml = new StringWriter();
    DefaultXMLDiffOutput f = new DefaultXMLDiffOutput(xml);
    f.setWriteXMLDeclaration(false);
    f.setNamespaces(namespaces);
    f.start();
    for (XMLToken token : tokens) {
      f.handle(Operator.MATCH, token);
    }
    f.end();
    return xml.toString();
  }

  public static String toXML(Sequence sequence) {
    StringWriter xml = new StringWriter();
    DefaultXMLDiffOutput f = new DefaultXMLDiffOutput(xml);
    f.setWriteXMLDeclaration(false);
    f.setNamespaces(sequence.getNamespaces());
    f.start();
    for (XMLToken token : sequence) {
      f.handle(Operator.MATCH, token);
    }
    f.end();
    return xml.toString();
  }


}
