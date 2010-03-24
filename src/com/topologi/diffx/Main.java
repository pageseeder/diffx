package com.topologi.diffx;

/* ============================================================================
 * ARTISTIC LICENCE
 * 
 * Preamble
 * 
 * The intent of this document is to state the conditions under which a Package
 * may be copied, such that the Copyright Holder maintains some semblance of 
 * artistic control over the development of the package, while giving the users
 * of the package the right to use and distribute the Package in a more-or-less
 * customary fashion, plus the right to make reasonable modifications.
 *
 * Definitions:
 *  - "Package" refers to the collection of files distributed by the Copyright 
 *    Holder, and derivatives of that collection of files created through 
 *    textual modification.
 *  - "Standard Version" refers to such a Package if it has not been modified, 
 *    or has been modified in accordance with the wishes of the Copyright 
 *    Holder.
 *  - "Copyright Holder" is whoever is named in the copyright or copyrights 
 *    for the package.
 *  - "You" is you, if you're thinking about copying or distributing this 
 *    Package.
 *  - "Reasonable copying fee" is whatever you can justify on the basis of 
 *    media cost, duplication charges, time of people involved, and so on. 
 *    (You will not be required to justify it to the Copyright Holder, but only 
 *    to the computing community at large as a market that must bear the fee.)
 *  - "Freely Available" means that no fee is charged for the item itself, 
 *    though there may be fees involved in handling the item. It also means 
 *    that recipients of the item may redistribute it under the same conditions
 *    they received it.
 *
 * 1. You may make and give away verbatim copies of the source form of the 
 *    Standard Version of this Package without restriction, provided that you 
 *    duplicate all of the original copyright notices and associated 
 *    disclaimers.
 *
 * 2. You may apply bug fixes, portability fixes and other modifications 
 *    derived from the Public Domain or from the Copyright Holder. A Package 
 *    modified in such a way shall still be considered the Standard Version.
 *
 * 3. You may otherwise modify your copy of this Package in any way, provided 
 *    that you insert a prominent notice in each changed file stating how and 
 *    when you changed that file, and provided that you do at least ONE of the 
 *    following:
 * 
 *    a) place your modifications in the Public Domain or otherwise make them 
 *       Freely Available, such as by posting said modifications to Usenet or 
 *       an equivalent medium, or placing the modifications on a major archive 
 *       site such as ftp.uu.net, or by allowing the Copyright Holder to 
 *       include your modifications in the Standard Version of the Package.
 * 
 *    b) use the modified Package only within your corporation or organization.
 *
 *    c) rename any non-standard executables so the names do not conflict with 
 *       standard executables, which must also be provided, and provide a 
 *       separate manual page for each non-standard executable that clearly 
 *       documents how it differs from the Standard Version.
 * 
 *    d) make other distribution arrangements with the Copyright Holder.
 *
 * 4. You may distribute the programs of this Package in object code or 
 *    executable form, provided that you do at least ONE of the following:
 * 
 *    a) distribute a Standard Version of the executables and library files, 
 *       together with instructions (in the manual page or equivalent) on where
 *       to get the Standard Version.
 *
 *    b) accompany the distribution with the machine-readable source of the 
 *       Package with your modifications.
 * 
 *    c) accompany any non-standard executables with their corresponding 
 *       Standard Version executables, giving the non-standard executables 
 *       non-standard names, and clearly documenting the differences in manual 
 *       pages (or equivalent), together with instructions on where to get 
 *       the Standard Version.
 *
 *    d) make other distribution arrangements with the Copyright Holder.
 *
 * 5. You may charge a reasonable copying fee for any distribution of this 
 *    Package. You may charge any fee you choose for support of this Package. 
 *    You may not charge a fee for this Package itself. However, you may 
 *    distribute this Package in aggregate with other (possibly commercial) 
 *    programs as part of a larger (possibly commercial) software distribution 
 *    provided that you do not advertise this Package as a product of your own.
 *
 * 6. The scripts and library files supplied as input to or produced as output 
 *    from the programs of this Package do not automatically fall under the 
 *    copyright of this Package, but belong to whomever generated them, and may
 *    be sold commercially, and may be aggregated with this Package.
 *
 * 7. C or perl subroutines supplied by you and linked into this Package shall 
 *    not be considered part of this Package.
 *
 * 8. The name of the Copyright Holder may not be used to endorse or promote 
 *    products derived from this software without specific prior written 
 *    permission.
 * 
 * 9. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED 
 *    WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF 
 *    MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * ============================================================================
 */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.topologi.diffx.algorithm.DiffXAlgorithm;
