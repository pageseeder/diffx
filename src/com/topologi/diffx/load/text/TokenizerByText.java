/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.load.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.topologi.diffx.config.TextGranularity;
import com.topologi.diffx.event.TextEvent;
import com.topologi.diffx.event.impl.CharactersEvent;

/**
 * The tokeniser for characters events.
 * 
 * <p>This class is not synchronized.
 * 
 * @author Christophe Lauret
 * @version 10 May 2010
 */
public final class TokenizerByText implements TextTokenizer {

  /**
   * Creates a new tokenizer.
   */
  public TokenizerByText() {
  }

  /**
   * {@inheritDoc}
   */
  public List<TextEvent> tokenize(CharSequence seq) {
    if (seq == null) return null;
    if (seq.length() == 0) return Collections.emptyList();
    List<TextEvent> events = new ArrayList<TextEvent>(seq.length());
    // TODO change behaviour depending on whitespace processing
    events.add(new CharactersEvent(seq));
    return events;
  }

  /**
   * Always <code>TextGranularity.CHARACTER</code>.
   * 
   * {@inheritDoc}
   */
  public TextGranularity granurality() {
    return TextGranularity.TEXT;
  }
}
