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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.pageseeder.diffx.event.impl.LineEvent;
import org.pageseeder.diffx.sequence.EventSequence;

/**
 * Records the line events in a text.
 *
 * @author Christophe Lauret
 *
 * @version 17 October 2006
 */
public final class TextRecorder implements Recorder {

  /**
   * Runs the recorder on the specified file.
   *
   * <p>This method will count on the {@link org.xml.sax.InputSource} to guess the correct encoding.
   *
   * @param file The file to process.
   *
   * @return The recorded sequence of events.
   *
   * @throws LoadingException If thrown whilst parsing.
   * @throws IOException      Should I/O error occur.
   */
  @Override
  public EventSequence process(File file) throws LoadingException, IOException {
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line = reader.readLine();
    int count = 0;
    EventSequence seq = new EventSequence();
    while (line != null) {
      seq.addEvent(new LineEvent(line, ++count));
      line = reader.readLine();
    }
    reader.close();
    return seq;
  }

  /**
   * Runs this recorder on the specified string.
   *
   * @param text The text string to process.
   *
   * @return The recorded sequence of events.
   *
   * @throws LoadingException If thrown whilst parsing.
   * @throws IOException      Should I/O error occur.
   */
  @Override
  public EventSequence process(String text) throws LoadingException, IOException {
    BufferedReader reader = new BufferedReader(new StringReader(text));
    String line = reader.readLine();
    int count = 0;
    EventSequence seq = new EventSequence();
    while (line != null) {
      seq.addEvent(new LineEvent(line, ++count));
      line = reader.readLine();
    }
    return seq;
  }

}
