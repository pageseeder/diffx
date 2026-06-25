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
package org.pageseeder.diffx.format;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.DiffException;
import org.pageseeder.diffx.load.SAXLoader;
import org.pageseeder.diffx.xml.Sequence;
import org.pageseeder.diffx.token.impl.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.pageseeder.diffx.api.Operator.*;

/**
 * Test class for the strict formatter.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
final class StrictXMLDiffOutputTest {

  private static final String NS_DECL = "xmlns:diff=\"https://www.pageseeder.org/diffx\"";

  private final SAXLoader recorder = new SAXLoader();

  private StrictXMLDiffOutput output = null;

  private StringWriter w = null;

  @BeforeEach
  void setUp() {
    this.w = new StringWriter();
    this.output = new StrictXMLDiffOutput(this.w);
    this.output.setWriteXMLDeclaration(false);
  }

  // --- Constructors ---

  @Test
  void testOutputStreamConstructor() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    StrictXMLDiffOutput osOutput = new StrictXMLDiffOutput(out);
    osOutput.setWriteXMLDeclaration(false);
    osOutput.start();
    osOutput.handle(MATCH, new XMLStartElement("a"));
    osOutput.handle(MATCH, new XMLEndElement("a"));
    String result = out.toString(StandardCharsets.UTF_8);
    assertTrue(result.contains("<a"));
    assertTrue(result.contains("</a>"));
  }

  // --- Start / end elements ---

  @Test
  void testMatchedOpenAndClose() throws DiffException {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("a"));
    this.output.handle(MATCH, new XMLEndElement("a"));
    this.output.end();
    assertEquivalentToXML("<a/>");
    String xml = "<a " + NS_DECL + "></a>";
    assertEquals(xml, this.w.toString());
  }

  @Test
  void testInsertedElement() {
    this.output.start();
    this.output.handle(INS, new XMLStartElement("a"));
    this.output.handle(INS, new XMLEndElement("a"));
    String result = this.w.toString();
    assertTrue(result.contains("diff:insert=\"true\""));
  }

  @Test
  void testDeletedElement() {
    this.output.start();
    this.output.handle(DEL, new XMLStartElement("a"));
    this.output.handle(DEL, new XMLEndElement("a"));
    String result = this.w.toString();
    assertTrue(result.contains("diff:delete=\"true\""));
  }

  @Test
  void testNestedElements() throws DiffException {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("a"));
    this.output.handle(MATCH, new XMLStartElement("b"));
    this.output.handle(MATCH, new XMLEndElement("b"));
    this.output.handle(MATCH, new XMLEndElement("a"));
    this.output.end();
    assertEquivalentToXML("<a><b/></a>");
  }

  // --- Attributes ---

  @Test
  void testMatchedAttribute() throws DiffException {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("a"));
    this.output.handle(MATCH, new XMLAttribute("", "x", "y"));
    this.output.handle(MATCH, new XMLEndElement("a"));
    this.output.end();
    assertEquivalentToXML("<a x='y'/>");
    String xml = "<a " + NS_DECL + " x=\"y\"></a>";
    assertEquals(xml, this.w.toString());
  }

  @Test
  void testInsertedAttributeIsWritten() {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("a"));
    this.output.handle(INS, new XMLAttribute("", "x", "y"));
    this.output.handle(MATCH, new XMLEndElement("a"));
    String result = this.w.toString();
    assertTrue(result.contains("x=\"y\""), "Inserted attribute should be written");
  }

  @Test
  void testDeletedAttributeIsSkipped() {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("a"));
    this.output.handle(DEL, new XMLAttribute("", "x", "y"));
    this.output.handle(MATCH, new XMLEndElement("a"));
    String result = this.w.toString();
    assertFalse(result.contains("x=\"y\""), "Deleted attribute should not be written");
  }

  @Test
  void testMultipleAttributes() throws DiffException {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("a"));
    this.output.handle(MATCH, new XMLAttribute("", "x", "1"));
    this.output.handle(MATCH, new XMLAttribute("", "y", "2"));
    this.output.handle(MATCH, new XMLEndElement("a"));
    this.output.end();
    assertEquivalentToXML("<a x='1' y='2'/>");
  }

  // --- Text tokens ---

  @Test
  void testMatchedText() {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("p"));
    this.output.handle(MATCH, new WordToken("hello"));
    this.output.handle(MATCH, new XMLEndElement("p"));
    String result = this.w.toString();
    assertTrue(result.contains("hello"));
    assertFalse(result.contains("<ins"), "Matched text should not be wrapped in ins");
    assertFalse(result.contains("<del"), "Matched text should not be wrapped in del");
  }

  @Test
  void testInsertedTextWrappedInIns() {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("p"));
    this.output.handle(INS, new WordToken("added"));
    this.output.handle(MATCH, new XMLEndElement("p"));
    String result = this.w.toString();
    assertTrue(result.contains("<ins"));
    assertTrue(result.contains("added"));
    assertTrue(result.contains("</ins>"));
  }

  @Test
  void testDeletedTextWrappedInDel() {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("p"));
    this.output.handle(DEL, new WordToken("removed"));
    this.output.handle(MATCH, new XMLEndElement("p"));
    String result = this.w.toString();
    assertTrue(result.contains("<del"));
    assertTrue(result.contains("removed"));
    assertTrue(result.contains("</del>"));
  }

  @Test
  void testConsecutiveInsertsShareTag() {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("p"));
    this.output.handle(INS, new WordToken("hello"));
    this.output.handle(INS, new WordToken("world"));
    this.output.handle(MATCH, new XMLEndElement("p"));
    String result = this.w.toString();
    int insOpenCount = countOccurrences(result, "<ins");
    assertEquals(1, insOpenCount, "Consecutive inserts should share a single <ins> tag");
  }

  @Test
  void testConsecutiveDeletesShareTag() {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("p"));
    this.output.handle(DEL, new WordToken("hello"));
    this.output.handle(DEL, new WordToken("world"));
    this.output.handle(MATCH, new XMLEndElement("p"));
    String result = this.w.toString();
    int delOpenCount = countOccurrences(result, "<del");
    assertEquals(1, delOpenCount, "Consecutive deletes should share a single <del> tag");
  }

  @Test
  void testOperatorSwitchClosesAndOpensTag() {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("p"));
    this.output.handle(DEL, new WordToken("old"));
    this.output.handle(INS, new WordToken("new"));
    this.output.handle(MATCH, new XMLEndElement("p"));
    String result = this.w.toString();
    assertTrue(result.contains("<del"), "Should have del tag");
    assertTrue(result.contains("</del>"), "Del tag should be closed");
    assertTrue(result.contains("<ins"), "Should have ins tag");
    assertTrue(result.contains("</ins>"), "Ins tag should be closed");
    int delIdx = result.indexOf("</del>");
    int insIdx = result.indexOf("<ins");
    assertTrue(delIdx < insIdx, "Del should close before ins opens");
  }

  @Test
  void testMatchAfterEditClosesTag() {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("p"));
    this.output.handle(INS, new WordToken("added"));
    this.output.handle(MATCH, new WordToken("kept"));
    this.output.handle(MATCH, new XMLEndElement("p"));
    String result = this.w.toString();
    assertTrue(result.contains("</ins>"), "Ins tag should be closed before match");
    int insCloseIdx = result.indexOf("</ins>");
    int keptIdx = result.indexOf("kept");
    assertTrue(insCloseIdx < keptIdx, "Ins should close before matched text");
  }

  // --- Other token types ---

  @Test
  void testCommentToken() {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("a"));
    this.output.handle(MATCH, new XMLComment("a comment"));
    this.output.handle(MATCH, new XMLEndElement("a"));
    String result = this.w.toString();
    assertTrue(result.contains("<!--a comment-->"));
  }

  @Test
  void testProcessingInstructionToken() {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("a"));
    this.output.handle(MATCH, new XMLProcessingInstruction("target", "data"));
    this.output.handle(MATCH, new XMLEndElement("a"));
    String result = this.w.toString();
    assertTrue(result.contains("<?target"));
    assertTrue(result.contains("data"));
  }

  // --- XML declaration ---

  @Test
  void testWithXMLDeclaration() {
    this.output.setWriteXMLDeclaration(true);
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("a"));
    this.output.handle(MATCH, new XMLEndElement("a"));
    String result = this.w.toString();
    assertTrue(result.startsWith("<?xml"), "Should start with XML declaration");
  }

  @Test
  void testWithoutXMLDeclaration() {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("a"));
    this.output.handle(MATCH, new XMLEndElement("a"));
    String result = this.w.toString();
    assertFalse(result.startsWith("<?xml"), "Should not start with XML declaration");
  }

  // --- Namespace declaration ---

  @Test
  void testDiffNamespaceDeclaredOnFirstElement() {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("root"));
    this.output.handle(MATCH, new XMLStartElement("child"));
    this.output.handle(MATCH, new XMLEndElement("child"));
    this.output.handle(MATCH, new XMLEndElement("root"));
    String result = this.w.toString();
    int firstDeclIdx = result.indexOf(NS_DECL);
    assertTrue(firstDeclIdx >= 0, "Diff namespace should be declared");
    int secondDeclIdx = result.indexOf(NS_DECL, firstDeclIdx + NS_DECL.length());
    assertEquals(-1, secondDeclIdx, "Diff namespace should only be declared once");
  }

  // --- Element with edit operator followed by non-attribute token ---

  @Test
  void testEditElementClosedByNonMatchingOperator() {
    this.output.start();
    this.output.handle(INS, new XMLStartElement("p"));
    this.output.handle(INS, new WordToken("text"));
    this.output.handle(INS, new XMLEndElement("p"));
    String result = this.w.toString();
    assertTrue(result.contains("diff:insert=\"true\""));
    assertTrue(result.contains("<ins"));
    assertTrue(result.contains("text"));
  }

  @Test
  void testMixedOperatorsOnElements() {
    this.output.start();
    this.output.handle(MATCH, new XMLStartElement("root"));
    this.output.handle(DEL, new XMLStartElement("old"));
    this.output.handle(DEL, new XMLEndElement("old"));
    this.output.handle(INS, new XMLStartElement("new"));
    this.output.handle(INS, new XMLEndElement("new"));
    this.output.handle(MATCH, new XMLEndElement("root"));
    this.output.end();
    String result = this.w.toString();
    assertTrue(result.contains("diff:delete=\"true\""));
    assertTrue(result.contains("diff:insert=\"true\""));
  }

  // --- helpers ---

  private void assertEquivalentToXML(String xml) throws DiffException {
    Sequence exp = this.recorder.load(xml);
    Sequence seq = this.recorder.load(this.w.toString());
    try {
      assertEquals(exp, seq);
    } catch (AssertionError ex) {
      PrintWriter pw = new PrintWriter(System.err);
      seq.export(pw);
      pw.flush();
      System.err.println(this.w.toString());
      throw ex;
    }
  }

  private static int countOccurrences(String text, String substring) {
    int count = 0;
    int idx = 0;
    while ((idx = text.indexOf(substring, idx)) != -1) {
      count++;
      idx += substring.length();
    }
    return count;
  }

}
