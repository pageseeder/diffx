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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.event.TextEvent;
import org.pageseeder.diffx.event.impl.CharactersEvent;
import org.pageseeder.diffx.event.impl.SpaceEvent;

/**
 * The tokenizer for characters events.
 *
 * <p>This class is not synchronized.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TokenizerByChar implements TextTokenizer {

  /**
   * Map characters to events in order to recycle events as they are created.
   */
  private final Map<Character, TextEvent> recycling = new HashMap<>();

  /**
   * Creates a new tokenizer.
   */
  public TokenizerByChar() {
  }

  @Override
  public List<TextEvent> tokenize(CharSequence seq) {
    if (seq == null) throw new NullPointerException("Character sequence is null");
    if (seq.length() == 0) return Collections.emptyList();
    List<TextEvent> events = new ArrayList<>(seq.length());
    Character c = null;
    for (int i=0; i < seq.length(); i++) {
      c = Character.valueOf(seq.charAt(i));
      TextEvent e = this.recycling.get(c);
      if (e == null) {
        if (Character.isWhitespace(c.charValue())) {
          e = SpaceEvent.getInstance(c);
        } else {
          e = new CharactersEvent(c+"");
        }
      }
      events.add(e);
    }
    return events;
  }

  /**
   * Always <code>TextGranularity.CHARACTER</code>.
   */
  @Override
  public TextGranularity granularity() {
    return TextGranularity.CHARACTER;
  }
}
