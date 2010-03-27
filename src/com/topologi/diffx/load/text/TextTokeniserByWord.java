/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.load.text;

import java.util.NoSuchElementException;

import com.topologi.diffx.event.TextEvent;
import com.topologi.diffx.event.impl.SpaceEvent;
import com.topologi.diffx.event.impl.WordEvent;
import com.topologi.diffx.event.lang.Repertory;

/**
 * A tokeniser for characters events.
 * 
 * <p>This tokeniser is a bit smarter than the other tokeniser, and more configurable;
 * eventually, it will replace the {@link com.topologi.diffx.load.text.CharactersTokeniser}.
 * 
 * @author Christophe Lauret
 * @version 23 December 2004
 */
public final class TextTokeniserByWord implements TextTokeniser {

// constants ----------------------------------------------------------------------------------

  /**
   * Constant for text bytes.
   */
  private static final byte TEXT = 0;

  /**
   * Constant for the white spaces except new lines.
   */
  private static final byte SPACE = 1;

  /**
   * Constant for new lines exclusively.
   */
  private static final byte NEW_LINE = 2;

// class attributes ---------------------------------------------------------------------------

  /**
   * The underlying character sequence.
   */
  private final CharSequence seq;

// state fields -------------------------------------------------------------------------------

  /**
   * The number of tokens.
   */
  private transient int max = -1;

  /**
   * The marker, corresponds to the first character of the next token to be returned.
   */
  private transient int marker = 0;

  /**
   * A repertory of words to use, to reuse words that have already been created.
   */
  private transient Repertory repertory = null;

// methods and constructors -------------------------------------------------------------------

  /**
   * Creates a new tokeniser.
   * 
   * @param cs The character sequence to tokenise.
   * 
   * @throws NullPointerException If the specified character sequence is <code>null</code>.
   */
  public TextTokeniserByWord(CharSequence cs) throws NullPointerException {
    if (cs == null) throw new NullPointerException("The string buffer cannot be null.");
    this.seq = cs;
  }

  /**
   * Calculates the number of times that this tokenizer's <code>nextToken</code> method can be 
   * called before it generates an exception.
   * 
   * @return The number of tokens.
   */
  public int countTokens() {
    // no tokens for empty char sequences
    if (this.seq.length() == 0) return 0;
    // already calculated
    if (this.max >= 0) return this.max;
    // count the number of different types
    byte type = -1;
    int counter = 0;
    for (int i = 0; i < this.seq.length(); i++)
      if (type != getType(seq.charAt(i)) || type == NEW_LINE) {
        type = getType(seq.charAt(i));
        counter++;
      }
    this.max = counter;
    return counter;
  }

  /**
   * Returns the following token.
   * 
   * @return The character event.
   * 
   * @throws NoSuchElementException If the last token has already been returned.
   */
  public TextEvent nextToken() throws NoSuchElementException {
    // there are no more tokens
    if (this.marker == this.seq.length())
      throw new NoSuchElementException("All tokens have been returned.");
    // get the next token
    byte type = getType(this.seq.charAt(this.marker));
    // if it's a newline, we just return it directly
    if (type == NEW_LINE) {
      this.marker++;
      return SpaceEvent.NEW_LINE;
    }
    // otherwise, ew go through each char individually
    for (int i = this.marker; i < this.seq.length(); i++) {
      if (type != getType(this.seq.charAt(i))) {
        TextEvent e = newToken(type, this.marker, i);
        this.marker = i;
        return e;
      }
    }
    // the last token
    TextEvent e = newToken(type, this.marker, this.seq.length());
    this.marker = this.seq.length();
    return e;
  }

  /**
   * Specifies a repertory to use for this tokeniser.
   * 
   * @param rep The repertory to use.
   */
  public void useRepertory(Repertory rep) {
    this.repertory = rep;
  }

  /**
   * Returns the type corresponding to the specified character.
   *  
   * @param c The character to test.
   * 
   * @return The corresponding type.
   */
  private static byte getType(char c) {
    if (c == '\n') return NEW_LINE; 
    else return (Character.isWhitespace(c))? SPACE : TEXT;
  }

  /**
   * Returns the type corresponding to the specified character.
   * 
   * @param type  The type of characters event.
   * @param start The start of the subsequence to take.
   * @param end   The end of the subsequence to take.
   * 
   * @return The corresponding event.
   */
  private TextEvent newToken(byte type, int start, int end) {
    switch (type) {
      case TEXT:
        String word = this.seq.subSequence(start, end).toString();
        if (this.repertory == null)
          return new WordEvent(word);
        else
          return this.repertory.update(word);
      case NEW_LINE:
        return SpaceEvent.NEW_LINE;
      case SPACE:
        return SpaceEvent.getInstance(this.seq.subSequence(start, end));
      default:
        throw new NoSuchElementException("Cannot create token of unknown type.");
    }
  }

}
