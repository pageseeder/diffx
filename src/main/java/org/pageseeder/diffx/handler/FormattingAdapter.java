package org.pageseeder.diffx.handler;

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.format.DiffXFormatter;

import java.io.IOException;

public class FormattingAdapter implements DiffHandler {

  private final DiffXFormatter formatter;

  public FormattingAdapter(DiffXFormatter formatter) {
    this.formatter = formatter;
  }

  @Override
  public void handle(Operator operator, DiffXEvent event) throws IOException, IllegalStateException {
    switch (operator) {
      case MATCH:
        this.formatter.format(event);
        break;
      case INS:
        this.formatter.insert(event);
        break;
      case DEL:
        this.formatter.delete(event);
        break;
      default:
        // Ignore
    }
  }
}
