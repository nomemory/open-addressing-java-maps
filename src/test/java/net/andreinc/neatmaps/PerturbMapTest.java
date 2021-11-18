package net.andreinc.neatmaps;

import org.junit.jupiter.api.BeforeAll;

import static net.andreinc.mockneat.unit.objects.From.fromStrings;
import static net.andreinc.mockneat.unit.seq.Seq.seq;

public class PerturbMapTest {
    public static class TestStringKeys extends AbstractMapTest.StringKeysTest {
        @BeforeAll
        public void init() {
            this.mapSupplier = () -> new PerturbMap<>();
        }
    }
    public static class TestIntegerKeys extends AbstractMapTest.IntKeysTest {
        @BeforeAll
        public void init() {
            this.mapSupplier = () -> new PerturbMap<>();
        }
    }
}
