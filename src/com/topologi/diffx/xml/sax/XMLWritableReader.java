/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.xml.sax;

import java.io.IOException;
import java.util.Map;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;

import com.topologi.diffx.xml.XMLWritable;
import com.topologi.diffx.xml.XMLWriter;

/**
 * An XMLReader implementation that can be used to parse XMLWritable objects.
 * 
 * <p>Typically, XMLWritable objects are wrapped into an 
 * <code>XMLWritableInputSource</code> so that the <code>XMLReader</code> API
 * methods are used; however, it is perfectly possible to parse directly an
 * <code>XMLWritable</code> object. 
 * 
 * @see org.xml.sax.XMLReader
 * @see com.topologi.diffx.xml.XMLWritable
 * @see com.topologi.diffx.xml.sax.XMLWritableInputSource
 * 
 * @author  Christophe Lauret 
 * @version 27 May 2005
 */
public final class XMLWritableReader implements XMLReader {

  /**
   * The URI of the namespace feature.
   */
  private static final String NAMESPACES = "http://xml.org/sax/features/namespaces";

  /**
   * The URI of the namespace prefixes feature.
   */
  private static final String NS_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";

  /**
   * The features used by this XML reader implementation.
   */
  private Map features = new java.util.HashMap();

  /**
   * The content reader this XMLReader will use.
   */
  private ContentHandler handler;

  /**
   * Creates a new XML Reader. 
   */
  public XMLWritableReader() {
    setFeature(NAMESPACES, true);
    setFeature(NS_PREFIXES, false);
  }

// XMLReader methods implementation -----------------------------------------------------

  /**
   * @see org.xml.sax.XMLReader#getContentHandler()
   */
  public ContentHandler getContentHandler() {
    return this.handler;
  }

  /**
   * @see org.xml.sax.XMLReader#setContentHandler(ContentHandler)
   */
  public void setContentHandler(ContentHandler chandler) {
    this.handler = chandler;
  }

  /**
   * Returns <code>null</code>.
   * 
   * @see org.xml.sax.XMLReader#getErrorHandler()
   */
  public ErrorHandler getErrorHandler() {
    return null;
  }

  /**
   * Does nothing.
   * 
   * @see org.xml.sax.XMLReader#setErrorHandler(ErrorHandler)
   */
  public void setErrorHandler(ErrorHandler ehandler) {
  }

  /**
   * Returns <code>null</code>.
   * 
   * @see org.xml.sax.XMLReader#getDTDHandler()
   */
  public DTDHandler getDTDHandler() {
    return null;
  }

  /**
   * Does nothing.
   * 
   * @see org.xml.sax.XMLReader#setDTDHandler(DTDHandler)
   */
  public void setDTDHandler(DTDHandler dhandler) {
  }

  /**
   * Returns <code>null</code>.
   * 
   * @see org.xml.sax.XMLReader#getEntityResolver()
   */
  public EntityResolver getEntityResolver() {
    return null;
  }

  /**
   * Returns <code>null</code>.
   * 
   * @see org.xml.sax.XMLReader#setEntityResolver(EntityResolver)
   */
  public void setEntityResolver(EntityResolver resolver) {
  }

  /**
   * Returns <code>null</code>.
   * 
   * @see org.xml.sax.XMLReader#getProperty(String)
   */
  public Object getProperty(java.lang.String name) {
    return null;
  }

  /**
   * Does nothing.
   * 
   * @see org.xml.sax.XMLReader#setProperty(String, Object)
   */
  public void setProperty(java.lang.String name, java.lang.Object value) {
  }

  /**
   * @see org.xml.sax.XMLReader#getFeature(String)
   */
  public boolean getFeature(java.lang.String name) {
	// TODO: handling of features
    return ((Boolean)features.get(name)).booleanValue();
  }

  /**
   * @see org.xml.sax.XMLReader#setFeature(String, boolean)
   */
  public void setFeature(java.lang.String name, boolean value) {
    // TODO: handling of features
    this.features.put(name, Boolean.valueOf(value));
  }

  /**
   * @see org.xml.sax.XMLReader#parse(String)
   */
  public void parse(String systemId) throws IOException, SAXException {
    throw new SAXException(
      this.getClass().getName()
      + " cannot be used with system identifiers (URIs)");
  }

  /**
   * @see org.xml.sax.XMLReader#parse(InputSource)
   */
  public void parse(InputSource input) throws IOException, SAXException {
    if (input instanceof XMLWritableInputSource) {
      parse((XMLWritableInputSource)input);
    } else {
      throw new SAXException("Unsupported InputSource specified. Must be a XMLWritableInputSource");
    }
  }

  /**
   * @see org.xml.sax.XMLReader#parse(InputSource)
   */
  public void parse(XMLWritableInputSource input) throws IOException, SAXException {
    parse(input.getXMLWritable());
  }

  /**
   * @see org.xml.sax.XMLReader#parse(InputSource)
   */
  public void parse(XMLWritable xml) throws IOException, SAXException {
    if (xml == null)
      throw new NullPointerException("Parameter projectTeam must not be null");
    if (this.handler == null)
      throw new IllegalStateException("ContentHandler not set");
    // start handling the document
    this.handler.startDocument();
    XMLWriter xw = new XMLWriterSAX(this.handler);
    xml.toXML(xw);
    this.handler.endDocument();
  }

}