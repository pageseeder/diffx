package org.pageseeder.diffx.bench;

import org.pageseeder.diffx.algorithm.*;
import org.pageseeder.diffx.api.DiffAlgorithm;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class Registries {
  private Registries() {}

  public static Map<String, Supplier<DiffAlgorithm<?>>> algorithms() {
    Map<String, Supplier<DiffAlgorithm<?>>> m = new LinkedHashMap<>();
    m.put("Wu", WuAlgorithm::new);
    m.put("MyersGreedy", MyersGreedyAlgorithm::new);
    m.put("MyersGreedy2", MyersGreedyAlgorithm2::new);
    m.put("MyersLinear", MyersLinearAlgorithm::new);
    m.put("KumarRangan", KumarRanganAlgorithm::new);
    m.put("KumarRangan2", KumarRanganAlgorithm2::new);
    m.put("Histogram", HistogramAlgorithm::new);
    m.put("Patience", PatienceAlgorithm::new);
    m.put("Hirschberg", HirschbergAlgorithm::new);
    m.put("Hirschberg2", HirschbergAlgorithm2::new);
    m.put("WagnerFischer", WagnerFischerAlgorithm::new);
    return m;
  }

  public static Map<String, Supplier<DiffAlgorithmBench.ListPairSpec<?>>> datasets() {
    Map<String, Supplier<DiffAlgorithmBench.ListPairSpec<?>>> m = new LinkedHashMap<>();
    List<Integer> lengths = List.of(100, 250, 500, 750, 1000, 2500, 5000, 7500, 10000, 25000);
    List<Double> variations = List.of(0.05, 0.10, 0.25, 0.50, 0.75, 0.90, 0.95);
    for (int length : lengths) {
      for (double v : variations) {
        String vf = (v < .1 ? "0" : "") +(int)Math.round(v*100);
        m.put("chars_"+length+"_var"+vf, () -> Datasets.getRandomStringPair(length, v));
        m.put("patterns_"+length+"_var"+vf, () -> Datasets.getPatternStringPair(length, v));
      }
    }
    return m;
  }

  public static void main(String[] args) {
    for (Map.Entry<String, Supplier<DiffAlgorithmBench.ListPairSpec<?>>> entry : datasets().entrySet()) {
      System.out.println(entry.getKey());
    }
  }
}