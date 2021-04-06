package org.pageseeder.diffx.handler;

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.event.DiffXEvent;

import java.io.IOException;
import java.util.logging.Handler;

public class MuxHandler implements DiffHandler {

  private final DiffHandler[] handlers;

  public MuxHandler(DiffHandler ...handlers) {
    this.handlers = handlers;
  }

  @Override
  public void start() {
    for (DiffHandler handler : handlers) handler.start();
  }

  @Override
  public void handle(Operator operator, DiffXEvent event) throws IOException, IllegalStateException {
    for (DiffHandler handler : handlers) handler.handle(operator, event);
  }

  @Override
  public void end() {
    for (DiffHandler handler : handlers) handler.end();
  }
}
