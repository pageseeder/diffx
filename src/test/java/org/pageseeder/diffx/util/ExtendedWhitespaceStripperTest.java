package org.pageseeder.diffx.util;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.api.LoadingException;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.load.SAXLoader;
import org.pageseeder.diffx.token.impl.XMLStartElement;
import org.pageseeder.diffx.util.ExtendedWhitespaceStripper.StripWhitespace;
import org.pageseeder.diffx.xml.Namespace;
import org.pageseeder.diffx.xml.Sequence;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;

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

  // --- forElement with namespaced elements ---

  @Test
  void testForElement_WithNamespace() {
    Namespace ns = new Namespace("http://example.com", "ex");
    ExtendedWhitespaceStripper stripper = new ExtendedWhitespaceStripper();
    stripper.setAlwaysIgnore(ns, "container");
    stripper.setMaybeIgnore(ns, "item");
    assertEquals(StripWhitespace.ALWAYS, stripper.forElement(new XMLStartElement("http://example.com", "container")));
    assertEquals(StripWhitespace.LEADING, stripper.forElement(new XMLStartElement("http://example.com", "item")));
    assertEquals(StripWhitespace.NEVER, stripper.forElement(new XMLStartElement("http://example.com", "other")));
    assertEquals(StripWhitespace.NEVER, stripper.forElement(new XMLStartElement("container")));
  }

  // --- replaceByTrailing ---

  @Test
  void testReplaceByTrailing_ReplacesLeadingWithTrailing() {
    ExtendedWhitespaceStripper stripper = newStripper();
    Deque<StripWhitespace> context = new ArrayDeque<>();
    context.push(StripWhitespace.ALWAYS);
    context.push(StripWhitespace.LEADING);
    context.push(StripWhitespace.NEVER);
    stripper.replaceByTrailing(context);
    assertEquals(StripWhitespace.NEVER, context.pop());
    assertEquals(StripWhitespace.TRAILING, context.pop());
    assertEquals(StripWhitespace.ALWAYS, context.pop());
  }

  // --- Whitespace preserved in NEVER context ---

  @Test
  void testStrip_PreservesWhitespaceInNeverContext() {
    assertStrippedInto(
        "<div><b>a</b> <i>b</i></div>",
        "<div><b>a</b> <i>b</i></div>"
    );
  }

  // --- LEADING with non-whitespace text not starting with space ---

  @Test
  void testStrip_LeadingTextWithoutLeadingSpace() {
    assertStrippedInto(
        "<p>hello world</p>",
        "<p>hello world</p>"
    );
  }

  // --- Empty element in always-ignore context ---

  @Test
  void testStrip_EmptyAlwaysIgnoreElement() {
    assertStrippedInto(
        "<ul>   </ul>",
        "<ul></ul>"
    );
  }

  // --- Nested always-ignore elements ---

  @Test
  void testStrip_NestedAlwaysIgnore() {
    assertStrippedInto(
        "<table>  <ul>  <li>text</li>  </ul>  </table>",
        "<table><ul><li>text</li></ul></table>"
    );
  }

  // --- Trailing whitespace preserved before inline element ---

  @Test
  void testStrip_TrailingWhitespaceBeforeInline() {
    assertStrippedInto(
        "<p>text <b>bold</b></p>",
        "<p>text <b>bold</b></p>"
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
