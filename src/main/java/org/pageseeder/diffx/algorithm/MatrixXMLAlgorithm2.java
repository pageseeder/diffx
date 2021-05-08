/*
 * Copyright (c) 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.algorithm;

import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.handler.OperationsBuffer;
import org.pageseeder.diffx.token.AttributeToken;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.Token;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public final class MatrixXMLAlgorithm2 implements DiffAlgorithm<Token> {

  @Override
  public void diff(List<? extends Token> first, List<? extends Token> second, DiffHandler<Token> handler) {
    Instance instance = new Instance();
    instance.computeMatrix(first, second);
    List<Operation<Token>> operations = instance.backtrace(first, second);
    Collections.reverse(operations);
    for (Operation<Token> operation : operations) {
      handler.handle(operation.operator(), operation.token());
    }
    System.out.println(instance);
  }

  private static class Instance {

    protected int[][] matrix;

    private void computeMatrix(List<? extends Token> first, List<? extends Token> second) {
      int length1 = first.size();
      int length2 = second.size();
      this.matrix = new int[length1 + 1][length2 + 1];

      Operation<StartElementToken>[] startJ = new Operation[length1+1];
      Deque<Operation<StartElementToken>> starts = new ArrayDeque<>();

      // allocate storage for array L;
      for (int i = 0; i <= length1; i++) {
        for (int j = 0; j <= length2; j++) {
          // we reach the end of the sequence (fill with 0)
          if (i == 0 || j == 0) {
            this.matrix[i][j] = 0;
          } else {
            Token a = first.get(i - 1);
            Token b = second.get(j - 1);
            if (a.equals(b)) {
              if (a instanceof StartElementToken) {
                starts.push(new Operation<>(Operator.MATCH, (StartElementToken)a));
              }
              int score = (a instanceof AttributeToken)? 3 : (a instanceof StartElementToken)? 2 : 1;
              // the tokens are the same
              this.matrix[i][j] = this.matrix[i - 1][j - 1]  + score;
            } else {
              int ins = this.matrix[i - 1][j];
              int del = this.matrix[i][j - 1];

              // Insertion
              if (ins >= del && a instanceof StartElementToken) {
                starts.push(new Operation<>(Operator.INS, (StartElementToken)a));
              // Deletion
              } else if (del >= ins && b instanceof StartElementToken) {
                starts.push(new Operation<>(Operator.DEL, (StartElementToken)b));
              }

              // different tokens
              this.matrix[i][j] = Math.max(ins, del);
            }
          }
        }
      }
    }


    private List<Operation<Token>> backtrace(List<? extends Token> first, List<? extends Token> second) {
      OperationsBuffer<Token> buffer = new OperationsBuffer<>();
      final int length1 = first.size();
      final int length2 = second.size();
      int i = length1;
      int j = length2;
      Token t1;
      Token t2;
      Deque<Operation<Token>> started = new ArrayDeque<>();
      // Backtrack start walking the matrix
      while (i > 0 && j > 0) {
        t1 = first.get(i-1);
        t2 = second.get(j-1);
        if (this.matrix[i][j-1] < this.matrix[i-1][j]) {
          if (t1 instanceof EndElementToken) {
            started.push(new Operation<>(Operator.INS, ((EndElementToken)t1).getOpenElement()));
          }
          buffer.handle(Operator.INS, t1);
          i--;
        } else if (this.matrix[i][j-1] > this.matrix[i-1][j]) {
          if (t2 instanceof EndElementToken) {
            started.push(new Operation<>(Operator.DEL, ((EndElementToken)t2).getOpenElement()));
          }
          buffer.handle(Operator.DEL, t2);
          j--;
        } else if (this.matrix[i][j-1] == this.matrix[i-1][j]) {
          if (t1.equals(t2)) {
            if (t2 instanceof EndElementToken) {
              started.push(new Operation<>(Operator.MATCH, ((EndElementToken)t2).getOpenElement()));
            }
            buffer.handle(Operator.MATCH, t1);
            i--;
            j--;
          } else {
            System.out.print("choice: ("+Operator.INS+t1+") or ("+Operator.DEL+t2+") -> ");
            boolean insert = true;
            if (!started.isEmpty()) {
              Operation<Token> op = started.peek();
              if (t2 instanceof StartElementToken && op.token().equals(t2) && op.operator() == Operator.DEL) {
                insert = false;
              }
              if (t1 instanceof AttributeToken) {
                insert = false;
              }
            }
            if (insert) {
              System.out.println("("+Operator.INS+t1+")!");
              if (t1 instanceof EndElementToken) {
                started.push(new Operation<>(Operator.INS, ((EndElementToken)t1).getOpenElement()));
              }
              buffer.handle(Operator.INS, t1);
              i--;
            } else {
              System.out.println("("+Operator.DEL+t2+")!");
              if (t2 instanceof EndElementToken) {
                started.push(new Operation<>(Operator.DEL, ((EndElementToken)t2).getOpenElement()));
              }
              buffer.handle(Operator.DEL, t2);
              j--;
            }
          }
        }
      }

      // finish off the tokens from the first sequence
      while (i > 0) {
        buffer.handle(Operator.INS, first.get(i-1));
        i--;
      }
      // finish off the tokens from the second sequence
      while (j > 0) {
        buffer.handle(Operator.DEL, second.get(j-1));
        j--;
      }
      return buffer.getOperations();
    }

    @Override
    public String toString() {
      StringBuilder out = new StringBuilder();
      for (int j = 0; j < this.matrix[0].length; j++) {
        for (int[] element : this.matrix) {
          out.append(element[j]).append("\t");
        }
        out.append('\n');
      }
      return out.toString();
    }

  }

}
