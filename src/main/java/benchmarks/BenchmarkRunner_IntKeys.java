//package benchmarks;
//
//import net.andreinc.mockneat.abstraction.MockUnitInt;
//import net.andreinc.mockneat.abstraction.MockUnitString;
//import net.andreinc.neatmaps.OaMap;
//import org.openjdk.jmh.annotations.*;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import static net.andreinc.mockneat.unit.objects.From.fromInts;
//import static net.andreinc.mockneat.unit.text.Strings.strings;
//import static net.andreinc.mockneat.unit.types.Ints.ints;
//
//@BenchmarkMode(Mode.AverageTime)
//@OutputTimeUnit(TimeUnit.SECONDS)
//@State(Scope.Benchmark)
//@Fork(value = 1, jvmArgs = {"-Xms6G", "-Xmx8G"})
//@Warmup(iterations = 1)
//@Measurement(iterations = 1)
//public class BenchmarkRunner_IntKeys {
//
//    private static final MockUnitInt rndInt = ints().range(1_000_000, Integer.MAX_VALUE);
//    private static final MockUnitString rndString = strings().size(ints().range(2, 8));
//
//    @Param({"100000"})
//    private int inserts;
//
//    @Param({"10000000", "50000000"})
//    private int reads;
//
//    public void insertsAndReads(Map<Integer, String> map) {
//        List<Integer> keys = new ArrayList<>();
//        Integer key;
//        String value;
//        for (int i = 0; i < inserts; i++) {
//            key = rndInt.get();
//            value = rndString.get();
//            map.put(key, value);
//            map.put(i, i + "");
//            keys.add(key);
//            keys.add(i);
//        }
//        for (int i = 0; i < reads; i++) {
//            if (null == map.get(fromInts(keys).get())) {
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
//    public void oaMap_Inserts_Reads() {
//        insertsAndReads(new OaMap<>());
//    }
//
//    public static void main(String[] args) throws Exception {
//        org.openjdk.jmh.Main.main(args);
//    }
//}
