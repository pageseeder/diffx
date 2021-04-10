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
package org.pageseeder.diffx.core;

import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.handler.DiffHandler;

import java.io.IOException;
import java.util.List;

/**
 * A raw diff algorithm.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public interface DiffAlgorithm {

  /**
   * Performs the comparison and report changes to the specified handler.
   *
   * @param first   The first list of events to compare (inserted)
   * @param second  The first list of events to compare (deleted)
   * @param handler The handler for the results of the comparison
   *
   * @throws IOException If thrown by the handler.
   */
  void diff(List<? extends DiffXEvent> first, List<? extends DiffXEvent> second, DiffHandler handler) throws IOException;

}