package org.pageseeder.diffx.api;

/**
 * Capability for diff algorithms that can choose from which side,
 * tokens are emitted for MATCH operations.
 *
 * @author Christophe Lauret
 *
 * @version 1.3.3
 * @since 1.3.3
 */
public interface MatchPreferenceConfigurable {

  /**
   * @return true if matching tokens should be emitted from the "from" sequence; false means from the "to" sequence.
   */
  boolean isPreferFrom();

  /**
   * @param preferFrom true to emit matching tokens from the "from" sequence; false to emit them from the "to" sequence.
   */
  void setPreferFrom(boolean preferFrom);
}
