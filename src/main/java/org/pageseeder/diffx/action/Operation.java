package org.pageseeder.diffx.action;

import org.pageseeder.diffx.event.DiffXEvent;

/**
 * An atomic Diff operation associated with a single event.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class Operation {

  private final Operator operator;

  private final DiffXEvent event;

  public Operation(Operator operator, DiffXEvent event) {
    this.operator = operator;
    this.event = event;
  }

  public Operator operator() {
    return operator;
  }

  public DiffXEvent event() {
    return event;
  }

  @Override
  public int hashCode() {
    return this.operator.hashCode()+ 31*this.event.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Operation) return equals((Operation)obj);
    return false;
  }

  public boolean equals(Operation operation) {
    if (operation == null)
      return false;
    if (operation == this)
      return true;
    return operation.operator == this.operator && operation.event.equals(this.event);
  }

  @Override
  public String toString() {
    return this.operator.toString()+this.event;
  }

  /**
   * @return the reserve operation by swapping INS with DEL.
   */
  public Operation reverse() {
    switch (this.operator) {
      case DEL:
        return new Operation(Operator.INS, this.event);
      case INS:
        return new Operation(Operator.DEL, this.event);
      default:
        return this;
    }
  }

}
