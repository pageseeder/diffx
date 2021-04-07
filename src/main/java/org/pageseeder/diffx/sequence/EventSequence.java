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
package org.pageseeder.diffx.sequence;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.pageseeder.diffx.event.DiffXEvent;

/**
 * A sequence of events used for the Diff-X algorithm.
 *
 * <p>This class wraps a list of <code>DiffXEvent</code>s and provide method to
 * access and modify the content of the list using strongly typed methods.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 *
 * @since 0.7
 */
public final class EventSequence {

  // Class attributes ---------------------------------------------------------------------------

  /**
   * The prefix mapping for the elements in this sequence.
   */
  private final PrefixMapping prefixMapping = new PrefixMapping();

  /**
   * The sequence of events.
   */
  private final List<DiffXEvent> sequence;

  // Constructors -------------------------------------------------------------------------------

  /**
   * Creates a new event sequence.
   */
  public EventSequence() {
    this.sequence = new LinkedList<>();
  }

  /**
   * Creates a new event sequence of the specified size.
   *
   * @param size The size of the sequence.
   */
  public EventSequence(int size) {
    this.sequence = new ArrayList<>(size);
  }

  /**
   * Creates a new event sequence of the specified size.
   *
   * @param events The size of the sequence.
   */
  public EventSequence(List<DiffXEvent> events) {
    this.sequence = events;
  }

  /**
   * Adds a sequence of events to this sequence.
   *
   * @param seq The sequence of events to be added.
   */
  public void addSequence(EventSequence seq) {
    this.sequence.addAll(seq.sequence);
  }

  /**
   * Adds an event to this sequence.
   *
   * @param e The event to be added.
   */
  public void addEvent(DiffXEvent e) {
    this.sequence.add(e);
  }

  /**
   * Inserts an event to this sequence at the specified position.
   *
   * @param i The position of the event.
   * @param e The event to be added.
   */
  public void addEvent(int i, DiffXEvent e) {
    this.sequence.add(i, e);
  }


  /**
   * Adds an event to this sequence.
   *
   * @param events The event to be added.
   */
  public void addEvents(List<? extends DiffXEvent> events) {
    this.sequence.addAll(events);
  }

  /**
   * Returns the event at position i.
   *
   * @param i The position of the event.
   *
   * @return the event at position i.
   */
  public DiffXEvent getEvent(int i) {
    return this.sequence.get(i);
  }

  /**
   * Replaces an event of this sequence at the specified position.
   *
   * @param index The 0-based index of the position.
   * @param e     The event to be inserted.
   *
   * @return The event at the previous position.
   */
  public DiffXEvent setEvent(int index, DiffXEvent e) {
    return this.sequence.set(index, e);
  }

  /**
   * Removes an event from this sequence at the specified position.
   *
   * @param index The 0-based index of the position.
   *
   * @return The removed event.
   */
  public DiffXEvent removeEvent(int index) {
    return this.sequence.remove(index);
  }

  /**
   * The size of the sequence.
   *
   * @return The number of events in the sequence.
   */
  public int size() {
    return this.sequence.size();
  }

  /**
   * Returns a event iterator for this list.
   *
   * @return The event iterator for this sequence.
   */
  public EventIterator eventIterator() {
    return new EventIterator(this.sequence.iterator());
  }

  /**
   * Returns the sequence of events.
   *
   * @return the sequence of events.
   */
  public List<DiffXEvent> events() {
    return this.sequence;
  }

  // Object methods -----------------------------------------------------------------------------

  @Override
  public int hashCode() {
    return this.sequence.size();
  }

  /**
   * Returns <code>true</code> if the specified event sequence is the same as this one.
   *
   * @param seq The sequence of events to compare with this one.
   *
   * @return <code>true</code> if the specified event sequence is equal to this one;
   *         <code>false</code> otherwise.
   */
  public boolean equals(EventSequence seq) {
    if (seq == null) return false;
    return equals(this.sequence, seq.sequence);
  }

  /**
   * Returns <code>true</code> if the specified event sequence is the same as this one.
   *
   * <p>This method will redirect to the {@link #equals(EventSequence)} method if the
   * specified object is an instance of {@link EventSequence}.
   *
   * @param o The sequence of events to compare with this one.
   *
   * @return <code>true</code> if the specified event sequence is equal to this one;
   *         <code>false</code> otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof EventSequence)) return false;
    return this.equals((EventSequence)o);
  }

  /**
   * Returns the string representation of this sequence.
   *
   * @return The string representation of this sequence.
   */
  @Override
  public String toString() {
    return "Event Sequence ["+size()+"]";
  }

  /**
   * Export the sequence.
   *
   * @param w The print writer receiving the SAX events.
   */
  public void export(PrintWriter w) {
    for (DiffXEvent event : this.sequence) {
      w.println(event.toString());
    }
    w.flush();
  }

  /**
   * Maps a namespace URI to a prefix.
   *
   * @see PrefixMapping#add(String, String)
   *
   * @param uri    The namespace URI to map.
   * @param prefix The prefix to use.
   *
   * @throws NullPointerException if the URI or prefix is <code>null</code>
   */
  public void mapPrefix(String uri, String prefix) throws NullPointerException {
    this.prefixMapping.add(uri, prefix);
  }

  /**
   * Returns the prefix mapping for the namespace URIs in this sequence.
   *
   * @return the prefix mapping for the namespace URIs in this sequence.
   */
  public PrefixMapping getPrefixMapping() {
    return this.prefixMapping;
  }

  /**
   * An iterator over the event elements in the sequences.
   *
   * @author Christophe Lauret
   * @version 0.9.0
   */
  public static final class EventIterator implements Iterator<DiffXEvent> {

    /**
     * The wrapped iterator.
     */
    private final Iterator<DiffXEvent> iterator;

    /**
     * Creates a new iterator wrapping the specified list iterator.
     *
     * @param iterator The iterator to wrap.
     */
    private EventIterator(Iterator<DiffXEvent> iterator) {
      this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
      return this.iterator.hasNext();
    }

    @Override
    public DiffXEvent next() {
      return this.iterator.next();
    }

    /**
     * Returns the next event.
     *
     * @see java.util.Iterator#next()
     *
     * @return the next element in the iteration.
     * @throws NoSuchElementException iteration has no more event elements.
     */
    public DiffXEvent nextEvent() throws NoSuchElementException {
      return this.iterator.next();
    }

    /**
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
      this.iterator.remove();
    }

  }

  private static boolean equals(List<DiffXEvent> first, List<DiffXEvent> second) {
    if (first.size() != second.size()) return false;
    DiffXEvent x1;
    DiffXEvent x2;
    for (int i = 0; i < first.size(); i++) {
      x1 = first.get(i);
      x2 = second.get(i);
      if (!x1.equals(x2)) return false;
    }
    return true;
  }
}
