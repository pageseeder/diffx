package org.pageseeder.diffx.core;


import org.pageseeder.diffx.algorithm.GuanoAlgorithm;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.handler.CoalescingFilter;
import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.handler.HandlerFormatter;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;
import java.util.List;

public class DefaultXMLProcessor implements DiffProcessor {

  private boolean coalesce = false;

  /**
   * Set whether to consecutive text operations should be coalesced into a single operation.
   *
   * @param coalesce <code>true</code> to coalesce; <code>false</code> to leave a separate operations.
   */
  public void setCoalesce(boolean coalesce) {
    this.coalesce = coalesce;
  }

  @Override
  public void process(List<? extends DiffXEvent> first, List<? extends DiffXEvent> second, DiffHandler handler) throws IOException {
    EventSequence seq1 = new EventSequence();
    for (DiffXEvent event : first) seq1.addEvent(event);
    EventSequence seq2 = new EventSequence();
    for (DiffXEvent event : second) seq2.addEvent(event);
    GuanoAlgorithm algorithm = new GuanoAlgorithm(seq1, seq2);

    HandlerFormatter formatter = new HandlerFormatter(getFilter(handler));
    handler.start();
    algorithm.process(formatter);
    handler.end();
  }

  private DiffHandler getFilter(DiffHandler handler) {
    return this.coalesce ? new CoalescingFilter(handler) : handler;
  }

  @Override
  public String toString() {
    return "DefaultXMLProcessor{" +
        "coalesce=" + coalesce +
        '}';
  }
}
