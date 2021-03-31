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

import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.event.TextEvent;
import org.pageseeder.diffx.event.impl.IgnorableSpaceEvent;
import org.pageseeder.diffx.event.impl.SpaceEvent;
import org.pageseeder.diffx.event.impl.WordEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The tokenizer for characters events.
 *
 * <p>This class is not synchronized.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class TokenizerBySpaceWord implements TextTokenizer {

  /**
   * Map characters to events in order to recycle events as they are created.
   */
  private final Map<String, TextEvent> recycling = new HashMap<>();

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
  public TokenizerBySpaceWord(WhiteSpaceProcessing whitespace) {
    if (whitespace == null) throw new NullPointerException("the white space processing must be specified.");
    this.whitespace = whitespace;
  }

  @Override
  public List<TextEvent> tokenize(CharSequence seq) {
    if (seq == null) throw new NullPointerException("Character sequence is null");
    if (seq.length() == 0) return Collections.emptyList();
    // We assume that on average we generate 1 event per 4 chars
    List<TextEvent> events = new ArrayList<>(seq.length() / 4);

    Pattern p = Pattern.compile("( ?[A-Za-z_'-]+)|(\\S)");
    Matcher m = p.matcher(seq);
    int index = 0;

    // Add segments before each match found
    while (m.find()) {
      if (index != m.start() && whitespace != WhiteSpaceProcessing.IGNORE) {
        String space = seq.subSequence(index, m.start()).toString();
        events.add(getSpaceEvent(space));
      }
      // We don't even need to record a white space if they are ignored!
      String word = seq.subSequence(m.start(), m.end()).toString();
      events.add(getWordEvent(word));
      index = m.end();
    }

    // Add remaining word if any
    if (index != seq.length()) {
      String space = seq.subSequence(index, seq.length()).toString();
      events.add(getSpaceEvent(space));
    }

    return events;
  }

  /**
   * Always <code>TextGranularity.WORD</code>.
   */
  @Override
  public TextGranularity granularity() {
    return TextGranularity.WORD;
  }

  public static List<TextEvent> tokenize(CharSequence seq, WhiteSpaceProcessing whitespace) {
    TokenizerBySpaceWord tokenizer = new TokenizerBySpaceWord(whitespace);
    return tokenizer.tokenize(seq);
  }

  /**
   * Returns the word event corresponding to the specified characters.
   *
   * @param word the characters of the word
   * @return the corresponding word event
   */
  private TextEvent getWordEvent(String word) {
    TextEvent e = this.recycling.get(word);
    if (e == null) {
      e = new WordEvent(word);
      this.recycling.put(word, e);
    }
    return e;
  }

  /**
   * Returns the space event corresponding to the specified characters.
   *
   * @param space the characters of the space
   * @return the corresponding space event
   */
  private TextEvent getSpaceEvent(String space) {
    // preserve the actual white space used
    TextEvent e = this.recycling.get(space);
    if (e == null) {
      if (this.whitespace == WhiteSpaceProcessing.PRESERVE) {
        e = new IgnorableSpaceEvent(space);
      } else {
        e = SpaceEvent.getInstance(space);
      }
      this.recycling.put(space, e);
    }
    return e;
  }

}
