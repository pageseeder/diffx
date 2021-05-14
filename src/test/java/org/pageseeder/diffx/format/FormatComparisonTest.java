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

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.DiffException;
import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.Operations;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.core.DefaultXMLProcessor;
import org.pageseeder.diffx.handler.OperationsBuffer;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.test.TestTokens;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.xml.NamespaceSet;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class FormatComparisonTest {

  private static void printDiffOutputs(String xml1, String xml2) throws IOException, DiffException {
    Sequence from = TestTokens.loadSequence(xml1, TextGranularity.SPACE_WORD);
    Sequence to = TestTokens.loadSequence(xml2, TextGranularity.SPACE_WORD);
    NamespaceSet namespaces = NamespaceSet.merge(from.getNamespaces(), to.getNamespaces());
    List<Operation<XMLToken>> operations = toOperations(from, to);
    printAllOutputs(operations, namespaces);
  }

  private static List<Operation<XMLToken>> toOperations(Sequence from, Sequence to) {
    OperationsBuffer<XMLToken> handler = new OperationsBuffer<>();
    DefaultXMLProcessor processor = new DefaultXMLProcessor();
    processor.setCoalesce(true);
    processor.diff(to.tokens(), from.tokens(), handler);
    return handler.getOperations();
  }

  private static void printAllOutputs(List<Operation<XMLToken>> operations, NamespaceSet namespaces) throws IOException {
    printSafeXMLFormatter(operations, namespaces);
    printDefaultXMLOutput(operations, namespaces);
    printComprehensiveXMLOutput(operations, namespaces);
    printStrictXMLOutput(operations, namespaces);
    printReportXMLOutput(operations, namespaces);
  }

  private static void printSafeXMLFormatter(List<Operation<XMLToken>> operations, NamespaceSet namespaces) throws IOException {
    StringWriter xml = new StringWriter();
    XMLDiffXFormatter formatter = new SafeXMLFormatter(xml);
    formatter.setWriteXMLDeclaration(false);
    formatter.declarePrefixMapping(new org.pageseeder.diffx.sequence.PrefixMapping(namespaces));
    format(operations, formatter);
    xml.flush();
    System.out.println(formatter.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printDefaultXMLOutput(List<Operation<XMLToken>> operations, NamespaceSet namespaces) {
    StringWriter xml = new StringWriter();
    XMLDiffOutput output = new DefaultXMLDiffOutput(xml);
    printXMLDiffOutput(operations, namespaces, output);
    System.out.println(output.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printStrictXMLOutput(List<Operation<XMLToken>> operations, NamespaceSet namespaces) {
    StringWriter xml = new StringWriter();
    XMLDiffOutput output = new StrictXMLDiffOutput(xml);
    printXMLDiffOutput(operations, namespaces, output);
    System.out.println(output.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printComprehensiveXMLOutput(List<Operation<XMLToken>> operations, NamespaceSet namespaces) {
    StringWriter xml = new StringWriter();
    CompleteXMLDiffOutput output = new CompleteXMLDiffOutput(xml);
    printXMLDiffOutput(operations, namespaces, output);
    System.out.println(output.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printReportXMLOutput(List<Operation<XMLToken>> operations, NamespaceSet namespaces) {
    StringWriter xml = new StringWriter();
    XMLDiffReporter output = new XMLDiffReporter(xml);
    printXMLDiffOutput(operations, namespaces, output);
    System.out.println(output.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printXMLDiffOutput(List<Operation<XMLToken>> operations, NamespaceSet namespaces, XMLDiffOutput output) {
    output.setWriteXMLDeclaration(false);
    output.setNamespaces(namespaces);
    output.start();
    Operations.handle(operations, output);
    output.end();
  }

  private static void format(List<Operation<XMLToken>> operations, DiffXFormatter formatter) throws IOException {
    for (Operation<XMLToken> operation : operations) {
      switch (operation.operator()) {
        case MATCH:
          formatter.format(operation.token());
          break;
        case INS:
          formatter.insert(operation.token());
          break;
        case DEL:
          formatter.delete(operation.token());
          break;
        default:
      }
    }
  }

  @Test
  public void compareOutputExample1() throws IOException, DiffException {
    String xml1 = "<body><p class='test'>Hello</p><ul><li>Monday evening</li><li>Tuesday night</li></ul></body>";
    String xml2 = "<body><p id='a'>Hello</p><ol><li>Monday</li><li>Thursday night</li></ol></body>";
    printDiffOutputs(xml1, xml2);
  }

  @Test
  public void compareOutputExample2() throws IOException, DiffException {
    String xml1 = "<body><p id='1'>Other representations might be used by specialist equipment</p></body>";
    String xml2 = "<body><p id='2'>Another representation may be used by specialist equipment.</p></body>";
    printDiffOutputs(xml1, xml2);
  }

  @Test
  public void compareOutputExample3() throws IOException, DiffException {
    String xml1 = "<body><a href='https//example.org' title='Example' class='link'/></body>";
    String xml2 = "<body><a href='https//example.com' download='' class='link'/></body>";
    printDiffOutputs(xml1, xml2);
  }

  @Test
  public void compareOutputExample4() throws IOException, DiffException {
    String xml1 = "<body>An <i>important</i> date</body>";
    String xml2 = "<body>An <b>important</b> date</body>";
    printDiffOutputs(xml1, xml2);
  }

  @Test
  public void compareOutputExample5() throws IOException, DiffException {
    String xml1 = "<body><svg xmlns='http://www.w3.org/2000/svg' version='1.1'><rect width='100%' height='100%' fill='red' /></svg></body>";
    String xml2 = "<body><svg xmlns='http://www.w3.org/2000/svg' width='300' height='200'><rect width='100%' height='100%' fill='blue' /></svg></body>";
    printDiffOutputs(xml1, xml2);
  }

  @Test
  public void compareOutputExample6() throws IOException, DiffException {
    String xml1 = "<root xmlns='https://example.org' xmlns:net='https://example.net' net:plus='+'></root>";
    String xml2 = "<root xmlns='https://example.org' xmlns:net='https://example.net' net:minus='-'></root>";
    printDiffOutputs(xml1, xml2);
  }

  @Test
  public void compareOutputExample7() throws IOException, DiffException {
    String xml1 = "<html xml:lang='en'/>";
    String xml2 = "<html xml:lang='es'/>";
    printDiffOutputs(xml1, xml2);
  }

}
