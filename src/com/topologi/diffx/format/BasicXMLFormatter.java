package com.topologi.diffx.format;

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

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Stack;


import com.topologi.diffx.config.DiffXConfig;
import com.topologi.diffx.event.AttributeEvent;
import com.topologi.diffx.event.CloseElementEvent;
import com.topologi.diffx.event.DiffXEvent;
import com.topologi.diffx.event.TextEvent;
import com.topologi.diffx.event.OpenElementEvent;
import com.topologi.diffx.event.impl.ProcessingInstructionEvent;
import com.topologi.diffx.sequence.PrefixMapping;
import com.topologi.diffx.util.Constants;
import com.topologi.diffx.xml.XMLWriterNSImpl;
import com.topologi.diffx.xml.XMLWriter;

/**
 * A XML formatter that simply uses a different namespace for any inserted or modified
 * node.
 * 
 * <p>Nodes that have not changed are kept the way they are.
 * 
 * <p>Nodes that have been modified will always be in a different namespace and will be
 * reported as follows:
 * <p><b>Elements:</b>
 * <pre>
 *   &lt;mod:element name="<i>elt.getName()</i>" uri="<i>elt.getURI()</i>"&gt;
 *     <i>...</i>
 *   &lt;/mod:element&gt;
 * </pre>
 *
 * <p><b>Attributes:</b>
 * <pre>
 *   &lt;mod:attribute name="<i>att.getName()</i>" uri="<i>att.getURI()</i>" value="<i>att.getValue()</i>"/&gt;
 * </pre>
 *
 * <p><b>Texts:</b>
 * <pre>
 *   &lt;mod:text&gt;<i>text.getCharacters()</i>&lt;/mod:text&gt;
 * </pre>
 *
 * @author Christophe Lauret
 * @version 17 May 2005
 */
public final class BasicXMLFormatter implements XMLDiffXFormatter {

// class attributes ---------------------------------------------------------------------------

  /**
   * The output goes here.
   */
  private final XMLWriter xml;

  /**
   * The DiffX configuration to use
   */
  private DiffXConfig config = new DiffXConfig(); 

// state variables ----------------------------------------------------------------------------

  /**
   * Set to <code>true</code> to include the XML declaration. This attribute is
   * set to <code>false</code> when the {@link #setWriteXMLDeclaration(boolean)}
   * is called with <code>false</code> or once the XML declaration has been written. 
   */
  private transient boolean writeXMLDeclaration = true;

  /**
   * Indicates whether the XML writer has been setup already. 
   */
  private transient boolean isSetup = false;

  /**
   * Indicates whether some text is being inserted or removed.
   * 
   * 0 = indicate format or no open text element.
   * +1 = indicates an insert open text element.
   * -1 = indicates an delete open text element.
   */
  private transient short textFormat = 0;

  /**
   * A stack of attributes to insert.
   */
  private transient Stack insAttributes = new Stack();

  /**
   * A stack of attributes to delete.
   */
  private transient Stack delAttributes = new Stack();

// constructors -------------------------------------------------------------------------------

  /**
   * Creates a new formatter using the specified writer.
   * 
   * @param w The writer to use.
   * 
   * @throws NullPointerException If the specified writer is <code>null</code>.
   */
  public BasicXMLFormatter(Writer w) throws NullPointerException {
	if (w == null)
      throw new NullPointerException("The XML formatter requires a writer");
    this.xml = new XMLWriterNSImpl(w, false);
  }

// methods ------------------------------------------------------------------------------------

  /**
   * @see DiffXFormatter#format(DiffXEvent) 
   */
  public void format(DiffXEvent e) throws IOException {
	  if (!isSetup) setUpXML();
    endTextChange();
    if (!(e instanceof AttributeEvent)) flushAttributes();
    e.toXML(xml);
    if (e instanceof TextEvent)
      if (config.isIgnoreWhiteSpace() && !config.isPreserveWhiteSpace())
        this.xml.writeXML(" ");
    this.xml.flush();
  }

  /**
   * @see DiffXFormatter#insert(DiffXEvent)
   */
  public void insert(DiffXEvent e) throws IOException {
    change(e, +1);
  }

  /**
   * @see DiffXFormatter#delete(DiffXEvent)
   */
  public void delete(DiffXEvent e) throws IOException {
    change(e, -1);
  }

