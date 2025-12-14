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

import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.core.DefaultXMLProcessor;
import org.pageseeder.diffx.core.DiffProcessor;
import org.pageseeder.diffx.core.OptimisticXMLProcessor;
import org.pageseeder.diffx.core.TextOnlyProcessor;
import org.pageseeder.diffx.format.*;
import org.pageseeder.diffx.load.*;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.util.CommandLine;
import org.pageseeder.diffx.xml.NamespaceSet;
import org.pageseeder.diffx.xml.Sequence;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class to centralise the access to this API from the command line.
 *
 * @author Christophe Lauret
 * @version 1.0.1
 * @version 0.9.0
 */
public final class Main {

  /**
   * Prevents creation of instances.
   */
  private Main() {
  }

  /**
   * Returns <code>true</code> if the two specified files are XML equivalent by looking at the
   * sequence SAX events reported an XML reader.
   *
   * @param xmlA The first XML stream to compare.
   * @param xmlB The first XML stream to compare.
   *
   * @return <code>true</code> If the XML are considered equivalent;
   * <code>false</code> otherwise.
   * @throws DiffException Should a Diff-X exception occur.
   * @throws IOException   Should an I/O exception occur.
   */
  public static boolean equivalent(File xmlA, File xmlB) throws DiffException, IOException {
    XMLLoader loader = new SAXLoader();
    Sequence seq0 = loader.load(xmlA);
    Sequence seq1 = loader.load(xmlB);
    return seq0.equals(seq1);
  }

  /**
   * Returns <code>true</code> if the two specified input streams are equivalent by looking at the
   * sequence SAX events reported an XML reader.
   *
   * @param xmlA The first XML stream to compare.
   * @param xmlB The first XML stream to compare.
   *
   * @return <code>true</code> If the XML are considered equivalent;
   * <code>false</code> otherwise.
   * @throws DiffException Should a Diff-X exception occur.
   * @throws IOException   Should an I/O exception occur.
   */
  public static boolean equivalent(InputStream xmlA, InputStream xmlB) throws DiffException, IOException {
    SAXLoader loader = new SAXLoader();
    Sequence seq0 = loader.load(new InputSource(xmlA));
    Sequence seq1 = loader.load(new InputSource(xmlB));
    return seq0.equals(seq1);
  }

  /**
   * Returns <code>true</code> if the two specified readers are equivalent by looking at the
   * sequence SAX events reported an XML reader.
   *
   * @param xmlA The first XML stream to compare.
   * @param xmlB The first XML stream to compare.
   *
   * @return <code>true</code> If the XML are considered equivalent;
   * <code>false</code> otherwise.
   * @throws DiffException If a DiffX exception is reported by the loaders.
   * @throws IOException   Should an I/O exception occur.
   */
  public static boolean equivalent(Reader xmlA, Reader xmlB) throws DiffException, IOException {
    SAXLoader loader = new SAXLoader();
    Sequence seq0 = loader.load(new InputSource(xmlA));
    Sequence seq1 = loader.load(new InputSource(xmlB));
    return seq0.equals(seq1);
  }

  /**
   * Compares the two specified XML nodes and prints the diff onto the given writer.
   *
   * @param xmlA   The first XML node to compare.
   * @param xmlB   The second XML node to compare.
   * @param out    Where the output goes.
   * @param config The DiffX configuration to use.
   *
   * @throws DiffException Should a Diff-X exception occur.
   * @throws IOException   Should an I/O exception occur.
   */
  public static void diff(Node xmlA, Node xmlB, Writer out, DiffConfig config)
      throws DiffException, IOException {
    // records the tokens from the XML
    DOMLoader loader = new DOMLoader();
    if (config != null) {
      loader.setConfig(config);
    }
    Sequence seq1 = loader.load(xmlA);
    Sequence seq2 = loader.load(xmlB);
    // start slicing
    diff(seq1, seq2, out);
  }

