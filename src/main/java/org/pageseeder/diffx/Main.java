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

import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.core.DefaultXMLProcessor;
import org.pageseeder.diffx.core.DiffProcessor;
import org.pageseeder.diffx.core.OptimisticXMLProcessor;
import org.pageseeder.diffx.core.TextOnlyProcessor;
import org.pageseeder.diffx.format.*;
import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.load.*;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.util.CommandLine;
import org.pageseeder.diffx.xml.NamespaceSet;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Utility class to centralise the access to this API from the command line.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class Main {

  /**
   * Prevents creation of instances.
   */
  private Main() {
  }

  // equivalent methods -------------------------------------------------------------------

  /**
   * Returns <code>true</code> if the two specified files are XML equivalent by looking at the
   * sequence SAX events reported an XML reader.
   *
   * @param xml1 The first XML stream to compare.
   * @param xml2 The first XML stream to compare.
   *
   * @return <code>true</code> If the XML are considered equivalent;
   * <code>false</code> otherwise.
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static boolean equivalent(File xml1, File xml2)
      throws DiffXException, IOException {
    Loader loader = new SAXLoader();
    Sequence seq0 = loader.load(xml1);
    Sequence seq1 = loader.load(xml2);
    return seq0.equals(seq1);
  }

  /**
   * Returns <code>true</code> if the two specified input streams are equivalent by looking at the
   * sequence SAX events reported an XML reader.
   *
   * @param xml1 The first XML stream to compare.
   * @param xml2 The first XML stream to compare.
   *
   * @return <code>true</code> If the XML are considered equivalent;
   * <code>false</code> otherwise.
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static boolean equivalent(InputStream xml1, InputStream xml2)
      throws DiffXException, IOException {
    SAXLoader loader = new SAXLoader();
    Sequence seq0 = loader.load(new InputSource(xml1));
    Sequence seq1 = loader.load(new InputSource(xml2));
    return seq0.equals(seq1);
  }

  /**
   * Returns <code>true</code> if the two specified readers are equivalent by looking at the
   * sequence SAX events reported an XML reader.
   *
   * @param xml1 The first XML stream to compare.
   * @param xml2 The first XML stream to compare.
   *
   * @return <code>true</code> If the XML are considered equivalent;
   * <code>false</code> otherwise.
   * @throws DiffXException If a DiffX exception is reported by the loaders.
   * @throws IOException    Should an I/O exception occur.
   */
  public static boolean equivalent(Reader xml1, Reader xml2)
      throws DiffXException, IOException {
    SAXLoader loader = new SAXLoader();
    Sequence seq0 = loader.load(new InputSource(xml1));
    Sequence seq1 = loader.load(new InputSource(xml2));
    return seq0.equals(seq1);
  }

  // diff methods -------------------------------------------------------------------------

  /**
   * Compares the two specified XML nodes and prints the diff onto the given writer.
   *
   * @param xml1   The first XML node to compare.
   * @param xml2   The second XML node to compare.
   * @param out    Where the output goes.
   * @param config The DiffX configuration to use.
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static void diff(Node xml1, Node xml2, Writer out, DiffXConfig config)
      throws DiffXException, IOException {
    // records the tokens from the XML
    DOMLoader loader = new DOMLoader();
    if (config != null) {
      loader.setConfig(config.toDiffConfig());
    }
    Sequence seq1 = loader.load(xml1);
    Sequence seq2 = loader.load(xml2);
    // start slicing
    diff(seq1, seq2, out);
  }

  /**
   * Compares the two specified <code>NodeList</code>s and prints the diff onto the given writer.
   *
   * <p>Only the first node in the node list is sequenced.
   *
   * @param xml1   The first XML node list to compare.
   * @param xml2   The second XML node list to compare.
   * @param out    Where the output goes.
   * @param config The DiffX configuration to use.
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static void diff(NodeList xml1, NodeList xml2, Writer out, DiffXConfig config)
      throws DiffXException, IOException {
    // records the tokens from the XML
    DOMLoader loader = new DOMLoader();
    if (config != null) {
      loader.setConfig(config.toDiffConfig());
    }
    Sequence seq1 = loader.load(xml1);
    Sequence seq2 = loader.load(xml2);
    // start slicing
    diff(seq1, seq2, out);
  }

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param xml1   The first XML reader to compare.
   * @param xml2   The first XML reader to compare.
   * @param out    Where the output goes.
   * @param config The DiffX configuration to use.
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static void diff(Reader xml1, Reader xml2, Writer out, DiffXConfig config)
      throws DiffXException, IOException {
    // records the tokens from the XML
    SAXLoader loader = new SAXLoader();
    if (config != null) {
      loader.setConfig(config.toDiffConfig());
    }
    Sequence seq1 = loader.load(new InputSource(xml1));
    Sequence seq2 = loader.load(new InputSource(xml2));
    // start slicing
    diff(seq1, seq2, out);
  }

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param xml1 The first XML reader to compare.
   * @param xml2 The first XML reader to compare.
   * @param out  Where the output goes
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static void diff(Reader xml1, Reader xml2, Writer out)
      throws DiffXException, IOException {
    // records the tokens from the XML
    SAXLoader loader = new SAXLoader();
    Sequence seq1 = loader.load(new InputSource(xml1));
    Sequence seq2 = loader.load(new InputSource(xml2));
    // start slicing
    diff(seq1, seq2, out);
  }

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param xml1 The first XML input stream to compare.
   * @param xml2 The first XML input stream to compare.
   * @param out  Where the output goes
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static void diff(InputStream xml1, InputStream xml2, OutputStream out)
      throws DiffXException, IOException {
    // records the tokens from the XML
    SAXLoader loader = new SAXLoader();
    Sequence seq1 = loader.load(new InputSource(xml1));
    Sequence seq2 = loader.load(new InputSource(xml2));
    diff(seq1, seq2, new OutputStreamWriter(out));
  }

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
    DefaultXMLProcessor processor = new DefaultXMLProcessor();
    processor.diff(seq1.tokens(), seq2.tokens(), output);
  }

  // command line -------------------------------------------------------------------------

  /**
   * Main entry point from the command line.
   *
   * @param args The command-line arguments
   */
  public static void main(String[] args) {
    // TODO: better command-line interface
    if (args.length < 2) {
      usage();
      return;
    }
    try {
      boolean profile = CommandLine.hasSwitch("-profile", args);
      boolean quiet = CommandLine.hasSwitch("-quiet", args);

      // get the files
      File xml1 = new File(args[args.length - 2]);
      File xml2 = new File(args[args.length - 1]);

      // loading
      long t0 = System.currentTimeMillis();
      Loader loader = getRecorder(args);
      if (loader == null) return;
      Sequence seq1 = loader.load(xml1);
      Sequence seq2 = loader.load(xml2);
      long t1 = System.currentTimeMillis();
      if (profile) {
        System.err.println("Loaded files in " + (t1 - t0) + "ms");
      }

      // get the config
      DiffXConfig config = new DiffXConfig();
      config.setGranularity(getTextGranularity(args));
      config.setWhiteSpaceProcessing(getWhiteSpaceProcessing(args));
      if (!quiet) {
        System.err.println("Whitespace processing: " + getTextGranularity(args) + " " + getWhiteSpaceProcessing(args));
      }

      // get and setup the formatter
      Writer out = new OutputStreamWriter(getOutput(args), StandardCharsets.UTF_8);
      DiffHandler output = getOutputFormat(args, out);
      if (output == null) return;
      if (output instanceof XMLDiffOutput) {
        NamespaceSet namespaces = NamespaceSet.merge(seq1.getNamespaces(), seq2.getNamespaces());
        ((XMLDiffOutput) output).setNamespaces(namespaces);
      }

      // start algorithm
      if (!quiet) {
        System.err.println("Matrix: " + seq1.size() + "x" + seq2.size());
      }
      DiffProcessor processor = getProcessor(args);
      if (processor == null) return;
      processor.diff(seq1.tokens(), seq2.tokens(), output);

      long t2 = System.currentTimeMillis();
      if (profile) {
        System.err.println("Executed algorithm files in " + (t2 - t1) + "ms");
      }

    } catch (Throwable ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Displays the usage on the System.err console
   */
  public static void usage() {
    System.err.println("Compare the SAX events returned by two XML files.");
    System.err.println("usage:");
    System.err.println("  Main [options] xml_file1 xml_file2");
    System.err.println("where:");
    System.err.println("  xml_file1 = Path to the new XML file");
    System.err.println("  xml_file2 = Path to the old XML file");
    System.err.println("options:");
    System.err.println("  -profile        Display profiling info");
    System.err.println("  -o [output]     The output file");
    System.err.println("  -l [loader]     Choose a specific loader");
    System.err.println("                   sax* | dom | stream | stax | text");
    System.err.println("  -p [processor]  Choose a specific algorithm");
    System.err.println("                   optimistic* | xml | text");
    System.err.println("  -f [format]     Choose a specific formatter");
    System.err.println("                   default* | complete | strict | report");
    System.err.println("  -w [whitespace] Define whitespace processing");
    System.err.println("                   preserve* | compare | ignore");
    System.err.println("  -g [granul]     Define text diffing granularity");
    System.err.println("                   word* | text | character");
    System.err.println(" * indicates option used by default.");
    System.exit(1);
  }

  /**
   * @param args The command line arguments.
   *
   * @return The loader to use.
   */
  private static Loader getRecorder(String[] args) {
    String loaderArg = CommandLine.getParameter("-l", args);
    if (loaderArg == null || "sax".equals(loaderArg))
      return new SAXLoader();
    if ("dom".equals(loaderArg))
      return new DOMLoader();
    if ("text".equals(loaderArg))
      return new LineLoader();
    if ("stream".equals(loaderArg))
      return new XMLStreamLoader();
    if ("stax".equals(loaderArg))
      return new XMLEventLoader();
    usage();
    return null;
  }

  /**
   * @param args The command line arguments.
   *
   * @return The output to use.
   * @throws FileNotFoundException If the file does not exist.
   */
  private static OutputStream getOutput(String[] args) throws FileNotFoundException {
    String outArg = CommandLine.getParameter("-o", args);
    if (outArg == null)
      return System.out;
    return new BufferedOutputStream(new FileOutputStream(outArg));
  }

  /**
   * @param args The command line arguments.
   *
   * @return The algorithm to use.
   */
  private static DiffProcessor getProcessor(String[] args) {
    String loaderArg = CommandLine.getParameter("-p", args);
    if (loaderArg == null || "optimistic".equals(loaderArg))
      return new DefaultXMLProcessor();
    if ("xml".equals(loaderArg))
      return new OptimisticXMLProcessor();
    if ("text".equals(loaderArg))
      return new TextOnlyProcessor();
    usage();
    return null;
  }

  /**
   * @param args The command line arguments.
   * @param out  The writer to use.
   *
   * @return The formatter to use.
   */
  private static DiffHandler getOutputFormat(String[] args, Writer out) {
    String formatArg = CommandLine.getParameter("-f", args);
    if (formatArg == null || "default".equals(formatArg))
      return new DefaultXMLDiffOutput(out);
    if ("complete".equals(formatArg))
      return new CompleteXMLDiffOutput(out);
    if ("strict".equals(formatArg))
      return new StrictXMLDiffOutput(out);
    if ("report".equals(formatArg))
      return new XMLDiffReporter(out);
    usage();
    return null;
  }

  /**
   * @param args The command line arguments.
   *
   * @return The formatter to use.
   */
  private static WhiteSpaceProcessing getWhiteSpaceProcessing(String[] args) {
    String formatArg = CommandLine.getParameter("-w", args);
    if (formatArg == null || "preserve".equals(formatArg))
      return WhiteSpaceProcessing.PRESERVE;
    if ("compare".equals(formatArg))
      return WhiteSpaceProcessing.COMPARE;
    if ("ignore".equals(formatArg))
      return WhiteSpaceProcessing.IGNORE;
    usage();
    return null;
  }

  /**
   * @param args The command line arguments.
   *
   * @return The formatter to use.
   */
  private static TextGranularity getTextGranularity(String[] args) {
    String formatArg = CommandLine.getParameter("-g", args);
    if (formatArg == null || "word".equals(formatArg))
      return TextGranularity.SPACE_WORD;
    if ("text".equals(formatArg))
      return TextGranularity.TEXT;
    if ("character".equals(formatArg))
      return TextGranularity.CHARACTER;
    usage();
    return null;
  }
}
