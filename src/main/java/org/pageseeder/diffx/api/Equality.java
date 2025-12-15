package org.pageseeder.diffx.api;

/**
 * Strategy interface to compute equality between two elements for the diff purpose.
 *
 * <h2>Contract</h2>
 * <p>This equality should behave like an <em>equivalence relation</em> unless explicitly documented otherwise:</p>
 * <ul>
 *   <li><strong>Reflexive</strong>: {@code equals(a, a)} is {@code true}.</li>
 *   <li><strong>Symmetric</strong>: {@code equals(a, b)} == {@code equals(b, a)}.</li>
 *   <li><strong>Transitive</strong>: if {@code equals(a, b)} and {@code equals(b, c)} then {@code equals(a, c)}.</li>
 *   <li><strong>Consistent</strong>: repeated calls return the same result provided inputs do not change.</li>
 * </ul>
 * <p>Implementations must document any deviation from these rules (for example, "similarity" matching),
 * as it may affect correctness and runtime characteristics of algorithms using this interface.</p>
 *
 * <h2>Null-handling</h2>
 * <p>Unless specified by the implementation, inputs are expected to be non-null.</p>
 *
 * <h2>Performance notes</h2>
 * <ul>
 *   <li>This method may be called very frequently; implementations should be fast and avoid allocations.</li>
 *   <li>Prefer reusing a single instance (for example, a singleton) rather than creating new instances in tight loops.</li>
 *   <li>Keep comparisons side effect free; avoid I/O, locking, and other blocking work.</li>
 * </ul>
 *
 * @param <T> the type of elements being compared
 *
 * @author Christophe Lauret
 *
 * @version 1.3.0
 * @since 1.3.0
 */
@FunctionalInterface
public interface Equality<T> {

  /**
   * Returns {@code true} if {@code a} and {@code b} are considered equal for the purpose of the diff.
   *
   * @param a the first element to compare, must not be null
   * @param b the second element to compare, must not be null
   *
   * @return {@code true} if the two elements are equal; {@code false} otherwise
   */
  boolean equals(T a, T b);

}
