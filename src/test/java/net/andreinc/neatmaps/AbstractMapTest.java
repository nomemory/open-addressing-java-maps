package net.andreinc.neatmaps;

import net.andreinc.mockneat.abstraction.MockUnit;
import net.andreinc.mockneat.unit.objects.From;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static net.andreinc.mockneat.unit.address.Addresses.addresses;
import static net.andreinc.mockneat.unit.misc.Cars.cars;
import static net.andreinc.mockneat.unit.objects.Constant.constant;
import static net.andreinc.mockneat.unit.objects.Probabilities.probabilities;
import static net.andreinc.mockneat.unit.seq.IntSeq.intSeq;
import static net.andreinc.mockneat.unit.text.Words.words;
import static net.andreinc.mockneat.unit.types.Ints.ints;
import static net.andreinc.mockneat.unit.user.Names.names;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractMapTest<K, V> {

    protected static final int DEFAULT_MAP_SIZE = 100_000;

    protected Map<K,V> testMap;
    protected Supplier<Map<K, V>> mapSupplier;
    protected MockUnit<K> keyGenerator;
    protected Supplier<V> valuesSupplier;
    protected V valueConstant;
    protected K keyConstant;

    @BeforeAll
    protected void beforeAll() {
        this.valuesSupplier = constant(valueConstant).supplier();
    }

    @BeforeEach
    protected void beforeEach() {
        this.testMap = mapSupplier.get();
    }

    @AfterEach
    protected void afterEach() {
        this.testMap = null;
    }

    @Test
    public void testAllValuesPresent() {
        List<K> initialKeys = keyGenerator.list(DEFAULT_MAP_SIZE).get();
        initialKeys.forEach(key ->{
            testMap.put(key, valuesSupplier.get());
        });

        initialKeys.forEach(key -> {
            V value = testMap.get(key);
            Assertions.assertNotNull(value);
            Assertions.assertEquals(value, valuesSupplier.get());
        });
    }

    @Test
    public void testRemoves() {
        List<K> initialKeys = keyGenerator.list(DEFAULT_MAP_SIZE).get();
        Set<K> removedKeys = From.from(initialKeys).set(DEFAULT_MAP_SIZE/2).get();

        initialKeys.forEach(key -> {
            testMap.put(key, valuesSupplier.get());
        });

        removedKeys.forEach(key -> {
            testMap.put(key, valuesSupplier.get());
        });

        removedKeys.forEach(key -> {
            Assertions.assertEquals(testMap.remove(key), "ABC");
            Assertions.assertNull(testMap.remove(key));
            Assertions.assertNull(testMap.get(key));
        });
    }

    @Test
    public void testSizeIs1() {
        constant(keyConstant).list(10).get().forEach(key -> {
            testMap.put(key, valuesSupplier.get());
        });
        Assertions.assertEquals(testMap.size(),1);
    }

    @Test
    public void testSize() {
        Set<K> keys = keyGenerator.set(DEFAULT_MAP_SIZE).get();
        keys.forEach(key -> {
            testMap.put(key, valuesSupplier.get());
        });
        Assertions.assertEquals(keys.size(), testMap.size());
    }

    @Test
    public void testUpdates() {
        Set<K> keys = keyGenerator.set(DEFAULT_MAP_SIZE).get();
        keys.forEach(key -> {
            testMap.put(key, valueConstant);
        });
        Assertions.assertEquals(keys.size(), testMap.size());
        keys.forEach(key -> {
            Assertions.assertEquals(testMap.get(key), valueConstant);
            testMap.put(key, null);
            Assertions.assertNull(testMap.get(key));
        });
    }

    public static abstract class StringKeysTest extends AbstractMapTest<String, String> {
        public StringKeysTest() {
            super();
            this.keyGenerator =
                    probabilities(String.class)
                            .add(0.2, names().full())
                            .add(0.2, addresses())
                            .add(0.2, words())
                            .add(0.2, cars())
                            .add(0.2, ints().mapToString())
                            .mapToString();
            this.valueConstant = "ABC";
            this.keyConstant = "XYZ";
        }
    }

    public static abstract class IntKeysTest extends AbstractMapTest<Integer, String> {
        public IntKeysTest() {
            super();
            this.mapSupplier = () -> new OALinearProbingMap<>();
            this.keyGenerator = intSeq();
            this.valueConstant = "ABC";
            this.keyConstant = 0;
        }
    }
}
