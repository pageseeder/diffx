package org.pageseeder.diffx.util;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.api.LoadingException;
import org.pageseeder.diffx.load.SAXLoader;
import org.pageseeder.diffx.load.XMLLoader;
import org.pageseeder.diffx.token.impl.XMLStartElement;
import org.pageseeder.diffx.xml.Sequence;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExtendedWhitespaceStripperTest {

  @Test
  void testStrip_ForElement() {
    ExtendedWhitespaceStripper stripper = newStripper();
    assertEquals(ExtendedWhitespaceStripper.StripWhitespace.ALWAYS, stripper.forElement(new XMLStartElement("table")));
    assertEquals(ExtendedWhitespaceStripper.StripWhitespace.MAYBE, stripper.forElement(new XMLStartElement("td")));
    assertEquals(ExtendedWhitespaceStripper.StripWhitespace.NEVER, stripper.forElement(new XMLStartElement("span")));
  }

  @Test
  void testStrip_RemovesWhitespaceInIgnoredElements() {
    Sequence input = load("<ul>  <li>test</li>  </ul>");
    Sequence expect = load("<ul><li>test</li></ul>");
    ExtendedWhitespaceStripper stripper = newStripper();
    Sequence result = stripper.process(input);
    assertEquals(expect, result);
  }

  /**
   * Tests that whitespace outside the ignored elements is preserved.
   */
  @Test
  void testStrip_PreservesWhitespaceOutsideIgnoredElements() {
    Sequence input = load("<p>Do not <i>remove</i>  </p>");
    ExtendedWhitespaceStripper stripper = newStripper();
    Sequence result = stripper.process(input);
    assertEquals(input, result);
  }

  /**
   * Ensures that elements not included in the ignore list are unaffected.
   */
  @Test
  void testStrip_DoesNotAffectElementsOutsideIgnoreList() {
    Sequence input = load("<ul>  \n<li>  </li>\n</ul>");
    Sequence expect = load("<ul><li></li></ul>");
    ExtendedWhitespaceStripper stripper = newStripper();
    Sequence result = stripper.process(input);
    assertEquals(expect, result);
  }

  @Test
  void testStrip_MixedContent1() {
    Sequence input = load("<ul>  <li> <p>test</p> </li>  </ul>");
    Sequence expect = load("<ul><li><p>test</p></li></ul>");
    ExtendedWhitespaceStripper stripper = newStripper();
    Sequence result = stripper.process(input);
    assertEquals(expect, result);
  }

  @Test
  void testStrip_MixedContent2() {
    Sequence input = load("<ul>  <li> <b>test</b> </li>  </ul>");
    Sequence expect = load("<ul><li> <b>test</b> </li></ul>");
    ExtendedWhitespaceStripper stripper = newStripper();
    Sequence result = stripper.process(input);
    assertEquals(expect, result);
  }

  @Test
  void testStrip_MixedContent3() {
    Sequence input = load("<ul>  <li> <b>test</b> <i>again</i> </li>  </ul>");
    Sequence expect = load("<ul><li> <b>test</b> <i>again</i> </li></ul>");
    ExtendedWhitespaceStripper stripper = newStripper();
    Sequence result = stripper.process(input);
    assertEquals(expect, result);
  }

  @Test
  void testStrip_MixedContent4() {
    Sequence input = load("<ul>  <li> <p>test</p> keep </li>  </ul>");
    Sequence expect = load("<ul><li><p>test</p> keep </li></ul>");
    ExtendedWhitespaceStripper stripper = newStripper();
    Sequence result = stripper.process(input);
    assertEquals(expect, result);
  }

  @Test
  void testStrip_MixedContent5() {
    Sequence input = load("<p>A <i>Simple</i> example.</p>");
    Sequence expect = load("<p>A <i>Simple</i> example.</p>");
    ExtendedWhitespaceStripper stripper = newStripper();
    Sequence result = stripper.process(input);
    assertEquals(expect, result);
  }

  @Test
  void testStrip_MixedContent6() {
    Sequence input = load("<p> A <i>Simple</i> example.\n</p>");
    Sequence expect = load("<p> A <i>Simple</i> example.\n</p>");
    ExtendedWhitespaceStripper stripper = newStripper();
    Sequence result = stripper.process(input);
    assertEquals(expect, result);
  }

  private ExtendedWhitespaceStripper newStripper() {
    ExtendedWhitespaceStripper stripper = new ExtendedWhitespaceStripper();
    stripper.setAlwaysIgnore("article", "ol", "section", "table", "ul");
    stripper.setMaybeIgnore("li", "td", "th", "div", "p");
    return stripper;
  }

  private Sequence load(String xml) {
    try {
      XMLLoader loader = new SAXLoader();
      return loader.load(new StringReader(xml));
    } catch (LoadingException | IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}
