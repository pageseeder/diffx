package org.pageseeder.diffx.token;

public interface XMLTokenProvider {

  boolean isNamespaceAware();

  XMLToken newToken(XMLTokenType type, String name, String value, String uri);

  StartElementToken newStartElement(String uri, String name);

  StartElementToken newStartElement(String uri, String localName, String qName);

  EndElementToken newEndElement(StartElementToken start);

  AttributeToken newAttribute(String name, String value);

  AttributeToken newAttribute(String uri, String name, String value);

  AttributeToken newAttribute(String uri, String localName, String qName, String value);

  TextToken newText(String text);

}
