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
package org.pageseeder.diffx;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

final class ExtensionTest {

  @Test
  void diff_identicalNodes_returnsNode() throws Exception {
    Node a = toNode("<root><item>Hello</item></root>");
    Node b = toNode("<root><item>Hello</item></root>");
    Node result = Extension.diff(a, b, "IGNORE", "TEXT");
    assertNotNull(result);
    assertEquals("root", result.getNodeName());
  }

  @Test
  void diff_differentText_returnsNode() throws Exception {
    Node a = toNode("<root><item>Hello</item></root>");
    Node b = toNode("<root><item>World</item></root>");
    Node result = Extension.diff(a, b, "IGNORE", "TEXT");
    assertNotNull(result);
  }

  @Test
  void diff_differentStructure_returnsNode() throws Exception {
    Node a = toNode("<root><a>text</a></root>");
    Node b = toNode("<root><b>text</b></root>");
    Node result = Extension.diff(a, b, "IGNORE", "TEXT");
    assertNotNull(result);
  }

  @Test
  void diff_emptyElements_returnsNode() throws Exception {
    Node a = toNode("<root/>");
    Node b = toNode("<root/>");
    Node result = Extension.diff(a, b, "IGNORE", "TEXT");
    assertNotNull(result);
  }

  @Test
  void diff_withAttributes_returnsNode() throws Exception {
    Node a = toNode("<root attr=\"1\">text</root>");
    Node b = toNode("<root attr=\"2\">text</root>");
    Node result = Extension.diff(a, b, "IGNORE", "TEXT");
    assertNotNull(result);
  }

  @Test
  void diff_preserveWhitespace_returnsNode() throws Exception {
    Node a = toNode("<root> hello </root>");
    Node b = toNode("<root>hello</root>");
    Node result = Extension.diff(a, b, "PRESERVE", "TEXT");
    assertNotNull(result);
  }

  @Test
  void diff_compareWhitespace_returnsNode() throws Exception {
    Node a = toNode("<root> hello </root>");
    Node b = toNode("<root>hello</root>");
    Node result = Extension.diff(a, b, "COMPARE", "TEXT");
    assertNotNull(result);
  }

  @Test
  void diff_wordGranularity_returnsNode() throws Exception {
    Node a = toNode("<root>one two three</root>");
    Node b = toNode("<root>one three</root>");
    Node result = Extension.diff(a, b, "IGNORE", "WORD");
    assertNotNull(result);
  }

  @Test
  void diff_characterGranularity_returnsNode() throws Exception {
    Node a = toNode("<root>abc</root>");
    Node b = toNode("<root>axc</root>");
    Node result = Extension.diff(a, b, "IGNORE", "CHARACTER");
    assertNotNull(result);
  }

  @Test
  void diff_spaceWordGranularity_returnsNode() throws Exception {
    Node a = toNode("<root>one two three</root>");
    Node b = toNode("<root>one three</root>");
    Node result = Extension.diff(a, b, "IGNORE", "SPACE_WORD");
    assertNotNull(result);
  }

  @Test
  void diff_invalidWhitespace_throwsException() throws Exception {
    Node a = toNode("<root>a</root>");
    Node b = toNode("<root>b</root>");
    assertThrows(IllegalArgumentException.class, () ->
        Extension.diff(a, b, "INVALID", "TEXT")
    );
  }

  @Test
  void diff_invalidGranularity_throwsException() throws Exception {
    Node a = toNode("<root>a</root>");
    Node b = toNode("<root>b</root>");
    assertThrows(IllegalArgumentException.class, () ->
        Extension.diff(a, b, "IGNORE", "INVALID")
    );
  }

  @Test
  void diff_nullNode1_throwsNullPointerException() throws Exception {
    Node b = toNode("<root>b</root>");
    assertThrows(NullPointerException.class, () ->
        Extension.diff(null, b, "IGNORE", "TEXT")
    );
  }

  @Test
  void diff_nullNode2_throwsNullPointerException() throws Exception {
    Node a = toNode("<root>a</root>");
    assertThrows(NullPointerException.class, () ->
        Extension.diff(a, null, "IGNORE", "TEXT")
    );
  }

  @Test
  void diff_nullWhitespace_throwsNullPointerException() throws Exception {
    Node a = toNode("<root>a</root>");
    Node b = toNode("<root>b</root>");
    assertThrows(NullPointerException.class, () ->
        Extension.diff(a, b, null, "TEXT")
    );
  }

  @Test
  void diff_nullGranularity_throwsNullPointerException() throws Exception {
    Node a = toNode("<root>a</root>");
    Node b = toNode("<root>b</root>");
    assertThrows(NullPointerException.class, () ->
        Extension.diff(a, b, "IGNORE", null)
    );
  }

  @Test
  void diff_nestedElements_returnsNode() throws Exception {
    Node a = toNode("<root><a><b>text</b></a></root>");
    Node b = toNode("<root><a><c>text</c></a></root>");
    Node result = Extension.diff(a, b, "IGNORE", "TEXT");
    assertNotNull(result);
  }

  @Test
  void diff_multipleChildren_returnsNode() throws Exception {
    Node a = toNode("<root><a>1</a><b>2</b><c>3</c></root>");
    Node b = toNode("<root><a>1</a><b>X</b><c>3</c></root>");
    Node result = Extension.diff(a, b, "IGNORE", "TEXT");
    assertNotNull(result);
  }

  @Test
  void diff_withNamespaces_returnsNode() throws Exception {
    Node a = toNode("<root xmlns:ns=\"http://example.com\"><ns:item>A</ns:item></root>");
    Node b = toNode("<root xmlns:ns=\"http://example.com\"><ns:item>B</ns:item></root>");
    Node result = Extension.diff(a, b, "IGNORE", "TEXT");
    assertNotNull(result);
  }

  private static Node toNode(String xml) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
    return doc.getDocumentElement();
  }

}
