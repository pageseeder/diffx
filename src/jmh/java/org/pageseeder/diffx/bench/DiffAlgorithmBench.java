package org.pageseeder.diffx.bench;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 2)
@State(Scope.Thread)
public class DiffAlgorithmBench {

  @Param({
//      "Wu",
//      "MyersGreedy",
//      "MyersLinear",
      "KumarRangan",
      "KumarRangan2",
//      "Hirschberg",
//      "Hirschberg2",
//      "WagnerFischer"
  })
  public String algorithmName;

  @Param({
      "chars_100_var05",
      "chars_1000_var05",
      "chars_1000_var25",
      "chars_1000_var50",
      "chars_1000_var75",
      "chars_1000_var95",
      "chars_2500_var05",
      "chars_5000_var05",
      "chars_7500_var05",
      "chars_10000_var05",
      "chars_25000_var05",
      "patterns_10000_var05"
  })
  public String datasetName;

  @Param({
      "NOP",
//      "COUNTING"
  })
  public String handlerMode;

  private DiffAlgorithm<Object> algorithm;
  private ListPairSpec<Object> spec;

  private List<Object> from;
  private List<Object> to;

  private DiffHandler<Object> nop;
  private CountingHandler<Object> counting;

  @Setup(Level.Trial)
  @SuppressWarnings("unchecked")
  public void setupTrial() {
    Map<String, Supplier<DiffAlgorithm<?>>> algorithms = Registries.algorithms();
    Map<String, Supplier<ListPairSpec<?>>> datasets = Registries.datasets();

    Supplier<DiffAlgorithm<?>> algoFactory = algorithms.get(algorithmName);
    if (algoFactory == null) throw new IllegalArgumentException("Unknown algorithm: " + algorithmName);
    this.algorithm = (DiffAlgorithm<Object>) algoFactory.get();

    Supplier<ListPairSpec<?>> datasetFactory = datasets.get(datasetName);
    if (datasetFactory == null) throw new IllegalArgumentException("Unknown dataset: " + datasetName);
    this.spec = (ListPairSpec<Object>) datasetFactory.get();

    this.nop = (op, token) -> { /* NOP */ };
    this.counting = new CountingHandler<>();
  }

  @Setup(Level.Iteration)
  public void setupIteration() {
    // Fresh lists per iteration so algorithms can’t mutate shared inputs.
    this.from = spec.freshFrom();
    this.to = spec.freshTo();
    this.counting.reset();
  }

  @Benchmark
  public void diff(Blackhole bh) {
    if ("NOP".equals(handlerMode)) {
      algorithm.diff(from, to, nop);
      // cheap consumption to avoid any weird “nothing happened” elimination
      bh.consume(from.size());
      bh.consume(to.size());
    } else {
      algorithm.diff(from, to, counting);
      bh.consume(counting.checksum());
    }
  }

  public static final class CountingHandler<T> implements DiffHandler<T> {
    private long ins, del, eq, other;

    @Override
    public void handle(Operator operator, T token) {
      switch (operator) {
        case INS: ins++; break;
        case DEL: del++; break;
        case MATCH: eq++; break;
        default: other++; break;
      }
    }

    public void reset() {
      ins = del = eq = other = 0;
    }

    public long checksum() {
      return ins * 31 + del * 17 + eq * 13 + other * 7;
    }
  }

  public static final class ListPairSpec<T> {
    private final T[] fromArray;
    private final T[] toArray;

    public ListPairSpec(T[] fromArray, T[] toArray) {
      this.fromArray = fromArray;
      this.toArray = toArray;
    }

    public List<T> freshFrom() {
      List<T> out = new ArrayList<>(fromArray.length);
      for (T t : fromArray) out.add(t);
      return out;
    }

    public List<T> freshTo() {
      List<T> out = new ArrayList<>(toArray.length);
      for (T t : toArray) out.add(t);
      return out;
    }
  }
}