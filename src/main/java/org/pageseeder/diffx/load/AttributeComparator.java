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
package org.pageseeder.diffx.load;

import java.util.Comparator;

import org.pageseeder.diffx.event.AttributeEvent;
import org.pageseeder.diffx.event.impl.AttributeEventImpl;
import org.pageseeder.diffx.event.impl.AttributeEventNSImpl;

/**
 * A comparator in order to put attributes in the correct order, that is in the alphabetical order
 * of the attribute name and namespace URI.
 *
 * @author Christophe Lauret
 * @version 10 May 2010
 */
final class AttributeComparator implements Comparator<AttributeEvent> {

  /**
   * Compares two objects if they are attributes.
   *
   * {@inheritDoc}
   */
  @Override
  public int compare(AttributeEvent o1, AttributeEvent o2) throws ClassCastException {
    if (o1 instanceof AttributeEventImpl && o2 instanceof AttributeEventImpl)
      return compare((AttributeEventImpl)o1, (AttributeEventImpl)o2);
    else if (o1 instanceof AttributeEventNSImpl && o2 instanceof AttributeEventNSImpl)
      return compare((AttributeEventNSImpl)o1, (AttributeEventNSImpl)o2);
    else
      return 0;
  }

  /**
   * Compares two attribute events using their name.
   *
   * @param att1 The first attribute to be compared.
   * @param att2 The second attribute to be compared.
   *
   * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
   *         than the second.
   */
  public int compare(AttributeEventImpl att1, AttributeEventImpl att2) {
    return att1.getName().compareTo(att2.getName());
  }

  /**
   * Compares two simple attribute events using their name and namespace URI.
   *
   * @param att1 The first attribute to be compared.
   * @param att2 The second attribute to be compared.
   *
   * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
   *         than the second.
   */
  public int compare(AttributeEventNSImpl att1, AttributeEventNSImpl att2) {
    return toCName(att1).compareTo(toCName(att2));
  }

  /**
   * Returns a comparable name from the given attribute's namespace URI and name.
   *
   * @param att The attribute.
   * @return The comparable name.
   */
  private static String toCName(AttributeEventNSImpl att) {
    return att.getURI() != null? att.getURI() + ':' + att.getName() : att.getName();
  }

}
