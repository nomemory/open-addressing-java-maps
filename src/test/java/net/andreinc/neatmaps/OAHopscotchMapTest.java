package net.andreinc.neatmaps;

import org.junit.jupiter.api.BeforeAll;

public class OAHopscotchMapTest {
    public static class TestStringKeys extends AbstractMapTest.StringKeysTest {
        @BeforeAll
        public void init() {
            this.mapSupplier = () -> new OAHopscotchMap_draft<>();
        }
    }
    public static class TestIntegerKeys extends AbstractMapTest.IntKeysTest {
        @BeforeAll
        public void init() {
            this.mapSupplier = () -> new OAHopscotchMap_draft<>();
        }
    }
}
