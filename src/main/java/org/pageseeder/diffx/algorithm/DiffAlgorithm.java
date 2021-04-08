package org.pageseeder.diffx.algorithm;

import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.handler.DiffHandler;

import java.io.IOException;
import java.util.List;

public interface DiffAlgorithm {

  /**
   * Performs the comparison and uses the specified handler.
   *
   * @param first   The first list of events to compare (inserted)
   * @param second  The first list of events to compare (deleted)
   * @param handler The handler for the results of the comparison
   *
   * @throws IOException If thrown by the formatter.
   */
  void diff(List<? extends DiffXEvent> first, List<? extends DiffXEvent> second, DiffHandler handler) throws IOException;

}
