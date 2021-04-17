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
package org.pageseeder.diffx.load;

import org.pageseeder.diffx.sequence.Sequence;
import org.xml.sax.InputSource;

import java.io.IOException;

/**
 * Defines recorders that are specific to XML.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.6.0
 */
public interface XMLRecorder extends Recorder {

  /**
   * Runs the recorder on the specified input source.
   *
   * @param is The input source.
   *
   * @return The recorded sequence of tokens.
   *
   * @throws LoadingException If thrown whilst parsing.
   * @throws IOException      Should I/O error occur.
   */
  Sequence process(InputSource is) throws LoadingException, IOException;

}
