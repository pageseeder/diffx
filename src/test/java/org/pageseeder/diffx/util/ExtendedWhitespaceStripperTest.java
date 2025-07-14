package org.pageseeder.diffx.util;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.api.LoadingException;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.load.SAXLoader;
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
    assertEquals(ExtendedWhitespaceStripper.StripWhitespace.LEADING, stripper.forElement(new XMLStartElement("td")));
    assertEquals(ExtendedWhitespaceStripper.StripWhitespace.NEVER, stripper.forElement(new XMLStartElement("span")));
  }

  @Test
  void testStrip_RemovesWhitespaceInIgnoredElements() {
    assertStrippedInto(
        "<ul>  <li>test</li>  </ul>",
        "<ul><li>test</li></ul>"
    );
  }

  @Test
  void testStrip_RemovesWhitespaceInIgnoredElements2() {
    assertStrippedInto(
        "<ul>  <li>  test  </li>  </ul>",
        "<ul><li>test</li></ul>"
    );
  }

  @Test
  void testStrip_RemovesWhitespaceInIgnoredElements3() {
    assertStrippedInto(
        "<ul>  <li>  multiple  words  </li>  </ul>",
        "<ul><li>multiple  words</li></ul>"
    );
  }

  @Test
  void testStrip_RemovesWhitespaceInIgnoredElements4() {
    assertStrippedInto(
        "<ul>  <li>  multiple  words  </li>  <li>  <p> test </p>  </li></ul>",
        "<ul><li>multiple  words</li><li><p>test</p></li></ul>"
    );
  }

  /**
   * Tests that whitespace outside the ignored elements is preserved.
   */
  @Test
  void testStrip_PreservesWhitespaceOutsideIgnoredElements() {
    assertStrippedInto(
        "<p>Do not <i>remove</i>  </p>",
        "<p>Do not <i>remove</i></p>"
    );
  }

  /**
   * Ensures that elements not included in the ignore list are unaffected.
   */
  @Test
  void testStrip_DoesNotAffectElementsOutsideIgnoreList() {
    assertStrippedInto(
        "<ul>  \n<li>  </li>\n</ul>",
        "<ul><li></li></ul>"
    );
  }

  @Test
  void testStrip_MixedContent1() {
    assertStrippedInto(
        "<ul>  <li> <p>test</p> </li>  </ul>",
        "<ul><li><p>test</p></li></ul>"
    );
  }

  @Test
  void testStrip_MixedContent2() {
    assertStrippedInto(
        "<ul>  <li> <b>test</b> </li>  </ul>",
        "<ul><li><b>test</b></li></ul>"
    );
  }

  @Test
  void testStrip_MixedContent3() {
    assertStrippedInto(
        "<ul>  <li> <b>test</b> <i>again</i> </li>  </ul>",
        "<ul><li><b>test</b> <i>again</i></li></ul>"
    );
  }

  @Test
  void testStrip_MixedContent4() {
    assertStrippedInto(
        "<ul>  <li> <p>test</p> keep </li>  </ul>",
        "<ul><li><p>test</p> keep</li></ul>"
    );
  }

  @Test
  void testStrip_MixedContent5() {
    assertStrippedInto(
        "<p>A <i>Simple</i> example.</p>",
        "<p>A <i>Simple</i> example.</p>"
    );
  }

  @Test
  void testStrip_MixedContent6() {
    assertStrippedInto(
        "<p> A <i>Simple</i> example.\n</p>",
        "<p>A <i>Simple</i> example.</p>"
    );
  }

  @Test
  void testStrip_MixedContent7() {
    assertStrippedInto(
        "<p><i>A Simple</i> example.\n</p>",
        "<p><i>A Simple</i> example.</p>"
    );
  }

  private ExtendedWhitespaceStripper newStripper() {
    ExtendedWhitespaceStripper stripper = new ExtendedWhitespaceStripper();
    stripper.setAlwaysIgnore("article", "ol", "section", "table", "ul");
    stripper.setMaybeIgnore("div", "li", "td", "th", "p");
    return stripper;
  }

  private Sequence load(String xml) {
    try {
      SAXLoader loader = new SAXLoader();
      loader.setConfig(DiffConfig.getDefault().granularity(TextGranularity.SPACE_WORD));
      return loader.load(new StringReader(xml));
    } catch (LoadingException | IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private void assertStrippedInto(String source, String expect) {
    Sequence input = load(source);
    Sequence exp = load(expect);
    Sequence got = newStripper().process(input);
    assertEquals(exp.tokens(), got.tokens());
  }

}
