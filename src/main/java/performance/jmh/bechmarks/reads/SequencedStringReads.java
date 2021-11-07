package performance.jmh.bechmarks.reads;

import org.openjdk.jmh.annotations.*;
import performance.jmh.model.MapTypes;
import performance.jmh.model.StringsSources;

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
@Fork(value = 1, jvmArgs = {"-Xms6G", "-Xmx16G"})
@Warmup(iterations = 2, time = 5)
@Measurement(iterations = 3, time = 5)
public class SequencedStringReads {

    @Param({"SEQUENCED_KEYS_1_000_000"})
    private StringsSources input;

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
    public void randomReads() {
        testedMap.get(fromStrings(keys).get());
    }

    @Benchmark
    public void randomReadsWithMisses() {
        testedMap.get(fromStrings(keysWithMisses).get());
    }

}
