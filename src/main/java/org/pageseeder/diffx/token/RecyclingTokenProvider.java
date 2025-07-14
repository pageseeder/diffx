package org.pageseeder.diffx.token;

import org.pageseeder.diffx.token.impl.*;
import org.pageseeder.diffx.util.LimitedSizeCache;

public class RecyclingTokenProvider implements XMLTokenProvider {

  private static final int DEFAULT_MAX_ELEMENTS = 100;

  /**
   * Indicates whether the factory should generate namespace tokens.
   */
  private final boolean isNamespaceAware;

  /**
   * Start elements cache capped at {@link #DEFAULT_MAX_ELEMENTS}.
   */
  private final LimitedSizeCache<String, StartElementToken> startElements =
      new LimitedSizeCache<>(DEFAULT_MAX_ELEMENTS);

  /**
   * End elements cache capped at {@link #DEFAULT_MAX_ELEMENTS}.
   */
  private final LimitedSizeCache<StartElementToken, EndElementToken> endElements =
      new LimitedSizeCache<>(DEFAULT_MAX_ELEMENTS);

  /**
   * Creates a new namespace-aware factory for tokens.
   */
  public RecyclingTokenProvider() {
    this.isNamespaceAware = true;
  }

  /**
   * Creates a factory for tokens.
   *
   * @param isNamespaceAware <code>true</code> to create new namespace-aware factory;
   *                         <code>false</code> otherwise.
   */
  public RecyclingTokenProvider(boolean isNamespaceAware) {
    this.isNamespaceAware = isNamespaceAware;
  }

  @Override
  public boolean isNamespaceAware() {
    return this.isNamespaceAware;
  }

  @Override
  public StartElementToken newStartElement(String uri, String name) {
    if (this.isNamespaceAware && !uri.isEmpty()) {
      return startElements.getOrCreate("{"+uri+"}"+name, k -> new XMLStartElement(uri, name));
    }
    return startElements.getOrCreate(name, XMLStartElement::new);
  }

  @Override
  public StartElementToken newStartElement(String uri, String localName, String qName) {
    if (this.isNamespaceAware) {
      return uri.isEmpty()
          ? startElements.getOrCreate(localName, k -> new XMLStartElement(uri, localName))
          : startElements.getOrCreate("{"+uri+"}"+localName, k -> new XMLStartElement(uri, localName));
    }
    return startElements.getOrCreate(qName, XMLStartElement::new);
  }

  @Override
  public EndElementToken newEndElement(StartElementToken start) {
    return endElements.getOrCreate(start, XMLEndElement::new);
  }

  @Override
  public AttributeToken newAttribute(String name, String value) {
    return new XMLAttribute(name, value);
  }

  @Override
  public AttributeToken newAttribute(String uri, String name, String value) {
    return this.isNamespaceAware ? new XMLAttribute(uri, name, value) : new XMLAttribute(name, value);
  }

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
