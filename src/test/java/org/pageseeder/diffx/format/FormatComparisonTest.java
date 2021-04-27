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
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.Operations;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.core.DefaultXMLProcessor;
import org.pageseeder.diffx.handler.OperationHandler;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.test.Events;
import org.pageseeder.diffx.xml.PrefixMapping;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class FormatComparisonTest {

  @Test
  public void compareOutputExample1() throws IOException, DiffXException {
    String xml1 = "<body><p class='test'>Hello</p><ul><li>Monday evening</li><li>Tuesday night</li></ul></body>";
    String xml2 = "<body><p id='a'>Hello</p><ol><li>Monday</li><li>Thursday night</li></ol></body>";
    printDiffOutputs(xml1, xml2);
  }

  @Test
  public void compareOutputExample2() throws IOException, DiffXException {
    String xml1 = "<body><p id='1'>Other representations might be used by specialist equipment</p></body>";
    String xml2 = "<body><p id='2'>Another representation may be used by specialist equipment.</p></body>";
    printDiffOutputs(xml1, xml2);
  }

  @Test
  public void compareOutputExample3() throws IOException, DiffXException {
    String xml1 = "<body><a href='https//example.org' title='Example' class='link'/></body>";
    String xml2 = "<body><a href='https//example.com' download='' class='link'/></body>";
    printDiffOutputs(xml1, xml2);
  }

  @Test
  public void compareOutputExample4() throws IOException, DiffXException {
    String xml1 = "<body>An <i>important</i> date</body>";
    String xml2 = "<body>An <b>important</b> date</body>";
    printDiffOutputs(xml1, xml2);
  }

  @Test
  public void compareOutputExample5() throws IOException, DiffXException {
    String xml1 = "<body><svg xmlns='http://www.w3.org/2000/svg' version='1.1'><rect width='100%' height='100%' fill='red' /></svg></body>";
    String xml2 = "<body><svg xmlns='http://www.w3.org/2000/svg' width='300' height='200'><rect width='100%' height='100%' fill='blue' /></svg></body>";
    printDiffOutputs(xml1, xml2);
  }

  private static void printDiffOutputs(String xml1, String xml2) throws IOException, DiffXException {
    Sequence from = Events.loadSequence(xml1, TextGranularity.SPACE_WORD);
    Sequence to = Events.loadSequence(xml2, TextGranularity.SPACE_WORD);
    PrefixMapping mapping = PrefixMapping.merge(from.getPrefixMapping(), to.getPrefixMapping());
    List<Operation> operations = toOperations(from, to);
    printAllOutputs(operations, mapping);
  }

  private static List<Operation> toOperations(Sequence from, Sequence to) {
    OperationHandler handler = new OperationHandler();
    DefaultXMLProcessor processor = new DefaultXMLProcessor();
    processor.setCoalesce(true);
    processor.diff(to.tokens(), from.tokens(), handler);
    return handler.getOperations();
  }

  private static void printAllOutputs(List<Operation> operations, PrefixMapping namespaces) throws IOException {
    printSafeXMLFormatter(operations, namespaces);
    printSafeXMLOutput(operations, namespaces);
    printBasicXMLOutput(operations, namespaces);
    printConvenientXMLDiffOutput(operations, namespaces);
    printDefaultXMLOutput(operations, namespaces);
    printSmartXMLDiffOutput(operations, namespaces);
    printStrictXMLOutput(operations, namespaces);
  }

  private static void printSafeXMLFormatter(List<Operation> operations, PrefixMapping namespaces) throws IOException {
    StringWriter xml = new StringWriter();
    XMLDiffXFormatter formatter = new SafeXMLFormatter(xml);
    formatter.setWriteXMLDeclaration(false);
    formatter.declarePrefixMapping(new org.pageseeder.diffx.sequence.PrefixMapping(namespaces));
    Operations.format(operations, formatter);
    xml.flush();
    System.out.println(formatter.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printSafeXMLOutput(List<Operation> operations, PrefixMapping namespaces) throws IOException {
    StringWriter xml = new StringWriter();
    XMLDiffOutput output = new SafeXMLDiffOutput(xml);
    printXMLDiffOutput(operations, namespaces, output);
    System.out.println(output.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printStrictXMLOutput(List<Operation> operations, PrefixMapping namespaces) {
    StringWriter xml = new StringWriter();
    XMLDiffOutput output = new StrictXMLDiffOutput(xml);
    printXMLDiffOutput(operations, namespaces, output);
    System.out.println(output.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printConvenientXMLDiffOutput(List<Operation> operations, PrefixMapping namespaces) {
    StringWriter xml = new StringWriter();
    XMLDiffOutput output = new ConvenientXMLDiffOutput(xml);
    printXMLDiffOutput(operations, namespaces, output);
    System.out.println(output.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printBasicXMLOutput(List<Operation> operations, PrefixMapping namespaces) {
    StringWriter xml = new StringWriter();
    XMLDiffOutput output = new BasicXMLDiffOutput(xml);
    printXMLDiffOutput(operations, namespaces, output);
    System.out.println(output.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printDefaultXMLOutput(List<Operation> operations, PrefixMapping namespaces) {
    StringWriter xml = new StringWriter();
    DefaultXMDiffOutput output = new DefaultXMDiffOutput(xml);
    printXMLDiffOutput(operations, namespaces, output);
    System.out.println(output.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printSmartXMLDiffOutput(List<Operation> operations, PrefixMapping namespaces) {
    StringWriter xml = new StringWriter();
    XMLDiffOutput output = new SmartXMLDiffOutput(xml);
    printXMLDiffOutput(operations, namespaces, output);
    System.out.println(output.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printXMLDiffOutput(List<Operation> operations, PrefixMapping namespaces, XMLDiffOutput output) {
    output.setWriteXMLDeclaration(false);
    output.setPrefixMapping(namespaces);
    output.start();
    Operations.handle(operations, output);
    output.end();
  }

}
