package org.pageseeder.diffx.handler;

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.format.DiffXFormatter;

import java.io.IOException;

public class HandlerFormatter implements DiffXFormatter {

  private final DiffHandler handler;

  public HandlerFormatter(DiffHandler handler) {
    this.handler = handler;
  }

  @Override
  public void format(DiffXEvent event) throws IOException, IllegalStateException {
    this.handler.handle(Operator.MATCH, event);
  }

  @Override
  public void insert(DiffXEvent event) throws IOException, IllegalStateException {
    this.handler.handle(Operator.INS, event);
  }

  @Override
  public void delete(DiffXEvent event) throws IOException, IllegalStateException {
    this.handler.handle(Operator.DEL, event);
  }

  @Override
  public void setConfig(DiffXConfig config) {

  }
}
