package org.pageseeder.diffx.handler;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.XMLTokenType;
import org.pageseeder.diffx.token.impl.NilToken;
import org.pageseeder.diffx.token.impl.XMLEndElement;

import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

/**
 * This class ensures that XML edit operations (insertions, deletions, matches)
 * maintain a balanced structure with properly paired start and end elements.
 *
 * <p>It processes queued XML tokens and applies logic to ensure well-formed XML during diffs.
 *
 * <p>This class extends the DiffFilter to modify or supplement diff handling behavior
 * specific to XML token streams.
 *
 * <p>Responsibilities of the XMLEventBalancer:
 * <ul>
 *   <li>Balances start and end elements during XML edit operations.</li>
 *   <li>Tracks unclosed XML elements that need a corresponding end element.</li>
 *   <li>Resolves mismatches by tracking insertions, deletions, and attributes.</li>
 *   <li>Ensures errors are flagged when XML mismatch corrections are not possible.</li>
 * </ul>
 *
 * <p>Note: This class is still experimental and subject to change.</p>
 *
 * @author Christophe Lauret
 *
 * @since 1.2.1
 * @version 1.2.1
 */
@ApiStatus.Experimental
public class XMLEventBalancer extends DiffFilter<XMLToken> {

  /**
   * Constructs a new {@code XMLEventBalancer} with the specified {@link DiffHandler}.
   *
   * @param handler The {@link DiffHandler} to be used for processing XML tokens. Must not be null.
   */
  public XMLEventBalancer(DiffHandler<XMLToken> handler) {
    super(handler);
  }

  /**
   * Keeps track of start elements tokens without a matching end element.
   */
  private final Deque<Operation<StartElementToken>> unclosed = new ArrayDeque<>();

  /**
   * Deletions from the current list of successive edits.
   */
  private final Queue<XMLToken> deletions = new ArrayDeque<>();

  /**
   * Insertions from the current list of successive edits.
   */
  private final Queue<XMLToken> insertions = new ArrayDeque<>();

  /**
   * Last operator used (never null)
   */
  private Operator lastOperator = Operator.MATCH;

  /**
   * Last token (never null)
   */
  private XMLToken lastToken = NilToken.getInstance();

  /**
   * Flag indicating when the handler is unable to fix the XML.
   */
  private boolean hasError = false;

  @Override
  public void handle(Operator operator, XMLToken token) throws UncheckedIOException, IllegalStateException {
    if (operator == Operator.DEL) {
      this.deletions.add(token);
    } else if (operator == Operator.INS) {
      this.insertions.add(token);
    } else {
      flushChanges();
      if (token.getType() == XMLTokenType.END_ELEMENT && !matchStart(Operator.MATCH, (EndElementToken) token)) {
        sendMatchingEndElement();
      } else {
        send(operator, token);
      }
    }
  }

  private void flushChanges() {
    while (!this.insertions.isEmpty() || !this.deletions.isEmpty()) {
      // Flush attributes if the last token sent was an open element or attribute
      flushAttributes();

      // At this point there are no attributes left, tokens can only be START_ELEMENT, END_ELEMENT, TEXT, and OTHER
      XMLToken nextInsertion = this.insertions.peek();
      XMLToken nextDeletion = this.deletions.peek();

      Operation<StartElementToken> context = this.unclosed.peek();

      if (isEndElement(nextDeletion)) {
        if (matchStart(Operator.DEL, (EndElementToken) nextDeletion)) {
          send(Operator.DEL, this.deletions.remove());
        } else if (context != null && context.operator() == Operator.INS && !this.insertions.isEmpty()) {
          send(Operator.INS, this.insertions.remove());
        } else {
          this.hasError = !followedByMatchingStart(this.deletions, (EndElementToken) nextDeletion);
          sendMatchingEndElement();
          this.deletions.remove();
        }

      } else if (isEndElement(nextInsertion)) {
        if (matchStart(Operator.INS, (EndElementToken) nextInsertion)) {
          send(Operator.INS, this.insertions.remove());
        } else if (context != null && context.operator() == Operator.DEL&& !this.deletions.isEmpty()) {
          send(Operator.DEL, this.deletions.remove());
        } else {
          this.hasError = !followedByMatchingStart(this.insertions, (EndElementToken) nextInsertion);
          sendMatchingEndElement();
          this.insertions.remove();
        }

      } else {

        if (this.lastOperator == Operator.DEL && nextDeletion != null)
          send(Operator.DEL, this.deletions.remove());
        else if (this.lastOperator == Operator.INS && nextInsertion != null)
          send(Operator.INS, this.insertions.remove());
        else if (nextDeletion != null)
          send(Operator.DEL, this.deletions.remove());
        else if (nextInsertion != null)
          send(Operator.INS, this.insertions.remove());
      }
    }
  }

