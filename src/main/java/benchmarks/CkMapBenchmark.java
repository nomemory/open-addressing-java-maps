//package benchmarks;
//
//import net.andreinc.mockneat.abstraction.MockUnitString;
//import net.andreinc.neatmaps.OaMap;
//import net.andreinc.neatmaps.ck.CkMap;
//import org.openjdk.jmh.annotations.*;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
//import static net.andreinc.mockneat.unit.objects.From.fromStrings;
//import static net.andreinc.mockneat.unit.text.Strings.strings;
//import static net.andreinc.mockneat.unit.types.Ints.ints;
//
//
//@BenchmarkMode(Mode.AverageTime)
//@OutputTimeUnit(TimeUnit.SECONDS)
//@State(Scope.Benchmark)
//@Fork(value = 1, jvmArgs = {"-Xms6G", "-Xmx8G"})
//@Warmup(iterations = 1)
//@Measurement(iterations = 1)
//public class CkMapBenchmark {
//
//    private static final MockUnitString rndString = strings().size(ints().range(2, 1024));
//
//    @Param({"1000000"})
//    private int inserts;
//
//    @Param({"1000000", "50000000"})
//    private int reads;
//
//    public void insertsAndReads(Map<String, String> map) {
//        List<String> keys = new ArrayList<>();
//        String key, value;
//        for(int i = 0; i < inserts; i++) {
//            key = rndString.get();
//            value = rndString.get();
//            map.put(key, value);
//            map.put(i+"", i+"");
//            keys.add(key);
//            keys.add(i+"");
//        }
//        for(int i = 0; i < reads; i++) {
//            if (null==map.get(fromStrings(keys).get())) {
//                throw new IllegalArgumentException();
//            }
//        }
//    }
//
//    @Benchmark
//    public void hashMap_Inserts_Reads() {
//        insertsAndReads(new HashMap<>());
//    }
//
//    @Benchmark
//    public void ckMap_Inserts_Reads() {
//        insertsAndReads(new CkMap<>());
//    }
//
//    public static void main(String[] args) throws IOException {
//        org.openjdk.jmh.Main.main(args);
//
//    }
//}
//
