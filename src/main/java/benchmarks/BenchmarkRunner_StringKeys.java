package benchmarks;

import net.andreinc.neatmaps.CkooMap;
import net.andreinc.neatmaps.OaMapLP;
import net.andreinc.neatmaps.OaMapLP_noMixer;
import org.openjdk.jmh.annotations.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static net.andreinc.mockneat.unit.objects.From.fromStrings;
import static net.andreinc.mockneat.unit.types.Ints.ints;
import static net.andreinc.mockneat.unit.user.Names.names;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms6G", "-Xmx8G"})
@Warmup(iterations = 1)
@Measurement(iterations = 1)
public class BenchmarkRunner_StringKeys {

    @Param({"10000000"})
    private int inserts;

    @Param({"30000000"})
    private int reads;

    private final Map<String, String> hash = new HashMap<>();
    private final Map<String, String> ckoo = new OaMapLP<>();
    private final List<String> names = new ArrayList<>();;

    @Setup
    public void initMaps() {
        for(int i = 0; i < inserts/2; i++) {
            String key = names().full().get();
            names.add(key);
            hash.put(key, "");
            ckoo.put(key, "");
            hash.put(i+"", "");
            ckoo.put(i+"", "");
        }
    }

    public void insertsAndReads(Map<String, String> map) {
        for(int i = 0; i < reads; i++) {
            map.get(fromStrings(names).get());
            map.get(ints().range(0, inserts/2).mapToString().get());

        }
    }

    @Benchmark
    public void hashMap_Inserts_Reads() {
        insertsAndReads(new HashMap<>());
    }

    @Benchmark
    public void oaMapRH_Inserts_Reads() {
        insertsAndReads(new CkooMap<>());
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
