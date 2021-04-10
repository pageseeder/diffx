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
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.handler.DiffHandler;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Performs the diff comparison using an optimized version of the linear space algorithm
 * of S.Kiran Kumar and C.Pandu Rangan.
 *
 * <p>This algorithm starts from the length of the first sequence as the maximum possible
 * LCS and reduces the length for every difference with the second sequence.
 *
 * <p>The time complexity is O(n(m-p)) and the space complexity is O(n+m).
 *
 * <p>For S. Kiran Kumar and C. Pandu Rangan. <i>A linear space algorithm for the LCS problem</i>,
 * Acta Informatica. Volume 24 ,  Issue 3  (June 1987); Copyright Springer-Verlag 1987
 *
 * <p>This class reuses portions of code originally written by Mikko Koivisto and Tuomo Saarni.
 *
 * <p><a href="http://dblp.uni-trier.de/rec/bibtex/journals/acta/KumarR87">
 * http://dblp.uni-trier.de/rec/bibtex/journals/acta/KumarR87</a>
 *
 * <p><a href="http://users.utu.fi/~tuiisa/Java/">http://users.utu.fi/~tuiisa/Java/</a>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class KumarRanganAlgorithm implements DiffAlgorithm {

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = false;

  @Override
  public void diff(List<? extends DiffXEvent> first, List<? extends DiffXEvent> second, DiffHandler handler) throws IOException {
    Instance instance = new Instance(first, second);
    instance.process(handler);
  }

  // static helpers -----------------------------------------------------------------------------

  /**
   * Copies the first array into the second one up to the specified index (included).
   *
   * @param a   The first array.
   * @param b   The second array.
   * @param len The 0-based index of the last copied value.
   */
  private static void copyUpTo(int[] a, int[] b, int len) {
    System.arraycopy(a, 0, b, 0, len + 1);
  }

  /**
   * A stateful instance.
   *
   * Where possible, the name of the variables match the names used in the algorithm published in
   * "A Linear Space Algorithm for the LCS Problem".
   */
  private static class Instance {

    // Global integer arrays needed in the computation of the LCS
    private int[] R1, R2;
    private int[] LL, LL1, LL2;

    // Global integer variables needed in the computation of the LCS.
    private int R;
    private int S;

    /**
     * A counter for the index of the second sequence when generating the diff.
     */
    private int J = 0;

    private final List<? extends DiffXEvent> first;
    private final List<? extends DiffXEvent> second;

    /**
     * Events are reported here.
     */
    private DiffHandler handler;

    Instance(List<? extends DiffXEvent> first, List<? extends DiffXEvent> second) {
      this.first = Objects.requireNonNull(first);
      this.second = Objects.requireNonNull(second);
    }

    /**
     * Writes the diff sequence using the specified handler.
     *
     * @param handler The handler for the output.
     *
     * @throws IOException If thrown by the handler.
     */
    public void process(DiffHandler handler) throws IOException {
      final int m = this.first.size();
      final int n = this.second.size();
      int p = calculateLength(m, n);
      this.handler = handler;

      // Execute the LCS algorithm for the complete sequences
      computeLCS(0, m - 1, 0, n - 1, m, n, p);
    }

    // helpers ------------------------------------------------------------------------------------

    /**
     * Initialises the state variables.
     *
     * @param n The length of the second sequence
     */
    private void init(int n) {
      this.R1 = new int[n+1];
      this.R2 = new int[n+1];
      this.LL = new int[n+1];
      this.LL1 = new int[n+1];
      this.LL2 = new int[n+1];
      this.J = 0;
    }

    /**
     * An implementation of the LCS algorithm as defined by Kumar and Rangan.
     *
     * <p>Given A and B are strings of length m and n respectively, and p, the length of the LCS of A and B,
     * algorithm LCS calculates the longest common subsequence of A and B.</p>
     *
     * <p>We assume that A and B are stored globally and that substrings of these strings can be passed as
     * arguments by passing two numbers, namely the left and right end indices of the substring.</p>
     *
     * <p>The divide and conquer technique is used in solving the problem.</p>
     *
     * <p>The algorithm finds a perfect cut for the string pair (A, B), solves the subproblems of finding the
     * LCS of A1 and B1, and A2 and B2, and combines the solutions.</p>
     *
     * <pre>
     *  CS (A, B, m, n, p, C)
     *     if m-p<2 1
     *        then solve the base case
     *  2     else begin
     *  2.1      find a perfect cut (u, v), for A and B;
     *  2.2      LCS(A(1 :u), B(l:v), u, v, u-w, C1);
     *           LCS(A(u+ 1 :m), B(v+ 1 :n), m-u, n-v, m-u-w', C2);
     *  2.3      C=C1C2
     *        end
     * </pre>
     *
     * @param startA The start index of the first sequence.
     * @param endA   The last index of the first sequence.
     * @param startB The start index of the second sequence.
     * @param endB   The last index of the second sequence.
     * @param m      The length of the first sequence.
     * @param n      The length of the second sequence.
     * @param p      The length of LCS between indexes startA and endA.
     *               Similarly between indexes b_start and b_end
     *
     * @throws IOException If thrown by the formatter
     */
    private void computeLCS(int startA, int endA, int startB, int endB, int m, int n, int p) throws IOException {
      if (m - p < 2) {
        // (i) Step 1, or the base case (waste is less than 2 characters)
        computeLCSBaseCase(startA, endA, startB, endB, m, n, p);
      } else {
        // (ii) Step 2.1, or finding the perfect cut (waste is more than 1 character, process recursively)
        computeLCSMoreWaste(startA, endA, startB, endB, m, n, p);
      }
    }

    /**
     * An implementation of the <code>fillone</code> procedure as defined by Kumar and Rangan.
     *
     * <p>This is used to find the index from where the longest common subsequence so far can
     * be found.</p>
     *
     * <p>We use two arrays R1(0:n) and R2(0:n) to store the given and the calculated values respectively.
     * <p>Assume m >= s >= length of the LCS of A and B.</p>
     *
     * <p>Given Ls+1(1), Ls(2), Ls+1(3)... in the array elements R1(1), R1(2), R1(3)... the procedure calculates
     * Ls+1(0) (=n+ 1), Ls(1), Ls-1(2)... and returns them in the array elements R2(0),R2(1), R2(2)...</p>
     *
     * <p>Variable r is used to indicate the largest k such that Ls+2-k(k) is defined.
     * At the end of the computation, r is updated to indicate the largest k such that Ls+1-k(k) is defined.</p>
     *
     * <pre>
     *   fillone (A, b, m, n, R 1, R 2, r, s)
     *      begin
     *   1.    j := 1; i := s;
     *         over := false; R2(O) := n+1;
     *   2.    while i > 0 and not over do
     *         begin
     *   2.1     if j > r then lower B := O else lower B := R1(j);
     *           pos B := R2(j-1) - 1;
     *   2.2     while pos B > lower B and A(i) != B(pos/3) do
     *           pos B := pos B - 1;
     *   2.3     temp := max(pos B, lower B);
     *           if temp = 0 then over := true
     *           else begin
     *              R2(j) := temp;
     *              i := i-1; j := j+l
     *           end
     *        end;
     *   3.   r=j-1
     *      end
     * </pre>
     *
     * <p>In step 2.2, all the symbols of string B are compared sequentially from the last symbol to the first symbol.</p>
     * <p>Hence step 2.2 is executed at most n times. It is obvious that other steps are not executed more than n times.</p>
     * <p>Hence, time complexity is O(n).</p>
     * <p>The procedure uses only linear arrays R1, R2, so the space complexity is O(n+m).</p>
     *
     * @param startA The start index of the first sequence.
     * @param endA   The last index of the first sequence.
     * @param startB The start index of the second sequence.
     * @param endB   The last index of the second sequence.
     * @param m      The length of the first sequence.
     * @param n      The length of the second sequence.
     * @param sign   This is used to mark wether to start from the beginning of the string
     *               or from the end of the string.
     */
    private void fillOne(int startA, int endA, int startB, int endB, int m, int n, int sign) {
      int i = this.S;
      int j = 1;
      boolean over = false;
      this.R2[0] = n+1;

      while (i > 0 & !over) {
        int lowerB = (j > this.R) ? 0 : this.R1[j];
        int posB = this.R2[j - 1] - 1;

        // The real index in the global char table is:
        // current_index * sign + beginning index of the subchararray
        while (posB > lowerB && !this.first.get((i - 1) * sign + startA)
            .equals(this.second.get((posB - 1) * sign + startB))) {
          posB--;
        }
        int temp = Math.max(posB, lowerB);
        if (temp == 0) {
          over = true;
        } else {
          this.R2[j] = temp;
          i--;
          j++;
        }
      }
      this.R = j - 1;
    }

    /**
     * Implements procedure <code>calmid</code> from the Kumar-Rangan algorithm.
     *
     * <p>This procedure calculates Lm-x+1-j(j) values and store them in LL(j) (j>0).</p>
     * <p>r is set to the largest j such that Lm-x+1-j(j) is defined.</p>
     *
     * <pre>
     *   calmid (A, B, m, n, x, LL, r)
     *      begin
     *  1.     r:=0;
     *  2.     for s:=m down to m-x do
     *         begin
     *            fillone(A, B, m, n, R1, R2, r, s);
     *            R1(j) := R2(j) [j:=0...r]
     *         end;
     *  3.     LL(j) := R1(j) [j:=0...r]
     *      end
     * </pre>
     *
     * <p>The loop body is executed exactly (x + 1) times.</p>
     *
     * <p>In each iteration of the loop, a call to fillone and copying of R2(j) into R1(j) is done, which take O(n)
     * and O(r) (< O(n)) time respectively. Hence, the time complexity is O(n(x + 1)).</p>
     *
     * <p>The procedure uses only linear arrays R1, R2, LL, so the space complexity is O(n+m).</p>
     *
     * <p>Implementation note: we use integer arrays to keep track where the longest common subsequence can be found.</p>
     *
     * @param startA The start index of the first sequence.
     * @param endA   The last index of the first sequence.
     * @param startB The start index of the second sequence.
     * @param endB   The last index of the second sequence.
     * @param m      The length of the first sequence.
     * @param n      The length of the second sequence.
     * @param sign   This is used to mark whether to start from the beginning of the string
     *               or from the end of the string.
     * @param x      The length of characters not included in the LCS between indexes startA and endA.
     *               Similarly between indexes startB and endB.
     *
     * @return       Array of 1-indexes of B in LCS
     */
    private int[] calMid(int startA, int endA, int startB, int endB, int m, int n, int sign, int x) {
      this.LL = new int[n+1];
      this.R = 0;
      for (this.S = m; this.S >= m - x; this.S--) {
        fillOne(startA, endA, startB, endB, m, n, sign);
        copyUpTo(this.R2, this.R1, this.R);
      }
      copyUpTo(this.R1, this.LL, this.R);
      return this.LL;
    }


    /**
     * Computes the longest common subsequence for the specified boundaries when the waste
     * is (strictly) less than 2 events.
     *
     * <p>This method is iterative; NOT recursive.
     *
     * <p>This is an implementation of Step 1 (or base case) as described by Kumar-Rangan:</p>
     *
     * <pre>
     *   (i) Step 1, or the base case:
     *   1. calmid (A, B, m, n, m--p, LL, r);
     *      i := 1;
     *   2. while i < p and A(i) = B(LL(p-i+1)) do
     *         begin
     *            C(i) := A(i);
     *            i := i+1
     *         end;
     *   3. i := i+1;
     *   4. while i < m do
     *         begin
     *            C(i-1) := A(i);
     *            i := i+1
     *         end
     * </pre>
     *
     * <p>When p = m, the statements in step 4 are not executed at all and A is copied into C in step 2.</p>
     *
     * <p>When p=m-1, the step 3 can be seen as the statement that drops out one character from A to get C.</p>
     *
     * <p>It is easy to verify that the wrong character is not dropped.</p>
     *
     * @param startA The start 0-based index of the first sequence.
     * @param endA   The last 0-based index of the first sequence.
     * @param startB The start 0-based index of the second sequence.
     * @param endB   The last 0-based index of the second sequence.
     * @param m      The length of the first sequence.
     * @param n      The length of the second sequence.
     * @param p      The length of LCS between indexes startA and endA.
     *
     * @throws IOException If thrown by the formatter.
     */
    private void computeLCSBaseCase(int startA, int endA, int startB, int endB, int m, int n, int p)
        throws IOException {

      // 1. Compute LL
      // `LL` contains the relative 1-based index of the event in the second sequence in reverse order
      // `m - p` is number of diffs with the first subsequence
      this.LL = calMid(startA, endA, startB, endB, m, n, 1, m - p);
      if (DEBUG) {
        System.err.println("SEQ1={"+startA+" -> "+endA+"} SEQ2={"+startB+" -> "+endB+"}  M="+m+" n="+n+" p="+p);
        printLL();
      }

      // Deleted elements from the second sequence
      // `LL[p] - 1 + startB` contains index of the first item in the second subsequence matching the first subsequence
      deleteUpTo(this.LL[p] - 1 + startB);

      int i = 0;

      // 2. Start in order for the first subsequence and get the index of the second subsequence
      while (i < p && this.first.get(i + startA).equals(this.second.get(this.LL[p - i] - 1 + startB))) {
        this.handler.handle(Operator.MATCH, this.first.get(i + startA));
        this.J++;
        i++;
        if (i < p) {
          // removed events from the second subsequence
          deleteUpTo(this.LL[p - i] - 1 + startB);
        }
      }

      // possibly an event from the first subsequence to insert
      if (i < m) {
        this.handler.handle(Operator.INS, this.first.get(i + startA));
      }

      // 3.
      i++;

      // 4. The second part of the first subsequence
      while (i < m) {
        this.handler.handle(Operator.MATCH, this.first.get(i + startA));
        this.J++;
        deleteUpTo(this.LL[p - i] - 1 + startB);
        i++;
      }

      // finish writing the missing events from the second subsequence
      deleteUpTo(this.LL[0] - 1 + startB);
    }

    /**
     * Computes the longest common subsequence for the specified boundaries when the waste
     * is more than 1 character.
     *
     * <p>This method is recursive and will process each subsequence with the LCS algorithm.
     *
     * <p>This is an implementation of Step 2.1 (finding the perfect cut of the LCS algorithm by Kumar-Rangan</p>
     *
     * <pre>
     *   (ii) Step 2.1, or finding the perfect cut:
     *   2.1.1 calmid(^A^, ^B^, m, n, w, LL1, rl);
     *         LLI(j) := n+1-LL1(j) [j:=0...r1];
     *   2.1.2 calmid(A, B, m, n, w', LL2, r2);
     *   2.1.3 u := k + w:k <= r1 and p-k <= r2 and LL1(k) < LL2(p-k);
     *         v := LL1(k)
     * </pre>
     *
     * @param startA The start 0-based index of the first sequence.
     * @param endA   The last 0-based index of the first sequence.
     * @param startB The start 0-based index of the second sequence.
     * @param endB   The last 0-based index of the second sequence.
     * @param m      The length of the first sequence.
     * @param n      The length of the second sequence.
     * @param p      The length of LCS between indexes startA and endA.
     *
     * @throws IOException If thrown by the formatter.
     */
    private void computeLCSMoreWaste(int startA, int endA, int startB, int endB, int m, int n, int p)
        throws IOException {
      // The indexes of the perfect cut
      int u, v;

      int r1, r2;

      int waste1 = (int)Math.ceil((m - p) / 2.0f);
      this.LL1 = calMid(endA, startA, endB, startB, m, n, -1, waste1);

      // Saves the value changed in calmid from global variable R to variable r1
      r1 = this.R;
      for (int j = 0; j <= r1; j++) {
        this.LL1[j] = n + 1 - this.LL1[j];
      }

      int waste2 = (int)Math.floor((m - p) / 2.0f);
      this.LL2 = calMid(startA, endA, startB, endB, m, n, 1, waste2);

      // Saves the value changed in calmid from global variable R to variable r2
      r2 = this.R;

      int k = Math.max(r1, r2);

      while (k > 0) {
        if (k <= r1 && p - k <= r2 && this.LL1[k] < this.LL2[p - k]) {
          break;
        } else {
          k--;
        }
      }

      u = k + waste1;
      v = this.LL1[k];

      // recursively call the LCS method to process the two subsequences
      computeLCS(startA, startA + u - 1, startB, startB + v - 1, u - 1+1, v - 1+1, u - waste1);
      computeLCS(startA + u, endA, startB + v, endB, endA - startA + 1 - u, endB - startB + 1 - v, m - u - waste2);
    }


    /**
     * Calculates the LCS length as described by Kumar-Rangan.
     *
     * <pre>
     *  (iii) Calculation of p:
     *  length(A, B, m, n, p);
     *     begin
     *  1.    r := O; s := m+l;
     *  2.    while s > r do
     *           begin
     *           s := s-1;
     *           fillone(A, B, m, n, R1, R2, r, s);
     *           R1(j) := R2(j) [j:=0...r] end;
     *  3.    p := s;
     *     end
     * </pre>
     *
     * @param m The length of the first sequence.
     * @param n The length of the second sequence.
     *
     * @return The LCS length.
     */
    private int calculateLength(int m, int n) {
      init(n);
      this.R = 0;
      this.S = m + 1;
      // iterate for every difference with the first sequence
      while (this.S > this.R) {
        this.S--;
        // fill up R2 up to the first difference using the entire sequences
        fillOne(0, m - 1, 0, n - 1, m, n, 1);
        // copy the content of R2 to R1 up to R
        copyUpTo(this.R2, this.R1, this.R);
      }
      // both R1 and R2 now contain the indexes(+1) of the first sequence that forms the LCS
      if (DEBUG) {
        System.err.println("LCS length="+this.S);
      }
      return this.S;
    }

    /**
     * Write the deleted events to the formatter.
     *
     * @param jSeq2 The index of the LL array for the next event of the second sequence.
     *
     * @throws IOException If thrown by the formatter.
     */
    private void deleteUpTo(int jSeq2) throws IOException {
      while (jSeq2 > this.J) {
        this.handler.handle(Operator.DEL, this.second.get(this.J++));
      }
    }

    private void printLL() {
      System.err.print(" LL={");
      for (int element : this.LL) {
        System.err.print(" "+element);
      }
      System.err.println(" }");
    }

  }

}