import com.topologi.diffx.algorithm.DiffXFitWesyma;
import com.topologi.diffx.algorithm.DiffXFitopsy;
import com.topologi.diffx.algorithm.DiffXFitsy;
import com.topologi.diffx.algorithm.DiffXKumarRangan;
import com.topologi.diffx.config.DiffXConfig;
import com.topologi.diffx.format.BasicXMLFormatter;
import com.topologi.diffx.format.ConvenientXMLFormatter;
import com.topologi.diffx.format.DiffXFormatter;
import com.topologi.diffx.format.SmartXMLFormatter;
import com.topologi.diffx.format.StrictXMLFormatter;
import com.topologi.diffx.load.Recorder;
import com.topologi.diffx.load.SAXRecorder;
import com.topologi.diffx.load.DOMRecorder;
import com.topologi.diffx.load.TextRecorder;
import com.topologi.diffx.sequence.EventSequence;
import com.topologi.diffx.sequence.SequenceSlicer;
import com.topologi.diffx.util.CommandLine;

/**
 * Utility class to centralise the access to this API from the command line.
 * 
 * @author Christophe Lauret
 * @version 17 October 2006
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
   * sequance SAX events reported an XML reader.
   * 
   * @param xml1 The first XML stream to compare.
   * @param xml2 The first XML stream to compare.
   * 
   * @return <code>true</code> If the XML are considered equivalent;
   *         <code>false</code> otherwise.
   * 
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static boolean equivalent(File xml1, File xml2) 
      throws DiffXException, IOException {
    Recorder recorder = new SAXRecorder();
    EventSequence seq0 = recorder.process(xml1);
    EventSequence seq1 = recorder.process(xml2);
    return seq0.equals(seq1);
  }

  /**
   * Returns <code>true</code> if the two specified inputstream are equivalent by looking at the
   * sequance SAX events reported an XML reader.
   *
   * @param xml1 The first XML stream to compare.
   * @param xml2 The first XML stream to compare.
   *   
   * @return <code>true</code> If the XML are considered equivalent;
   *         <code>false</code> otherwise.
   * 
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static boolean equivalent(InputStream xml1, InputStream xml2)
      throws DiffXException, IOException {
    SAXRecorder recorder = new SAXRecorder();
    EventSequence seq0 = recorder.process(new InputSource(xml1));
    EventSequence seq1 = recorder.process(new InputSource(xml2));
    return seq0.equals(seq1);
  }

  /**
   * Returns <code>true</code> if the two specified readers are equivalent by looking at the
   * sequance SAX events reported an XML reader.
   *
   * @param xml1 The first XML stream to compare.
   * @param xml2 The first XML stream to compare.
   *  
   * @return <code>true</code> If the XML are considered equivalent;
   *         <code>false</code> otherwise.
   * 
   * @throws DiffXException If a DiffX exception is reported by the recorders.
   * @throws IOException    Should an I/O exception occur.
   */
  public static boolean equivalent(Reader xml1, Reader xml2)
      throws DiffXException, IOException {
    SAXRecorder recorder = new SAXRecorder();
    EventSequence seq0 = recorder.process(new InputSource(xml1));
    EventSequence seq1 = recorder.process(new InputSource(xml2));
    return seq0.equals(seq1);
  }

