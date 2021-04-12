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
package org.pageseeder.diffx.core;

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.algorithm.ElementState;
import org.pageseeder.diffx.algorithm.Matrix;
import org.pageseeder.diffx.algorithm.MatrixProcessor;
import org.pageseeder.diffx.event.AttributeEvent;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.format.ShortStringFormatter;
import org.pageseeder.diffx.handler.DiffHandler;

import java.util.List;

/**
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class GuanoAlgorithm implements DiffAlgorithm {

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = false;

  @Override
  public void diff(List<? extends DiffXEvent> first, List<? extends DiffXEvent> second, DiffHandler handler) {

    final int length1 = first.size();
    final int length2 = second.size();

    // handle the case when one of the two sequences is empty
    if (length1 == 0 || length2 == 0) {
      // the first sequence is empty, events from the second sequence have been deleted
      for (DiffXEvent event : second) {
        handler.handle(Operator.DEL, event);
      }
      // the second sequence is empty, events from the first sequence have been inserted
      for (DiffXEvent event : first) {
        handler.handle(Operator.INS, event);
      }
      return;
    }

    // calculate the LCS length to fill the matrix
    MatrixProcessor builder = new MatrixProcessor();
    builder.setInverse(true);
    Matrix matrix = builder.process(first, second);
    ElementState estate = new ElementState();

    int i = 0;
    int j = 0;
    DiffXEvent e1;
    DiffXEvent e2;
    // start walking the matrix
    while (i < length1 && j < length2) {
      e1 = first.get(i);
      e2 = second.get(j);
      // we can only insert or delete, priority to insert
      if (matrix.isGreaterX(i, j)) {
        // follow the natural path and insert
        if (estate.okInsert(e1) && !estate.hasPriorityOver(e2, e1)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+j+"] >i +"+ShortStringFormatter.toShortString(e1));
          }
          handler.handle(Operator.INS, e1);
          estate.insert(e1);
          i++;

          // if we can format checking at the stack, let's do it
        } else if (e1.equals(e2) && estate.okFormat(e1)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+(j+1)+"] >f "+ShortStringFormatter.toShortString(e1));
          }
          handler.handle(Operator.MATCH, e1);
          estate.format(e1);
          i++; j++;

          // go counter current and delete
        } else if (estate.okDelete(e2)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+i+","+(j+1)+"] >d -"+ShortStringFormatter.toShortString(e2));
          }
          handler.handle(Operator.DEL, e2);
          estate.delete(e2);
          j++;

        } else {
          if (DEBUG) {
            System.err.print("\n(i) case greater X");
          }
          if (DEBUG) {
            printLost(i, j, matrix, estate, first, second);
          }
          break;
        }

        // we can only insert or delete, priority to delete
      } else if (matrix.isGreaterY(i, j)) {
        // follow the natural and delete
        if (estate.okDelete(e2) && !estate.hasPriorityOver(e1, e2)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+i+","+(j+1)+"] <d -"+ShortStringFormatter.toShortString(e2));
          }
          handler.handle(Operator.DEL, e2);
          estate.delete(e2);
          j++;

          // if we can format checking at the stack, let's do it
        } else if (e1.equals(e2) && estate.okFormat(e1)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+(j+1)+"] <f "+ShortStringFormatter.toShortString(e1));
          }
          handler.handle(Operator.MATCH, e1);
          estate.format(e1);
          i++; j++;

          // insert (counter-current)
        } else if (estate.okInsert(e1)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+j+"] <i +"+ShortStringFormatter.toShortString(e1));
          }
          handler.handle(Operator.INS, e1);
          estate.insert(e1);
          i++;

        } else {
          if (DEBUG) {
            System.err.println("\n(i) case greater Y");
          }
          if (DEBUG) {
            printLost(i, j, matrix, estate, first, second);
          }
          break;
        }

        // elements from i inserted and j deleted
        // we have to make a choice for where we are going
      } else if (matrix.isSameXY(i, j)) {
        // if we can format checking at the stack, let's do it
        if (e1.equals(e2) && estate.okFormat(e1)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+(j+1)+"] =f "+ShortStringFormatter.toShortString(e1));
          }
          handler.handle(Operator.MATCH, e1);
          estate.format(e1);
          i++; j++;

          // we can insert the closing tag
        } else if (estate.okInsert(e1)
            && !(e2 instanceof AttributeEvent && !(e1 instanceof AttributeEvent))) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+j+"] =i +"+ShortStringFormatter.toShortString(e1));
          }
          estate.insert(e1);
          handler.handle(Operator.INS, e1);
          i++;

          // we can delete the closing tag
        } else if (estate.okDelete(e2)
            && !(e1 instanceof AttributeEvent && !(e2 instanceof AttributeEvent))) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+i+","+(j+1)+"] =d -"+ShortStringFormatter.toShortString(e2));
          }
          handler.handle(Operator.DEL, e2);
          estate.delete(e2);
          j++;

        } else {
          if (DEBUG) {
            System.err.println("\n(i) case same");
          }
          if (DEBUG) {
            printLost(i, j, matrix, estate, first, second);
          }
          break;
        }
      } else {
        if (DEBUG) {
          System.err.println("\n(i) case ???");
        }
        if (DEBUG) {
          printLost(i, j, matrix, estate, first, second);
        }
        break;
      }
      if (DEBUG) {
        System.err.println("    stack:"+estate.currentChange()+ShortStringFormatter.toShortString(estate.current()));
      }
    }

    // finish off the events from the first sequence
    while (i < length1) {
      if (DEBUG) {
        System.err.println("["+i+","+j+"]->["+(i+1)+","+j+"] _i -"+ShortStringFormatter.toShortString(first.get(i)));
      }
      estate.insert(first.get(i));
      handler.handle(Operator.INS, first.get(i));
      i++;
    }
    // finish off the events from the second sequence
    while (j < length2) {
      if (DEBUG) {
        System.err.println("["+i+","+j+"]->["+i+","+(j+1)+"] _d -"+ShortStringFormatter.toShortString(second.get(j)));
      }
      estate.delete(second.get(j));
      handler.handle(Operator.DEL, second.get(j));
      j++;
    }
  }

  /**
   * Print information when the algorithm gets lost in the matrix,
   * ie when it does not know which direction to follow.
   *
   * @param i The X position.
   * @param j The Y position.
   */
  private void printLost(int i, int j, Matrix matrix, ElementState estate, List<? extends DiffXEvent> first, List<? extends DiffXEvent> second) {
    DiffXEvent e1 = first.get(i);
    DiffXEvent e2 = second.get(j);
    System.err.println("(!) Ambiguous choice in ("+i+","+j+")");
    System.err.println(" ? +"+ShortStringFormatter.toShortString(e1));
    System.err.println(" ? -"+ShortStringFormatter.toShortString(e2));
    System.err.println(" current="+ShortStringFormatter.toShortString(estate.current()));
    System.err.println(" value in X+1="+matrix.get(i+1, j));
    System.err.println(" value in Y+1="+matrix.get(i, j+1));
    System.err.println(" equals="+e1.equals(e2));
    System.err.println(" greaterX="+matrix.isGreaterX(i, j));
    System.err.println(" greaterY="+matrix.isGreaterY(i, j));
    System.err.println(" sameXY="+matrix.isSameXY(i, j));
    System.err.println(" okFormat1="+estate.okFormat(e1));
    System.err.println(" okFormat2="+estate.okFormat(e2));
    System.err.println(" okInsert="+estate.okInsert(e1));
    System.err.println(" okDelete="+estate.okDelete(e2));
  }

}
