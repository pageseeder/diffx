package org.pageseeder.diffx.algorithm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.api.LoadingException;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.test.TestTokens;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.XMLTokenType;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This tests checks that the diff algorithm produces consistent output for different operations.
 *
 * <ul>
 *   <li>All deletes are from the "from" sequence</li>
 *   <li>All inserts are from the "to" sequence</li>
 *   <li>All matches are from the "to" sequence</li>
 * </ul>
 *
 * @author Christophe Lauret
 *
 * @version 1.3.2
 * @since 1.3.2
 */
public abstract class ConsistentOutputTest extends AlgorithmTest<XMLToken> {

  @Test
  void testConsistent_1() {
    assertConsistent("<p>test</p>", "<p>test</p>", TextGranularity.TEXT);
  }

  @Test
  void testConsistent_2() {
    assertConsistent("<p>test1</p>", "<p>test2</p>", TextGranularity.TEXT);
  }

  @Test
  void testConsistent_3() {
    assertConsistent("<p>test</p>", "<q>test</q>", TextGranularity.TEXT);
  }

  @Test
  void testConsistent_4() {
    assertConsistent("<p m='1'>test</p>", "<p m='1'>test</p>", TextGranularity.TEXT);
  }

  @Test
  void testConsistent_5() {
    assertConsistent("<p m='1'>test</p>", "<p m='2'>test</p>", TextGranularity.TEXT);
  }

  @Test
  void testConsistent_6() {
    assertConsistent("<p m='1'>a b c</p>", "<p m='1'>a b c</p>", TextGranularity.SPACE_WORD);
  }

  @Test
  void testConsistent_7() {
    assertConsistent("<p m='1'>a c b</p>", "<p m='2'>a b c</p>", TextGranularity.SPACE_WORD);
  }

  @Test
  void testConsistent_8() {
    assertConsistent("<p m='1' n=''>a c b</p>", "<p m='2'>a b c</p>", TextGranularity.SPACE_WORD);
  }

  @Test
  void testConsistent_9() {
    assertConsistent("<p m='1'>a c b</p>", "<p m='2' n=''>a b c</p>", TextGranularity.SPACE_WORD);
  }

  private void assertConsistent(String from, String to, TextGranularity granularity) {
    try {
      List<OriginToken> fromTokens = loadWithOrigin(from, granularity, 1);
      List<OriginToken> toTokens = loadWithOrigin(to, granularity, 2);
      DiffAlgorithm<XMLToken> diff = getDiffAlgorithm();
      diff.diff(fromTokens, toTokens, (op, t) -> {
        Assertions.assertInstanceOf(OriginToken.class, t);
        if (op == Operator.DEL) {
          Assertions.assertEquals(1, ((OriginToken) t).source, "Deleted token should have source 1: " + op + t);
        } else if (op == Operator.INS) {
          Assertions.assertEquals(2, ((OriginToken) t).source, "Inserted token should have source 2: "+op+t);
        } else {
          Assertions.assertEquals(2, ((OriginToken) t).source, "Matched token should have source 2: "+op+t);
        }
      });
    } catch (LoadingException ex) {
      throw new RuntimeException(ex);
    }
  }

  private static List<OriginToken> loadWithOrigin(String xml, TextGranularity granularity, int source) throws LoadingException {

    return TestTokens.loadSequence(xml, granularity).stream()
        .map(token -> {
          if (token instanceof StartElementToken) return new OriginStartElementToken((StartElementToken)token, source);
          if (token instanceof EndElementToken) return new OriginEndElementToken((EndElementToken)token, source);
          return new OriginToken(token, source);
        })
        .collect(Collectors.toList());
  }

  private static class OriginStartElementToken extends OriginToken implements StartElementToken {
    OriginStartElementToken(StartElementToken token, int source) {
      super(token, source);
    }
  }

  private static class OriginEndElementToken extends OriginToken implements EndElementToken {
    OriginEndElementToken(EndElementToken token, int source) {
      super(token, source);
    }

    @Override
    public StartElementToken getStartElement() {
//      return ((EndElementToken)this.token).getStartElement();
      return new OriginStartElementToken(((EndElementToken)this.token).getStartElement(), source);
    }

    @Override
    public boolean match(StartElementToken token) {
      return ((EndElementToken)this.token).match(token);
    }
  }

  private static class OriginToken implements XMLToken {

    final XMLToken token;

    final int source;

    public OriginToken(XMLToken token, int source) {
      this.token = token;
      this.source = source;
    }

    @Override
    public XMLTokenType getType() {
      return this.token.getType();
    }

    @Override
    public String getName() {
      return this.token.getName();
    }

    @Override
    public String getValue() {
      return this.token.getValue();
    }

    @Override
    public String getNamespaceURI() {
      return this.token.getNamespaceURI();
    }

    @Override
    public boolean isWhitespace() {
      return this.token.isWhitespace();
    }

    @Override
    public boolean equals(XMLToken token) {
      return this.token.equals(token);
    }

    public boolean equals(Object o) {
      if (o instanceof OriginToken) return this.token.equals(((OriginToken) o).token);
      if (o instanceof XMLToken) return this.token.equals((XMLToken)o);
      return false;
    }

    @Override
    public int hashCode() {
      return this.token.hashCode();
    }

    @Override
    public void toXML(XMLStreamWriter xml) throws XMLStreamException {
      this.token.toXML(xml);
    }

    @Override
    public void toXML(XMLWriter xml) throws IOException {
      this.token.toXML(xml);
    }

    @Override
    public String toString() {
      return this.source+":"+this.token.toString();
    }
  }

}
