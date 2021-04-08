package org.pageseeder.diffx.format;

import org.junit.Test;
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.Operations;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.core.DefaultXMLProcessor;
import org.pageseeder.diffx.core.DiffProcessor;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.handler.OperationHandler;
import org.pageseeder.diffx.test.Events;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class FormatComparisonTest {

  @Test
  public void compareFormatsExample1() throws IOException, DiffXException {
    String xml1 = "<body><p class='test'>Hello</p><ul><li>Monday evening</li><li>Tuesday night</li></ul></body>";
    String xml2 = "<body><p id='a'>Hello</p><ol><li>Monday</li><li>Thursday night</li></ol></body>";
    List<Operation> operations = toOperations(xml1, xml2);
    printAll(operations);
  }

  @Test
  public void compareFormatsExample2() throws IOException, DiffXException {
    String xml1 = "<body><p id='1'>Other representations might be used by specialist equipment</p></body>";
    String xml2 = "<body><p id='2'>Another representation may be used by specialist equipment.</p></body>";
    List<Operation> operations = toOperations(xml1, xml2);
    printAll(operations);
  }

  private static List<Operation> toOperations(String xml1, String xml2) throws IOException, DiffXException {
    List<? extends DiffXEvent> from = Events.recordXMLEvents(xml1, TextGranularity.SPACE_WORD);
    List<? extends DiffXEvent> to = Events.recordXMLEvents(xml2, TextGranularity.SPACE_WORD);
    OperationHandler handler = new OperationHandler();
    DefaultXMLProcessor processor = new DefaultXMLProcessor();
    processor.setCoalesce(true);
    processor.diff(to, from, handler);
    return handler.getOperations();
  }

  private static void printAll(List<Operation> operations) throws IOException {
    printBasicXMLFormatter(operations);
    printConvenientXMLFormatter(operations);
    printSafeXMLFormatter(operations);
    printSmartXMLFormatter(operations);
    printStrictXMLFormatter(operations);
  }

  private static void printBasicXMLFormatter(List<Operation> operations) throws IOException {
    StringWriter xml = new StringWriter();
    XMLDiffXFormatter formatter = new BasicXMLFormatter(xml);
    Operations.format(operations, formatter);
    xml.flush();
    System.out.println(formatter.getClass().getSimpleName());
    System.out.println(xml.toString());
  }

  private static void printConvenientXMLFormatter(List<Operation> operations) throws IOException {
    StringWriter xml = new StringWriter();
    XMLDiffXFormatter formatter = new ConvenientXMLFormatter(xml);
    Operations.format(operations, formatter);
    xml.flush();
    System.out.println(formatter.getClass().getSimpleName());
    System.out.println(xml.toString());
  }

  private static void printSafeXMLFormatter(List<Operation> operations) throws IOException {
    StringWriter xml = new StringWriter();
    XMLDiffXFormatter formatter = new SafeXMLFormatter(xml);
    Operations.format(operations, formatter);
    xml.flush();
    System.out.println(formatter.getClass().getSimpleName());
    System.out.println(xml.toString());
  }

  private static void printSmartXMLFormatter(List<Operation> operations) throws IOException {
    StringWriter xml = new StringWriter();
    XMLDiffXFormatter formatter = new SmartXMLFormatter(xml);
    Operations.format(operations, formatter);
    xml.flush();
    System.out.println(formatter.getClass().getSimpleName());
    System.out.println(xml.toString());
  }

  private static void printStrictXMLFormatter(List<Operation> operations) throws IOException {
    StringWriter xml = new StringWriter();
    XMLDiffXFormatter formatter = new StrictXMLFormatter(xml);
    Operations.format(operations, formatter);
    xml.flush();
    System.out.println(formatter.getClass().getSimpleName());
    System.out.println(xml.toString());
  }

}
