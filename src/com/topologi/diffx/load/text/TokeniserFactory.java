/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.load.text;

import com.topologi.diffx.config.DiffXConfig;

/**
 * Factory for tokenisers.
 * 
 * <p>This class is designed to returned tokenisers that corresponds to the given
 * configuration. 
 * 
 * @deprecated use TokenizerFactory instead
 * 
 * @author Christophe Lauret
 * @version 27 April 2005
 */
public final class TokeniserFactory {

// class attributes -------------------------------------------------------------------------------

  /**
   * Indicates whether the factory should generate namespace events. 
   */
  private final DiffXConfig config;

// constructors -----------------------------------------------------------------------------------

  /**
   * Creates a factory for tokenisers.
   * 
   * @param config The configuration to use.
   * 
   * @throws NullPointerException If the configuration is <code>null</code>.
   */
  public TokeniserFactory(DiffXConfig config) throws NullPointerException {
    if (config == null) throw new NullPointerException("Factory requires a tokeniser."); 
    this.config = config;
  }

// methods ------------------------------------------------------------------------------

  /**
   * Returns the text tokeniser for the specified text according to the
   * configuration of this tokeniser.
   * 
   * @param text The text to tokenize.
   * 
   * @return The corresponding text tokeniser.
   */
  public TextTokeniser makeTokeniser(CharSequence text) {
    switch(config.getWhiteSpaceProcessing()) {
    case COMPARE: // consider + preserve [COMPARE]
      return new TextTokeniserByWord(text);
    case PRESERVE: // ignore + preserve [PRESERVE]
      return new TextTokeniserIgnoreSpace(text);
    case IGNORE: // ignore + trash [IGNORE]
      return new TextTokeniserNoSpace(text);
    default:
      throw new IllegalStateException("Unsupported whitespace configuration: "+config.getWhiteSpaceProcessing()); 
    }
  }

  /**
   * Returns the configuration used by this factory.
   * 
   * @return the configuration used by this factory.
   */
  public DiffXConfig getConfig() {
    return this.config;
  }

}