  /**
   * Compares the two specified <code>NodeList</code>s and prints the diff onto the given writer.
   *
   * <p>Only the first node in the node list is sequenced.
   *
   * @param xmlA   The first XML node list to compare.
   * @param xmlB   The second XML node list to compare.
   * @param out    Where the output goes.
   * @param config The DiffX configuration to use.
   *
   * @throws DiffException Should a Diff-X exception occur.
   * @throws IOException   Should an I/O exception occur.
   */
  public static void diff(NodeList xmlA, NodeList xmlB, Writer out, DiffConfig config)
      throws DiffException, IOException {
    // records the tokens from the XML
    DOMLoader loader = new DOMLoader();
    if (config != null) {
      loader.setConfig(config);
    }
    Sequence seq1 = loader.load(xmlA);
    Sequence seq2 = loader.load(xmlB);
    // start slicing
    diff(seq1, seq2, out);
  }

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param xmlA   The first XML reader to compare.
   * @param xmlB   The first XML reader to compare.
   * @param out    Where the output goes.
   * @param config The DiffX configuration to use.
   *
   * @throws DiffException Should a Diff-X exception occur.
   * @throws IOException   Should an I/O exception occur.
   */
  public static void diff(Reader xmlA, Reader xmlB, Writer out, DiffConfig config)
      throws DiffException, IOException {
    // records the tokens from the XML
    SAXLoader loader = new SAXLoader();
    if (config != null) {
      loader.setConfig(config);
    }
    Sequence seq1 = loader.load(new InputSource(xmlA));
    Sequence seq2 = loader.load(new InputSource(xmlB));
    // start slicing
    diff(seq1, seq2, out);
  }

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param xmlA The first XML reader to compare.
   * @param xmlB The first XML reader to compare.
   * @param out  Where the output goes
   *
   * @throws DiffException Should a Diff-X exception occur.
   * @throws IOException   Should an I/O exception occur.
   */
  public static void diff(Reader xmlA, Reader xmlB, Writer out) throws DiffException, IOException {
    // records the tokens from the XML
    SAXLoader loader = new SAXLoader();
    Sequence seq1 = loader.load(new InputSource(xmlA));
    Sequence seq2 = loader.load(new InputSource(xmlB));
    // start slicing
    diff(seq1, seq2, out);
  }

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param xmlA The first XML input stream to compare.
   * @param xmlB The first XML input stream to compare.
   * @param out  Where the output goes
   *
   * @throws DiffException Should a Diff-X exception occur.
   * @throws IOException   Should an I/O exception occur.
   */
  public static void diff(InputStream xmlA, InputStream xmlB, OutputStream out)
      throws DiffException, IOException {
    // records the tokens from the XML
    SAXLoader loader = new SAXLoader();
    Sequence seq1 = loader.load(new InputSource(xmlA));
    Sequence seq2 = loader.load(new InputSource(xmlB));
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
  @SuppressWarnings("java:S106")
  public static void main(String[] args) {
    if (args.length < 2) {
      usage();
      return;
    }
    try {
      boolean profile = CommandLine.hasSwitch("-profile", args);
      boolean quiet = CommandLine.hasSwitch("-quiet", args);
      boolean allowDoctype = CommandLine.hasSwitch("-allowdoctype", args);

      // get the files
      File xmlA = toFile(args[args.length - 2]);
      File xmlB = toFile(args[args.length - 1]);

      // loading
      // TODO Use nanotime for profiling
      long t0 = System.currentTimeMillis();
      XMLLoader loader = getLoader(args);
      if (loader == null) return;
      Sequence seq1 = loader.load(xmlA);
      Sequence seq2 = loader.load(xmlB);
      long t1 = System.currentTimeMillis();
      if (profile) {
        System.err.println("Loaded files in " + (t1 - t0) + "ms");
      }

      // get the config
      DiffConfig config = DiffConfig.getDefault()
          .granularity(getTextGranularity(args))
          .whitespace(getWhiteSpaceProcessing(args))
          .allowDoctypeDeclaration(allowDoctype);
      if (!quiet) {
        System.err.println("Whitespace processing: " + config.granularity() + " " + config.whitespace());
      }

      // get and set up the formatter
      Writer out = new OutputStreamWriter(getOutput(args), StandardCharsets.UTF_8);
      XMLDiffOutput output = getOutputFormat(args, out);
      if (output == null) return;
      NamespaceSet namespaces = NamespaceSet.merge(seq1.getNamespaces(), seq2.getNamespaces());
      output.setNamespaces(namespaces);

      // start algorithm
      if (!quiet) {
        System.err.println("Matrix: " + seq1.size() + "x" + seq2.size());
      }
      DiffProcessor<XMLToken> processor = getProcessor(args);
      if (processor == null) return;
      processor.diff(seq1.tokens(), seq2.tokens(), output);

      long t2 = System.currentTimeMillis();
      if (profile) {
        System.err.println("Executed algorithm files in " + (t2 - t1) + "ms");
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Displays the usage on the <code>System.err</code> console
   */
  @SuppressWarnings("SpellCheckingInspection")
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
    System.err.println("  -p [processor]  Choose a specific processor");
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
  private static XMLLoader getLoader(String[] args) {
    String loaderArg = CommandLine.getParameter("-l", args);
    if (loaderArg == null || "sax".equals(loaderArg))
      return new SAXLoader();
    if ("dom".equals(loaderArg))
      return new DOMLoader();
//    if ("text".equals(loaderArg))
//      return new LineLoader();
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
  private static OutputStream getOutput(String[] args) throws IOException {
    String outArg = CommandLine.getParameter("-o", args);
    if (outArg == null)
      return System.out;
    return new BufferedOutputStream(Files.newOutputStream(Paths.get(outArg)));
  }

  /**
   * @param args The command line arguments.
   *
   * @return The algorithm to use.
   */
  private static DiffProcessor<XMLToken> getProcessor(String[] args) {
    String loaderArg = CommandLine.getParameter("-p", args);
    if (loaderArg == null || "optimistic".equals(loaderArg))
      return new DefaultXMLProcessor();
    if ("xml".equals(loaderArg))
      return new OptimisticXMLProcessor();
    if ("text".equals(loaderArg))
      return new TextOnlyProcessor<>();
    usage();
    return null;
  }

  /**
   * @param args The command line arguments.
   * @param out  The writer to use.
   *
   * @return The formatter to use.
   */
  private static XMLDiffOutput getOutputFormat(String[] args, Writer out) {
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

  private static File toFile(String arg) {
    try {
      File f = new File(arg).getCanonicalFile();
      if (!f.exists() || f.isDirectory() || !f.canRead())
        throw new IllegalArgumentException("File does not exist, cannot be read or is a directory");
      return f;
    } catch (IOException ex) {
      throw new IllegalArgumentException("Illegal file argument", ex);
    }
  }
}
