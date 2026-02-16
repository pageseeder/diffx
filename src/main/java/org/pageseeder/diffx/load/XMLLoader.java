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
import org.pageseeder.diffx.api.LoadingException;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.xml.Sequence;
import org.xml.sax.InputSource;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * Defines loaders that are specific to XML.
 *
 * <p>Default implementations are provided to read the contents to load from a file or a string.
 *
 * <p>Loaders should be implemented so that they can be reused when performing a comparison,
 * for example, to load the contents of the files or strings to compare. But they are not
 * expected to be thread safe.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.6.0
 */
public interface XMLLoader extends Loader<XMLToken> {

  /**
   * Loads the XML tokens from the specified input source.
   *
   * @param is The input source.
   *
   * @return The recorded sequence of tokens.
   * @throws LoadingException If thrown whilst parsing.
   * @throws IOException      Should an I/O error occur.
   */
  Sequence load(InputSource is) throws LoadingException, IOException;

  /**
   * Loads the content of the specified file as a sequence of XML tokens.
   *
   * <p>This method relies on the {@link InputSource} to guess the correct encoding.
   *
   * @param file The file to process.
   *
   * @return The recorded sequence of tokens.
   * @throws LoadingException If thrown while parsing.
   * @throws IOException      Should an I/O error occur.
   */
  @Override
  default Sequence load(File file) throws LoadingException, IOException {
    try (InputStream in = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
      return load(new InputSource(in));
    }
  }

  /**
   * Loads the content of the specified file as a sequence of XML tokens using the charset provided.
   *
   * @param file    The file to process.
   * @param charset Charset for reading the file.
   *
   * @return The recorded sequence of tokens.
   * @throws LoadingException If thrown while parsing.
   * @throws IOException      Should an I/O error occur.
   */
  @Override
  default Sequence load(File file, Charset charset) throws LoadingException, IOException {
    try (Reader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), charset))) {
      return load(new InputSource(reader));
    }
  }

  /**
   * Loads the content of the specified reader as a sequence of XML tokens.
   *
   * @param reader The reader to process.
   *
   * @return The recorded sequence of tokens.
   * @throws LoadingException If thrown while parsing.
   * @throws IOException      Should an I/O error occur.
   */
  @Override
  default Sequence load(Reader reader) throws LoadingException, IOException {
    return this.load(new InputSource(reader));
  }

  /**
   * Parse the contents of the specified string as a sequence of XML tokens.
   *
   * <p>This method is provided for convenience. It is best to only use this method for
   * short strings.
   *
   * @param source The XML string to process.
   *
   * @return The corresponding sequence of tokens.
   * @throws LoadingException If thrown while parsing.
   */
  @Override
  default Sequence load(String source) throws LoadingException {
    try {
      return this.load(new InputSource(new StringReader(source)));
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

}
