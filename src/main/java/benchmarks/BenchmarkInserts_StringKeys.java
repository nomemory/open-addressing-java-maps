package benchmarks;

import net.andreinc.mockneat.abstraction.MockUnitString;
import org.openjdk.jmh.annotations.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.andreinc.mockneat.unit.address.Addresses.addresses;
import static net.andreinc.mockneat.unit.misc.Cars.cars;
import static net.andreinc.mockneat.unit.objects.Probabilities.probabilities;
import static net.andreinc.mockneat.unit.text.Words.words;
import static net.andreinc.mockneat.unit.types.Ints.ints;
import static net.andreinc.mockneat.unit.user.Names.names;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms6G", "-Xmx8G"})
@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 2, time = 10)
public class BenchmarkInserts_StringKeys {
    
    @Param({"1000000", "10000000" })
    private int mapSize;

    @Param({"HashMap", "OALinearProbingMap", "OARobinHoodMap", "OAPYPeturbMap"})
    private BenchMapTypes mapClass;

    private Map testedMap;

    private MockUnitString keyGenerator =
            probabilities(String.class)
                    .add(0.2, names().full())
                    .add(0.2, addresses())
                    .add(0.2, words())
                    .add(0.2, cars())
                    .add(0.2, ints().mapToString())
                    .mapToString();

    @Setup(Level.Trial)
    public void init() {
        this.testedMap = mapClass.getSupplier().get();
    }

    @Benchmark
    public void randomInserts() {
        for (int i = 0; i < mapSize; i++) {
            testedMap.put(keyGenerator.get(), "");
        }
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
