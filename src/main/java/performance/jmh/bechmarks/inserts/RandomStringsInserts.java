package performance.jmh.bechmarks.inserts;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import performance.jmh.types.MapTypes;
import performance.jmh.types.StringsSourceTypes;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 3, jvmArgs = {"-Xms6G", "-Xmx16G"})
@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 6, time = 10)
public class RandomStringsInserts {

    @Param({"KEYS_STRING_100_000", "KEYS_STRING_1_000_000", "KEYS_STRING_10_000_000"})
    private StringsSourceTypes numberOfInserts;

    @Param({"LProbMap", "LProbBinsMap", "LProbRadarMap", "RobinHoodMap", "PerturbMap", "HashMap"})
    private MapTypes mapClass;

    private Map testedMap;
    private List<String> toInsert;

    @Setup(Level.Trial)
    public void init() throws IOException {
        this.testedMap = mapClass.getSupplier().get();
        this.toInsert = numberOfInserts.getData();
    }

    @Benchmark
    public void randomInserts(Blackhole bh) {
        toInsert.forEach(k -> bh.consume(testedMap.put(k, null)));
    }
}