/*
 * Copyright 2010-2015 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.load.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.event.TextEvent;
import org.pageseeder.diffx.event.impl.CharactersEvent;
import org.pageseeder.diffx.event.impl.IgnorableSpaceEvent;
import org.pageseeder.diffx.event.impl.SpaceEvent;

/**
 * The tokeniser for characters events.
 *
 * <p>This class is not synchronized.
 *
 * @author Christophe Lauret
 * @version 11 May 2010
 */
public final class TokenizerByText implements TextTokenizer {

  /**
   * Define the whitespace processing.
   */
  private final WhiteSpaceProcessing whitespace;

  /**
   * Creates a new tokenizer.
   *
   * @param whitespace the whitespace processing for this tokenizer.
   *
   * @throws NullPointerException if the white space processing is not specified.
   */
  public TokenizerByText(WhiteSpaceProcessing whitespace) {
    if (whitespace == null) throw new NullPointerException("the white space processing must be specified.");
    this.whitespace = whitespace;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<TextEvent> tokenize(CharSequence seq) {
    if (seq == null) return null;
    if (seq.length() == 0) return Collections.emptyList();
    int x = TokenizerUtils.getLeadingWhiteSpace(seq);
    int y = TokenizerUtils.getTrailingWhiteSpace(seq);
    // no leading or trailing spaces return a singleton in all configurations
    if (x == 0 && y == 0) {
      TextEvent e = new CharactersEvent(seq);
      return Collections.singletonList(e);
    }
    // The text node is only white space (white space = trailing space)
    if (x == seq.length()) {
      switch (this.whitespace) {
        case COMPARE:
          return Collections.singletonList((TextEvent)SpaceEvent.getInstance(seq.toString()));
        case PRESERVE:
          return Collections.singletonList((TextEvent)new IgnorableSpaceEvent(seq.toString()));
        case IGNORE:
          return Collections.emptyList();
        default:
      }
      TextEvent e = new CharactersEvent(seq);
      return Collections.singletonList(e);
    }
    // some trailing or leading whitespace, behaviour changes depending on whitespace processing
    List<TextEvent> events = null;
    switch (this.whitespace) {
      case COMPARE:
        events = new ArrayList<TextEvent>(1 + (x > 0 ? 1 : 0) + (y > 0 ? 1 : 0));
        if (x > 0) {
          events.add(SpaceEvent.getInstance(seq.subSequence(0, x)));
        }
        events.add(new CharactersEvent(seq.subSequence(x, seq.length()-y)));
        if (y > 0) {
          events.add(SpaceEvent.getInstance(seq.subSequence(seq.length()-y, seq.length())));
        }
        break;
      case PRESERVE:
        events = new ArrayList<TextEvent>(1 + (x > 0 ? 1 : 0) + (y > 0 ? 1 : 0));
        if (x > 0) {
          events.add(new IgnorableSpaceEvent(seq.subSequence(0, x)));
        }
        events.add(new CharactersEvent(seq.subSequence(x, seq.length()-y)));
        if (y > 0) {
          events.add(new IgnorableSpaceEvent(seq.subSequence(seq.length()-y, seq.length())));
        }
        break;
      case IGNORE:
        TextEvent e = new CharactersEvent(seq.subSequence(x, seq.length()-y));
        events = Collections.singletonList(e);
        break;
      default:
    }
    return events;
  }

  /**
   * Always <code>TextGranularity.CHARACTER</code>.
   *
   * {@inheritDoc}
   */
  @Override
  public TextGranularity granurality() {
    return TextGranularity.TEXT;
  }

}
