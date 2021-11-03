package net.andreinc.neatmaps.test;

import net.andreinc.mockneat.abstraction.MockUnit;
import net.andreinc.mockneat.unit.objects.From;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import static net.andreinc.mockneat.unit.objects.Constant.constant;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractMapTest<K, V> {

    protected static final int DEFAULT_MAP_SIZE = 100_000;

    protected Map<K,V> testMap;
    protected Supplier<Map<K, V>> mapSupplier;
    protected MockUnit<K> keyGenerator;
    protected Supplier<V> valuesSupplier;
    protected V constant;

    @BeforeAll
    protected void init() {
        this.testMap = mapSupplier.get();
        this.valuesSupplier = constant(constant).supplier();
    }

    @AfterAll
    protected void clear() {
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
            if (value==null) {
                System.out.println(key);
            }
//            Assertions.assertNotNull(value);
//            Assertions.assertEquals(value, valuesSupplier.get());
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
}
