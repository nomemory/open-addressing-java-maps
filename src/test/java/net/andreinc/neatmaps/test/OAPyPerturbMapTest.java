package net.andreinc.neatmaps.test;

import net.andreinc.mockneat.abstraction.MockUnitString;
import net.andreinc.neatmaps.OALinearProbingMap;
import net.andreinc.neatmaps.OAPyPerturbMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.andreinc.mockneat.unit.address.Addresses.addresses;
import static net.andreinc.mockneat.unit.misc.Cars.cars;
import static net.andreinc.mockneat.unit.objects.Constant.constant;
import static net.andreinc.mockneat.unit.objects.From.fromStrings;
import static net.andreinc.mockneat.unit.objects.Probabilities.probabilities;
import static net.andreinc.mockneat.unit.seq.Seq.seq;
import static net.andreinc.mockneat.unit.text.Words.words;
import static net.andreinc.mockneat.unit.types.Ints.ints;
import static net.andreinc.mockneat.unit.user.Names.names;

public class OAPyPerturbMapTest extends AbstractMapTest<String, String> {

    public OAPyPerturbMapTest() {
        super();
        this.mapSupplier = () -> new OAPyPerturbMap<>();
        this.keyGenerator =
                probabilities(String.class)
                        .add(0.2, names().full())
                        .add(0.2, addresses())
                        .add(0.2, words())
                        .add(0.2, cars())
                        .add(0.2, ints().mapToString())
                        .mapToString();
        this.constant = "ABC";
    }

    @Test
    public void testAllValuesPresent() {
        super.testAllValuesPresent();
    }

    @Test
    public void testRemoves() {
        super.testRemoves();
    }
}
