//package benchmarks;
//
//import org.openjdk.jmh.annotations.*;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import static net.andreinc.mockneat.unit.address.Addresses.addresses;
//import static net.andreinc.mockneat.unit.misc.Cars.cars;
//import static net.andreinc.mockneat.unit.objects.From.fromStrings;
//import static net.andreinc.mockneat.unit.objects.Probabilities.probabilities;
//import static net.andreinc.mockneat.unit.text.Words.words;
//import static net.andreinc.mockneat.unit.types.Ints.ints;
//import static net.andreinc.mockneat.unit.user.Names.names;
//
//
//@BenchmarkMode(Mode.AverageTime)
//@OutputTimeUnit(TimeUnit.MICROSECONDS)
//@State(Scope.Benchmark)
//@Fork(value = 1, jvmArgs = {"-Xms6G", "-Xmx8G"})
//@Warmup(iterations = 2, time = 5)
//@Measurement(iterations = 5, time = 10)
//public class BenchmarkReads_StringKeys {
//
//    @Param({"1000000", "10000000" })
//    private int mapSize;
//
//    @Param({"HashMap", "OALinearProbingMap", "OARobinHoodMap", "OAPYPeturbMap"})
//    private BenchMapTypes mapClass;
//
//    private final List<String> possibleKeys = new ArrayList<>();
//    private List<String> possibleKeysWithMisses;
//
//    private Map testedMap;
//
//    @Setup(Level.Trial)
//    public void initMaps() {
//        testedMap = mapClass.getSupplier().get();
//        for(int i = 0; i < mapSize; i++) {
//            String key = probabilities(String.class)
//                    .add(0.2, names().full())
//                    .add(0.2, addresses())
//                    .add(0.2, words())
//                    .add(0.2, cars())
//                    .add(0.2, ints().mapToString())
//                    .get();
//            possibleKeys.add(key);
//            testedMap.put(key, "");
//        }
//        possibleKeysWithMisses =
//                probabilities(String.class)
//                    .add(0.3, fromStrings(possibleKeys))
//                    .add(0.7, names().full().append("ABC"))
//                    .list(mapSize)
//                    .get();
//    }
//
//    @Benchmark
//    public void randomReads() {
//        testedMap.get(fromStrings(possibleKeys).get());
//    }
//
//    @Benchmark
//    public void randomReadsWithMisses() {
//        testedMap.get(fromStrings(possibleKeysWithMisses));
//    }
//
//    @Benchmark
//    public void readAllValues() {
//        for(int i = 0; i < possibleKeys.size(); i++) {
//            testedMap.get(possibleKeys.get(i));
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//        org.openjdk.jmh.Main.main(args);
//    }
//}
