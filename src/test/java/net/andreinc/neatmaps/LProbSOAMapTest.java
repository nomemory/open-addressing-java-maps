package net.andreinc.neatmaps;

import org.junit.jupiter.api.BeforeAll;

public class LProbSOAMapTest {
    public static class TestStringKeys extends AbstractMapTest.StringKeysTest {
        @BeforeAll
        public void init() {
            this.mapSupplier = () -> new LProbSOAMap<>();
        }
    }
    public static class TestIntegerKeys extends AbstractMapTest.IntKeysTest {
        @BeforeAll
        public void init() {
            this.mapSupplier = () -> new LProbSOAMap<>();
        }
    }
}
