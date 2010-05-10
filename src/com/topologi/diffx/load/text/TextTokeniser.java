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
import com.topologi.diffx.event.lang.Repertory;

/**
 * An interface for text tokenisers.
 * 
 * <p>Text tokenisers are used to return {@link com.topologi.diffx.event.TextEvent}
 * from a piece of text.
 * 
 * @deprecated use <code>TextTokenizer</code> instead
 * 
 * @author Christophe Lauret
 * @version 3 February 2005
 */
public interface TextTokeniser {

  /**
   * Calculates the number of times that this tokenizer's <code>nextToken</code> method can be 
   * called before it generates an exception.
   * 
   * @return The number of tokens.
   */
  int countTokens();

  /**
   * Returns the following token.
   * 
   * @return The character event.
   * 
   * @throws NoSuchElementException If the last token has already been returned.
   */
  TextEvent nextToken() throws NoSuchElementException;

  /**
   * Specifies a repertory to use for this tokeniser.
   * 
   * <p>Tokenisers don't have to use a repertory, but they should specify whether they
   * will use the repertory or not.
   * 
   * @param repertory The repertory to use.
   */
  void useRepertory(Repertory repertory);

}