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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.pageseeder.diffx.algorithm.DiffXAlgorithm;
import org.pageseeder.diffx.algorithm.GuanoAlgorithm;
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.format.SafeXMLFormatter;
import org.pageseeder.diffx.load.DOMRecorder;
import org.pageseeder.diffx.sequence.EventSequence;
import org.pageseeder.diffx.sequence.PrefixMapping;
import org.pageseeder.diffx.sequence.SequenceSlicer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
 * Note: the method signatures requires DOM arguments, include the <code>Saxon-DOM</code> jar
 * on your classpath to use this extension function with Saxon.
 *
 * @author Christophe Lauret
 * @version 18 May 2010
 */
public final class Extension {

  /**
   * Maps the DOM builder factory to use with the given DOM package.
   *
   * <p>This is because some XSLT processors will only accept certain types DOM objects.
   */
  private static final Map<String, String> BUILDERS = new Hashtable<>();
  static {
    BUILDERS.put("net.sf.saxon.dom", "net.sf.saxon.dom.DocumentBuilderFactoryImpl");
  }

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
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static Node diff(Node xml1, Node xml2, String whitespace, String granularity)
      throws DiffXException, IOException {

    // Get the config
    DiffXConfig config = toConfig(whitespace, granularity);

    // Get Sequences
    DOMRecorder loader = new DOMRecorder();
    loader.setConfig(config);
    EventSequence seq1 = loader.process(xml1);
    EventSequence seq2 = loader.process(xml2);
    if (seq1.size() == 0 && seq2.size() == 0) return null;

    // Start comparing
    StringWriter out = new StringWriter();
    diff(seq1, seq2, out, config);

    // Return a node
    try {
      String factory = getFactoryClass(xml1, xml2);
      return toNode(out.toString(), config, factory);
    } catch (Exception ex) {
      throw new DiffXException("Could not generate Node from Diff-X result", ex);
    }
  }

  // private helpers ------------------------------------------------------------------------------

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param seq1   The first XML reader to compare.
   * @param seq2   The first XML reader to compare.
   * @param out    Where the output goes.
   * @param config The DiffX configuration to use.
   *
   * @throws IOException    Should an I/O exception occur.
   */
  private static void diff(EventSequence seq1, EventSequence seq2, Writer out, DiffXConfig config)
      throws IOException {
    SafeXMLFormatter formatter = new SafeXMLFormatter(out);
    PrefixMapping mapping = new PrefixMapping();
    mapping.add(seq1.getPrefixMapping());
    mapping.add(seq2.getPrefixMapping());
    formatter.declarePrefixMapping(mapping);
    if (config != null) {
      formatter.setConfig(config);
    }
    SequenceSlicer slicer = new SequenceSlicer(seq1, seq2);
    slicer.slice();
    slicer.formatStart(formatter);
    DiffXAlgorithm df = new GuanoAlgorithm(seq1, seq2);
    df.process(formatter);
    slicer.formatEnd(formatter);
  }

  /**
   * Returns the Diff-X config for the specified argument as String.
   *
   * @param whitespace  A valid white space processing value.
   * @param granularity A valid text granularity value.
   *
   * @return the Diff-X config for the specified arguments as String.
   */
  private static DiffXConfig toConfig(String whitespace, String granularity) {
    WhiteSpaceProcessing ws = WhiteSpaceProcessing.valueOf(whitespace);
    TextGranularity tg = TextGranularity.valueOf(granularity);
    return new DiffXConfig(ws, tg);
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
  private static Node toNode(String xml, DiffXConfig config, String factory) throws IOException, ParserConfigurationException, SAXException {
    DocumentBuilderFactory dbFactory = factory == null ? DocumentBuilderFactory.newInstance()
        : DocumentBuilderFactory.newInstance(factory, Extension.class.getClassLoader());
    dbFactory.setNamespaceAware(config.isNamespaceAware());
    dbFactory.setExpandEntityReferences(true);
    dbFactory.setValidating(false);
    DocumentBuilder builder = dbFactory.newDocumentBuilder();
    Document document = builder.parse(new InputSource(new StringReader(xml)));
    return document.getDocumentElement();
  }

  /**
   * Returns the factory class to use based on the given <code>NodeList</code>s.
   *
   * @param xml1 the first node list.
   * @param xml2 the second node list.
   */
  private static String getFactoryClass(Node xml1, Node xml2) {
    Package pkg = xml1 != null ? xml1.getClass().getPackage()
                : xml2 != null ? xml2.getClass().getPackage()
                : null;
    return pkg == null ? null : BUILDERS.get(pkg.getName());
  }

}
