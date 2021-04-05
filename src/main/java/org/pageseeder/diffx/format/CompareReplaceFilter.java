package org.pageseeder.diffx.format;

import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.algorithm.TextOnlyAlgorithm;
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.TextEvent;
import org.pageseeder.diffx.load.text.TextTokenizer;
import org.pageseeder.diffx.load.text.TokenizerBySpaceWord;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public final class CompareReplaceFilter implements DiffXFormatter {

  /**
   * Target format.
   */
  private final DiffXFormatter target;

  /**
   * The previous text operation.
   */
  private Operation previous = null;

  // TODO initialize using config
  private TextTokenizer tokenizer = new TokenizerBySpaceWord(WhiteSpaceProcessing.PRESERVE);

  public CompareReplaceFilter(DiffXFormatter target) {
    this.target = target;
  }

  @Override
  public void format(DiffXEvent event) throws IOException, IllegalStateException {
    flushText();
    this.target.format(event);
  }

  @Override
  public void insert(DiffXEvent event) throws IOException, IllegalStateException {
    if (event instanceof TextEvent) {
      if (this.previous != null && this.previous.operator() == Operator.DEL) {
        diff((TextEvent)event, (TextEvent)this.previous.event());
        this.previous = null;
      } else {
        flushText();
        this.previous = new Operation(Operator.INS, event);
      }
    } else {
      flushText();
      this.target.insert(event);
    }
  }

  @Override
  public void delete(DiffXEvent event) throws IOException, IllegalStateException {
    if (event instanceof TextEvent) {
      if (this.previous != null && this.previous.operator() == Operator.INS) {
        diff((TextEvent)this.previous.event(), (TextEvent)event);
        this.previous = null;
      } else {
        flushText();
        this.previous = new Operation(Operator.DEL, event);
      }
    } else {
      flushText();
      this.target.delete(event);
    }
  }

  @Override
  public void setConfig(DiffXConfig config) {
  }

  private void diff(TextEvent a, TextEvent b) throws IOException {
    EventSequence seqA = new EventSequence();
    this.tokenizer.tokenize(a.getCharacters()).forEach(seqA::addEvent);
    EventSequence seqB = new EventSequence();
    this.tokenizer.tokenize(b.getCharacters()).forEach(seqB::addEvent);
    TextOnlyAlgorithm diff = new TextOnlyAlgorithm(seqA, seqB);
    diff.process(this.target);
  }

  /**
   * Flush the previous text event to the target formatter and clear the buffer if there is any text event.
   *
   * @throws IOException If thrown by the target filter.
   */
  public void flushText() throws IOException {
    if (this.previous != null) {
      if (this.previous.operator() == Operator.KEEP) this.target.format(this.previous.event());
      else if (this.previous.operator() == Operator.INS) this.target.insert(this.previous.event());
      else if (this.previous.operator() == Operator.DEL) this.target.delete(this.previous.event());
      this.previous = null;
    }
  }

}