// diff methods -------------------------------------------------------------------------

  /**
   * Compares the two specified xml files and prints the diff onto the given writer. 
   *
   * @param xml1   The first XML node to compare.
   * @param xml2   The first XML node to compare.
   * @param out    Where the output goes.
   * @param config The DiffX configuration to use.
   * 
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static void diff(Node xml1, Node xml2, Writer out, DiffXConfig config)
      throws DiffXException, IOException {
    // records the events from the XML
    DOMRecorder loader = new DOMRecorder();
    if (config != null) loader.setConfig(config);
    EventSequence seq1 = loader.process(xml1);
    EventSequence seq2 = loader.process(xml2);
    // start slicing
    diff(seq1, seq2, out, config);
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
    // records the events from the XML
    SAXRecorder recorder = new SAXRecorder();
    if (config != null) recorder.setConfig(config);
    EventSequence seq1 = recorder.process(new InputSource(xml1));
    EventSequence seq2 = recorder.process(new InputSource(xml2));
    // start slicing
    diff(seq1, seq2, out, config);
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
    // records the events from the XML
    SAXRecorder recorder = new SAXRecorder();
    EventSequence seq1 = recorder.process(new InputSource(xml1));
    EventSequence seq2 = recorder.process(new InputSource(xml2));
    // start slicing
    diff(seq1, seq2, out, new DiffXConfig());
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
    // records the events from the XML
    SAXRecorder recorder = new SAXRecorder();
    EventSequence seq1 = recorder.process(new InputSource(xml1));
    EventSequence seq2 = recorder.process(new InputSource(xml2));
    diff(seq1, seq2, new OutputStreamWriter(out), new DiffXConfig());
  }

  /**
   * Compares the two specified xml files and prints the diff onto the given writer. 
   *
   * @param seq1   The first XML reader to compare.
   * @param seq2   The first XML reader to compare.
   * @param out    Where the output goes.
   * @param config The DiffX configuration to use.
   * 
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  private static void diff(EventSequence seq1, EventSequence seq2, Writer out, DiffXConfig config)
      throws DiffXException, IOException {
    SmartXMLFormatter formatter = new SmartXMLFormatter(out);
    formatter.declarePrefixMapping(seq1.getPrefixMapping());
    formatter.declarePrefixMapping(seq2.getPrefixMapping());
    
    if (config != null) formatter.setConfig(config);
    SequenceSlicer slicer = new SequenceSlicer(seq1, seq2);
    slicer.slice();
    slicer.formatStart(formatter);
    DiffXAlgorithm df = new DiffXFitopsy(seq1, seq2);
    df.process(formatter);
    slicer.formatEnd(formatter);
  }

// command line ------------------------------------------------------------------------- 

  /**
   * Main entry point from the command line. 
   * 
   * @param args The command-line arguments
   * 
   * @throws Exception If anything wrong happens.
   */
  public static void main(String[] args) throws Exception {
    // TODO: better command-lin interface
    if (args.length < 2) usage();
    try {
      boolean profile = CommandLine.hasSwitch("-profile", args);
      boolean slice = !CommandLine.hasSwitch("-noslice", args);

      // get the files
      File xml1 = new File(args[args.length - 2]);
      File xml2 = new File(args[args.length - 1]);

      // loading
      long t0 = System.currentTimeMillis();
      Recorder recorder = getRecorder(args);
      EventSequence seq1 = recorder.process(xml1);
      EventSequence seq2 = recorder.process(xml2);
      long t1 = System.currentTimeMillis();
      if (profile) System.err.println("loaded files in "+(t1 - t0)+"ms");

      // get the config
      DiffXConfig config = new DiffXConfig();

      // get and setup the formatter
      Writer out = new OutputStreamWriter(getOutput(args), "utf-8");
      DiffXFormatter formatter = getFormatter(args, out);
//      formatter.declarePrefixMapping(seq1.getPrefixMapping());
//      formatter.declarePrefixMapping(seq2.getPrefixMapping());
      formatter.setConfig(config);

      // pre-slicing
      SequenceSlicer slicer = new SequenceSlicer(seq1, seq2);
      if (slice) {
        slicer.slice();
        slicer.formatStart(formatter);
      }

      // start algorithm
      DiffXAlgorithm df = getAlgorithm(args, seq1, seq2);
      df.process(formatter);
      
      // post-slicing
      if (slice) {
        slicer.formatEnd(formatter);
      }

      long t2 = System.currentTimeMillis();
      if (profile) System.err.println("executed algorithm files in "+(t2 - t1)+"ms");

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
    System.err.println("  -profile    Display profiling info");
    System.err.println("  -noslice    Do not use slicing");
    System.err.println("  -o [output] The output file");
    System.err.println("  -L [loader] Choose a specific loader");
    System.err.println("               sax* | dom | text");
    System.err.println("  -A [algo]   Choose a specific algorithm");
    System.err.println("               fitsy* | fitopsy | kumar | wesyma");
    System.err.println("  -F [format] Choose a specific formatter");
    System.err.println("               smart* | basic | convenient | strict | short");
    System.err.println(" * indicates option used by default.");
    System.exit(1);
  }

  /**
   * @param args The command line arguments.
   * @return The recorder to use.
   */
  private static Recorder getRecorder(String[] args) {
    String loaderArg = CommandLine.getParameter("-L", args);
    if (loaderArg == null || "sax".equals(loaderArg))
      return new SAXRecorder();
    else if ("dom".equals(loaderArg))
      return new DOMRecorder();
    else if ("text".equals(loaderArg))
      return new TextRecorder();
    else
      usage();
    return null;
   }

  /**
   * @param args The command line arguments.
   * @return The output to use.
   * 
   * @throws FileNotFoundException If the file does not exist.
   */
  private static OutputStream getOutput(String[] args) throws FileNotFoundException {
    String outArg = CommandLine.getParameter("-o", args);
    if (outArg == null)
      return System.out;
    else
      return new BufferedOutputStream(new FileOutputStream(outArg));
  }

  /**
   * @param args The command line arguments.
   * @param seq1 The first sequence.
   * @param seq2 The second sequence.
   * @return The algorithm to use.
   */
  private static DiffXAlgorithm getAlgorithm(String[] args, EventSequence seq1, EventSequence seq2) {
    String loaderArg = CommandLine.getParameter("-A", args);
    if (loaderArg == null || "fitsy".equals(loaderArg))
      return new DiffXFitsy(seq1, seq2);
    else if ("fitopsy".equals(loaderArg))
      return new DiffXFitopsy(seq1, seq2);
    else if ("kumar".equals(loaderArg))
      return new DiffXKumarRangan(seq1, seq2);
    else if ("wesyma".equals(loaderArg))
      return new DiffXFitWesyma(seq1, seq2);
    else
      usage();
    return null;
  }

  /**
   * @param args The command line arguments.
   * @param out  The writer to use.
   * @return The formatter to use.
   * @throws IOException Should and I/O error occur
   */
  private static DiffXFormatter getFormatter(String[] args, Writer out) throws IOException {
    String formatArg = CommandLine.getParameter("-F", args);
    if (formatArg == null || "smart".equals(formatArg))
      return new SmartXMLFormatter(out);
    else if ("convenient".equals(formatArg))
      return new ConvenientXMLFormatter(out);
    else if ("basic".equals(formatArg))
      return new BasicXMLFormatter(out);
    else if ("strict".equals(formatArg))
      return new StrictXMLFormatter(out);
    else if ("short".equals(formatArg))
      return new StrictXMLFormatter(out);
    else
      usage();
    return null;
  }

}
