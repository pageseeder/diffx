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

import java.io.File;
import java.io.IOException;

import org.pageseeder.diffx.sequence.EventSequence;

/**
 * A class implementing this interface must be able to produce a sequence of event
 * from a specified input.
 *
 * @author Christophe Lauret
 * @version 0.6.0
 */
public interface Recorder {

  /**
   * Runs the recorder on the specified file.
   *
   * @param file The file to process.
   *
   * @return The recorded sequence of events.
   *
   * @throws LoadingException If thrown while parsing.
   * @throws IOException      Should I/O error occur.
   */
  EventSequence process(File file) throws LoadingException, IOException;

  /**
   * Runs the recorder on the specified string.
   *
   * @param xml The string to process.
   *
   * @return The recorded sequence of events.
   *
   * @throws LoadingException If thrown while parsing.
   * @throws IOException      Should I/O error occur.
   */
  EventSequence process(String xml) throws LoadingException, IOException;

}
