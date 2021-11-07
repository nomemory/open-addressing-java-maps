package performance.jmh.bechmarks.reads;

import org.openjdk.jmh.infra.Blackhole;
import performance.jmh.model.MapTypes;
import performance.jmh.model.StringsSources;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.andreinc.mockneat.unit.objects.From.fromStrings;
import static net.andreinc.mockneat.unit.objects.Probabilities.probabilities;
import static net.andreinc.mockneat.unit.text.Strings.strings;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 3, jvmArgs = {"-Xms6G", "-Xmx16G"})
@Warmup(iterations = 5, time = 5)
@Measurement(iterations = 10, time = 5)
public class RandomStringsReads {

//    @Param({"KEYS_STRING_1_000_000", "KEYS_STRING_10_000_000"})
    @Param({"KEYS_STRING_100_000"})
    private StringsSources input;

//    @Param({"HashMap", "OACkooSAMap","OALinearProbingMap", "OARobinHoodMap", "OAPYPerturbMap"})
    @Param({"HashMap", "OAHopscotchMap" })
    private MapTypes mapClass;

    private Map testedMap;
    private List<String> keys;
    private List<String> keysWithMisses;

    @Setup(Level.Trial)
    public void initMaps() throws IOException {
        testedMap = mapClass.getSupplier().get();
        keys = input.getData();
        keys.forEach(k -> {
            testedMap.put(k, null);
        });
        keysWithMisses =
                probabilities(String.class)
                        .add(0.5, fromStrings(keys))
                        .add(0.5, strings())
                        .mapToString()
                        .list(keys.size())
                        .get();
    }

    @Benchmark
    public void randomReads(Blackhole bh) {
        bh.consume(testedMap.get(fromStrings(keys).get()));
    }

    @Benchmark
    public void randomReadsWithMisses(Blackhole bh) {
        bh.consume(testedMap.get(fromStrings(keysWithMisses).get()));
    }

}
