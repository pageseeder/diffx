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
import com.topologi.diffx.event.impl.WordEvent;
import com.topologi.diffx.event.lang.Repertory;

/**
 * A tokeniser for text events that ignore and does not preserve whitespaces.
 * 
 * <p>This tokeniser works at the word level, and will essetially prune out all the
 * white spaces from the text to return only a list of successive words.
 * 
 * @author Christophe Lauret
 * @version 23 December 2004
 */
public final class TextTokeniserNoSpace implements TextTokeniser {

// constants ----------------------------------------------------------------------------------

  /**
   * Constant for text bytes.
   */
  private static final byte TEXT = 0;

  /**
   * Constant for the white spaces except new lines.
   */
  private static final byte WHITE = 1;

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
  public TextTokeniserNoSpace(CharSequence cs) throws NullPointerException {
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
    byte nextType = -1;
    for (int i = 0; i < this.seq.length(); i++) {
      nextType = getType(seq.charAt(i));
      if (type != nextType) {
        type = getType(seq.charAt(i));
        if (nextType == TEXT) counter++;
      }
    }
    this.max = counter;
    return counter;
  }

  /**
   * Returns the following token.
   * 
   * @return The next text event.
   * 
   * @throws NoSuchElementException If the last token has already been returned.
   */
  public TextEvent nextToken() throws NoSuchElementException {
    // there are no more tokens
    if (this.marker == this.seq.length())
      throw new NoSuchElementException("All tokens have been returned.");
    // get the next token
    byte type = getType(this.seq.charAt(this.marker));
    byte nextType = 1;
    for (int i = this.marker; i < this.seq.length(); i++) {
      nextType = getType(this.seq.charAt(i));
      if (type != nextType) {
        if (type == TEXT) {
          TextEvent e = newToken(type, this.marker, i);
          this.marker = i;
          return e;
        } else {
          type = nextType;
          this.marker = i;
        }
      }
    }
    // the last token
    if (type == TEXT) {
      TextEvent e = newToken(type, this.marker, this.seq.length());
      this.marker = this.seq.length();
      return e;
    } else {
      this.marker = this.seq.length();
      throw new NoSuchElementException("All tokens have been returned.");
    }
  }

  /**
   * Specifies a repertory to use for this tokeniser.
   * 
   * @param rep The repertory to use.
   */
  public void useRepertory(Repertory rep) {
    this.repertory = rep;
  }

// private helpers ----------------------------------------------------------------------------

  /**
   * Returns the type corresponding to the specified character.
   *  
   * @param c The character to test.
   * 
   * @return The corresponding type.
   */
  private static byte getType(char c) {
    return (Character.isWhitespace(c))? WHITE : TEXT;
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
      default:
        throw new NoSuchElementException("Cannot create token of unknown type.");
    }
  }

}
