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
import org.pageseeder.diffx.format.ShortStringFormatter;
import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.handler.MuxHandler;
import org.pageseeder.diffx.token.AttributeToken;
import org.pageseeder.diffx.token.Token;

import java.util.List;

/**
 * An XML-aware algorithm based on the Wagner-Fisher algorithm.
 *
 * <p>This algorithm uses a matrix and a stack of elements to compute the edit path.</p>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class MatrixXMLAlgorithm implements DiffAlgorithm {

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = false;

  @Override
  public void diff(List<? extends Token> first, List<? extends Token> second, DiffHandler handler) {

    final int lengthA = first.size();
    final int lengthB = second.size();

    // handle the case when one of the two sequences is empty
    if (lengthA == 0 || lengthB == 0) {
      // the first sequence is empty, tokens from the second sequence have been deleted
      for (Token token : second) {
        handler.handle(Operator.DEL, token);
      }
      // the second sequence is empty, tokens from the first sequence have been inserted
      for (Token token : first) {
        handler.handle(Operator.INS, token);
      }
      return;
    }

    // calculate the LCS length to fill the matrix
    MatrixProcessor builder = new MatrixProcessor();
    builder.setInverse(true);
    Matrix matrix = builder.process(first, second);
    ElementState estate = new ElementState();
    MuxHandler actual = new MuxHandler(handler, estate);

    int i = 0;
    int j = 0;
    Token tokenA;
    Token tokenB;
    // start walking the matrix
    while (i < lengthA && j < lengthB) {
      tokenA = first.get(i);
      tokenB = second.get(j);
      // we can only insert or delete, priority to insert
      if (matrix.isGreaterX(i, j)) {
        // follow the natural path and insert
        if (estate.isAllowed(Operator.INS, tokenA) && !estate.hasPriorityOver(tokenB, tokenA)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+j+"] >i +"+ShortStringFormatter.toShortString(tokenA));
          }
          actual.handle(Operator.INS, tokenA);
          i++;

          // if we can format checking at the stack, let's do it
        } else if (tokenA.equals(tokenB) && estate.isAllowed(Operator.MATCH, tokenA)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+(j+1)+"] >f "+ShortStringFormatter.toShortString(tokenA));
          }
          actual.handle(Operator.MATCH, tokenA);
          i++; j++;

          // go counter current and delete
        } else if (estate.isAllowed(Operator.DEL, tokenB)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+i+","+(j+1)+"] >d -"+ShortStringFormatter.toShortString(tokenB));
          }
          actual.handle(Operator.DEL, tokenB);
          j++;

        } else {
          if (DEBUG) {
            System.err.print("\n(i) case greater X");
            printLost(i, j, matrix, estate, first, second);
          }
          break;
        }

        // we can only insert or delete, priority to delete
      } else if (matrix.isGreaterY(i, j)) {
        // follow the natural and delete
        if (estate.isAllowed(Operator.DEL, tokenB) && !estate.hasPriorityOver(tokenA, tokenB)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+i+","+(j+1)+"] <d -"+ShortStringFormatter.toShortString(tokenB));
          }
          actual.handle(Operator.DEL, tokenB);
          j++;

          // if we can format checking at the stack, let's do it
        } else if (tokenA.equals(tokenB) && estate.isAllowed(Operator.MATCH, tokenA)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+(j+1)+"] <f "+ShortStringFormatter.toShortString(tokenA));
          }
          actual.handle(Operator.MATCH, tokenA);
          i++; j++;

          // insert (counter-current)
        } else if (estate.isAllowed(Operator.INS, tokenA)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+j+"] <i +"+ShortStringFormatter.toShortString(tokenA));
          }
          actual.handle(Operator.INS, tokenA);
          i++;

        } else {
          if (DEBUG) {
            System.err.println("\n(i) case greater Y");
            printLost(i, j, matrix, estate, first, second);
          }
          break;
        }

        // elements from i inserted and j deleted
        // we have to make a choice for where we are going
      } else if (matrix.isSameXY(i, j)) {
        // if we can format checking at the stack, let's do it
        if (tokenA.equals(tokenB) && estate.isAllowed(Operator.MATCH, tokenA)) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+(j+1)+"] =f "+ShortStringFormatter.toShortString(tokenA));
          }
          actual.handle(Operator.MATCH, tokenA);
          i++; j++;

          // we can insert the closing tag
        } else if (estate.isAllowed(Operator.INS, tokenA)
            && !(tokenB instanceof AttributeToken && !(tokenA instanceof AttributeToken))) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+(i+1)+","+j+"] =i +"+ShortStringFormatter.toShortString(tokenA));
          }
          actual.handle(Operator.INS, tokenA);
          i++;

          // we can delete the closing tag
        } else if (estate.isAllowed(Operator.DEL, tokenB)
            && !(tokenA instanceof AttributeToken && !(tokenB instanceof AttributeToken))) {
          if (DEBUG) {
            System.err.print("["+i+","+j+"]->["+i+","+(j+1)+"] =d -"+ShortStringFormatter.toShortString(tokenB));
          }
          actual.handle(Operator.DEL, tokenB);
          j++;

        } else {
          if (DEBUG) {
            System.err.println("\n(i) case same");
            printLost(i, j, matrix, estate, first, second);
          }
          break;
        }
      } else {
        if (DEBUG) {
          System.err.println("\n(i) case ???");
          printLost(i, j, matrix, estate, first, second);
        }
        break;
      }
      if (DEBUG) {
        System.err.println("    stack:"+estate.currentChange()+ShortStringFormatter.toShortString(estate.current()));
      }
    }

    // finish off the tokens from the first sequence
    while (i < lengthA) {
      if (DEBUG) {
        System.err.println("["+i+","+j+"]->["+(i+1)+","+j+"] _i -"+ShortStringFormatter.toShortString(first.get(i)));
      }
      actual.handle(Operator.INS, first.get(i));
      i++;
    }
    // finish off the tokens from the second sequence
    while (j < lengthB) {
      if (DEBUG) {
        System.err.println("["+i+","+j+"]->["+i+","+(j+1)+"] _d -"+ShortStringFormatter.toShortString(second.get(j)));
      }
      actual.handle(Operator.DEL, second.get(j));
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
  private void printLost(int i, int j, Matrix matrix, ElementState estate, List<? extends Token> first, List<? extends Token> second) {
    Token tokenA = first.get(i);
    Token tokenB = second.get(j);
    System.err.println("(!) Ambiguous choice in ("+i+","+j+")");
    System.err.println(" ? +"+ShortStringFormatter.toShortString(tokenA));
    System.err.println(" ? -"+ShortStringFormatter.toShortString(tokenB));
    System.err.println(" current="+ShortStringFormatter.toShortString(estate.current()));
    System.err.println(" value in X+1="+matrix.get(i+1, j));
    System.err.println(" value in Y+1="+matrix.get(i, j+1));
    System.err.println(" equals="+tokenA.equals(tokenB));
    System.err.println(" greaterX="+matrix.isGreaterX(i, j));
    System.err.println(" greaterY="+matrix.isGreaterY(i, j));
    System.err.println(" sameXY="+matrix.isSameXY(i, j));
    System.err.println(" okFormat1="+estate.isAllowed(Operator.MATCH, tokenA));
    System.err.println(" okFormat2="+estate.isAllowed(Operator.MATCH, tokenB));
    System.err.println(" okInsert="+estate.isAllowed(Operator.INS, tokenA));
    System.err.println(" okDelete="+estate.isAllowed(Operator.DEL, tokenB));
  }

}
