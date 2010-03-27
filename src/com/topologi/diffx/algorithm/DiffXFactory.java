/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.algorithm;

import java.lang.reflect.Constructor;

import com.topologi.diffx.sequence.EventSequence;

/**
 * Factory for creating a Diff-X algorithm instance.
 * 
 * @author  Christophe Lauret
 * @version 3 February 2005
 */
public final class DiffXFactory {

  /**
   * Private constructor.
   *
   * <p>This constructor prevents the class from being instantiated.
   */
  private DiffXFactory() {
  }

  /**
   * The classes of the arguments of the constructor.
   */
  private static final Class[] ARGS = new Class[]{EventSequence.class, EventSequence.class};

  /**
   * Creates a diffex instance using the specified class name and event sequences.
   * 
   * @param className The class name of the diffex implementation to use.
   * @param sequence1 The first sequence to use for the diffex constructor.
   * @param sequence2 The second sequence to use for the diffex constructor.
   * 
   * @return A Diff-X algorithm instance.
   * 
   * @throws FactoryException Should an error occur when trying to instantiate the class. 
   */
  public static DiffXAlgorithm createDiffex(String className,
                                            EventSequence sequence1,
                                            EventSequence sequence2) 
     throws FactoryException {
    DiffXAlgorithm diffex = null;
    try {
      Class cls = Class.forName(className);
      Constructor cons = cls.getConstructor(ARGS);
      diffex = (DiffXAlgorithm)cons.newInstance(new EventSequence[]{sequence1, sequence2});
    } catch (Exception ex) {
      throw new FactoryException(ex);
    }
    return diffex;
  }

}
