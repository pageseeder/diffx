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
package org.pageseeder.diffx.format;

import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.token.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A formatter which can relay the method calls to multiple formatters.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class MultiplexFormatter implements DiffXFormatter {

  /**
   * the list of formatters to use.
   */
  private final List<DiffXFormatter> formatters;

  /**
   * Creates a new formatter without any underlying formatters.
   */
  public MultiplexFormatter() {
    this.formatters = new ArrayList<>();
  }

  /**
   * Creates a new formatter wrapping the specified formatter.
   *
   * @param f The formatter to use.
   */
  public MultiplexFormatter(DiffXFormatter f) {
    this.formatters = new ArrayList<>(1);
    this.formatters.add(f);
  }

  /**
   * Creates a new formatter wrapping the specified formatter.
   *
   * @param f The formatter to use.
   */
  public MultiplexFormatter(DiffXFormatter... f) {
    this.formatters = new ArrayList<>(f.length);
    this.formatters.addAll(Arrays.asList(f));
  }

  /**
   * Adds a formatter to multiplex.
   *
   * @param f The Diff-X formatter to add.
   */
  public void add(DiffXFormatter f) {
    this.formatters.add(f);
  }

  @Override
  public void format(Token e) throws IOException {
    for (DiffXFormatter f : this.formatters) {
      f.format(e);
    }
  }

  @Override
  public void insert(Token e) throws IOException {
    for (DiffXFormatter f : this.formatters) {
      f.insert(e);
    }
  }

  @Override
  public void delete(Token e) throws IOException, IllegalStateException {
    for (DiffXFormatter f : this.formatters) {
      f.delete(e);
    }
  }

  @Override
  public void setConfig(DiffXConfig config) {
    for (DiffXFormatter f : this.formatters) {
      f.setConfig(config);
    }
  }
}
