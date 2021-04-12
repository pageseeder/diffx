package org.pageseeder.diffx.event;

import java.util.List;

public interface ElementEvent extends DiffXEvent {

  /**
   * @return The local name of the element.
   */
  String getName();

  /**
   * @return The namespace URI the element belongs to.
   */
  String getURI();

  /**
   * Returns all the events for this element, starting with the
   * <code>OpenElementEvent</code> and ending with the <code>CloseElementEvent</code>.
   *
   * @return the list of events making up this element
   */
  List<DiffXEvent> getEvents();

}