  /**
   * Reports a change in XML.
   * 
   * @param e   The diff-x event that has been inserted or deleted.
   * @param mod The modification flag (positive for inserts, negative for deletes).
   *
   * @throws IOException an I/O exception if an error occurs. 
   */
  private void change(DiffXEvent e, int mod) throws IOException {
    if (!isSetup) setUpXML();
    // change in element
    if (e instanceof OpenElementEvent) {
      flushAttributes();
      endTextChange();
      this.xml.openElement((mod > 0)? Constants.INSERT_NS_URI : Constants.DELETE_NS_URI, "element", false);
      this.xml.attribute("name", ((OpenElementEvent)e).getName());
      this.xml.attribute("ns-uri", ((OpenElementEvent)e).getURI());

    // change in element
    } else if (e instanceof CloseElementEvent) {
      flushAttributes();
      endTextChange();
      this.xml.closeElement();

    // change in text
    } else if (e instanceof TextEvent) {
      flushAttributes();
      switchTextChange(mod);
      e.toXML(this.xml);
      if (config.isIgnoreWhiteSpace() && !config.isPreserveWhiteSpace())
        this.xml.writeXML(" ");

    // put the attribute as part of the 'delete' namespace
    } else if (e instanceof AttributeEvent) {
      if (mod > 0)
        this.insAttributes.push(e);
      else
        this.delAttributes.push(e);

      // put the attribute as part of the 'delete' namespace
    } else if (e instanceof ProcessingInstructionEvent) {
      flushAttributes();
      endTextChange();
      this.xml.openElement((mod > 0)? Constants.INSERT_NS_URI : Constants.DELETE_NS_URI, "processing-instruction", false);
      this.xml.attribute("data", ((ProcessingInstructionEvent)e).getData());
      this.xml.attribute("target", ((ProcessingInstructionEvent)e).getTarget());

    // just format naturally
    } else {
      flushAttributes();
      endTextChange();
      e.toXML(this.xml);
    }
    this.xml.flush();
  }

  /**
   * @see com.topologi.diffx.format.DiffXFormatter#setConfig(com.topologi.diffx.DiffXConfig)
   */
  public void setConfig(DiffXConfig config) {
    this.config = config;
  }

  /**
   * @see XMLDiffXFormatter#setWriteXMLDeclaration(boolean)
   */
  public void setWriteXMLDeclaration(boolean show) {
    this.writeXMLDeclaration = show;
  }

  /**
   * Adds the prefix mapping to this class.
   * 
   * @param mapping The prefix mapping to add.
   */
  public void declarePrefixMapping(PrefixMapping mapping) {
    for (Enumeration uris = mapping.getURIs(); uris.hasMoreElements();) {
      String uri = (String)uris.nextElement();
      this.xml.setPrefixMapping(uri, mapping.getPrefix(uri));
    }
  }

// private helpers ----------------------------------------------------------------------------

  /**
   * Set up the XML.
   * 
   * @throws IOException an I/O exception if an error occurs.
   */
  private void setUpXML() throws IOException {
    if (this.writeXMLDeclaration) this.xml.xmlDecl();
  	this.xml.setPrefixMapping(Constants.DELETE_NS_URI, "del");
	  this.xml.setPrefixMapping(Constants.INSERT_NS_URI, "ins");
	  this.writeXMLDeclaration = false;
	  this.isSetup = true;
  }

  /**
   * Formats the end of a text change.
   * 
   * @throws IOException If throws by XMl writer.
   */
  private void endTextChange() throws IOException {
    if (this.textFormat != 0) {
      this.xml.closeElement();
      this.textFormat = 0;
    }
  }

  /**
   * Switch between text changes.
   * 
   * @param mod The modification flag (positive for inserts, negative for deletes).
   * 
   * @throws IOException If throws by XMl writer.
   */
  private void switchTextChange(int mod) throws IOException {
    // insert
    if (mod > 0) {
      if (this.textFormat < 0) this.xml.closeElement();
      if (this.textFormat <= 0) {
        this.xml.openElement(Constants.INSERT_NS_URI, "text", false);
        this.textFormat = +1;
      }
    // delete
    } else {
      if (this.textFormat > 0) this.xml.closeElement();
      if (this.textFormat >= 0) {
        this.xml.openElement(Constants.DELETE_NS_URI, "text", false);
        this.textFormat = -1;
      }
    }
  }

  /**
   * Writes any attribute that has not be written.
   * 
   * @throws IOException Should an I/O error occur.
   */
  private void flushAttributes() throws IOException {
    flushAttributes(this.insAttributes, Constants.INSERT_NS_URI);
    flushAttributes(this.delAttributes, Constants.DELETE_NS_URI);
  }

  /**
   * Writes any attribute that has not be written.
   * 
   * @param atts The attribute stack.
   * @param uri  The Namespace URI required
   * 
   * @throws IOException Should an I/O error occur.
   */
  private void flushAttributes(Stack atts, String uri) throws IOException {
    while (!atts.empty()) {
      AttributeEvent att = (AttributeEvent)atts.pop();
      this.xml.openElement(uri, "attribute", false);
      this.xml.attribute("name", att.getName());
      if (att.getURI() != null) this.xml.attribute("ns-uri", att.getURI());
      this.xml.attribute("value", att.getValue());
      this.xml.closeElement();
    } 
  }

}
