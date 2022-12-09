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

import org.pageseeder.diffx.api.Loader;
import org.pageseeder.diffx.token.impl.LineToken;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the contents of a text file as list of line tokens.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.7.0
 */
public final class LineLoader implements Loader<LineToken> {

  /**
   * Loads the contents of the specified file using the default settings from the {@link FileReader}.
   *
   * @param file The file to process.
   *
   * @return The recorded sequence of tokens.
   * @throws IOException Should an I/O error occur.
   */
  @Override
  public List<LineToken> load(File file) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      return getLines(reader);
    }
  }

  /**
   * Loads the contents of the specified file using the charset provided.
   *
   * @param file    The file to process.
   * @param charset THe
   *
   * @return The corresponding sequence of tokens.
   * @throws IOException Should an I/O error occur.
   */
  @Override
  public List<LineToken> load(File file, Charset charset) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), charset))) {
      return getLines(reader);
    }
  }

  /**
   * Loads the contents of the specified file using the charset provided.
   *
   * @return The corresponding sequence of tokens.
   * @throws IOException Should an I/O error occur.
   */
  @Override
  public List<LineToken> load(Reader reader) throws IOException {
    try (BufferedReader buffer = new BufferedReader(reader)) {
      return getLines(buffer);
    }
  }

  /**
   * Runs this loader on the specified string.
   *
   * @param text The text string to process.
   *
   * @return The recorded sequence of tokens.
   */
  @Override
  public List<LineToken> load(String text) {
    try {
      BufferedReader reader = new BufferedReader(new StringReader(text));
      return getLines(reader);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  private List<LineToken> getLines(BufferedReader reader) throws IOException {
    String line = reader.readLine();
    int count = 0;
    List<LineToken> sequence = new ArrayList<>();
    while (line != null) {
      sequence.add(new LineToken(line, ++count));
      line = reader.readLine();
    }
    return sequence;
  }

}
