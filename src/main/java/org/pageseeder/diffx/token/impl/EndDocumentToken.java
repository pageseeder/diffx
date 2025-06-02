package org.pageseeder.diffx.token.impl;

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.XMLTokenType;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * Represents the "End Document" token in an XML processing context.
 *
 * <p>This token signifies the conclusion of an XML document and is primarily used
 * in scenarios involving XML token comparison or processing. Its type is
 * defined as {@link XMLTokenType#END_DOCUMENT}.
 *
 * <p>As an implementation of the {@link XMLToken} interface, this token provides
 * standard methods to interact with XML, although its contributions to XML
 * serialization are effectively null (e.g., no XML output is generated).
 *
 * <p>Equality is based on whether the compared instance is also an
 * {@code EndDocumentToken}.
 *
 * @author Christophe Lauret
 *
 * @version 1.2.0
 * @since 1.2.0
 */
public class EndDocumentToken implements XMLToken {

  @Override
  public @NotNull String getName() {
    return "";
  }

  @Override
  public String getValue() {
    return "";
  }

  @Override
  public @NotNull XMLTokenType getType() {
    return XMLTokenType.END_DOCUMENT;
  }

  @Override
  public boolean equals(XMLToken token) {
    return token != null && token.getType() == XMLTokenType.END_DOCUMENT;
  }

  @Override
  public void toXML(@NotNull XMLStreamWriter xml) throws XMLStreamException {
    // Does nothing
  }

  @Override
  public void toXML(XMLWriter xmlWriter) throws IOException {
    // Does nothing
  }

}
