package com.topologi.diffx.config;

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

/**
 * The configuration to use with a DiffX operation.
 * 
 * <p>This class acts as a container for a set of properties that can be applied to the
 * main components of Diffx such as the:
 * <ul>
 *   <li>The {@link com.topologi.diffx.load.XMLRecorder} implementations,</li>
 *   <li>The {@link com.topologi.diffx.algorithm.DiffXAlgorithm} implementations,<li>
 *   <li>and the {@link com.topologi.diffx.format.DiffXFormatter} implementations.<li>
 * </ul>
 * 
 * <p>In order to produce the correct results, the configuration must be applied
 * throughout the three steps of processing.
 * 
 * <p>There is an illegal state in this configuration, if the the diffx is not namespace
 * aware it cannot not report the differences in the prefixes. Therefore it is impossible
 * to set both flags to <code>false</code>.
 * 
 * <p>The <code>set</code> methods for those flags will ensure that this situation does
 * not occur. The general rule is that the flag being set takes precedence.
 * 
 * <p>Note that it simply mimics SAX2 which cannot have the features
 * <code>http://xml.org/sax/features/namespaces</code> and
 * <code>http://xml.org/sax/features/namespace-prefixes</code> both set to 
 * <code>false</code>. 
 * 
 * @see com.topologi.diffx.load.XMLRecorder
 * @see com.topologi.diffx.algorithm.DiffXAlgorithm
 * @see com.topologi.diffx.format.DiffXFormatter
 * 
 * @author Christophe Lauret
 * @version 15 April 2004
 */
public final class DiffXConfig {

// class attributes ---------------------------------------------------------------------------

  /**
   * Indicates whether the differences in white spaces should be ignored.
   */
  private boolean ignoreWhiteSpace = false;

  /**
   * Indicates whether the white spaces should be preserved.
   */ 
  private boolean preserveWhiteSpace = true;

  /**
   * Indicates whether the namespaces should be handled or ignored.
   */ 
  private boolean isNamespaceAware = true;

  /**
   * Indicates whether difference in prefixes should be reported.
   */
  private boolean reportPrefixDifferences = false;

// class attributes ---------------------------------------------------------------------------

  /**
   * Creates a new configuration for DiffX
   */
  public DiffXConfig() {
  }

// methods ------------------------------------------------------------------------------------

  /**
   * Sets whether the differences in white spaces should be ignored or not.
   * 
   * @param ignore <code>true</code> to ignore differences in white spaces;
   *               <code>false</code> otherwise.
   */
  public void setIgnoreWhiteSpace(boolean ignore) {
    this.ignoreWhiteSpace = ignore;
  }

  /**
   * Sets whether the white spaces should be preserved or not.
   * 
   * @param preserve <code>true</code> to preserve the white spaces;
   *                 <code>false</code> otherwise.
   */
  public void setPreserveWhiteSpace(boolean preserve) {
    this.preserveWhiteSpace = preserve;
  }

  /**
   * Sets whether the Diff-X should take namespaces into account.
   * 
   * <p>It is more efficient to disable namespace processing when the XML to 
   * compare are not expected to use any namespace.
   * 
   * <p>In order to avoid an illegal state, if this flag is set to <code>false</code>
   * and the differences in prefixes will be automatically reported.
   * 
   * @param aware <code>true</code> to preserve the white spaces;
   *              <code>false</code> otherwise.
   */
  public void setNamespaceAware(boolean aware) {
    this.isNamespaceAware = aware;
    if (!aware)
      this.reportPrefixDifferences = true;
  }

  /**
   * Sets whether the Diff-X should report differences in prefixes.
   *
   * <p>In order to avoid an illegal state, if this flag is set to <code>false</code>
   * and then the processor becomes namespace aware.
   *
   * @param report <code>true</code> to report differences in prefixes;
   *               <code>false</code> to ignore them.
   */
  public void setReportPrefixDifferences(boolean report) {
    this.reportPrefixDifferences = report;
    if (!report)
      this.isNamespaceAware = true;
  }

  /**
   * Indicates whether the differences in white spaces should be ignored or not.
   * 
   * @return <code>true</code> to ignore differences in white spaces;
   *         <code>false</code> otherwise.
   */
  public boolean isIgnoreWhiteSpace() {
    return this.ignoreWhiteSpace;
  }

  /**
   * Indicates whether the white spaces are preserved or not.
   * 
   * @return <code>true</code> to preserve the white spaces;
   *         <code>false</code> otherwise.
   */
  public boolean isPreserveWhiteSpace() {
    return this.preserveWhiteSpace;
  }

  /**
   * Indicates whether the Diff-X takes namespaces into account.
   * 
   * @return <code>true</code> to preserve the white spaces;
   *         <code>false</code> otherwise.
   */
  public boolean isNamespaceAware() {
    return this.isNamespaceAware;
  }

  /**
   * Returns whether the differences in prefixes are reported.
   * 
   * @return <code>true</code> to report differences in prefixes;
   *         <code>false</code> to ignore them.
   */
  public boolean isReportPrefixDifferences() {
    return this.reportPrefixDifferences;
  }

}
