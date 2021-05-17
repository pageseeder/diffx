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
package org.pageseeder.diffx.algorithm;

import org.pageseeder.diffx.sequence.XMLSequence;

import java.lang.reflect.Constructor;

/**
 * Factory for creating a Diff-X algorithm instance.
 *
 * @author Christophe Lauret
 * @version 11 May 2010
 */
@Deprecated
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
  private static final Class<?>[] ARGS = new Class<?>[]{XMLSequence.class, XMLSequence.class};

  /**
   * Creates a Diff-X instance using the specified class name and token sequences.
   *
   * @param className The class name of the Diff-X algorithm implementation to use.
   * @param sequence1 The first sequence to use for the Diff-X constructor.
   * @param sequence2 The second sequence to use for the Diff-X constructor.
   *
   * @return A Diff-X algorithm instance.
   * @throws FactoryException Should an error occur when trying to instantiate the class.
   */
  @SuppressWarnings("unchecked")
  public static DiffXAlgorithm newAlgorithm(String className, XMLSequence sequence1, XMLSequence sequence2)
      throws FactoryException {
    DiffXAlgorithm algorithm;
    try {
      Class<DiffXAlgorithm> cls = (Class<DiffXAlgorithm>) Class.forName(className);
      Constructor<DiffXAlgorithm> cons = cls.getConstructor(ARGS);
      algorithm = cons.newInstance(sequence1, sequence2);
    } catch (Exception ex) {
      throw new FactoryException(ex);
    }
    return algorithm;
  }

}
