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

import net.sf.saxon.dom.NodeOverNodeInfo;
import net.sf.saxon.s9api.*;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link Extension} class as a Saxon XSLT extension function.
 *
 * <p>Saxon-HE does not support reflexive Java extension functions (removed in 9.8),
 * so these tests register the function explicitly using the s9api {@link ExtensionFunction} API.
 * The XSLT namespace and function name match the Javadoc documentation.</p>
 */
final class ExtensionXSLTTest {

  private static final QName DIFF_FUNCTION = new QName("org.pageseeder.diffx.Extension", "diff");

  @Test
  void diff_viaXSLT_identicalNodes() throws Exception {
    String xml = "<root><a><item>Hello</item></a><b><item>Hello</item></b></root>";
    String result = transformWithDiff(xml, "IGNORE", "TEXT");
    assertNotNull(result);
    assertTrue(result.contains("item"), "Result should contain the element name: " + result);
    assertTrue(result.contains("Hello"), "Result should contain the text: " + result);
  }

  @Test
  void diff_viaXSLT_differentText() throws Exception {
    String xml = "<root><a><item>Hello</item></a><b><item>World</item></b></root>";
    String result = transformWithDiff(xml, "IGNORE", "TEXT");
    assertNotNull(result);
    assertTrue(result.contains("item"), "Result should contain the element name: " + result);
  }

  @Test
  void diff_viaXSLT_differentStructure() throws Exception {
    String xml = "<root><a><p>text</p></a><b><q>text</q></b></root>";
    String result = transformWithDiff(xml, "IGNORE", "TEXT");
    assertNotNull(result);
  }

  @Test
  void diff_viaXSLT_wordGranularity() throws Exception {
    String xml = "<root><a><p>one two three</p></a><b><p>one three</p></b></root>";
    String result = transformWithDiff(xml, "IGNORE", "WORD");
    assertNotNull(result);
    assertTrue(result.contains("one"), "Result should contain shared text: " + result);
  }

  @Test
  void diff_viaXSLT_preserveWhitespace() throws Exception {
    String xml = "<root><a><p> hello </p></a><b><p>hello</p></b></root>";
    String result = transformWithDiff(xml, "PRESERVE", "TEXT");
    assertNotNull(result);
  }

  private static String transformWithDiff(String xml, String whitespace, String granularity) throws SaxonApiException {
    String xslt = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<xsl:stylesheet version=\"2.0\"\n"
        + "    xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n"
        + "    xmlns:diffx=\"org.pageseeder.diffx.Extension\">\n"
        + "  <xsl:output method=\"xml\" indent=\"no\"/>\n"
        + "  <xsl:template match=\"/\">\n"
        + "    <result>\n"
        + "      <xsl:copy-of select=\"diffx:diff(/root/a/*, /root/b/*, '" + whitespace + "', '" + granularity + "')\"/>\n"
        + "    </result>\n"
        + "  </xsl:template>\n"
        + "</xsl:stylesheet>";

    Processor processor = new Processor(false);
    processor.registerExtensionFunction(new DiffExtensionFunction(processor));

    XsltCompiler compiler = processor.newXsltCompiler();
    XsltExecutable executable = compiler.compile(new StreamSource(new StringReader(xslt)));
    Xslt30Transformer transformer = executable.load30();

    StringWriter writer = new StringWriter();
    Serializer serializer = processor.newSerializer(writer);
    transformer.transform(new StreamSource(new StringReader(xml)), serializer);
    return writer.toString();
  }

  /**
   * Registers {@link Extension#diff(Node, Node, String, String)} as a Saxon integrated
   * extension function, bridging XdmNode arguments to DOM Nodes.
   */
  private static class DiffExtensionFunction implements ExtensionFunction {

    private final Processor processor;

    DiffExtensionFunction(Processor processor) {
      this.processor = processor;
    }

    @Override
    public QName getName() {
      return DIFF_FUNCTION;
    }

    @Override
    public SequenceType getResultType() {
      return SequenceType.makeSequenceType(ItemType.ANY_NODE, OccurrenceIndicator.ZERO_OR_ONE);
    }

    @Override
    public SequenceType[] getArgumentTypes() {
      return new SequenceType[]{
          SequenceType.makeSequenceType(ItemType.ANY_NODE, OccurrenceIndicator.ONE),
          SequenceType.makeSequenceType(ItemType.ANY_NODE, OccurrenceIndicator.ONE),
          SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE),
          SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE)
      };
    }

    @Override
    public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
      try {
        Node node1 = NodeOverNodeInfo.wrap(((XdmNode) arguments[0].itemAt(0)).getUnderlyingNode());
        Node node2 = NodeOverNodeInfo.wrap(((XdmNode) arguments[1].itemAt(0)).getUnderlyingNode());
        String whitespace = arguments[2].itemAt(0).getStringValue();
        String granularity = arguments[3].itemAt(0).getStringValue();

        Node result = Extension.diff(node1, node2, whitespace, granularity);
        if (result == null) return XdmEmptySequence.getInstance();

        DocumentBuilder builder = processor.newDocumentBuilder();
        return builder.wrap(result);
      } catch (Exception ex) {
        throw new SaxonApiException(ex);
      }
    }
  }

}
