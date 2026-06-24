/*
 * Copyright 2010-2015 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx;

import org.jspecify.annotations.Nullable;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.core.OptimisticXMLProcessor;
import org.pageseeder.diffx.format.DefaultXMLDiffOutput;
import org.pageseeder.diffx.load.DOMLoader;
import org.pageseeder.diffx.xml.NamespaceSet;
import org.pageseeder.diffx.xml.Sequence;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * To use Diff-X as an XSLT extension.
 *
 * <p>In Saxon, declare the namespace as:
 * <pre>{@code
 * <xsl:stylesheet version="2.0"
 *    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 *    xmlns:diffx="org.pageseeder.diffx.Extension"
 *    extension-element-prefixes="diffx"
 * >
 * }</pre>
 *
 * <p>Diff-X can be called within XSLT with:
 * <pre>{@code
 * <xsl:copy-of select="diffx:diff(/node1/to/compare, /node2/to/compare, 'IGNORE', 'TEXT')"/>
 * }</pre>
 *
 * <p>Note: the reflexive Java extension function mechanism used above requires
 * <b>Saxon-PE or Saxon-EE</b> (reflexive extensions were removed from Saxon-HE in version 9.8).
 * With Saxon-HE, register this function explicitly using the s9api
 * {@code Processor.registerExtensionFunction()} API.
 *
 * <p>The method signature requires DOM arguments; include the <code>Saxon-DOM</code> jar
 * on your classpath when using this extension function with Saxon.
 *
 * @author Christophe Lauret
 *
 * @version 1.3.4
 * @since 0.9.0
 */
public final class Extension {

  private Extension() {}

  /**
   * Maps the DOM builder factory to use with the given DOM package.
   *
   * <p>This is because some XSLT processors will only accept certain types DOM objects.
   */
  private static final Map<String, String> BUILDERS = Map.of(
      "net.sf.saxon.dom", "net.sf.saxon.dom.DocumentBuilderFactoryImpl"
  );

  /**
   * Compares the two specified <code>Node</code>s and returns the diff as a node.
   *
   * <p>Only the first node in the node list is sequenced.
   *
   * @param xml1        The first XML node to compare.
   * @param xml2        The second XML node to compare.
   * @param whitespace  The white space processing (a valid {@link WhiteSpaceProcessing} value).
   * @param granularity The text granularity (a valid {@link TextGranularity} value).
   *
   * @return The diff as a Node
   *
   * @throws DiffException Should a Diff exception occur.
   * @throws IOException   Should an I/O exception occur.
   */
  public static @Nullable Node diff(Node xml1, Node xml2, String whitespace, String granularity)
      throws DiffException, IOException {

    // Get the config
    DiffConfig config = toConfig(whitespace, granularity);

    // Get Sequences
    DOMLoader loader = new DOMLoader();
    loader.setConfig(config);
    Sequence seq1 = loader.load(xml1);
    Sequence seq2 = loader.load(xml2);
    if (seq1.isEmpty() && seq2.isEmpty()) return null;

    // Start comparing
    StringWriter out = new StringWriter();
    diff(seq1, seq2, out);

    // Return a node
    try {
      String factory = getFactoryClass(xml1, xml2);
      return toNode(out.toString(), config, factory);
    } catch (Exception ex) {
      throw new DiffException("Could not generate Node from Diff result", ex);
    }
  }

  // private helpers ------------------------------------------------------------------------------

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param seq1 The first XML reader to compare.
   * @param seq2 The first XML reader to compare.
   * @param out  Where the output goes.
   */
  private static void diff(Sequence seq1, Sequence seq2, Writer out) {
    DefaultXMLDiffOutput output = new DefaultXMLDiffOutput(out);
    NamespaceSet namespaces = NamespaceSet.merge(seq1.getNamespaces(), seq2.getNamespaces());
    output.setNamespaces(namespaces);
    OptimisticXMLProcessor processor = new OptimisticXMLProcessor();
    processor.diff(seq1.tokens(), seq2.tokens(), output);
  }

  /**
   * Returns the Diff-X config for the specified argument as String.
   *
   * @param whitespace  A valid white space processing value.
   * @param granularity A valid text granularity value.
   *
   * @return the Diff-X config for the specified arguments as String.
   */
  private static DiffConfig toConfig(String whitespace, String granularity) {
    WhiteSpaceProcessing ws = WhiteSpaceProcessing.valueOf(whitespace);
    TextGranularity tg = TextGranularity.valueOf(granularity);
    return new DiffConfig(ws, tg);
  }

  /**
   * Returns a node for the specified string value.
   *
   * @param xml     The XML to parse.
   * @param config  The DiffX configuration to use.
   * @param factory The class name of the DOM builder factory.
   *
   * @return the corresponding document node.
   */
  private static Node toNode(String xml, DiffConfig config, @Nullable String factory) throws IOException, ParserConfigurationException, SAXException {
    DocumentBuilderFactory dbFactory = newDocumentBuilderFactory(factory, config);
    DocumentBuilder builder = dbFactory.newDocumentBuilder();
    Document document = builder.parse(new InputSource(new StringReader(xml)));
    return document.getDocumentElement();
  }

  private static DocumentBuilderFactory newDocumentBuilderFactory(@Nullable String factory, DiffConfig config) throws ParserConfigurationException {
    DocumentBuilderFactory dbFactory;
    if (factory != null) {
      try {
        dbFactory = DocumentBuilderFactory.newInstance(factory, Extension.class.getClassLoader());
      } catch (FactoryConfigurationError ex) {
        dbFactory = DocumentBuilderFactory.newInstance();
      }
    } else {
      dbFactory = DocumentBuilderFactory.newInstance();
    }
    dbFactory.setNamespaceAware(config.isNamespaceAware());
    dbFactory.setValidating(false);
    dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    if (!config.allowDoctypeDeclaration()) {
      dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    }
    dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
    dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    dbFactory.setExpandEntityReferences(false);
    return dbFactory;
  }

  /**
   * Returns the factory class to use based on the given <code>NodeList</code>s.
   *
   * @param xml1 the first node list.
   * @param xml2 the second node list.
   */
  private static @Nullable String getFactoryClass(@Nullable Node xml1, @Nullable Node xml2) {
    Package pkg = xml1 != null ? xml1.getClass().getPackage()
        : xml2 != null ? xml2.getClass().getPackage()
        : null;
    return pkg == null ? null : BUILDERS.get(pkg.getName());
  }

}