  /**
   * Flushes pending attribute operations from the `deletions` and `insertions` queues.
   *
   * <p>This method checks whether the last processed token is of type START_ELEMENT or ATTRIBUTE.
   * If true, attributes marked for deletion or insertion are processed sequentially.
   *
   * <p>For each attribute token in the `deletions` queue, it removes the token and sends it with
   * the DEL (delete) operator. Similarly, for each attribute token in the `insertions` queue,
   * it removes the token and sends it with the INS (insert) operator.
   */
  private void flushAttributes() {
    XMLTokenType type = this.lastToken.getType();
    if (type == XMLTokenType.START_ELEMENT || type == XMLTokenType.ATTRIBUTE) {
      while (isAttribute(this.deletions.peek())) {
        send(Operator.DEL, this.deletions.remove());
      }
      while (isAttribute(this.insertions.peek())) {
        send(Operator.INS, this.insertions.remove());
      }
    }
  }

  public boolean hasError() {
    return this.hasError;
  }

  private boolean followedByMatchingStart(Queue<XMLToken> queue, EndElementToken endElement) {
    XMLToken following = followingPeek(queue);
    if (following == null) return false;
    return following.getType() == XMLTokenType.START_ELEMENT
        && endElement.getName().equals(following.getName())
        && endElement.getNamespaceURI().equals(following.getNamespaceURI());
  }

  private static @Nullable XMLToken followingPeek(Queue<XMLToken> queue) {
    if (queue.size() >= 2) {
      return queue.stream().skip(1).findFirst().orElse(null);
    }
    return null;
  }

  private static boolean isEndElement(@Nullable XMLToken token) {
    return token != null && token.getType() == XMLTokenType.END_ELEMENT;
  }

  private static boolean isAttribute(@Nullable XMLToken token) {
    return token != null && token.getType() == XMLTokenType.ATTRIBUTE;
  }

  private boolean matchStart(Operator operator, EndElementToken token) {
    Operation<StartElementToken> op = this.unclosed.peek();
    if (op == null) return false;
    return op.operator() == operator && token.match(op.token());
  }

  /**
   * We ignore the reported end element token and send the matching end element token
   */
  private void sendMatchingEndElement() {
    Operation<StartElementToken> lastStart = this.unclosed.peek();
    if (lastStart != null) {
      EndElementToken end = toEndElementToken(lastStart.token());
      send(lastStart.operator(), end);
    }
  }

  private EndElementToken toEndElementToken(StartElementToken token) {
    return new XMLEndElement(token);
  }

  private void send(Operator operator, XMLToken token) {
    this.target.handle(operator, token);
    this.lastOperator = operator;
    this.lastToken = token;
    if (token.getType() == XMLTokenType.START_ELEMENT) {
      this.unclosed.push(new Operation<>(operator, (StartElementToken) token));
    } else if (token.getType() == XMLTokenType.END_ELEMENT) {
      this.unclosed.pop();
    }
  }

  @Override
  public void end() {
    flushChanges();
    // May be necessary if some unclosed unchanged element remains
    while (!this.unclosed.isEmpty()) {
      sendMatchingEndElement();
    }
  }

}
