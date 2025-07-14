package org.pageseeder.diffx.token;

import org.pageseeder.diffx.token.impl.*;

public class DefaultTokenProvider implements XMLTokenProvider {

  /**
   * Indicates whether the factory should generate namespace tokens.
   */
  private final boolean isNamespaceAware;

  /**
   * Creates a new namespace-aware factory for tokens.
   */
  public DefaultTokenProvider() {
    this.isNamespaceAware = true;
  }

  /**
   * Creates a factory for tokens.
   *
   * @param isNamespaceAware <code>true</code> to create new namespace aware factory;
   *                         <code>false</code> otherwise.
   */
  public DefaultTokenProvider(boolean isNamespaceAware) {
    this.isNamespaceAware = isNamespaceAware;
  }

  @Override
  public boolean isNamespaceAware() {
    return this.isNamespaceAware;
  }

  /**
   * Returns the open element token from the uri and name given.
   *
   * <p>If the factory is namespace aware, it returns an open element implementation
   * using the namespace URI and the name.
   *
   * <p>If the factory is NOT namespace aware, it returns an open element implementation
   * using the specified name.
   *
   * <p>Use this implementation if the name of the element is determined prior to the
   * call of this method.
   *
   * @param uri  The namespace URI of the element (ignored if not namespace aware)
   * @param name The name of the element.
   *
   * @return The open element token from the uri and name given.
   */
  @Override
  public StartElementToken newStartElement(String uri, String name) {
    return this.isNamespaceAware ? new XMLStartElement(uri, name) : new XMLStartElement(name);
  }

  /**
   * Returns the open element token from the uri and names given.
   *
   * <p>If the factory is namespace aware, it returns an open element implementation
   * using the namespace URI and the local name.
   *
   * <p>If the factory is NOT namespace aware, it returns an open element implementation
   * using the qName (namespace-prefixed name).
   *
   * @param uri       The namespace URI of the element (ignored if not namespace aware)
   * @param localName The local name of the element.
   * @param qName     The qualified name of the element.
   *
   * @return The open element token from the uri and name given.
   */
  @Override
  public StartElementToken newStartElement(String uri, String localName, String qName) {
    return this.isNamespaceAware ? new XMLStartElement(uri, localName) : new XMLStartElement(qName);
  }

  /**
   * Returns the close element token from the corresponding open element token.
   *
   * @param start The corresponding open element token.
   *
   * @return The close element token from the corresponding open element token.
   */
  @Override
  public EndElementToken newEndElement(StartElementToken start) {
    return new XMLEndElement(start);
  }

  /**
   * Returns the attribute token from the name and value given.
   *
   * <p>If the factory is namespace aware, it returns an attribute on the NULL URI namespace.
   *
   * <p>If the factory is NOT namespace aware, it returns an attribute implementation
   * using the specified name.
   *
   * <p>Use this implementation if the name of the element is determined prior to the
   * call of this method.
   *
   * @param name  The name of the attribute.
   * @param value The value of the attribute.
   *
   * @return The open element token from the uri and name given.
   */
  @Override
  public AttributeToken newAttribute(String name, String value) {
    return new XMLAttribute(name, value);
  }

  /**
   * Returns the attribute token from the name and value given.
   *
   * <p>If the factory is namespace-aware, it returns an attribute implementation
   * using the namespace URI and the name.
   *
   * <p>If the factory is NOT namespace-aware, it returns an attribute implementation
   * using the specified name.
   *
   * <p>Use this implementation if the name of the element is determined prior to the
   * call of this method.
   *
   * @param uri   The namespace URI of the attribute (ignored if not namespace aware)
   * @param name  The name of the attribute.
   * @param value The value of the attribute.
   *
   * @return The open element token from the uri and name given.
   */
  @Override
  public AttributeToken newAttribute(String uri, String name, String value) {
    return this.isNamespaceAware ? new XMLAttribute(uri, name, value) : new XMLAttribute(name, value);
  }

  /**
   * Returns the attribute token from the name and value given.
   *
   * <p>If the factory is namespace-aware, it returns an attribute implementation
   * using the namespace URI and the local name.
   *
   * <p>If the factory is NOT namespace-aware, it returns an attribute implementation
   * using the qName (namespace-prefixed name).
   *
   * @param uri       The namespace URI of the attribute (ignored if not namespace aware)
   * @param localName The local name of the attribute.
   * @param qName     The qualified name of the attribute.
   * @param value     The value of the attribute.
   *
   * @return The open element token from the uri and name given.
   */
  @Override
  public AttributeToken newAttribute(String uri, String localName, String qName, String value) {
    return this.isNamespaceAware ? new XMLAttribute(uri, localName, value) : new XMLAttribute(qName, value);
  }

  @Override
  public XMLToken newToken(XMLTokenType type, String name, String value, String uri) {
    switch (type) {
      case TEXT: return newText(value);
      case START_ELEMENT: return newStartElement(uri, name);
      case END_ELEMENT: return newEndElement(newStartElement(uri, name));
      case ATTRIBUTE: return newAttribute(uri, name, value);
      case COMMENT: return new XMLComment(value);
      case PROCESSING_INSTRUCTION: return new XMLProcessingInstruction(name, value);
      case START_DOCUMENT: return new StartDocumentToken();
      case END_DOCUMENT: return new EndDocumentToken();
      default: throw new UnsupportedOperationException("Unsupported token type: " + type);
    }
  }

  @Override
  public TextToken newText(String text) {
    return new CharactersToken(text);
  }

}
