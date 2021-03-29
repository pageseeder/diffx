package org.pageseeder.diffx.event.impl;

import org.pageseeder.diffx.event.CloseElementEvent;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.ElementEvent;
import org.pageseeder.diffx.event.OpenElementEvent;
import org.pageseeder.xmlwriter.XMLWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ElementEventImpl extends DiffXEventBase implements ElementEvent {

  private final List<DiffXEvent> events;

  private final int hashCode;

  public ElementEventImpl(OpenElementEvent open, CloseElementEvent close, List<DiffXEvent> children) {
    this.events = new ArrayList<>();
    this.events.add(open);
    this.events.addAll(children);
    this.events.add(close);
    this.hashCode = toHashCode(this.events);
  }

  @Override
  public String getName() {
    return ((OpenElementEvent)this.events.get(0)).getName();
  }

  @Override
  public String getURI() {
    return ((OpenElementEvent)this.events.get(0)).getURI();
  }

  @Override
  public List<DiffXEvent> getEvents() {
    return this.events;
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  /**
   * Returns <code>true</code> if the event is an open element event.
   *
   * @param e The event to compare with this event.
   *
   * @return <code>true</code> if this event is equal to the specified event;
   *         <code>false</code> otherwise.
   */
  @Override
  public boolean equals(DiffXEvent e) {
    if (e.getClass() != this.getClass()) return false;
    ElementEventImpl element = (ElementEventImpl)e;
    if (element.hashCode != this.hashCode) return false;
    if (element.events.size() != this.events.size()) return false;
    return element.events.equals(this.events);
  }

  @Override
  public String toString() {
    return "element: "+this.getName();
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    for (DiffXEvent event : this.events) {
      event.toXML(xml);
    }
  }

  @Override
  public StringBuffer toXML(StringBuffer xml) {
    for (DiffXEvent event : this.events) {
      event.toXML(xml);
    }
    return xml;
  }

  /**
   * Calculates the hashcode for this event.
   *
   * @param events List of events
   * @return a number suitable as a hashcode.
   */
  private static int toHashCode(List<DiffXEvent> events) {
    int result = 1;
    for (DiffXEvent event : events)
      result = 31 * result + (event == null ? 0 : event.hashCode());
    return result;
  }

}
